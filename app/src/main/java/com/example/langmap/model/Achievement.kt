package com.example.langmap.model

import java.util.UUID

data class Achievement(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean
)
