package com.alpha.assistant.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val eventType: Int,
    val screenText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
