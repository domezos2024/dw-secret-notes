package com.snote.domezos.ui.screens

import android.app.Activity
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snote.domezos.R
import com.snote.domezos.data.Prefs
import java.util.Locale

data class LanguageOption(
    val tag: String,
    val nativeName: String,
    val englishName: String
)

private val LANGUAGES = listOf(
    LanguageOption("en", "English", "English"),
    LanguageOption("de", "Deutsch", "German"),
    LanguageOption("es", "Espanol", "Spanish"),
    LanguageOption("zh-CN", "Chinese", "Chinese (Simplified)"),
    LanguageOption("hi", "Hindi", "Hindi"),
    LanguageOption("ar", "Arabic", "Arabic"),
    LanguageOption("pt", "Portugues", "Portuguese"),
    LanguageOption("bn", "Bengali", "Bengali"),
    LanguageOption("ru", "Russkiy", "Russian"),
    LanguageOption("ja", "Japanese", "Japanese"),
    LanguageOption("fr", "Francais", "French"),
    LanguageOption("ur", "Urdu", "Urdu"),
    LanguageOption("id", "Indonesia", "Indonesian"),
    LanguageOption("ko", "Korean", "Korean"),
    LanguageOption("it", "Italiano", "Italian")
)

@Composable
fun LanguageScreen(isFirstRun: Boolean, onConfirm: () -> Unit) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val saved = Prefs.getLanguage(context)
    var selected by remember { mutableStateOf(saved ?: "en") }
    val previewContext = remember(selected) {
        val locale = when (selected) {
            "zh-CN" -> Locale.SIMPLIFIED_CHINESE
            "in" -> Locale.forLanguageTag("id")
            else -> Locale.forLanguageTag(selected)
        }
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
    fun getPreviewString(resId: Int): String = previewContext.getString(resId)
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Button(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        Prefs.setLanguage(context, selected)
                        onConfirm()
                        var currentContext = context
                        while (currentContext is ContextWrapper) {
                            if (currentContext is Activity) break
                            currentContext = currentContext.baseContext
                        }
                        (currentContext as? Activity)?.recreate()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val selectedLang = LANGUAGES.find { it.tag == selected }
                    val btnText = if (isFirstRun && selectedLang != null)
                        "${getPreviewString(R.string.language_btn_confirm)} · ${selectedLang.nativeName}"
                    else
                        getPreviewString(R.string.language_btn_confirm)
                    Text(text = btnText, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = if (isFirstRun) getPreviewString(R.string.language_first_run) else getPreviewString(R.string.language_title), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = getPreviewString(R.string.language_subtitle), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(LANGUAGES) { lang ->
                    val isSelected = selected == lang.tag
                    Card(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            selected = lang.tag
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = lang.nativeName, style = MaterialTheme.typography.titleMedium, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                                Text(text = lang.englishName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (isSelected) Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
