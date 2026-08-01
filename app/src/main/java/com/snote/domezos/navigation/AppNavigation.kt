package com.snote.domezos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.snote.domezos.billing.BillingHelper
import com.snote.domezos.ui.screens.HelpScreen
import com.snote.domezos.ui.screens.InfoScreen
import com.snote.domezos.ui.screens.LanguageScreen
import com.snote.domezos.ui.screens.MainScreen
import com.snote.domezos.ui.screens.PremiumScreen
import com.snote.domezos.ui.screens.TinyUrlScreen
import com.snote.domezos.ui.screens.TipScreen

@Composable
fun AppNavigation(
    startWithLanguagePicker: Boolean,
    showRatePrompt: Boolean = false,
    initialDecryptInput: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    onThemeChanged: (String) -> Unit = {},
    currentThemeId: String = "classic",
    isPremium: Boolean = false,
    onPremiumChanged: (Boolean) -> Unit = {},
    billingHelper: BillingHelper? = null
) {
    val navController = rememberNavController()
    val start = if (startWithLanguagePicker) Screen.Language.route else Screen.Main.route

    // Always keep the back stack at most [Main, currentScreen]: any menu jump from one
    // sub-screen to another pops the previous sub-screen first, so the top-left back
    // button (popBackStack) reliably lands on Main instead of retracing every hop.
    val navigateTo: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(Screen.Main.route) { inclusive = false }
            launchSingleTop = true
        }
    }
    val backToMain: () -> Unit = { navController.popBackStack() }

    LaunchedEffect(initialDecryptInput) {
        if (initialDecryptInput != null) {
            navController.navigate(Screen.Main.route) { launchSingleTop = true }
        }
    }

    NavHost(navController = navController, startDestination = start) {

        composable(Screen.Language.route) {
            LanguageScreen(
                isFirstRun = startWithLanguagePicker,
                onConfirm  = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Language.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onNavigate = navigateTo,
                initialDecryptInput = initialDecryptInput,
                onDeepLinkConsumed = onDeepLinkConsumed,
                onThemeChanged = onThemeChanged,
                currentThemeId = currentThemeId,
                isPremium = isPremium,
                showRatePrompt = showRatePrompt
            )
        }

        composable(Screen.Help.route) {
            HelpScreen(
                onNavigate = navigateTo,
                onBack     = backToMain,
                onThemeChanged = onThemeChanged,
                currentThemeId = currentThemeId
            )
        }

        composable(Screen.Info.route) {
            InfoScreen(
                onNavigate = navigateTo,
                onBack     = backToMain,
                onThemeChanged = onThemeChanged,
                currentThemeId = currentThemeId
            )
        }

        composable(Screen.Premium.route) {
            PremiumScreen(
                onNavigate = navigateTo,
                onBack     = backToMain,
                onThemeChanged = onThemeChanged,
                currentThemeId = currentThemeId,
                isPremium = isPremium,
                onPremiumChanged = onPremiumChanged,
                billingHelper = billingHelper
            )
        }

        composable(Screen.TinyUrl.route) {
            TinyUrlScreen(
                onBack = backToMain,
                onNavigate = navigateTo,
                onThemeChanged = onThemeChanged,
                currentThemeId = currentThemeId
            )
        }

        composable(Screen.Tip.route) {
            TipScreen(
                onBack = backToMain,
                onNavigate = navigateTo,
                onThemeChanged = onThemeChanged,
                currentThemeId = currentThemeId
            )
        }
    }
}
