package com.example.data

import androidx.room.Room
import androidx.room.RoomDatabase
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
actual fun getDatabaseBuilder(): RoomDatabase.Builder<SleepDatabase> {
    println("getDatabaseBuilder: Started")
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null
    )
    println("getDatabaseBuilder: documentDirectory: $documentDirectory")
    val dbFilePath = documentDirectory?.path + "/sleep_analysis_db.db"
    println("getDatabaseBuilder: dbFilePath: $dbFilePath")
    return Room.databaseBuilder<SleepDatabase>(
        name = dbFilePath
    )
}
