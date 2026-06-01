package com.alpha.assistant.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alpha.assistant.db.dao.EventDao
import com.alpha.assistant.db.dao.PatternDao
import com.alpha.assistant.db.entity.EventEntity
import com.alpha.assistant.db.entity.PatternEntity

@Database(
    entities = [EventEntity::class, PatternEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun patternDao(): PatternDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alpha_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
