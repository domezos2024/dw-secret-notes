package com.snote.domezos.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.snote.domezos.R
import com.snote.domezos.data.Backend
import com.snote.domezos.ui.components.AppTopBar

@Composable
fun TinyUrlScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onThemeChanged: (String) -> Unit,
    currentThemeId: String
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Site colors — matches the :root custom properties on domezos-ware.com/tinyURL.html
    val navy900 = Color(0xFF050d1f)
    val navy800 = Color(0xFF091428)
    val navy700 = Color(0xFF0d1e3a)
    val navyDeep = Color(0xFF02080f)
    val cyan = Color(0xFF00d4ff)
    val gold = Color(0xFFf0c040)
    val white = Color(0xFFf0f4ff)
    val muted = Color(0xFF8899bb)
    val glassBg = Color(0xA6091428) // rgba(9,20,40,.65)
    val glassBorder = Color(0x2E00d4ff) // rgba(0,212,255,.18)

    // Only animate the entrance while this screen is actually the foreground —
    // avoids burning frames on an unseen composable if the app is backgrounded mid-transition.
    val lifecycleOwner = LocalLifecycleOwner.current
    var isForeground by remember { mutableStateOf(true) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isForeground = when (event) {
                Lifecycle.Event.ON_RESUME -> true
                Lifecycle.Event.ON_PAUSE -> false
                else -> isForeground
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                currentRoute = "tinyUrl",
                onNavigate = onNavigate,
                onBack = onBack,
                onThemeChanged = onThemeChanged,
                currentThemeId = currentThemeId
            )
        },
        containerColor = navy900
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.linearGradient(
                        colors = listOf(navy700, navy900, navyDeep)
                    )
                )
        ) {
            // Two soft glow blobs approximating the site's radial-gradient background
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-120).dp, y = (-100).dp)
                    .size(420.dp)
                    .background(
                        Brush.radialGradient(colors = listOf(Color(0x40006AC8), Color.Transparent)),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 120.dp, y = 120.dp)
                    .size(380.dp)
                    .background(
                        Brush.radialGradient(colors = listOf(Color(0x4D003278), Color.Transparent)),
                        shape = CircleShape
                    )
            )

            AnimatedVisibility(
                visible = isForeground,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 20 },
                exit = fadeOut(tween(0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Header Section
                    Text(
                        text = Backend.HOST,
                        color = white,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = stringResource(R.string.tinyurl2_headline),
                        color = cyan,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = stringResource(R.string.tinyurl2_intro),
                        color = muted,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = {
                            // tinyURL.html only exists on snote.fun (domezos-ware.com/tinyURL.html 404s) — verified live, keep as-is.
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://snote.fun/tinyURL.html"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = cyan,
                            contentColor = navy900
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                    ) {
                        Text(stringResource(R.string.tinyurl2_open_button), fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
                    }

                    // Info Cards (Tiers) — mirrors the "Without Account / Registered / Premium" tiles on the site
                    TierCard(
                        title = stringResource(R.string.tinyurl2_tier_free_title),
                        badge = stringResource(R.string.tinyurl2_tier_free_badge),
                        icon = "👤",
                        borderColor = Color(0x4000d4ff), // rgba(0,212,255,.25)
                        badgeBg = Color(0x1F00d4ff), // rgba(0,212,255,.12)
                        badgeColor = cyan,
                        items = listOf(
                            stringResource(R.string.tinyurl2_tier_free_item1),
                            stringResource(R.string.tinyurl2_tier_free_item2),
                            stringResource(R.string.tinyurl2_tier_free_item3)
                        ),
                        cardBg = glassBg,
                        muted = muted,
                        cyan = cyan
                    )

                    TierCard(
                        title = stringResource(R.string.tinyurl2_tier_reg_title),
                        badge = stringResource(R.string.tinyurl2_tier_reg_badge),
                        icon = "🔓",
                        borderColor = Color(0x8000d4ff), // rgba(0,212,255,.5)
                        badgeBg = Color(0x2E00d4ff), // rgba(0,212,255,.18)
                        badgeColor = cyan,
                        items = listOf(
                            stringResource(R.string.tinyurl2_tier_reg_item1),
                            stringResource(R.string.tinyurl2_tier_reg_item2),
                            stringResource(R.string.tinyurl2_tier_reg_item3),
                            stringResource(R.string.tinyurl2_tier_reg_item4)
                        ),
                        cardBg = glassBg,
                        muted = muted,
                        cyan = cyan
                    )

                    TierCard(
                        title = stringResource(R.string.tinyurl2_tier_premium_title),
                        badge = stringResource(R.string.tinyurl2_tier_premium_badge),
                        icon = "⭐",
                        borderColor = Color(0x73f0c040), // rgba(240,192,64,.45)
                        badgeBg = Color(0x2Ef0c040), // rgba(240,192,64,.18)
                        badgeColor = gold,
                        titleColor = gold,
                        items = listOf(
                            stringResource(R.string.tinyurl2_tier_premium_item1),
                            stringResource(R.string.tinyurl2_tier_premium_item2),
                            stringResource(R.string.tinyurl2_tier_premium_item3),
                            stringResource(R.string.tinyurl2_tier_premium_item4),
                            stringResource(R.string.tinyurl2_tier_premium_item5),
                            stringResource(R.string.tinyurl2_tier_premium_item6)
                        ),
                        cardBg = glassBg,
                        muted = muted,
                        cyan = gold
                    )

                    // How it works — glass panel, matching .how-section on the site
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .border(1.dp, glassBorder, RoundedCornerShape(16.dp))
                            .background(glassBg, RoundedCornerShape(16.dp))
                            .padding(20.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.tinyurl2_how_it_works_title),
                            color = cyan,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp).align(Alignment.Start)
                        )

                        HowItWorksStep("1", stringResource(R.string.tinyurl2_step1))
                        HowItWorksStep("2", stringResource(R.string.tinyurl2_step2))
                        HowItWorksStep("3", stringResource(R.string.tinyurl2_step3))
                    }
                }
            }
        }
    }
}

@Composable
fun TierCard(
    title: String,
    badge: String,
    icon: String,
    borderColor: Color,
    badgeBg: Color,
    badgeColor: Color,
    titleColor: Color = Color.White,
    items: List<String>,
    cardBg: Color,
    muted: Color,
    cyan: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .background(cardBg, RoundedCornerShape(14.dp))
            .padding(20.dp)
    ) {
        Surface(
            color = badgeBg,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = badge.uppercase(),
                color = badgeColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                letterSpacing = 1.sp
            )
        }

        Text(text = icon, fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))

        Text(
            text = title,
            color = titleColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        items.forEach { item ->
            Row(modifier = Modifier.padding(bottom = 6.dp), verticalAlignment = Alignment.Top) {
                 Text(
                    text = "·",
                    color = cyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = item,
                    color = muted,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun HowItWorksStep(number: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFF00d4ff), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color(0xFF050d1f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color(0xFF8899bb),
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
