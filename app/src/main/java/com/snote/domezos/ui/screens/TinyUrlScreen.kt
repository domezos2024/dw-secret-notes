package com.snote.domezos.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snote.domezos.R
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

    // Site colors based on the CSS
    val navy900 = Color(0xFF050d1f)
    val navy800 = Color(0xFF091428)
    val cyan = Color(0xFF00d4ff)
    val gold = Color(0xFFf0c040)
    val white = Color(0xFFf0f4ff)
    val muted = Color(0xFF8899bb)

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
        containerColor = navy900 // Use the site's dark background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Header Section
            Text(
                text = "snote.fun",
                color = white,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "Shrink Your Links Instantly",
                color = cyan,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Paste any long URL and get a clean, shareable short link — hosted on snote.fun and active for 4 days.",
                color = muted,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = {
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
                Text("Open snote.fun/tinyURL", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(vertical = 8.dp))
            }

            // Info Cards (Tiers)
            TierCard(
                title = "Without Account",
                badge = "Free · No Account",
                icon = "👤",
                borderColor = Color(0x4000d4ff), // rgba(0,212,255,.25)
                badgeBg = Color(0x1F00d4ff), // rgba(0,212,255,.12)
                badgeColor = cyan,
                items = listOf(
                    "Shorten links to snote.fun/XXXXX",
                    "Link expires after 4 days",
                    "No registration required"
                ),
                navy800 = navy800,
                muted = muted,
                cyan = cyan
            )

            TierCard(
                title = "Registered User",
                badge = "Free · With Account",
                icon = "🔓",
                borderColor = Color(0x8000d4ff), // rgba(0,212,255,.5)
                badgeBg = Color(0x2E00d4ff), // rgba(0,212,255,.18)
                badgeColor = cyan,
                items = listOf(
                    "Links expire after 20 days",
                    "QR code for every link",
                    "Basic link statistics",
                    "Overview of all created links"
                ),
                navy800 = navy800,
                muted = muted,
                cyan = cyan
            )

            TierCard(
                title = "Premium",
                badge = "★ Premium",
                icon = "⭐",
                borderColor = Color(0x73f0c040), // rgba(240,192,64,.45)
                badgeBg = Color(0x2Ef0c040), // rgba(240,192,64,.18)
                badgeColor = gold,
                titleColor = gold,
                items = listOf(
                    "Everything from Registered",
                    "Custom expiry (days or unlimited)",
                    "Password-protected links",
                    "Advanced stats (click count & more)",
                    "Custom alias in the link",
                    "Click limit until auto-deactivation"
                ),
                navy800 = navy800,
                muted = muted,
                cyan = gold
            )
            
             // How it works
             Spacer(modifier = Modifier.height(24.dp))
             Text(
                text = "How It Works",
                color = cyan,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp).align(Alignment.Start)
            )
            
            HowItWorksStep("1", "Paste your long URL into the field.")
            HowItWorksStep("2", "Click TinyURL ↗ — your link is ready in seconds.")
            HowItWorksStep("3", "Copy, share, or open your short link.")
            
             Spacer(modifier = Modifier.height(24.dp))
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
    navy800: Color,
    muted: Color,
    cyan: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .background(navy800, RoundedCornerShape(14.dp))
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