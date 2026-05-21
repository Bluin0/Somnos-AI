package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.ConstructServices
import androidx.room.RoomDatabaseConstructor
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_entries ORDER BY dateString DESC")
    fun getAllEntries(): Flow<List<SleepEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: SleepEntry): Long

    @Query("DELETE FROM sleep_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Int)

    @Query("DELETE FROM sleep_entries WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteOldRecords(cutoffTimestamp: Long)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessagesFlow(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getAllMessages(): List<ChatMessage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Query("DELETE FROM chat_messages")
    suspend fun clearChat()
}

@Database(entities = [SleepEntry::class, ChatMessage::class], version = 2, exportSchema = false)
@ConstructServices(SleepDatabaseConstructor::class)
abstract class SleepDatabase : RoomDatabase() {
    abstract fun sleepDao(): SleepDao
    abstract fun chatDao(): ChatDao
}

// Requerido por Room KMP 2.7.0+ para la generación de código nativo
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SleepDatabaseConstructor : RoomDatabaseConstructor<SleepDatabase> {
    override fun initialize(): SleepDatabase
}

expect fun getDatabaseBuilder(): RoomDatabase.Builder<SleepDatabase>

fun getRoomDatabase(builder: RoomDatabase.Builder<SleepDatabase>): SleepDatabase {
    return builder
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
}
