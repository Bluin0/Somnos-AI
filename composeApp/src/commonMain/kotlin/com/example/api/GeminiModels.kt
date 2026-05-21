package com.example.api

import kotlinx.serialization.Serializable

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val role: String? = null,
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null,
    val inlineData: InlineData? = null
)

@Serializable
data class InlineData(
    val mimeType: String,
    val data: String // String en Base64
)

@Serializable
data class GenerationConfig(
    val responseMimeType: String? = null,
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content? = null
)

@Serializable
data class SleepExtractedData(
    val bedtime: String? = null,
    val wakeupTime: String? = null,
    val sleepDurationHours: Int? = null,
    val sleepDurationMinutes: Int? = null,
    val awakeHours: Int? = null,
    val awakeMinutes: Int? = null,
    val remHours: Int? = null,
    val remMinutes: Int? = null,
    val essentialHours: Int? = null,
    val essentialMinutes: Int? = null,
    val deepHours: Int? = null,
    val deepMinutes: Int? = null,
    val awakePercentage: Float? = null,
    val remPercentage: Float? = null,
    val essentialPercentage: Float? = null,
    val deepPercentage: Float? = null
)
