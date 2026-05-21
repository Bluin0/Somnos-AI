package com.example.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Declara la expectativa para la clave de API específica por plataforma
expect fun getGeminiApiKey(): String

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    val jsonConfig = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        encodeDefaults = true
    }

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(jsonConfig)
        }
    }

    suspend fun generateContent(apiKey: String, request: GenerateContentRequest): GenerateContentResponse {
        val url = "${BASE_URL}v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        return httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
