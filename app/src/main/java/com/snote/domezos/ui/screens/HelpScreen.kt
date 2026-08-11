package com.snote.domezos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snote.domezos.R
import com.snote.domezos.ui.components.AppTopBar
import com.snote.domezos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(onNavigate: (String) -> Unit, onBack: () -> Unit, onThemeChanged: (String) -> Unit = {}, currentThemeId: String = "classic") {
    Scaffold(
        topBar = { AppTopBar(currentRoute = "help", onNavigate = onNavigate, onBack = onBack, onThemeChanged = onThemeChanged, currentThemeId = currentThemeId) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = stringResource(R.string.help_title), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            HelpSection(title = stringResource(R.string.help_how_title), body = stringResource(R.string.help_how_body))
            HelpSection(title = stringResource(R.string.help_security_title), body = stringResource(R.string.help_security_body))
            HelpSection(title = stringResource(R.string.help_faq_q1), body = stringResource(R.string.help_faq_a1))
            HelpSection(title = stringResource(R.string.help_faq_q2), body = stringResource(R.string.help_faq_a2))
            Spacer(Modifier.height(8.dp))
            FooterNote()
        }
    }
}
@Composable
private fun HelpSection(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            Text(text = body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
