package com.example.langmap.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val prefs = application.getSharedPreferences("langmap_prefs", Context.MODE_PRIVATE)

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAuthenticated by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill in all fields"
            return
        }

        isLoading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    isAuthenticated = true
                    onSuccess()
                } else {
                    errorMessage = task.exception?.message ?: "Login failed"
                }
            }
    }

    fun signUp(
        email: String,
        password: String,
        confirmPassword: String,
        onboardingViewModel: OnboardingViewModel,
        onSuccess: () -> Unit
    ) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "Iltimos, barcha maydonlarni to'ldiring"
            return
        }

        if (password != confirmPassword) {
            errorMessage = "Parollar mos kelmadi"
            return
        }

        if (password.length < 6) {
            errorMessage = "Parol kamida 6 ta belgidan iborat bo'lishi kerak"
            return
        }

        isLoading = true
        errorMessage = null

        val speakingQuestions = listOf(
            "Ingliz tilida ravon gapirganda tushunmayman.",
            "So'z boyligim kamligi sababli gapirishga qiynalaman.",
            "Gapirganda doim noto'g'ri gaplar tuzaman.",
            "Tushunaman, lekin gapira olmayman.",
            "Ingliz tilida gapira olaman, lekin ravonroq bo'lishim kerak.",
            "Xohlagan paytimda mashq qilish uchun sherigim yo'q.",
            "Ingliz tilidagi filmlarni ko'rishda qiynalaman."
        )

        val speakingAnswers = listOf(
            onboardingViewModel.selectedUnderstandingLevel,
            onboardingViewModel.selectedAnswer12 ?: "",
            onboardingViewModel.selectedAnswer13 ?: "",
            onboardingViewModel.selectedAnswer14 ?: "",
            onboardingViewModel.selectedAnswer15 ?: "",
            onboardingViewModel.selectedAnswer16 ?: "",
            onboardingViewModel.selectedAnswer17 ?: ""
        )

        val speakingDifficultiesData = speakingQuestions.zip(speakingAnswers).map { (q, a) ->
            mapOf("question" to q, "answer" to a)
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        val userData = hashMapOf<String, Any>(
                            "age" to (onboardingViewModel.selectedAge?.label ?: ""),
                            "language" to onboardingViewModel.selectedLanguage,
                            "proficiency" to onboardingViewModel.selectedProficiency,
                            "goal" to onboardingViewModel.selectedGoal,
                            "goalLevel" to onboardingViewModel.selectedGoalLevel,
                            "lastLanguageTime" to onboardingViewModel.selectedLastLanguageTime,
                            "learningMethod" to onboardingViewModel.selectedLearningMethod,
                            "experience" to onboardingViewModel.selectedExperience,
                            "understandingLevel" to onboardingViewModel.selectedUnderstandingLevel,
                            "skill" to onboardingViewModel.selectedSkill,
                            "userName" to onboardingViewModel.userName,
                            "speakingDifficulties" to speakingDifficultiesData,
                            "email" to email,
                            "createdAt" to com.google.firebase.Timestamp.now()
                        )

                        db.collection("users").document(currentUser.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                prefs.edit().putBoolean("didCompleteOnboarding", true).apply()
                                isAuthenticated = true
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                errorMessage = "Ma'lumotlarni saqlashda xatolik: ${e.message}"
                            }
                    }
                } else {
                    errorMessage = task.exception?.message ?: "Registration failed"
                }
            }
    }

    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        if (email.isBlank()) {
            onResult(false, "Please enter your email address")
            return
        }

        isLoading = true
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    onResult(true, "Password reset link has been sent to your email")
                } else {
                    onResult(false, "Error: ${task.exception?.message}")
                }
            }
    }

    fun clearError() {
        errorMessage = null
    }
}
