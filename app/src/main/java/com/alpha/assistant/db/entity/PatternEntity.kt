package com.alpha.assistant.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patterns")
data class PatternEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patternType: String,
    val condition: String,
    val confidence: Float = 0.0f,
    val occurrences: Int = 0,
    val lastDetected: Long = System.currentTimeMillis()
)
