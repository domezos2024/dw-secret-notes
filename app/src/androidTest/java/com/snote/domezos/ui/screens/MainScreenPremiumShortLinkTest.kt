package com.snote.domezos.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.snote.domezos.R
import com.snote.domezos.ui.theme.ClassicTheme
import com.snote.domezos.ui.theme.DwSecretNotesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Fakes a premium user (isPremium = true) and a backend that answers with a short
 * snote.fun link, then verifies MainScreen surfaces that short link as-is instead of
 * mangling it into the long domezos-ware.org form.
 *
 * The real encrypt call goes through SecretWebView -> a hidden WebView -> the live backend,
 * which isn't something a test should depend on. `encryptOverride` (a test-only seam on
 * MainScreen) lets us fake just the backend's response and exercise the rest of the flow
 * for real.
 */
@RunWith(AndroidJUnit4::class)
class MainScreenPremiumShortLinkTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun stringRes(id: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    @Test
    fun premiumUserEncryptingGetsAShortSnoteFunLinkBack() {
        val fakeShortLink = "https://snote.fun?link=abc1234"

        composeTestRule.setContent {
            DwSecretNotesTheme(themeConfig = ClassicTheme) {
                MainScreen(
                    onNavigate = {},
                    isPremium = true,
                    encryptOverride = { _, _, callback -> callback(fakeShortLink) }
                )
            }
        }

        composeTestRule.onNodeWithText(stringRes(R.string.hint_enter_text))
            .performTextInput("geheime Nachricht")
        composeTestRule.onNodeWithText(stringRes(R.string.btn_encrypt)).performClick()

        // Premium-only alias box (MainScreen only renders this when isPremium == true).
        composeTestRule.onNodeWithText(stringRes(R.string.label_your_alias)).assertExists()

        // The link field must show exactly the short snote.fun link the "backend" returned,
        // not a rewritten/long-form version.
        composeTestRule.onNodeWithText(fakeShortLink).assertExists()
    }

    @Test
    fun premiumUserEncryptingGetsTheShortAliasDerivedFromTheLinkParam() {
        val fakeShortLink = "https://snote.fun?link=xyz9988"

        composeTestRule.setContent {
            DwSecretNotesTheme(themeConfig = ClassicTheme) {
                MainScreen(
                    onNavigate = {},
                    isPremium = true,
                    encryptOverride = { _, _, callback -> callback(fakeShortLink) }
                )
            }
        }

        composeTestRule.onNodeWithText(stringRes(R.string.hint_enter_text))
            .performTextInput("geheime Nachricht")
        composeTestRule.onNodeWithText(stringRes(R.string.btn_encrypt)).performClick()

        // formatGeneratedAlias() takes the first 8 chars after "link=" for snote.fun links.
        composeTestRule.onNodeWithText("xyz9988").assertExists()
    }

    @Test
    fun nonPremiumUserDoesNotSeeTheAliasBoxEvenWithAShortLink() {
        val fakeShortLink = "https://snote.fun?link=abc1234"

        composeTestRule.setContent {
            DwSecretNotesTheme(themeConfig = ClassicTheme) {
                MainScreen(
                    onNavigate = {},
                    isPremium = false,
                    encryptOverride = { _, _, callback -> callback(fakeShortLink) }
                )
            }
        }

        composeTestRule.onNodeWithText(stringRes(R.string.hint_enter_text))
            .performTextInput("geheime Nachricht")
        composeTestRule.onNodeWithText(stringRes(R.string.btn_encrypt)).performClick()

        // The alias box is premium-gated; without premium it must not appear, even though
        // the link itself is still the short snote.fun form.
        composeTestRule.onNodeWithText(stringRes(R.string.label_your_alias)).assertDoesNotExist()
        composeTestRule.onNodeWithText(fakeShortLink).assertExists()
    }
}
