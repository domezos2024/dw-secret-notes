package com.snote.domezos.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.LaunchedEffect
import com.snote.domezos.R
import com.snote.domezos.data.LocaleManager
import com.snote.domezos.data.Prefs
import com.snote.domezos.ui.components.SecretWebView
import com.snote.domezos.ui.theme.ALL_THEMES
import com.snote.domezos.ui.theme.ClassicTheme
import com.snote.domezos.ui.theme.DwSecretNotesTheme

class WidgetEncryptActivity : ComponentActivity() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.applyLocale(base))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appWidgetId = intent.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        val themeConfig = ALL_THEMES.find { it.id == Prefs.getTheme(this) } ?: ClassicTheme
        setContent {
            DwSecretNotesTheme(themeConfig = themeConfig) {
                WidgetEncryptScreen(appWidgetId = appWidgetId, onDismiss = { finish() })
            }
        }
    }
}

@Composable
private fun WidgetEncryptScreen(appWidgetId: Int, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val secretWebView = remember { SecretWebView(context) }
    var inputText by remember { mutableStateOf("") }
    var isEncrypting by remember { mutableStateOf(false) }
    var generatedLink by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    AndroidView(factory = { secretWebView }, modifier = Modifier.size(1.dp))

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun doEncrypt() {
        val text = inputText.trim()
        if (text.isEmpty()) return
        isEncrypting = true
        errorText = ""
        secretWebView.encrypt(text) { result ->
            isEncrypting = false
            if (result.startsWith("http")) {
                generatedLink = result
                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    Prefs.setWidgetLink(context, appWidgetId, result)
                    SecretWidgetProvider.updateWidget(
                        context,
                        AppWidgetManager.getInstance(context),
                        appWidgetId
                    )
                }
            } else {
                errorText = result
            }
        }
    }

    fun shareLink() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message, generatedLink))
        }
        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share_title))
        runCatching { context.startActivity(chooser) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .padding(16.dp)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (generatedLink.isEmpty()) {
                    WidgetEncryptInputContent(
                        inputText = inputText,
                        onInputTextChange = { inputText = it },
                        errorText = errorText,
                        isEncrypting = isEncrypting,
                        focusRequester = focusRequester,
                        onEncrypt = { doEncrypt() }
                    )
                } else {
                    WidgetEncryptResultContent(
                        generatedLink = generatedLink,
                        onShare = { shareLink() }
                    )
                }
            }
        }
    }
}

@Composable
private fun WidgetEncryptInputContent(
    inputText: String,
    onInputTextChange: (String) -> Unit,
    errorText: String,
    isEncrypting: Boolean,
    focusRequester: FocusRequester,
    onEncrypt: () -> Unit
) {
    OutlinedTextField(
        value = inputText,
        onValueChange = onInputTextChange,
        placeholder = { Text(stringResource(R.string.hint_enter_text)) },
        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(10.dp)
    )
    if (errorText.isNotEmpty()) {
        Text(errorText, color = MaterialTheme.colorScheme.error)
    }
    Button(
        onClick = onEncrypt,
        enabled = !isEncrypting && inputText.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = stringResource(R.string.btn_encrypt),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WidgetEncryptResultContent(generatedLink: String, onShare: () -> Unit) {
    OutlinedTextField(
        value = generatedLink,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.label_encrypted_link)) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = true
    )
    Button(
        onClick = onShare,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = stringResource(R.string.btn_share),
            fontWeight = FontWeight.Bold
        )
    }
}
