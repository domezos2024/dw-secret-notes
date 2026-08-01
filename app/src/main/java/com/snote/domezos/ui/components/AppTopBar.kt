package com.snote.domezos.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.snote.domezos.R
import com.snote.domezos.navigation.Screen
import com.snote.domezos.ui.theme.ALL_THEMES

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    onThemeChanged: (String) -> Unit = {},
    currentThemeId: String = "classic"
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showThemeSheet by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(painter = painterResource(id = R.drawable.app_icon), contentDescription = null, modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)))
                Text(text = stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            }
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onBack()
                }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cd_back), tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        },
        actions = {
            IconButton(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                menuExpanded = true
            }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu_more), tint = MaterialTheme.colorScheme.onSurface)
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.theme_selection_title), color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.secondary) },
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        menuExpanded = false
                        showThemeSheet = true
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.nav_help), color = MaterialTheme.colorScheme.onSurface) },
                    leadingIcon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
                    onClick = { menuExpanded = false; if (currentRoute != Screen.Help.route) onNavigate(Screen.Help.route) }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.nav_premium), color = MaterialTheme.colorScheme.onSurface) },
                    leadingIcon = { Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.secondary) },
                    onClick = { menuExpanded = false; if (currentRoute != Screen.Premium.route) onNavigate(Screen.Premium.route) }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.nav_language), color = MaterialTheme.colorScheme.onSurface) },
                    leadingIcon = { Icon(Icons.Default.Translate, null, tint = MaterialTheme.colorScheme.primary) },
                    onClick = { menuExpanded = false; if (currentRoute != Screen.Language.route) onNavigate(Screen.Language.route) }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.nav_tinyurl), color = MaterialTheme.colorScheme.onSurface) },
                    leadingIcon = { Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary) },
                    onClick = { menuExpanded = false; if (currentRoute != Screen.TinyUrl.route) onNavigate(Screen.TinyUrl.route) }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.nav_info), color = MaterialTheme.colorScheme.onSurface) },
                    leadingIcon = { Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary) },
                    onClick = { menuExpanded = false; if (currentRoute != Screen.Info.route) onNavigate(Screen.Info.route) }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.nav_tip), color = MaterialTheme.colorScheme.onSurface) },
                    leadingIcon = { Icon(Icons.Default.Coffee, null, tint = MaterialTheme.colorScheme.secondary) },
                    onClick = { menuExpanded = false; if (currentRoute != Screen.Tip.route) onNavigate(Screen.Tip.route) }
                )
            }
            if (showThemeSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showThemeSheet = false },
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
                        Text(
                            text = stringResource(R.string.theme_selection_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.height(420.dp)
                        ) {
                            items(ALL_THEMES) { theme ->
                                val isSelected = theme.id == currentThemeId
                                Card(
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showThemeSheet = false
                                        onThemeChanged(theme.id)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    border = BorderStroke(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth().height(36.dp)) {
                                            Box(modifier = Modifier.weight(2f).height(36.dp).background(theme.colorScheme.background))
                                            Box(modifier = Modifier.weight(1f).height(36.dp).background(theme.colorScheme.primary))
                                            Box(modifier = Modifier.weight(1f).height(36.dp).background(theme.colorScheme.secondary))
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = stringResource(theme.nameRes),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}
