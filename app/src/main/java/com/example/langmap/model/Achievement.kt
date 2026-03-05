package com.example.langmap.model

data class Achievement(
    val id: String = "",
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean
)
