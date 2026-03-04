package com.example.langmap.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("langmap_prefs", Context.MODE_PRIVATE)
    private val auth = FirebaseAuth.getInstance()

    var userLevel by mutableStateOf("B1 - Intermediate")
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

    init {
        fetchData()
    }

    fun fetchData() {
        val selectedLevel = prefs.getString("selectedProficiency", "") ?: ""
        userLevel = if (selectedLevel.isEmpty()) "Yangi o'rganuvchi" else selectedLevel

        val learningGoal = prefs.getString("selectedGoal", "") ?: ""
        val studyTime = prefs.getString("selectedDuration", "") ?: ""

        val goals = mutableListOf<String>()
        if (learningGoal.isNotEmpty()) goals.add(learningGoal)
        if (studyTime.isNotEmpty()) goals.add("$studyTime vaqt davomida o'rganish")
        goals.add("Kunlik 10 ta yangi so'z o'rganish")
        goals.add("Speaking mashqlarini bajarish")
        learningGoals = goals

        completedLessons = prefs.getInt("completedLessons", 0)
        streak = prefs.getInt("streak", 0)
        achievementsCount = prefs.getInt("achievements", 0)
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
