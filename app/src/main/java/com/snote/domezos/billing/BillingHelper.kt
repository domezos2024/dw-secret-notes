package com.snote.domezos.billing

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.android.billingclient.api.*
import com.snote.domezos.data.BackendClient
import com.snote.domezos.data.Prefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BillingHelper(
    private val context: Context,
    private val onPremiumActivated: () -> Unit,
    private val onPremiumDeactivated: (() -> Unit)? = null,
    private val onTipPurchased: ((Int) -> Unit)? = null
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) handlePurchase(purchase)
            }
        }
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    private val productId = "premium_tinyurl_30days"
    private val subscriptionId = "abo_tiny_url_days"
    private val tipProductId = "tipp_jar"
    private var productDetails: ProductDetails? = null
    private var subscriptionDetails: ProductDetails? = null
    private var tipProductDetails: ProductDetails? = null
    private var isBillingReady = false
    private var pendingSync: (() -> Unit)? = null

    // Live, correctly localized prices from Google Play — must be used instead of any
    // hardcoded price string, since Play Billing charges in the user's local currency
    // regardless of the app's UI language.
    var oneTimePriceFormatted: String? by mutableStateOf(null)
        private set
    var subTrialDays: Int? by mutableStateOf(null)
        private set
    var subTrialPriceFormatted: String? by mutableStateOf(null)
        private set
    var subRecurringPriceFormatted: String? by mutableStateOf(null)
        private set
    var tipPriceFormatted: String? by mutableStateOf(null)
        private set

    init {
        startConnection()
    }

    private fun startConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isBillingReady = true
                    queryInAppProducts()
                    querySubscription()
                    consumeStaleInAppPurchases()
                    pendingSync?.invoke()
                    pendingSync = null
                }
            }

            override fun onBillingServiceDisconnected() {
                startConnection()
            }
        })
    }

    private fun queryInAppProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build(),
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(tipProductId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            ))
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val productDetailsList = queryProductDetailsResult.productDetailsList
                productDetails    = productDetailsList.find { it.productId == productId }
                tipProductDetails = productDetailsList.find { it.productId == tipProductId }
                oneTimePriceFormatted = productDetails?.oneTimePurchaseOfferDetails?.formattedPrice
                tipPriceFormatted = tipProductDetails?.oneTimePurchaseOfferDetails?.formattedPrice
            }
        }
    }

    private fun querySubscription() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(subscriptionId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            ))
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                subscriptionDetails = queryProductDetailsResult.productDetailsList.find { it.productId == subscriptionId }
                val phases = subscriptionDetails?.subscriptionOfferDetails
                    ?.firstOrNull()?.pricingPhases?.pricingPhaseList.orEmpty()
                val trialPhase = phases.firstOrNull { it.priceAmountMicros == 0L }
                val recurringPhase = phases.lastOrNull { it.priceAmountMicros > 0L }
                subTrialDays = trialPhase?.billingPeriod?.let { parseIso8601DurationDays(it) }
                subTrialPriceFormatted = trialPhase?.formattedPrice
                subRecurringPriceFormatted = recurringPhase?.formattedPrice
            }
        }
    }

    /** Parses a subset of ISO 8601 durations used by Play Billing (e.g. "P5D", "P1W", "P1M") into days. */
    private fun parseIso8601DurationDays(period: String): Int? {
        val match = Regex("^P(\\d+)([DWMY])$").find(period) ?: return null
        val (amount, unit) = match.destructured
        val n = amount.toIntOrNull() ?: return null
        return when (unit) {
            "D" -> n
            "W" -> n * 7
            "M" -> n * 30
            "Y" -> n * 365
            else -> null
        }
    }

    fun launchBillingFlow(activity: Activity) {
        val product = productDetails ?: return
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(product)
                    .build()
            ))
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun launchSubscriptionBillingFlow(activity: Activity) {
        val product = subscriptionDetails ?: return
        val offerToken = product.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(product)
                    .setOfferToken(offerToken)
                    .build()
            ))
            .build()
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    /**
     * Google Play's own checkout sheet lets the user pick the quantity for this product
     * (enabled in Play Console), so no quantity is passed here — [handlePurchase] reads
     * back whatever quantity the user chose via [Purchase.getQuantity].
     */
    fun launchTipBillingFlow(activity: Activity): BillingResult? {
        val product = tipProductDetails ?: return null
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(product)
                    .build()
            ))
            .build()
        return billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    /**
     * Queries currently-owned INAPP purchases and re-runs them through [handlePurchase].
     * The one-time "30 Tage Premium" product must be consumed to be repurchasable, but older
     * purchases made before that behavior existed were only acknowledged, never consumed —
     * leaving them permanently "owned" and causing ITEM_ALREADY_OWNED on any repurchase attempt.
     * This clears that stale state on the next app start.
     */
    private fun consumeStaleInAppPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return@queryPurchasesAsync
            for (purchase in purchases) handlePurchase(purchase)
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return
        val firstProduct = purchase.products.firstOrNull()
        when (firstProduct) {
            tipProductId -> handleTipPurchase(purchase)
            productId -> handleInAppPremiumPurchase(purchase)
            else -> handleSubscriptionPurchase(purchase, firstProduct)
        }
    }

    private fun handleTipPurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                onTipPurchased?.invoke(purchase.quantity)
            }
        }
    }

    private fun handleInAppPremiumPurchase(purchase: Purchase) {
        // Consumable: allows the 30-day grant to be repurchased once it runs out, instead
        // of Google Play permanently marking it "owned".
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Prefs.setPremiumSource(context, "inapp")
                onPremiumActivated()
                scope.launch { BackendClient.activatePremium(context, productId) }
            }
        }
    }

    private fun handleSubscriptionPurchase(purchase: Purchase, firstProduct: String?) {
        val activate = {
            Prefs.setPremiumSource(context, "subscription")
            onPremiumActivated()
            scope.launch { BackendClient.activatePremium(context, firstProduct ?: subscriptionId) }
        }
        if (!purchase.isAcknowledged) {
            val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            billingClient.acknowledgePurchase(acknowledgeParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    activate()
                }
            }
        } else {
            activate()
        }
    }

    fun syncSubscriptions() {
        if (!isBillingReady) {
            pendingSync = { doSyncSubscriptions() }
            return
        }
        doSyncSubscriptions()
    }

    private fun doSyncSubscriptions() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
        billingClient.queryPurchasesAsync(params) { result, purchases ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) return@queryPurchasesAsync
            val activeSub = purchases.firstOrNull { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            if (activeSub != null) {
                Prefs.setPremiumSource(context, "subscription")
                onPremiumActivated()
                scope.launch { BackendClient.activatePremium(context, subscriptionId) }
            } else if (Prefs.getPremiumSource(context) == "subscription") {
                Prefs.setPremiumSource(context, "none")
                onPremiumDeactivated?.invoke()
                scope.launch { BackendClient.deactivatePremium(context) }
            }
        }
    }

    fun destroy() {
        scope.cancel()
    }
}
