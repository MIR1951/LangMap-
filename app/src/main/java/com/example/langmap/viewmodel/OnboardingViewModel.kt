package com.example.langmap.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.langmap.model.AgeOption

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("langmap_prefs", Context.MODE_PRIVATE)

    var pageIndex by mutableIntStateOf(0)
        private set

    var selectedAge by mutableStateOf<AgeOption?>(null)
        private set

    var selectedLanguage by mutableStateOf("")
        private set

    var selectedProficiency by mutableStateOf("")
        private set

    var selectedGoal by mutableStateOf("")
        private set

    var selectedGoalLevel by mutableStateOf("")
        private set

    var selectedLastLanguageTime by mutableStateOf("")
        private set

    var selectedLearningMethod by mutableStateOf("")
        private set

    var selectedExperience by mutableStateOf("")
        private set

    var selectedUnderstandingLevel by mutableStateOf("")
        private set

    var selectedSkill by mutableStateOf("")
        private set

    var userName by mutableStateOf("")
        private set

    var selectedAnswer12 by mutableStateOf<String?>(null)
        private set

    var selectedAnswer13 by mutableStateOf<String?>(null)
        private set

    var selectedAnswer14 by mutableStateOf<String?>(null)
        private set

    var selectedAnswer15 by mutableStateOf<String?>(null)
        private set

    var selectedAnswer16 by mutableStateOf<String?>(null)
        private set

    var selectedAnswer17 by mutableStateOf<String?>(null)
        private set

    var selectedInterests by mutableStateOf(setOf<String>())
        private set

    var selectedEvents by mutableStateOf(setOf<String>())
        private set

    var selectedDuration by mutableStateOf<String?>(null)
        private set

    var selectedStartTime by mutableStateOf<String?>(null)
        private set

    var showAuthView by mutableStateOf(false)
        private set

    val totalPages = 30

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        prefs.getString("selectedAge", null)?.let {
            selectedAge = AgeOption(label = it, imageName = "")
        }
        selectedLanguage = prefs.getString("selectedLanguage", "") ?: ""
        selectedProficiency = prefs.getString("selectedProficiency", "") ?: ""
        selectedGoal = prefs.getString("selectedGoal", "") ?: ""
        selectedGoalLevel = prefs.getString("selectedGoalLevel", "") ?: ""
        selectedLastLanguageTime = prefs.getString("selectedLastLanguageTime", "") ?: ""
        selectedLearningMethod = prefs.getString("selectedLearningMethod", "") ?: ""
        selectedExperience = prefs.getString("selectedExperience", "") ?: ""
        selectedUnderstandingLevel = prefs.getString("selectedUnderstandingLevel", "") ?: ""
        selectedSkill = prefs.getString("selectedSkill", "") ?: ""
        userName = prefs.getString("userName", "") ?: ""
        selectedAnswer12 = prefs.getString("selectedAnswer12", null)
        selectedAnswer13 = prefs.getString("selectedAnswer13", null)
        selectedAnswer14 = prefs.getString("selectedAnswer14", null)
        selectedAnswer15 = prefs.getString("selectedAnswer15", null)
        selectedAnswer16 = prefs.getString("selectedAnswer16", null)
        selectedAnswer17 = prefs.getString("selectedAnswer17", null)
        selectedDuration = prefs.getString("selectedDuration", null)
        selectedStartTime = prefs.getString("selectedStartTime", null)
        prefs.getStringSet("selectedInterests", null)?.let { selectedInterests = it }
        prefs.getStringSet("selectedEvents", null)?.let { selectedEvents = it }
    }

    private fun saveToPrefs(key: String, value: String?) {
        prefs.edit().putString(key, value).apply()
    }

    private fun saveSetToPrefs(key: String, value: Set<String>) {
        prefs.edit().putStringSet(key, value).apply()
    }

    fun updateSelectedAge(age: AgeOption) {
        selectedAge = age
        saveToPrefs("selectedAge", age.label)
    }

    fun updateSelectedLanguage(language: String) {
        selectedLanguage = language
        saveToPrefs("selectedLanguage", language)
    }

    fun updateSelectedProficiency(proficiency: String) {
        selectedProficiency = proficiency
        saveToPrefs("selectedProficiency", proficiency)
    }

    fun updateSelectedGoal(goal: String) {
        selectedGoal = goal
        saveToPrefs("selectedGoal", goal)
    }

    fun updateSelectedGoalLevel(level: String) {
        selectedGoalLevel = level
        saveToPrefs("selectedGoalLevel", level)
    }

    fun updateSelectedLastLanguageTime(time: String) {
        selectedLastLanguageTime = time
        saveToPrefs("selectedLastLanguageTime", time)
    }

    fun updateSelectedLearningMethod(method: String) {
        selectedLearningMethod = method
        saveToPrefs("selectedLearningMethod", method)
    }

    fun updateSelectedExperience(exp: String) {
        selectedExperience = exp
        saveToPrefs("selectedExperience", exp)
    }

    fun updateSelectedUnderstandingLevel(level: String) {
        selectedUnderstandingLevel = level
        saveToPrefs("selectedUnderstandingLevel", level)
    }

    fun updateSelectedSkill(skill: String) {
        selectedSkill = skill
        saveToPrefs("selectedSkill", skill)
    }

    fun updateUserName(name: String) {
        userName = name
        saveToPrefs("userName", name)
    }

    fun updateSelectedAnswer12(answer: String) {
        selectedAnswer12 = answer
        saveToPrefs("selectedAnswer12", answer)
    }

    fun updateSelectedAnswer13(answer: String) {
        selectedAnswer13 = answer
        saveToPrefs("selectedAnswer13", answer)
    }

    fun updateSelectedAnswer14(answer: String) {
        selectedAnswer14 = answer
        saveToPrefs("selectedAnswer14", answer)
    }

    fun updateSelectedAnswer15(answer: String) {
        selectedAnswer15 = answer
        saveToPrefs("selectedAnswer15", answer)
    }

    fun updateSelectedAnswer16(answer: String) {
        selectedAnswer16 = answer
        saveToPrefs("selectedAnswer16", answer)
    }

    fun updateSelectedAnswer17(answer: String) {
        selectedAnswer17 = answer
        saveToPrefs("selectedAnswer17", answer)
    }

    fun updateSelectedInterests(interests: Set<String>) {
        selectedInterests = interests
        saveSetToPrefs("selectedInterests", interests)
    }

    fun updateSelectedEvents(events: Set<String>) {
        selectedEvents = events
        saveSetToPrefs("selectedEvents", events)
    }

    fun updateSelectedDuration(duration: String) {
        selectedDuration = duration
        saveToPrefs("selectedDuration", duration)
    }

    fun updateSelectedStartTime(time: String) {
        selectedStartTime = time
        saveToPrefs("selectedStartTime", time)
    }

    fun navigateToPage(index: Int) {
        pageIndex = index
        prefs.edit().putInt("pageIndex", index).apply()
    }

    fun onNext() {
        if (pageIndex < totalPages - 1) {
            pageIndex += 1
        }
    }

    fun onBack() {
        if (pageIndex > 0) {
            pageIndex -= 1
        }
    }

    fun finishOnboarding() {
        prefs.edit().putBoolean("didCompleteOnboarding", true).apply()
        showAuthView = true
    }
}
