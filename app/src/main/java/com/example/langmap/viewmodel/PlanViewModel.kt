package com.example.langmap.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.langmap.model.Achievement
import com.example.langmap.model.LearningTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PlanViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("langmap_prefs", Context.MODE_PRIVATE)
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "LANGMAP"

    var weeklyProgress by mutableStateOf(listOf<Double>())
        private set

    var todayTasks by mutableStateOf(listOf<LearningTask>())
        private set

    var achievements by mutableStateOf(listOf<Achievement>())
        private set

    var userLevel by mutableStateOf("")
        private set

    var completedTasksToday by mutableStateOf(0)
        private set

    var totalTasksToday by mutableStateOf(0)
        private set

    var hasUnsavedChanges by mutableStateOf(false)
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
                    userLevel = proficiency

                    // Bugungi vazifalarni Firestore'dan olish
                    loadTodayTasks(userId, proficiency)

                    // Haftalik progressni olish
                    loadProgress(userId)

                    // Yutuqlarni olish
                    loadAchievements(userId)
                } else {
                    loadDefaults()
                }
            }
            .addOnFailureListener {
                loadDefaults()
            }
    }

    private fun loadTodayTasks(userId: String, level: String) {
        val todayKey = getTodayKey()

        db.collection("users").document(userId)
            .collection("dailyTasks").document(todayKey).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    // Bugungi vazifalar allaqachon yaratilgan — o'qish
                    @Suppress("UNCHECKED_CAST")
                    val tasksData = doc.get("tasks") as? List<Map<String, Any>>
                    if (tasksData != null) {
                        todayTasks = tasksData.map { taskMap ->
                            LearningTask(
                                id = taskMap["id"] as? String ?: "",
                                title = taskMap["title"] as? String ?: "",
                                description = taskMap["description"] as? String ?: "",
                                duration = taskMap["duration"] as? String ?: "",
                                isCompleted = taskMap["isCompleted"] as? Boolean ?: false
                            )
                        }
                        updateTaskCounts()
                    }
                } else {
                    // Bugungi vazifalar yo'q — yaratish
                    val tasks = generateTasksForLevel(level)
                    todayTasks = tasks
                    updateTaskCounts()
                    saveTodayTasks(userId, todayKey, tasks)
                }
            }
            .addOnFailureListener {
                todayTasks = generateTasksForLevel(level)
                updateTaskCounts()
            }
    }

    fun toggleTask(taskId: String) {
        // Faqat lokal holat o'zgartiriladi — Firestore'ga yozilmaydi
        todayTasks = todayTasks.map { task ->
            if (task.id == taskId) task.copy(isCompleted = !task.isCompleted)
            else task
        }
        updateTaskCounts()
        hasUnsavedChanges = true
    }

    fun saveChanges() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val todayKey = getTodayKey()

        // Firestore'ga saqlash
        saveTodayTasks(userId, todayKey, todayTasks)

        // Haftalik progressni yangilash
        updateTodayProgress(userId)

        // Stats yangilash
        updateStats(userId)

        // Yutuqlarni tekshirish
        checkAchievements(userId)

        hasUnsavedChanges = false
    }

    private fun saveTodayTasks(userId: String, todayKey: String, tasks: List<LearningTask>) {
        val tasksData = tasks.map { task ->
            mapOf(
                "id" to task.id,
                "title" to task.title,
                "description" to task.description,
                "duration" to task.duration,
                "isCompleted" to task.isCompleted
            )
        }

        db.collection("users").document(userId)
            .collection("dailyTasks").document(todayKey)
            .set(mapOf("tasks" to tasksData, "date" to todayKey))
            .addOnSuccessListener {
                Log.d(TAG, "✅ Bugungi vazifalar saqlandi: $todayKey")
            }
    }

    private fun updateTodayProgress(userId: String) {
        val completed = todayTasks.count { it.isCompleted }
        val total = todayTasks.size
        val progress = if (total > 0) completed.toDouble() / total else 0.0

        // Haftalik progress arrayda bugungi kunni yangilash
        val todayIndex = getDayOfWeekIndex()
        val updatedProgress = weeklyProgress.toMutableList()
        if (todayIndex < updatedProgress.size) {
            updatedProgress[todayIndex] = progress
            weeklyProgress = updatedProgress
        }

        // Firestore'da saqlash
        db.collection("users").document(userId)
            .collection("progress").document("weekly")
            .set(mapOf("days" to weeklyProgress))
    }

    private fun updateStats(userId: String) {
        // Barcha kunlardan jami statistikani hisoblash
        db.collection("users").document(userId)
            .collection("dailyTasks").get()
            .addOnSuccessListener { documents ->
                var totalCompleted = 0
                var daysWithTasks = 0

                documents.forEach { doc ->
                    @Suppress("UNCHECKED_CAST")
                    val tasksData = doc.get("tasks") as? List<Map<String, Any>>
                    if (tasksData != null) {
                        val dayCompleted = tasksData.count { it["isCompleted"] == true }
                        totalCompleted += dayCompleted
                        if (dayCompleted > 0) daysWithTasks++
                    }
                }

                val unlockedCount = achievements.count { it.isUnlocked }

                db.collection("users").document(userId)
                    .collection("stats").document("summary")
                    .set(mapOf(
                        "completedLessons" to totalCompleted,
                        "learnedWords" to totalCompleted * 5,
                        "streak" to daysWithTasks,
                        "achievementsCount" to unlockedCount,
                        "lastUpdated" to com.google.firebase.Timestamp.now()
                    ))
            }
    }

    private fun checkAchievements(userId: String) {
        val completedCount = todayTasks.count { it.isCompleted }
        val allCompleted = todayTasks.all { it.isCompleted } && todayTasks.isNotEmpty()

        val updatedAchievements = achievements.map { achievement ->
            when (achievement.id) {
                "first_step" -> achievement.copy(isUnlocked = true) // Doim ochiq
                "first_task" -> achievement.copy(isUnlocked = completedCount >= 1)
                "daily_star" -> achievement.copy(isUnlocked = allCompleted)
                "word_master" -> achievement.copy(isUnlocked = completedCount >= 3)
                "grammar_guru" -> achievement.copy(isUnlocked = completedCount >= 2)
                else -> achievement
            }
        }

        if (updatedAchievements != achievements) {
            achievements = updatedAchievements

            // Firestore'da saqlash
            updatedAchievements.forEach { achievement ->
                db.collection("users").document(userId)
                    .collection("achievements").document(achievement.id)
                    .set(mapOf(
                        "title" to achievement.title,
                        "description" to achievement.description,
                        "iconName" to achievement.iconName,
                        "isUnlocked" to achievement.isUnlocked
                    ))
            }
        }
    }

    private fun updateTaskCounts() {
        completedTasksToday = todayTasks.count { it.isCompleted }
        totalTasksToday = todayTasks.size
    }

    private fun loadProgress(userId: String) {
        db.collection("users").document(userId)
            .collection("progress").document("weekly").get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    @Suppress("UNCHECKED_CAST")
                    val days = doc.get("days") as? List<*>
                    weeklyProgress = days?.map { (it as? Number)?.toDouble() ?: 0.0 }
                        ?: List(7) { 0.0 }
                } else {
                    weeklyProgress = List(7) { 0.0 }
                    // Yaratish
                    db.collection("users").document(userId)
                        .collection("progress").document("weekly")
                        .set(mapOf("days" to weeklyProgress))
                }
            }
            .addOnFailureListener {
                weeklyProgress = List(7) { 0.0 }
            }
    }

    private fun loadAchievements(userId: String) {
        db.collection("users").document(userId)
            .collection("achievements").get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    achievements = documents.map { doc ->
                        Achievement(
                            id = doc.id,
                            title = doc.getString("title") ?: "",
                            description = doc.getString("description") ?: "",
                            iconName = doc.getString("iconName") ?: "star",
                            isUnlocked = doc.getBoolean("isUnlocked") ?: false
                        )
                    }
                } else {
                    // Yangi foydalanuvchi — default yutuqlarni yaratish
                    val defaults = getDefaultAchievements()
                    achievements = defaults

                    defaults.forEach { achievement ->
                        db.collection("users").document(userId)
                            .collection("achievements").document(achievement.id)
                            .set(mapOf(
                                "title" to achievement.title,
                                "description" to achievement.description,
                                "iconName" to achievement.iconName,
                                "isUnlocked" to achievement.isUnlocked
                            ))
                    }
                }
            }
            .addOnFailureListener {
                achievements = getDefaultAchievements()
            }
    }

    private fun generateTasksForLevel(level: String): List<LearningTask> {
        return when {
            level.contains("Boshlang'ich", ignoreCase = true) -> listOf(
                LearningTask(
                    id = "task_vocab_basic",
                    title = "Asosiy so'zlar",
                    description = "Kundalik 15 ta yangi so'z o'rganish",
                    duration = "15 min",
                    isCompleted = false
                ),
                LearningTask(
                    id = "task_grammar_basic",
                    title = "Grammar asoslari",
                    description = "Present Simple tuzilishi",
                    duration = "20 min",
                    isCompleted = false
                ),
                LearningTask(
                    id = "task_listening_basic",
                    title = "Tinglash mashqi",
                    description = "Oddiy dialog tinglash va tushunish",
                    duration = "10 min",
                    isCompleted = false
                )
            )
            level.contains("O'rta", ignoreCase = true) || level.contains("Intermediate", ignoreCase = true) -> listOf(
                LearningTask(
                    id = "task_vocab_inter",
                    title = "So'z boyligini kengaytirish",
                    description = "Mavzuli 20 ta yangi so'z",
                    duration = "20 min",
                    isCompleted = false
                ),
                LearningTask(
                    id = "task_grammar_inter",
                    title = "Grammar chuqurlash",
                    description = "Perfect tenses mashqlari",
                    duration = "25 min",
                    isCompleted = false
                ),
                LearningTask(
                    id = "task_speaking_inter",
                    title = "Speaking mashqi",
                    description = "Mavzu bo'yicha gapirish mashqi",
                    duration = "15 min",
                    isCompleted = false
                ),
                LearningTask(
                    id = "task_reading_inter",
                    title = "O'qish mashqi",
                    description = "Qisqa maqola o'qish va savollarga javob",
                    duration = "15 min",
                    isCompleted = false
                )
            )
            else -> listOf(
                LearningTask(
                    id = "task_vocab_adv",
                    title = "Advanced vocabulary",
                    description = "Idiomalar va phrasal verblar",
                    duration = "20 min",
                    isCompleted = false
                ),
                LearningTask(
                    id = "task_writing_adv",
                    title = "Academic writing",
                    description = "Essay yozish mashqi",
                    duration = "30 min",
                    isCompleted = false
                ),
                LearningTask(
                    id = "task_debate_adv",
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
                id = "first_step",
                title = "Birinchi qadam",
                description = "Ilovaga ro'yxatdan o'tish",
                iconName = "star",
                isUnlocked = true
            ),
            Achievement(
                id = "first_task",
                title = "Birinchi vazifa",
                description = "Birinchi vazifani bajarish",
                iconName = "check_circle",
                isUnlocked = false
            ),
            Achievement(
                id = "daily_star",
                title = "Kun yulduzi",
                description = "Barcha kunlik vazifalarni bajarish",
                iconName = "star",
                isUnlocked = false
            ),
            Achievement(
                id = "word_master",
                title = "So'z ustasi",
                description = "3 ta vazifani bajarish",
                iconName = "book",
                isUnlocked = false
            ),
            Achievement(
                id = "grammar_guru",
                title = "Grammar guru",
                description = "Grammar vazifasini bajarish",
                iconName = "check_circle",
                isUnlocked = false
            )
        )
    }

    private fun loadDefaults() {
        weeklyProgress = List(7) { 0.0 }
        todayTasks = generateTasksForLevel("Boshlang'ich")
        achievements = getDefaultAchievements()
        updateTaskCounts()
    }

    private fun getTodayKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getDayOfWeekIndex(): Int {
        val cal = Calendar.getInstance()
        // Dushanba = 0, Yakshanba = 6
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }
}
