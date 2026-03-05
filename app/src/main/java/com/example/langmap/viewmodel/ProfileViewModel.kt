package com.example.langmap.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("langmap_prefs", Context.MODE_PRIVATE)
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var userLevel by mutableStateOf("Yangi o'rganuvchi")
        private set

    var userName by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var learningGoals by mutableStateOf(listOf<String>())
        private set

    var learnedWords by mutableIntStateOf(0)
        private set

    var completedLessons by mutableIntStateOf(0)
        private set

    var streak by mutableIntStateOf(0)
        private set

    var achievementsCount by mutableIntStateOf(0)
        private set

    var interests by mutableStateOf(listOf<String>())
        private set

    var learningMethod by mutableStateOf("")
        private set

    init {
        fetchData()
    }

    fun fetchData() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            loadFromPrefs()
            return
        }

        email = auth.currentUser?.email ?: ""

        // Firestore'dan o'qish
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data ?: return@addOnSuccessListener

                    userName = data["userName"] as? String ?: ""
                    userLevel = data["proficiency"] as? String ?: "Yangi o'rganuvchi"
                    learningMethod = data["learningMethod"] as? String ?: ""

                    @Suppress("UNCHECKED_CAST")
                    interests = (data["interests"] as? List<String>) ?: emptyList()

                    // Maqsadlar
                    val goal = data["goal"] as? String ?: ""
                    val duration = data["duration"] as? String ?: ""
                    val goalLevel = data["goalLevel"] as? String ?: ""

                    val goals = mutableListOf<String>()
                    if (goal.isNotEmpty()) goals.add("🎯 $goal")
                    if (goalLevel.isNotEmpty()) goals.add("📈 $goalLevel")
                    if (duration.isNotEmpty()) goals.add("⏰ Kunlik $duration")
                    if (learningMethod.isNotEmpty()) goals.add("📖 $learningMethod")
                    learningGoals = goals

                    // Statistika — subcollection'dan
                    loadStats(userId)
                }
            }
            .addOnFailureListener {
                loadFromPrefs()
            }
    }

    private fun loadStats(userId: String) {
        db.collection("users").document(userId)
            .collection("stats").document("summary").get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    learnedWords = (doc.getLong("learnedWords") ?: 0).toInt()
                    completedLessons = (doc.getLong("completedLessons") ?: 0).toInt()
                    streak = (doc.getLong("streak") ?: 0).toInt()
                    achievementsCount = (doc.getLong("achievementsCount") ?: 0).toInt()
                }
                // Yangi foydalanuvchi uchun 0 qoladi — bu to'g'ri
            }
    }

    private fun loadFromPrefs() {
        userName = prefs.getString("userName", "") ?: ""
        val selectedLevel = prefs.getString("selectedProficiency", "") ?: ""
        userLevel = if (selectedLevel.isEmpty()) "Yangi o'rganuvchi" else selectedLevel
    }

    fun logout() {
        try {
            auth.signOut()
            clearPrefs()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearPrefs() {
        val editor = prefs.edit()
        val keysToRemove = listOf(
            "selectedAge", "selectedLanguage", "selectedProficiency",
            "selectedGoal", "selectedGoalLevel", "selectedLastLanguageTime",
            "selectedLearningMethod", "selectedExperience", "selectedUnderstandingLevel",
            "selectedSkill", "userName", "selectedAnswer12", "selectedAnswer13",
            "selectedAnswer14", "selectedAnswer15", "selectedAnswer16", "selectedAnswer17",
            "selectedInterests", "selectedEvents", "selectedDuration",
            "selectedStartTime", "pageIndex", "didCompleteOnboarding"
        )
        keysToRemove.forEach { editor.remove(it) }
        editor.apply()
    }
}
