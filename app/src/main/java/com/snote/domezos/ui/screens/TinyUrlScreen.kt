package com.snote.domezos.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.snote.domezos.R
import com.snote.domezos.ui.components.AppTopBar
import com.snote.domezos.ui.theme.*
import kotlin.random.Random

private const val TINYURL_WEB = "https://snote.fun/tinyURL.html"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TinyUrlScreen(onBack: () -> Unit, onNavigate: (String) -> Unit = {}, onThemeChanged: (String) -> Unit = {}, currentThemeId: String = "classic") {
    val context = LocalContext.current
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.primaryContainer
        )
    )

    Scaffold(
        topBar = { AppTopBar(currentRoute = "tinyurl", onNavigate = onNavigate, onBack = onBack, onThemeChanged = onThemeChanged, currentThemeId = currentThemeId) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(gradientBrush).padding(padding)) {
            ParticleBackground()
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Icon(imageVector = Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(60.dp))
                Text(text = stringResource(R.string.tinyurl_title), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.ExtraBold)
                Text(text = stringResource(R.string.tinyurl_subtitle), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = stringResource(R.string.tinyurl_desc), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = stringResource(R.string.tinyurl_premium_title), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                        Text(text = stringResource(R.string.tinyurl_premium_features), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, lineHeight = 24.sp)
                    }
                }
                Button(
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(TINYURL_WEB))) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Launch, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.tinyurl_btn_web), fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = TINYURL_WEB, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ParticleBackground() {
    var isVisible by remember { mutableStateOf(true) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isVisible = event == Lifecycle.Event.ON_RESUME
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (!isVisible) return

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val particles = remember {
        List(8) {
            ParticleData(x = Random.nextFloat(), y = Random.nextFloat(), size = Random.nextFloat() * 4f + 2f, speed = Random.nextFloat() * 0.05f + 0.01f)
        }
    }
    val animValues = particles.map { particle ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = (5000 / particle.speed).toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "particle_anim"
        )
    }
    val particleColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        particles.forEachIndexed { index, particle ->
            val currentY = (particle.y + animValues[index].value) % 1f
            drawCircle(color = particleColor, radius = particle.size.dp.toPx(), center = Offset(particle.x * width, currentY * height))
        }
    }
}

data class ParticleData(val x: Float, val y: Float, val size: Float, val speed: Float)
