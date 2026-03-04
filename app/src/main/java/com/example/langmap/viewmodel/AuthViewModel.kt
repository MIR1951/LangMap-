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
                if (task.isSuccessful) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        // Firestore'dan ma'lumotlarni o'qib, SharedPreferences'ga saqlash
                        syncFirestoreToPrefs(currentUser.uid) {
                            isLoading = false
                            isAuthenticated = true
                            onSuccess()
                        }
                    } else {
                        isLoading = false
                        errorMessage = "Login failed"
                    }
                } else {
                    isLoading = false
                    errorMessage = task.exception?.message ?: "Login failed"
                }
            }
    }

    /**
     * Login qilganda Firestore'dan foydalanuvchi ma'lumotlarini o'qib,
     * SharedPreferences'ga saqlaydi. Bu ProfileScreen va boshqa joylar
     * to'g'ri ishlashi uchun kerak.
     */
    private fun syncFirestoreToPrefs(userId: String, onComplete: () -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data ?: run {
                        onComplete()
                        return@addOnSuccessListener
                    }

                    val editor = prefs.edit()
                    editor.putString("userName", data["userName"] as? String ?: "")
                    editor.putString("selectedAge", data["age"] as? String ?: "")
                    editor.putString("selectedLanguage", data["language"] as? String ?: "")
                    editor.putString("selectedProficiency", data["proficiency"] as? String ?: "")
                    editor.putString("selectedGoal", data["goal"] as? String ?: "")
                    editor.putString("selectedGoalLevel", data["goalLevel"] as? String ?: "")
                    editor.putString("selectedLastLanguageTime", data["lastLanguageTime"] as? String ?: "")
                    editor.putString("selectedLearningMethod", data["learningMethod"] as? String ?: "")
                    editor.putString("selectedExperience", data["experience"] as? String ?: "")
                    editor.putString("selectedUnderstandingLevel", data["understandingLevel"] as? String ?: "")
                    editor.putString("selectedSkill", data["skill"] as? String ?: "")
                    editor.putString("selectedDuration", data["duration"] as? String)
                    editor.putString("selectedStartTime", data["startTime"] as? String)

                    // Speaking answers
                    editor.putString("selectedAnswer12", data["answer12"] as? String)
                    editor.putString("selectedAnswer13", data["answer13"] as? String)
                    editor.putString("selectedAnswer14", data["answer14"] as? String)
                    editor.putString("selectedAnswer15", data["answer15"] as? String)
                    editor.putString("selectedAnswer16", data["answer16"] as? String)
                    editor.putString("selectedAnswer17", data["answer17"] as? String)

                    // Sets
                    @Suppress("UNCHECKED_CAST")
                    val interests = (data["interests"] as? List<String>)?.toSet()
                    if (interests != null) editor.putStringSet("selectedInterests", interests)

                    @Suppress("UNCHECKED_CAST")
                    val events = (data["events"] as? List<String>)?.toSet()
                    if (events != null) editor.putStringSet("selectedEvents", events)

                    editor.putBoolean("didCompleteOnboarding", true)
                    editor.apply()
                }
                onComplete()
            }
            .addOnFailureListener {
                // Sync muvaffaqiyatsiz bo'lsa ham login'ni davom ettirish
                onComplete()
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
                            // Yangi qo'shilgan maydonlar
                            "interests" to onboardingViewModel.selectedInterests.toList(),
                            "events" to onboardingViewModel.selectedEvents.toList(),
                            "duration" to (onboardingViewModel.selectedDuration ?: ""),
                            "startTime" to (onboardingViewModel.selectedStartTime ?: ""),
                            "answer12" to (onboardingViewModel.selectedAnswer12 ?: ""),
                            "answer13" to (onboardingViewModel.selectedAnswer13 ?: ""),
                            "answer14" to (onboardingViewModel.selectedAnswer14 ?: ""),
                            "answer15" to (onboardingViewModel.selectedAnswer15 ?: ""),
                            "answer16" to (onboardingViewModel.selectedAnswer16 ?: ""),
                            "answer17" to (onboardingViewModel.selectedAnswer17 ?: ""),
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
