package com.snote.domezos.ui.screens

import android.content.Intent
import androidx.core.net.toUri
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.snote.domezos.R
import com.snote.domezos.data.ImageUtils
import com.snote.domezos.data.Prefs
import com.snote.domezos.navigation.Screen
import com.snote.domezos.ui.components.AppTopBar
import com.snote.domezos.ui.components.SecretWebView

private const val PASSPHRASE = "dw_secret_notes_passphrase_2026"

private val AD_RES_IDS = listOf(
    R.string.ad_1, R.string.ad_2, R.string.ad_3, R.string.ad_4, R.string.ad_5,
    R.string.ad_6, R.string.ad_7, R.string.ad_8, R.string.ad_9, R.string.ad_10
)

private val PREMIUM_RES_IDS = listOf(
    R.string.premium_active_1, R.string.premium_active_2, R.string.premium_active_3,
    R.string.premium_active_4, R.string.premium_active_5, R.string.premium_active_6,
    R.string.premium_active_7, R.string.premium_active_8, R.string.premium_active_9,
    R.string.premium_active_10
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigate: (String) -> Unit,
    initialDecryptInput: String? = null,
    onDeepLinkConsumed: () -> Unit = {},
    onThemeChanged: (String) -> Unit = {},
    currentThemeId: String = "classic",
    isPremium: Boolean = false,
    showRatePrompt: Boolean = false
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    var inputText by remember { mutableStateOf("") }
    var generatedAlias by remember { mutableStateOf("") }
    var generatedLink by remember { mutableStateOf("") }
    var encryptDone by remember { mutableStateOf(false) }
    var isEncrypting by remember { mutableStateOf(false) }
    val secretWebView = remember { SecretWebView(context) }
    var decryptInput by remember { mutableStateOf("") }
    var decryptedText by remember { mutableStateOf("") }
    var decryptError by remember { mutableStateOf("") }
    var decryptDone by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(60) }
    var isDecryptingRemote by remember { mutableStateOf(false) }
    var remotePass by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImagePreview by remember { mutableStateOf<Bitmap?>(null) }
    var decryptedImage by remember { mutableStateOf<Bitmap?>(null) }
    var showFullscreenImage by remember { mutableStateOf(false) }
    var adIndex by remember { mutableIntStateOf(0) }
    val currentAds = remember(isPremium) { if (isPremium) PREMIUM_RES_IDS else AD_RES_IDS }
    val adTitle = if (isPremium) stringResource(R.string.premium_active_title) else stringResource(R.string.ad_title)
    val adSymbol = if (isPremium) "✅" else "⭐"
    var showRateDialog by remember { mutableStateOf(showRatePrompt) }

    if (showRateDialog) {
        AlertDialog(
            onDismissRequest = {
                Prefs.setHasSeenRatePrompt(context)
                showRateDialog = false
            },
            title = { Text(stringResource(R.string.rate_dialog_title)) },
            text = { Text(stringResource(R.string.rate_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    Prefs.setHasSeenRatePrompt(context)
                    showRateDialog = false
                    launchInAppReview(context) { Prefs.setHasRatedApp(context, true) }
                }) {
                    Text(stringResource(R.string.rate_dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    Prefs.setHasSeenRatePrompt(context)
                    showRateDialog = false
                }) {
                    Text(stringResource(R.string.rate_dialog_dismiss))
                }
            }
        )
    }

    LaunchedEffect(isPremium) {
        while (true) {
            delay(25000)
            adIndex = (adIndex + 1) % currentAds.size
        }
    }

    AndroidView(factory = { secretWebView }, modifier = Modifier.size(1.dp))

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            selectedImagePreview = ImageUtils.decodeScaledBitmap(context, uri)
        }
    }

    suspend fun resolveLink(input: String): Pair<String, String>? {
        var currentUrl = input.trim()
        if (currentUrl.length == 5 && !currentUrl.contains("://") && !currentUrl.contains(".")) {
            return Pair("link:$currentUrl", "")
        }
        if (!currentUrl.startsWith("http") && (currentUrl.contains("snote.fun") || currentUrl.contains("domezos-ware.org"))) {
            currentUrl = "https://$currentUrl"
        }
        if (!currentUrl.startsWith("http")) {
            return if (currentUrl.length >= 5 && currentUrl.length <= 100) Pair(currentUrl, "") else null
        }
        val initialUri = try { android.net.Uri.parse(currentUrl) } catch (e: Exception) { null }
        val pass = initialUri?.getQueryParameter("pass") ?: if (currentUrl.contains("pass=")) currentUrl.substringAfter("pass=").substringBefore("&") else ""
        val isSnote = currentUrl.contains("snote.fun")
        var extractedAlias: String? = initialUri?.getQueryParameter("link") ?: initialUri?.getQueryParameter("com")
        var isLinkType = isSnote || currentUrl.contains("link=")
        if (extractedAlias == null) {
            if (currentUrl.contains("link=")) {
                extractedAlias = currentUrl.substringAfter("link=").substringBefore("&")
                isLinkType = true
            } else if (currentUrl.contains("com=")) {
                extractedAlias = currentUrl.substringAfter("com=").substringBefore("&")
                isLinkType = false
            } else if (isSnote && currentUrl.contains("snote.fun/")) {
                val path = initialUri?.path?.trim('/') ?: currentUrl.substringAfter("snote.fun/").substringBefore("?").substringBefore("/")
                if (path.isNotEmpty() && path.length >= 5) {
                    extractedAlias = path
                    isLinkType = true
                }
            }
        }
        if (extractedAlias != null && extractedAlias.length >= 5) {
            val finalIsLink = if (isSnote && !currentUrl.contains("com=")) true else isLinkType
            return Pair(if (finalIsLink) "link:$extractedAlias" else extractedAlias, pass)
        }
        return withContext(Dispatchers.IO) {
            try {
                var conn = (URL(currentUrl).openConnection() as HttpURLConnection)
                conn.instanceFollowRedirects = false
                conn.connectTimeout = 4000
                conn.readTimeout = 4000
                var responseCode = conn.responseCode
                var loopCount = 0
                while (responseCode in 300..308 && loopCount < 5) {
                    val location = conn.getHeaderField("Location") ?: break
                    currentUrl = if (location.startsWith("http")) location else URL(URL(currentUrl), location).toString()
                    conn = (URL(currentUrl).openConnection() as HttpURLConnection)
                    conn.instanceFollowRedirects = false
                    responseCode = conn.responseCode
                    loopCount++
                }
                val finalUri = android.net.Uri.parse(currentUrl)
                val com = finalUri.getQueryParameter("com")
                val link = finalUri.getQueryParameter("link")
                val rawAlias = com ?: link ?: finalUri.lastPathSegment ?: ""
                val resolvedPass = finalUri.getQueryParameter("pass") ?: ""
                if (rawAlias.isNotEmpty() && rawAlias.length >= 5) {
                    val isLink = com == null && (link != null || currentUrl.contains("snote.fun"))
                    Pair(if (isLink) "link:$rawAlias" else rawAlias, resolvedPass)
                } else null
            } catch (e: Exception) {
                val uri = android.net.Uri.parse(currentUrl)
                val com = uri.getQueryParameter("com")
                val link = uri.getQueryParameter("link")
                val rawAlias = com ?: link ?: uri.lastPathSegment ?: ""
                val resolvedPass = uri.getQueryParameter("pass") ?: ""
                if (rawAlias.isNotEmpty()) {
                    val isLink = com == null && (link != null || currentUrl.contains("snote.fun"))
                    Pair(if (isLink) "link:$rawAlias" else rawAlias, resolvedPass)
                } else null
            }
        }
    }

    fun doEncrypt() {
        val text = inputText.trim()
        if (text.isEmpty() && selectedImageUri == null) return
        
        // If text is empty but an image is present, we send a single space to the backend 
        // to ensure it doesn't fail on an empty "write" parameter.
        val finalParamsText = if (text.isEmpty() && selectedImageUri != null) " " else text
        
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        isEncrypting = true
        val imageBase64 = selectedImageUri?.let { ImageUtils.prepareForUpload(context, it) }
        secretWebView.encrypt(finalParamsText, imageBase64) { result ->
            isEncrypting = false
            if (result.startsWith("http")) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                generatedLink = result
                generatedAlias = if (result.contains("com=")) {
                    result.substringAfter("com=").substringBefore("&").take(12) + "..."
                } else if (result.contains("link=")) {
                    result.substringAfter("link=").take(8)
                } else "Link Ready"
                if (result.contains("snote.fun") && result.contains("link=")) {
                    val alias = result.substringAfter("link=").substringBefore("&").take(5)
                    clipboard.setText(AnnotatedString(alias))
                }
                encryptDone = true
                selectedImageUri = null
                selectedImagePreview = null
            } else {
                scope.launch { snackbarHostState.showSnackbar(result) }
            }
        }
    }

    fun doDecrypt() {
        val input = decryptInput.trim()
        if (input.isEmpty()) return
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        isDecryptingRemote = true
        decryptedImage = null
        scope.launch {
            val resolved = resolveLink(input)
            if (resolved == null) {
                decryptError = "not_found"
                isDecryptingRemote = false
                return@launch
            }
            val (alias, pass) = resolved
            val finalPass = if (pass.isNotEmpty()) pass else remotePass.ifEmpty { PASSPHRASE }
            secretWebView.decrypt(
                alias, finalPass,
                callback = { result ->
                    isDecryptingRemote = false
                    if (result.startsWith("ERROR:") || result == "not_found" || result.contains("Nachricht nicht gefunden")) {
                        decryptError = "not_found"
                    } else {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        // If it's just the dummy space we sent for image-only encryption, keep text empty
                        decryptedText = if (result == " ") "" else result
                        decryptError = ""
                        decryptDone = true
                    }
                },
                onImage = { base64 ->
                    val bmp = ImageUtils.bitmapFromBase64(base64)
                    if (bmp != null) {
                        decryptedImage = bmp
                        decryptDone = true
                        decryptError = ""
                        isDecryptingRemote = false
                    } else {
                        Log.e("MainScreen", "Failed to create bitmap from base64")
                    }
                }
            )
        }
    }

    LaunchedEffect(initialDecryptInput) {
        val value = initialDecryptInput?.trim().orEmpty()
        if (value.isNotEmpty()) {
            if (value.contains("|")) {
                decryptInput = value.substringBefore("|")
                remotePass = value.substringAfter("|")
            } else {
                decryptInput = value
                remotePass = ""
            }
            decryptDone = false
            decryptError = ""
            delay(100)
            if (decryptInput.length >= 5 || decryptInput.startsWith("http")) doDecrypt()
            onDeepLinkConsumed()
        }
    }

    LaunchedEffect(decryptDone) {
        if (decryptDone) {
            countdown = 60
            while (countdown > 0) {
                delay(1000)
                countdown--
            }
            decryptedText = ""
            decryptedImage = null
            decryptInput = ""
            decryptDone = false
        }
    }

    fun shareLink(link: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message, link))
        }
        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.share_title))
        runCatching { context.startActivity(chooser) }
            .onFailure { scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.error_share_unavailable)) } }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { AppTopBar(currentRoute = Screen.Main.route, onNavigate = onNavigate, onThemeChanged = onThemeChanged, currentThemeId = currentThemeId) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AnimatedContent(
                targetState = encryptDone,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) + slideInVertically(animationSpec = tween(400), initialOffsetY = { -50 }))
                        .togetherWith(fadeOut(animationSpec = tween(200)) + slideOutVertically(animationSpec = tween(200), targetOffsetY = { 50 }))
                },
                label = "EncryptState"
            ) { isDone ->
                if (!isDone) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                        Text(
                            text = if (selectedImageUri != null) stringResource(R.string.hint_enter_text_optional) else stringResource(R.string.hint_enter_text),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                            colors = dwTextFieldColors(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 1,
                            maxLines = 5,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                modifier = if (selectedImagePreview != null) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.AttachFile, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.size(8.dp))
                                Text(stringResource(R.string.btn_attach_image))
                            }

                            AnimatedVisibility(visible = selectedImagePreview != null) {
                                selectedImagePreview?.let { bmp ->
                                    Box {
                                        Image(
                                            bitmap = bmp.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                                        )
                                        IconButton(
                                            onClick = { selectedImageUri = null; selectedImagePreview = null },
                                            modifier = Modifier.size(20.dp).align(Alignment.TopEnd)
                                        ) { Text("✕", color = MaterialTheme.colorScheme.error) }
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = { doEncrypt() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isEncrypting && (inputText.isNotBlank() || selectedImageUri != null),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(if (isEncrypting) Icons.Default.LockOpen else Icons.Default.Lock, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = if (isEncrypting) "Encrypting..." else stringResource(R.string.btn_encrypt),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        SuccessBanner(stringResource(R.string.success_encrypted))
                        GlassCard {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                if (isPremium) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer).padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = stringResource(R.string.label_your_alias), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(text = generatedAlias, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    }
                                }
                                OutlinedTextField(
                                    value = generatedLink,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text(stringResource(R.string.label_encrypted_link), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    colors = dwTextFieldColors(),
                                    shape = RoundedCornerShape(10.dp),
                                    singleLine = true
                                )
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            clipboard.setText(AnnotatedString(generatedLink))
                                            scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.snackbar_link_copied)) }
                                            generatedLink = ""
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.size(8.dp))
                                        Text(stringResource(R.string.btn_copy), fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            shareLink(generatedLink); generatedLink = ""
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Share, null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.size(8.dp))
                                        Text(stringResource(R.string.btn_share), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                inputText = ""; encryptDone = false; selectedImageUri = null; selectedImagePreview = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(stringResource(R.string.btn_new_message), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

            AnimatedContent(
                targetState = decryptDone,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) + slideInVertically(animationSpec = tween(400), initialOffsetY = { 50 }))
                        .togetherWith(fadeOut(animationSpec = tween(200)) + slideOutVertically(animationSpec = tween(200), targetOffsetY = { -50 }))
                },
                label = "DecryptState"
            ) { isDone ->
                if (!isDone) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(stringResource(R.string.decrypt_hint_body), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = decryptInput,
                            onValueChange = { input ->
                                decryptInput = input
                                decryptError = ""
                                if (input.isEmpty()) { decryptDone = false; decryptedText = ""; decryptedImage = null }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.hint_decrypt_link), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            placeholder = { Text(stringResource(R.string.hint_decrypt_link), color = MaterialTheme.colorScheme.outline) },
                            colors = dwTextFieldColors(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 1,
                            maxLines = 5,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                        )
                        Button(
                            onClick = { doDecrypt() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isDecryptingRemote,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(if (isDecryptingRemote) Icons.Default.Lock else Icons.Default.LockOpen, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = if (isDecryptingRemote) stringResource(R.string.label_decrypting) else stringResource(R.string.btn_decrypt),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        AnimatedVisibility(visible = decryptError.isNotEmpty()) {
                            ErrorBanner(text = stringResource(if (decryptError == "not_found") R.string.error_not_found else R.string.error_decrypt_failed))
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        SuccessBanner(stringResource(R.string.success_decrypted))
                        GlassCard(borderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(text = stringResource(R.string.label_secret_message), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                                if (decryptedText.isNotEmpty()) {
                                    Text(text = decryptedText, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                                }
                                decryptedImage?.let { bmp ->
                                    Image(
                                        bitmap = bmp.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 400.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                showFullscreenImage = true
                                            },
                                        contentScale = ContentScale.Fit
                                    )
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                                Text(text = pluralStringResource(R.plurals.note_auto_delete, countdown, countdown), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.note_destroyed), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                decryptInput = ""; decryptedText = ""; decryptedImage = null; decryptDone = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.size(8.dp))
                            Text(stringResource(R.string.btn_read_another), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            PremiumAdBox(title = adTitle, text = stringResource(currentAds[adIndex]), symbol = adSymbol, onClick = { onNavigate(Screen.Premium.route) })
            Spacer(Modifier.height(8.dp))
            FooterNote()
        }
    }

    if (showFullscreenImage && decryptedImage != null) {
        Dialog(
            onDismissRequest = { showFullscreenImage = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showFullscreenImage = false
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = decryptedImage!!.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun PremiumAdBox(title: String, text: String, symbol: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(symbol, style = MaterialTheme.typography.headlineSmall)
                Column {
                    Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun GlassCard(
    borderColor: androidx.compose.ui.graphics.Color? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor ?: MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun SuccessBanner(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.primaryContainer).padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.symbol_success), color = MaterialTheme.colorScheme.primary)
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorBanner(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.errorContainer).padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(stringResource(R.string.symbol_warning), color = MaterialTheme.colorScheme.error)
        Text(text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun FooterNote() {
    val context = LocalContext.current
    var hasRated by remember { mutableStateOf(Prefs.hasRatedApp(context)) }
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(stringResource(R.string.footer_website), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        Text(stringResource(R.string.footer_copyright), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        if (hasRated) {
            Text(stringResource(R.string.footer_rate_thanks), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Text(stringResource(R.string.footer_rate_prompt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = com.snote.domezos.ui.theme.GoldAccent,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable {
                                launchInAppReview(context) {
                                    Prefs.setHasRatedApp(context, true)
                                    hasRated = true
                                }
                            }
                    )
                }
            }
        }
    }
}

private tailrec fun android.content.Context.findActivity(): android.app.Activity? = when (this) {
    is android.app.Activity -> this
    is android.content.ContextWrapper -> baseContext.findActivity()
    else -> null
}

private fun launchInAppReview(context: android.content.Context, onDone: () -> Unit) {
    val activity = context.findActivity() ?: return
    val reviewManager = com.google.android.play.core.review.ReviewManagerFactory.create(context)
    val request = reviewManager.requestReviewFlow()
    request.addOnCompleteListener { requestTask ->
        if (requestTask.isSuccessful) {
            val flow = reviewManager.launchReviewFlow(activity, requestTask.result)
            flow.addOnCompleteListener { onDone() }
        } else {
            // In-app review is only ever shown when the app was installed from Play (and is quota-limited even then).
            // Fall back to the Play Store listing so tapping "Rate" always does something.
            openPlayStoreListing(context)
            onDone()
        }
    }
}

private fun openPlayStoreListing(context: android.content.Context) {
    val packageName = context.packageName
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, "market://details?id=$packageName".toUri())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } catch (_: android.content.ActivityNotFoundException) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=$packageName".toUri())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

@Composable
fun dwTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedLabelColor = MaterialTheme.colorScheme.primary
)
