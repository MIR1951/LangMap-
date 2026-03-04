package com.example.langmap.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.langmap.ui.theme.Blue

@Composable
fun AuthScreen(
    onEmailSignUp: () -> Unit,
    onLogin: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onAppleSignIn: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C2E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            Text(
                "Let's finish your setup!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Create account to save your progress.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Apple Sign in
            Button(
                onClick = onAppleSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("🍎", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue with Apple", color = Color.Black, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign in
            Button(
                onClick = onGoogleSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue with Google", color = Color.Black, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email Sign in
            Button(
                onClick = onEmailSignUp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Continue with Email", color = Color.Black, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login
            TextButton(onClick = onLogin) {
                Text(
                    buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.7f))) {
                            append("Already have an account? ")
                        }
                        withStyle(SpanStyle(color = Blue)) {
                            append("Log in")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.weight(0.3f))

            // Terms
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White.copy(alpha = 0.7f))) {
                        append("By continuing, you agree to our ")
                    }
                    withStyle(SpanStyle(color = Blue)) { append("Privacy Policy") }
                    withStyle(SpanStyle(color = Color.White.copy(alpha = 0.7f))) { append(" and ") }
                    withStyle(SpanStyle(color = Blue)) { append("Terms of Service") }
                },
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
