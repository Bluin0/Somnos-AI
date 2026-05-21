package com.example.util

import java.util.regex.Pattern

object MedicationSanitizer {
    // Lista de principios activos, ansiolíticos, inductores del sueño y palabras asociadas
    private val MEDICATION_KEYWORDS = listOf(
        "lorazepam", "diazepam", "bromazepam", "lormetazepam", "alprazolam", "clonazepam",
        "zolpidem", "zopiclona", "eszopiclona", "melatonina", "doxilamina", "dormidina",
        "valeriana", "tranxilium", "lexatin", "orfidal", "valium", "sedotime", "psicotrópico",
        "pastilla para dormir", "pastillas para dormir", "antidepresivo", "ansiolítico",
        "pastilla", "pastillas", "medicamento", "medicación", "medicaciones", "medicina", "medicinas",
        "fármaco", "fármacos", "droga", "receta", "sumial"
    )

    data class SanitizeResult(
        val originalText: String,
        val sanitizedText: String,
        val wasMedicationDetected: Boolean
    )

    /**
     * Sanitiza el texto de forma local eliminando nombres de medicamentos concretos o dosis,
     * y retornando el texto anonimizado junto a un flag que indica si se guardó información sensible.
     */
    fun sanitize(text: String): SanitizeResult {
        if (text.isBlank()) return SanitizeResult(text, text, false)

        var sanitized = text
        var detected = false

        // 1. Filtrar medicamentos específicos o palabras clave de farmacia
        for (keyword in MEDICATION_KEYWORDS) {
            val pattern = Pattern.compile("(?i)\\b$keyword\\b|(?i)\\b${keyword}s\\b")
            val matcher = pattern.matcher(sanitized)
            if (matcher.find()) {
                detected = true
                sanitized = matcher.replaceAll("[Información Sensible: Medicación de Sueño Detectada y Asegurada]")
            }
        }

        // 2. Filtrar dosis con números (ejemplo: "10mg", "5 mg", "0.25 miligramos")
        val mgPattern = Pattern.compile("(?i)\\b\\d+(\\.\\d+)?\\s*(mg|g|miligramos|gramos|pastillas|pastillitas)\\b")
        val mgMatcher = mgPattern.matcher(sanitized)
        if (mgMatcher.find()) {
            detected = true
            sanitized = mgMatcher.replaceAll("[Dosis de Medicación Asegurada]")
        }

        return SanitizeResult(
            originalText = text,
            sanitizedText = sanitized,
            wasMedicationDetected = detected
        )
    }
}
