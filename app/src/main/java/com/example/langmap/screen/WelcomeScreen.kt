package com.example.langmap.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.langmap.ui.theme.Blue

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onGetStartedClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Blue.copy(alpha = 0.2f),
                        Blue.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // User testimonial
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.8f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            "Jessie Malmon, 24 🇺🇸",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            "I feel more comfortable with\nLearna. Highly recommend!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App rating
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "4.5",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    "Appstore Rating",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFCC00),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Download count
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.8f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(20.dp))
                    Text("+2M", style = MaterialTheme.typography.titleSmall)
                    Text("Download on Appstore", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Welcome text
            Text(
                "Welcome to Learna!",
                style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Boost your language skills with Learna\neffortlessly anytime, anywhere.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons
            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Log in",
                    color = Blue,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                Text("Let's Go", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms text
            Text(
                buildAnnotatedString {
                    append("By continuing, you agree to our ")
                    withStyle(SpanStyle(color = Blue)) { append("Privacy Policy") }
                    append(" and ")
                    withStyle(SpanStyle(color = Blue)) { append("Terms of Service") }
                },
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
