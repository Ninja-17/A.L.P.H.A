package com.alpha.assistant.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alpha.assistant.db.entity.PatternEntity

@Dao
interface PatternDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pattern: PatternEntity)

    @Query("SELECT * FROM patterns WHERE confidence > :minConfidence ORDER BY confidence DESC")
    suspend fun getHighConfidencePatterns(minConfidence: Float = 0.5f): List<PatternEntity>

    @Query("SELECT * FROM patterns ORDER BY lastDetected DESC LIMIT 20")
    suspend fun getRecentPatterns(): List<PatternEntity>

    @Query("UPDATE patterns SET occurrences = occurrences + 1, confidence = MIN(confidence + 0.1, 1.0) WHERE id = :id")
    suspend fun reinforcePattern(id: Long)

    @Query("SELECT * FROM patterns WHERE condition = :condition LIMIT 1")
    suspend fun findByCondition(condition: String): PatternEntity?
}
