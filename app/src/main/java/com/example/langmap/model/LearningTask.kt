package com.example.langmap.model

data class LearningTask(
    val id: String = "",
    val title: String,
    val description: String,
    val duration: String,
    val isCompleted: Boolean
)
