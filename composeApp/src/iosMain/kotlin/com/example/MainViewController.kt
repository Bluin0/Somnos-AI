package com.example

import androidx.compose.ui.window.ComposeUIViewController
import com.example.data.SleepRepository
import com.example.data.getDatabaseBuilder
import com.example.data.getRoomDatabase
import com.example.viewmodel.SleepViewModel
import com.russhwolf.settings.NSUserDefaultsSettings
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    // Inicializar Room local en iOS (Sandbox)
    val dbBuilder = getDatabaseBuilder()
    val database = getRoomDatabase(dbBuilder)
    val repository = SleepRepository(database)
    
    // Inicializar Settings nativos en iOS (NSUserDefaults)
    val settings = NSUserDefaultsSettings(platform.Foundation.NSUserDefaults.standardUserDefaults)
    
    val viewModel = SleepViewModel(repository, settings)

    return ComposeUIViewController {
        App(viewModel = viewModel)
    }
}
