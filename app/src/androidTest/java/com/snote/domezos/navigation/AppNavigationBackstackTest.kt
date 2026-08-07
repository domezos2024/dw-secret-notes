package com.snote.domezos.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.snote.domezos.R
import com.snote.domezos.ui.theme.ClassicTheme
import com.snote.domezos.ui.theme.DwSecretNotesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies the invariant documented in AppNavigation.kt: the back stack is always kept at
 * [Main, currentScreen], so the top-left back button lands on Main regardless of how many
 * sub-screens were hopped through via the menu.
 */
@RunWith(AndroidJUnit4::class)
class AppNavigationBackstackTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun stringRes(id: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private fun openMenuAndNavigateTo(itemLabel: String) {
        composeTestRule.onNodeWithContentDescription(stringRes(R.string.menu_more)).performClick()
        composeTestRule.onNodeWithText(itemLabel).performClick()
    }

    @Test
    fun backButtonFromASubScreenReachedViaAnotherSubScreenLandsOnMain() {
        composeTestRule.setContent {
            DwSecretNotesTheme(themeConfig = ClassicTheme) {
                AppNavigation(startWithLanguagePicker = false)
            }
        }

        // Main -> Premium
        openMenuAndNavigateTo(stringRes(R.string.nav_premium))
        composeTestRule.onNodeWithText(stringRes(R.string.premium_title)).assertExists()

        // Premium -> Help (a menu jump between two sub-screens, never passing back through Main)
        openMenuAndNavigateTo(stringRes(R.string.nav_help))
        composeTestRule.onNodeWithText(stringRes(R.string.help_title)).assertExists()

        // Pressing back from Help must land directly on Main, not retrace back to Premium.
        composeTestRule.onNodeWithContentDescription(stringRes(R.string.cd_back)).performClick()
        composeTestRule.onNodeWithText(stringRes(R.string.decrypt_hint_body)).assertExists()
    }

    @Test
    fun navigatingToTheSameScreenTwiceInARowStaysOnThatScreen() {
        composeTestRule.setContent {
            DwSecretNotesTheme(themeConfig = ClassicTheme) {
                AppNavigation(startWithLanguagePicker = false)
            }
        }

        openMenuAndNavigateTo(stringRes(R.string.nav_info))
        composeTestRule.onNodeWithText(stringRes(R.string.info_title)).assertExists()

        // AppTopBar's NavMenuItem only navigates when currentRoute != targetRoute; re-selecting
        // the current screen must be a no-op, not a crash or duplicate back-stack entry.
        openMenuAndNavigateTo(stringRes(R.string.nav_info))
        composeTestRule.onNodeWithText(stringRes(R.string.info_title)).assertExists()
    }
}
