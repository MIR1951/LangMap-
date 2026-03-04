package com.example.langmap.screen.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.langmap.ui.theme.Blue
import com.example.langmap.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showForgotPassword by remember { mutableStateOf(false) }

    if (showForgotPassword) {
        ForgotPasswordDialog(
            authViewModel = authViewModel,
            onDismiss = { showForgotPassword = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Back button
        IconButton(onClick = onDismiss) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Welcome back!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Log in to continue learning with\nyour private AI Tutor.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email field
        Text("E-mail address", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        Text("Password", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot password
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { showForgotPassword = true }) {
                Text("Forgot password?", color = Blue, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // OR divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
            Text("OR", modifier = Modifier.padding(horizontal = 16.dp), color = Color.Gray)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Social login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                shadowElevation = 4.dp,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("🍎", fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Surface(
                shape = CircleShape,
                shadowElevation = 4.dp,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("G", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Error message
        authViewModel.errorMessage?.let { error ->
            Text(error, color = Color.Red, fontSize = 14.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Login button
        Button(
            onClick = {
                authViewModel.signIn(email, password, onSuccess)
            },
            enabled = !authViewModel.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Blue)
        ) {
            if (authViewModel.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Log in", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Terms
        Text(
            buildAnnotatedString {
                append("By continuing, you agree to our ")
                withStyle(SpanStyle(color = Blue)) { append("Privacy Policy") }
                append(" and ")
                withStyle(SpanStyle(color = Blue)) { append("Terms of Service") }
            },
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ForgotPasswordDialog(
    authViewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    "Enter your email address and we'll send you a link to reset your password.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                resultMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, fontSize = 14.sp, color = if (it.startsWith("Error")) Color.Red else Color.Green)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    authViewModel.sendPasswordReset(email) { success, message ->
                        resultMessage = message
                        if (success) {
                            // Will dismiss on OK
                        }
                    }
                },
                enabled = !authViewModel.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Blue)
            ) {
                if (authViewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                } else {
                    Text("Send Reset Link")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
