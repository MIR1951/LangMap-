package com.example.langmap.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.langmap.model.Achievement
import com.example.langmap.model.LearningTask

class PlanViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("langmap_prefs", Context.MODE_PRIVATE)

    var weeklyProgress by mutableStateOf(listOf<Double>())
        private set

    var todayTasks by mutableStateOf(listOf<LearningTask>())
        private set

    var achievements by mutableStateOf(listOf<Achievement>())
        private set

    init {
        fetchData()
    }

    fun fetchData() {
        weeklyProgress = listOf(0.8, 0.6, 0.9, 0.7, 0.5, 0.3, 0.4)

        todayTasks = listOf(
            LearningTask(
                title = "So'z yodlash",
                description = "10 ta yangi so'z",
                duration = "15 min",
                isCompleted = true
            ),
            LearningTask(
                title = "Grammar",
                description = "Present Perfect",
                duration = "20 min",
                isCompleted = false
            ),
            LearningTask(
                title = "Speaking",
                description = "Dialog practice",
                duration = "10 min",
                isCompleted = false
            )
        )

        achievements = listOf(
            Achievement(
                title = "Birinchi qadam",
                description = "Birinchi darsni tugatish",
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
                description = "Barcha grammar testlarni topshirish",
                iconName = "check_circle",
                isUnlocked = false
            )
        )
    }
}
