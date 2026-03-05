package com.example.langmap.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.langmap.model.Achievement
import com.example.langmap.model.LearningTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PlanViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("langmap_prefs", Context.MODE_PRIVATE)
    private val db = FirebaseFirestore.getInstance()

    var weeklyProgress by mutableStateOf(listOf<Double>())
        private set

    var todayTasks by mutableStateOf(listOf<LearningTask>())
        private set

    var achievements by mutableStateOf(listOf<Achievement>())
        private set

    var userLevel by mutableStateOf("")
        private set

    init {
        fetchData()
    }

    fun fetchData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            loadDefaults()
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val data = document.data ?: run {
                        loadDefaults()
                        return@addOnSuccessListener
                    }

                    val proficiency = data["proficiency"] as? String ?: "Boshlang'ich"
                    val goal = data["goal"] as? String ?: ""
                    val duration = data["duration"] as? String ?: ""
                    val skill = data["skill"] as? String ?: ""
                    userLevel = proficiency

                    // Foydalanuvchining haqiqiy progressini Firestore'dan o'qish
                    loadProgress(userId)

                    // Foydalanuvchi darajasiga mos vazifalar
                    todayTasks = generateTasksForLevel(proficiency, goal, skill)

                    // Yutuqlarni Firestore'dan o'qish
                    loadAchievements(userId)
                } else {
                    loadDefaults()
                }
            }
            .addOnFailureListener {
                loadDefaults()
            }
    }

    private fun loadProgress(userId: String) {
        db.collection("users").document(userId)
            .collection("progress").document("weekly").get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val days = doc.get("days") as? List<Double>
                    weeklyProgress = days ?: listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                } else {
                    // Yangi foydalanuvchi — progress yo'q
                    weeklyProgress = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
                }
            }
            .addOnFailureListener {
                weeklyProgress = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            }
    }

    private fun loadAchievements(userId: String) {
        db.collection("users").document(userId)
            .collection("achievements").get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    achievements = documents.map { doc ->
                        Achievement(
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            iconName = doc.getString("iconName") ?: "star",
                            isUnlocked = doc.getBoolean("isUnlocked") ?: false
                        )
                    }
                } else {
                    // Default yutuqlar
                    achievements = getDefaultAchievements()
                }
            }
            .addOnFailureListener {
                achievements = getDefaultAchievements()
            }
    }

    private fun generateTasksForLevel(level: String, goal: String, skill: String): List<LearningTask> {
        return when {
            level.contains("Boshlang'ich", ignoreCase = true) -> listOf(
                LearningTask(
                    title = "Asosiy so'zlar",
                    description = "Kundalik 15 ta yangi so'z o'rganish",
                    duration = "15 min",
                    isCompleted = false
                ),
                LearningTask(
                    title = "Grammar asoslari",
                    description = "Present Simple tuzilishi",
                    duration = "20 min",
                    isCompleted = false
                ),
                LearningTask(
                    title = "Tinglash mashqi",
                    description = "Oddiy dialog tinglash va tushunish",
                    duration = "10 min",
                    isCompleted = false
                )
            )
            level.contains("O'rta", ignoreCase = true) || level.contains("Intermediate", ignoreCase = true) -> listOf(
                LearningTask(
                    title = "So'z boyligini kengaytirish",
                    description = "Mavzuli 20 ta yangi so'z",
                    duration = "20 min",
                    isCompleted = false
                ),
                LearningTask(
                    title = "Grammar chuqurlash",
                    description = "Perfect tenses mashqlari",
                    duration = "25 min",
                    isCompleted = false
                ),
                LearningTask(
                    title = "Speaking mashqi",
                    description = "Mavzu bo'yicha gapirish mashqi",
                    duration = "15 min",
                    isCompleted = false
                ),
                LearningTask(
                    title = "O'qish mashqi",
                    description = "Qisqa maqola o'qish va savollarga javob",
                    duration = "15 min",
                    isCompleted = false
                )
            )
            else -> listOf(
                LearningTask(
                    title = "Advanced vocabulary",
                    description = "Idiomalar va phrasal verblar",
                    duration = "20 min",
                    isCompleted = false
                ),
                LearningTask(
                    title = "Academic writing",
                    description = "Essay yozish mashqi",
                    duration = "30 min",
                    isCompleted = false
                ),
                LearningTask(
                    title = "Debate practice",
                    description = "Mavzu bo'yicha fikr bildirish",
                    duration = "20 min",
                    isCompleted = false
                )
            )
        }
    }

    private fun getDefaultAchievements(): List<Achievement> {
        return listOf(
            Achievement(
                title = "Birinchi qadam",
                description = "Ilovaga ro'yxatdan o'tish",
                iconName = "star",
                isUnlocked = true
            ),
            Achievement(
                title = "So'z ustasi",
                description = "100 ta so'z o'rganish",
                iconName = "book",
                isUnlocked = false
            ),
            Achievement(
                title = "Grammar guru",
                description = "10 ta grammar darsini tugatish",
                iconName = "check_circle",
                isUnlocked = false
            )
        )
    }

    private fun loadDefaults() {
        weeklyProgress = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        todayTasks = generateTasksForLevel("Boshlang'ich", "", "")
        achievements = getDefaultAchievements()
    }
}
