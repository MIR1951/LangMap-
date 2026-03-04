package com.example.langmap.model

import java.util.UUID

data class LearningTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val duration: String,
    val isCompleted: Boolean
)
