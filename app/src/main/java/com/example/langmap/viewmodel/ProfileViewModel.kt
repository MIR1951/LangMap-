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

                    // Real statistika — stats + achievements + dailyTasks
                    loadRealStats(userId)
                }
            }
            .addOnFailureListener {
                loadFromPrefs()
            }
    }

    private fun loadRealStats(userId: String) {
        // 1. Stats/summary dan olish
        db.collection("users").document(userId)
            .collection("stats").document("summary").get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    learnedWords = (doc.getLong("learnedWords") ?: 0).toInt()
                    completedLessons = (doc.getLong("completedLessons") ?: 0).toInt()
                    streak = (doc.getLong("streak") ?: 0).toInt()
                }
            }

        // 2. Haqiqiy achievements count
        db.collection("users").document(userId)
            .collection("achievements").get()
            .addOnSuccessListener { documents ->
                achievementsCount = documents.count { doc ->
                    doc.getBoolean("isUnlocked") == true
                }
            }

        // 3. Jami bajarilgan vazifalar (barcha kunlardan)
        db.collection("users").document(userId)
            .collection("dailyTasks").get()
            .addOnSuccessListener { documents ->
                var totalCompleted = 0
                var totalWords = 0
                var daysWithTasks = 0

                documents.forEach { doc ->
                    @Suppress("UNCHECKED_CAST")
                    val tasksData = doc.get("tasks") as? List<Map<String, Any>>
                    if (tasksData != null) {
                        val dayCompleted = tasksData.count { it["isCompleted"] == true }
                        totalCompleted += dayCompleted
                        totalWords += dayCompleted * 5
                        if (dayCompleted > 0) daysWithTasks++
                    }
                }

                completedLessons = totalCompleted
                learnedWords = totalWords
                streak = daysWithTasks

                // Stats/summary'ni yangilash
                db.collection("users").document(userId)
                    .collection("stats").document("summary")
                    .set(mapOf(
                        "completedLessons" to totalCompleted,
                        "learnedWords" to totalWords,
                        "streak" to daysWithTasks,
                        "achievementsCount" to achievementsCount,
                        "lastUpdated" to com.google.firebase.Timestamp.now()
                    ))
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
            // ViewModel holatini tozalash
            userName = ""
            email = ""
            userLevel = "Yangi o'rganuvchi"
            learningGoals = emptyList()
            interests = emptyList()
            learningMethod = ""
            learnedWords = 0
            completedLessons = 0
            streak = 0
            achievementsCount = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun clearPrefs() {
        // Barcha SharedPreferences'ni tozalash
        prefs.edit().clear().apply()
    }
}
