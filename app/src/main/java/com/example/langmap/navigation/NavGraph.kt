package com.example.langmap.navigation

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.langmap.screen.WelcomeScreen
import com.example.langmap.screen.auth.AuthScreen
import com.example.langmap.screen.auth.LoginScreen
import com.example.langmap.screen.auth.SignUpScreen
import com.example.langmap.screen.main.MainScreen
import com.example.langmap.screen.onboarding.OnboardingScreen
import com.example.langmap.viewmodel.AuthViewModel
import com.example.langmap.viewmodel.OnboardingViewModel
import com.google.firebase.auth.FirebaseAuth

object Routes {
    const val WELCOME = "welcome"
    const val ONBOARDING = "onboarding"
    const val AUTH = "auth"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val MAIN = "main"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as Application
    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(application)
    )
    val authViewModel: AuthViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(application)
    )

    val isAuthenticated = remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            isAuthenticated.value = auth.currentUser != null
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(listener)
        }
    }

    val startDestination = if (isAuthenticated.value) Routes.MAIN else Routes.WELCOME

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onLoginClick = { navController.navigate(Routes.LOGIN) },
                onGetStartedClick = { navController.navigate(Routes.ONBOARDING) }
            )
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onFinish = { navController.navigate(Routes.AUTH) { popUpTo(Routes.WELCOME) } }
            )
        }

        composable(Routes.AUTH) {
            AuthScreen(
                onEmailSignUp = { navController.navigate(Routes.SIGN_UP) },
                onLogin = { navController.navigate(Routes.LOGIN) },
                onGoogleSignIn = { /* TODO */ },
                onAppleSignIn = { /* TODO */ }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onDismiss = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SIGN_UP) {
            SignUpScreen(
                authViewModel = authViewModel,
                onboardingViewModel = onboardingViewModel,
                onDismiss = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                onLogout = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
