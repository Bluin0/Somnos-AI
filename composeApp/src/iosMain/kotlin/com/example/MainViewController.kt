package com.example

import androidx.compose.ui.window.ComposeUIViewController
import com.example.data.SleepRepository
import com.example.data.getDatabaseBuilder
import com.example.data.getRoomDatabase
import com.example.viewmodel.SleepViewModel
import com.russhwolf.settings.NSUserDefaultsSettings
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    try {
        println("MainViewController: Starting initialization...")
        // Inicializar Room local en iOS (Sandbox)
        val dbBuilder = getDatabaseBuilder()
        println("MainViewController: Database builder created.")
        val database = getRoomDatabase(dbBuilder)
        println("MainViewController: Database initialized successfully.")
        val repository = SleepRepository(database)
        println("MainViewController: Repository initialized.")
        
        // Inicializar Settings nativos en iOS (NSUserDefaults)
        val settings = NSUserDefaultsSettings(platform.Foundation.NSUserDefaults.standardUserDefaults)
        println("MainViewController: Settings initialized.")
        
        val viewModel = SleepViewModel(repository, settings)
        println("MainViewController: ViewModel initialized.")

        return ComposeUIViewController(configure = { enforceStrictPlistSanityCheck = false }) {
            App(viewModel = viewModel)
        }
    } catch (e: Throwable) {
        println("MainViewController: CRASH DETECTED!")
        println("MainViewController: Exception Message: ${e.message}")
        println("MainViewController: Exception details: $e")
        e.printStackTrace()
        throw e
    }
}
