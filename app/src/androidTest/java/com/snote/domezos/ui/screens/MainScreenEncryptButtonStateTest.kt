package com.snote.domezos.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.snote.domezos.R
import com.snote.domezos.ui.theme.ClassicTheme
import com.snote.domezos.ui.theme.DwSecretNotesTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenEncryptButtonStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun stringRes(id: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private fun setUpMainScreen() {
        composeTestRule.setContent {
            DwSecretNotesTheme(themeConfig = ClassicTheme) {
                MainScreen(onNavigate = {})
            }
        }
    }

    @Test
    fun encryptButtonIsDisabledWhenInputIsEmptyAndNoImageSelected() {
        setUpMainScreen()

        composeTestRule.onNodeWithText(stringRes(R.string.btn_encrypt)).assertIsNotEnabled()
    }

    @Test
    fun encryptButtonBecomesEnabledOnceTextIsEntered() {
        setUpMainScreen()

        composeTestRule.onNodeWithText(stringRes(R.string.hint_enter_text)).performTextInput("hello world")

        composeTestRule.onNodeWithText(stringRes(R.string.btn_encrypt)).assertIsEnabled()
    }

    @Test
    fun encryptButtonBecomesDisabledAgainAfterClearingText() {
        setUpMainScreen()

        composeTestRule.onNodeWithText(stringRes(R.string.hint_enter_text)).performTextInput("hello world")
        composeTestRule.onNodeWithText(stringRes(R.string.btn_encrypt)).assertIsEnabled()

        composeTestRule.onNode(
            androidx.compose.ui.test.hasSetTextAction()
        ).performTextClearance()

        composeTestRule.onNodeWithText(stringRes(R.string.btn_encrypt)).assertIsNotEnabled()
    }

    @Test
    fun blankWhitespaceOnlyInputKeepsTheEncryptButtonDisabled() {
        setUpMainScreen()

        composeTestRule.onNodeWithText(stringRes(R.string.hint_enter_text)).performTextInput("   ")

        composeTestRule.onNodeWithText(stringRes(R.string.btn_encrypt)).assertIsNotEnabled()
    }
}
