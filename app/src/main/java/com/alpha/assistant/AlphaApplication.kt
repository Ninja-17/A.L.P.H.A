package com.alpha.assistant

import android.app.Application
import com.alpha.assistant.db.AppDatabase

class AlphaApplication : Application() {

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
    }

    companion object {
        lateinit var instance: AlphaApplication
            private set
    }
}
