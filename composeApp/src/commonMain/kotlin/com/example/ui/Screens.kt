package com.example.ui

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {
    @Serializable
    data object Dashboard : Screen
    
    @Serializable
    data object Chat : Screen
    
    @Serializable
    data object AddEntry : Screen
}
