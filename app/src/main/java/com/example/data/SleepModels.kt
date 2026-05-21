package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_entries")
data class SleepEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String, // YYYY-MM-DD
    val bedtime: String, // HH:MM
    val wakeupTime: String, // HH:MM
    val sleepDurationHours: Int,
    val sleepDurationMinutes: Int,
    val awakeHours: Int,
    val awakeMinutes: Int,
    val remHours: Int,
    val remMinutes: Int,
    val essentialHours: Int,
    val essentialMinutes: Int,
    val deepHours: Int,
    val deepMinutes: Int,
    val awakePercentage: Float,
    val remPercentage: Float,
    val essentialPercentage: Float,
    val deepPercentage: Float,
    val medicationLogged: Boolean, // Indica si se registró medicación (información sensible protegida)
    val notes: String = "", // Notas generales, con nombres de medicamentos redactados por privacidad
    val sleepScore: Int = 0, // Calidad de sueño calculada real (0-100)
    val guessedScore: Int? = null, // Puntuación de sueño estimada opcional por el juego (0-100)
    val leaderboardOptIn: Boolean = false, // True si el usuario quiere subirlo al ranking
    val leaderboardNickname: String = "", // Apodo divertido seleccionado para el ranking
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sender: String, // "user" o "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)
