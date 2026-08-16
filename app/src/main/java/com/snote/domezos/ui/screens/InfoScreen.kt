package com.snote.domezos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.snote.domezos.BuildConfig
import com.snote.domezos.R
import com.snote.domezos.data.Backend
import com.snote.domezos.ui.components.AppTopBar
import com.snote.domezos.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(onNavigate: (String) -> Unit, onBack: () -> Unit, onThemeChanged: (String) -> Unit = {}, currentThemeId: String = "classic") {
    Scaffold(
        topBar = { AppTopBar(currentRoute = "info", onNavigate = onNavigate, onBack = onBack, onThemeChanged = onThemeChanged, currentThemeId = currentThemeId) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = stringResource(R.string.info_title), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            GlassCard {
                InfoRow(label = stringResource(R.string.info_version_label), value = BuildConfig.VERSION_NAME)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 6.dp))
                InfoRow(label = stringResource(R.string.info_developer_label), value = "Michael Bergfeld")
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 6.dp))
                InfoRow(label = stringResource(R.string.info_website_label), value = Backend.HOST)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 6.dp))
                InfoRow(label = stringResource(R.string.info_license_label), value = "MIT")
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("AES-256-CBC", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    Text(stringResource(R.string.info_crypto_note), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f))
                }
            }
            Spacer(Modifier.height(8.dp))
            FooterNote()
        }
    }
}
@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
