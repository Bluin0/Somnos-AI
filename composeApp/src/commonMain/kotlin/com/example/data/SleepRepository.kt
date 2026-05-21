package com.example.data

import kotlinx.coroutines.flow.Flow

class SleepRepository(private val database: SleepDatabase) {
    private val sleepDao = database.sleepDao()
    private val chatDao = database.chatDao()

    // --- Sleep Entries ---
    val allSleepEntries: Flow<List<SleepEntry>> = sleepDao.getAllEntries()

    suspend fun insertSleepEntry(entry: SleepEntry): Long {
        return sleepDao.insertEntry(entry)
    }

    suspend fun deleteSleepEntryById(id: Int) {
        sleepDao.deleteEntryById(id)
    }

    suspend fun deleteOldSleepEntries(cutoffTimestamp: Long) {
        sleepDao.deleteOldRecords(cutoffTimestamp)
    }

    // --- Chat Messages ---
    val allChatMessages: Flow<List<ChatMessage>> = chatDao.getAllMessagesFlow()

    suspend fun getAllMessagesList(): List<ChatMessage> {
        return chatDao.getAllMessages()
    }

    suspend fun insertChatMessage(message: ChatMessage) {
        chatDao.insertMessage(message)
    }

    suspend fun clearChatHistory() {
        chatDao.clearChat()
    }
}
