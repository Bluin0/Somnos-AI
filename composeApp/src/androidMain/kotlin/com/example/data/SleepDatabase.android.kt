package com.example.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

lateinit var appContext: Context

actual fun getDatabaseBuilder(): RoomDatabase.Builder<SleepDatabase> {
    val dbFile = appContext.getDatabasePath("sleep_analysis_db.db")
    return Room.databaseBuilder<SleepDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
