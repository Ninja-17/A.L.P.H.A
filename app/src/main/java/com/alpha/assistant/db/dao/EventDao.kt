package com.alpha.assistant.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alpha.assistant.db.entity.EventEntity

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY timestamp DESC LIMIT 100")
    suspend fun getRecentEvents(): List<EventEntity>

    @Query("SELECT * FROM events WHERE packageName = :packageName ORDER BY timestamp DESC LIMIT 50")
    suspend fun getEventsByApp(packageName: String): List<EventEntity>

    @Query("SELECT * FROM events WHERE timestamp BETWEEN :from AND :to ORDER BY timestamp ASC")
    suspend fun getEventsInRange(from: Long, to: Long): List<EventEntity>
}
