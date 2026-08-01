package com.snote.domezos.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.snote.domezos.R
import com.snote.domezos.ui.components.AppTopBar
import com.snote.domezos.ui.theme.SuccessGreen
import com.snote.domezos.billing.BillingHelper
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onThemeChanged: (String) -> Unit = {},
    currentThemeId: String = "classic",
    isPremium: Boolean = false,
    onPremiumChanged: (Boolean) -> Unit = {},
    billingHelper: BillingHelper? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf(0) } // 0 = one-time, 1 = subscription
    val features = remember {
        listOf(
            R.string.premium_feature_1,
            R.string.premium_feature_2,
            R.string.premium_feature_3,
            R.string.premium_feature_4,
            R.string.premium_feature_5
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { AppTopBar(currentRoute = "premium", onNavigate = onNavigate, onBack = onBack, onThemeChanged = onThemeChanged, currentThemeId = currentThemeId) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = stringResource(R.string.premium_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = stringResource(R.string.premium_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isPremium) {
                    Text(
                        text = stringResource(R.string.premium_active_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SuccessGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            // Features
            GlassCard(borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    features.forEachIndexed { index, resId ->
                        var visible by remember { mutableStateOf(false) }
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(index * 100L)
                            visible = true
                        }
                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(500)) + slideInHorizontally(tween(500)) { -20 }
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(resId),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Plan selection label
            Text(
                text = stringResource(R.string.premium_choose_plan),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )

            // Prices must reflect Google Play's live, locally-charged currency —
            // never hardcode a currency symbol, since Play Billing charges users
            // in their own local currency regardless of the app's UI language.
            val livePrice = billingHelper?.oneTimePriceFormatted
            val monthlyPriceText = if (livePrice != null) {
                stringResource(R.string.premium_price_monthly_fmt, livePrice)
            } else {
                stringResource(R.string.premium_price_monthly)
            }
            val liveTrialDays = billingHelper?.subTrialDays
            val liveRecurringPrice = billingHelper?.subRecurringPriceFormatted
            val subscriptionPriceText = if (liveRecurringPrice != null) {
                stringResource(R.string.premium_price_subscription_fmt, liveRecurringPrice)
            } else {
                stringResource(R.string.premium_price_subscription)
            }
            val trialBadgeText = if (liveTrialDays != null) {
                stringResource(R.string.premium_trial_badge_fmt, liveTrialDays)
            } else {
                stringResource(R.string.premium_trial_badge)
            }
            val subOfferTrialText = if (liveTrialDays != null && liveRecurringPrice != null) {
                stringResource(R.string.premium_sub_offer_trial_fmt, liveTrialDays, liveRecurringPrice)
            } else {
                stringResource(R.string.premium_sub_offer_trial)
            }
            val offerPriceText = if (livePrice != null) {
                stringResource(R.string.premium_offer_price_fmt, livePrice)
            } else {
                stringResource(R.string.premium_offer_price)
            }

            // Plan cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlanCard(
                    modifier = Modifier.weight(1f),
                    isSelected = selectedPlan == 0,
                    title = stringResource(R.string.premium_plan_monthly),
                    price = monthlyPriceText,
                    badge = null,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        selectedPlan = 0
                    }
                )
                PlanCard(
                    modifier = Modifier.weight(1f),
                    isSelected = selectedPlan == 1,
                    title = stringResource(R.string.premium_plan_subscription),
                    price = subscriptionPriceText,
                    badge = trialBadgeText,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        selectedPlan = 1
                    }
                )
            }

            // Dynamic offer details
            if (selectedPlan == 0) {
                GlassCard(borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stringResource(R.string.premium_offer_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                        listOf(
                            offerPriceText,
                            stringResource(R.string.premium_offer_duration),
                            stringResource(R.string.premium_offer_no_renewal),
                            stringResource(R.string.premium_offer_optional)
                        ).forEach { text ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            } else {
                GlassCard(borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = stringResource(R.string.premium_sub_offer_title),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                        listOf(
                            subOfferTrialText,
                            stringResource(R.string.premium_sub_offer_renewal),
                            stringResource(R.string.premium_sub_offer_cancel),
                            stringResource(R.string.premium_offer_optional)
                        ).forEach { text ->
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("•", color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall)
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Buy button
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (!isPremium) {
                        val activity = context as? Activity
                        if (activity != null) {
                            if (selectedPlan == 0) {
                                billingHelper?.launchBillingFlow(activity)
                            } else {
                                billingHelper?.launchSubscriptionBillingFlow(activity)
                            }
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Error: Activity context not found") }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isPremium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when {
                        isPremium -> stringResource(R.string.premium_active_title)
                        selectedPlan == 1 -> stringResource(R.string.premium_btn_subscription)
                        else -> stringResource(R.string.premium_btn_monthly)
                    },
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )
            }

            Text(
                text = stringResource(R.string.premium_terms),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            FooterNote()
        }
    }
}

@Composable
private fun PlanCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    title: String,
    price: String,
    badge: String?,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected)
        MaterialTheme.colorScheme.secondary
    else
        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)

    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (badge != null) {
                Text(
                    text = badge,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            } else {
                Spacer(Modifier.height(18.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = price,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
    }
}
