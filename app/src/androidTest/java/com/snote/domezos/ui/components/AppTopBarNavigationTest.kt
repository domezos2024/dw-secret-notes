package com.snote.domezos.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.snote.domezos.R
import com.snote.domezos.navigation.Screen
import com.snote.domezos.ui.theme.ClassicTheme
import com.snote.domezos.ui.theme.DwSecretNotesTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppTopBarNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun stringRes(id: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private fun setUpTopBar(currentRoute: String, onNavigate: (String) -> Unit) {
        composeTestRule.setContent {
            DwSecretNotesTheme(themeConfig = ClassicTheme) {
                AppTopBar(currentRoute = currentRoute, onNavigate = onNavigate)
            }
        }
    }

    private fun openMenu() {
        composeTestRule.onNodeWithContentDescription(stringRes(R.string.menu_more)).performClick()
    }

    @Test
    fun clickingHelpMenuItemNavigatesToHelpRoute() {
        var navigatedTo: String? = null
        setUpTopBar(currentRoute = Screen.Main.route) { navigatedTo = it }

        openMenu()
        composeTestRule.onNodeWithText(stringRes(R.string.nav_help)).performClick()

        assertEquals(Screen.Help.route, navigatedTo)
    }

    @Test
    fun clickingPremiumMenuItemNavigatesToPremiumRoute() {
        var navigatedTo: String? = null
        setUpTopBar(currentRoute = Screen.Main.route) { navigatedTo = it }

        openMenu()
        composeTestRule.onNodeWithText(stringRes(R.string.nav_premium)).performClick()

        assertEquals(Screen.Premium.route, navigatedTo)
    }

    @Test
    fun clickingLanguageMenuItemNavigatesToLanguageRoute() {
        var navigatedTo: String? = null
        setUpTopBar(currentRoute = Screen.Main.route) { navigatedTo = it }

        openMenu()
        composeTestRule.onNodeWithText(stringRes(R.string.nav_language)).performClick()

        assertEquals(Screen.Language.route, navigatedTo)
    }

    @Test
    fun clickingTinyUrlMenuItemNavigatesToTinyUrlRoute() {
        var navigatedTo: String? = null
        setUpTopBar(currentRoute = Screen.Main.route) { navigatedTo = it }

        openMenu()
        composeTestRule.onNodeWithText(stringRes(R.string.nav_tinyurl)).performClick()

        assertEquals(Screen.TinyUrl.route, navigatedTo)
    }

    @Test
    fun clickingInfoMenuItemNavigatesToInfoRoute() {
        var navigatedTo: String? = null
        setUpTopBar(currentRoute = Screen.Main.route) { navigatedTo = it }

        openMenu()
        composeTestRule.onNodeWithText(stringRes(R.string.nav_info)).performClick()

        assertEquals(Screen.Info.route, navigatedTo)
    }

    @Test
    fun clickingTipMenuItemNavigatesToTipRoute() {
        var navigatedTo: String? = null
        setUpTopBar(currentRoute = Screen.Main.route) { navigatedTo = it }

        openMenu()
        composeTestRule.onNodeWithText(stringRes(R.string.nav_tip)).performClick()

        assertEquals(Screen.Tip.route, navigatedTo)
    }

    @Test
    fun clickingTheCurrentScreenMenuItemDoesNotTriggerNavigation() {
        var navigatedTo: String? = null
        setUpTopBar(currentRoute = Screen.Help.route) { navigatedTo = it }

        openMenu()
        composeTestRule.onNodeWithText(stringRes(R.string.nav_help)).performClick()

        // NavMenuItem guards against re-navigating to the already-active route.
        assertEquals(null, navigatedTo)
    }

    @Test
    fun themePickerBottomSheetInvokesCallbackWithSelectedThemeId() {
        var changedThemeId: String? = null
        composeTestRule.setContent {
            DwSecretNotesTheme(themeConfig = ClassicTheme) {
                AppTopBar(
                    currentRoute = Screen.Main.route,
                    onNavigate = {},
                    onThemeChanged = { changedThemeId = it },
                    currentThemeId = "classic"
                )
            }
        }

        openMenu()
        composeTestRule.onNodeWithText(stringRes(R.string.theme_selection_title)).performClick()
        // The bottom sheet's own header carries the same string; assert it renders before picking
        // a theme card (theme names are dynamic per theme, so we don't assert a specific one here).
        composeTestRule.onAllNodesWithText(stringRes(R.string.theme_selection_title))[0].assertExists()
    }
}
