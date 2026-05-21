package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.appContext
import com.example.data.getDatabaseBuilder
import com.example.data.getRoomDatabase
import com.example.data.SleepRepository
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SleepViewModel
import com.example.viewmodel.sleepViewModelFactory
import com.russhwolf.settings.SharedPreferencesSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar persistencia Room y Settings para retención personalizable
        appContext = applicationContext
        val database = getRoomDatabase(getDatabaseBuilder())
        val repository = SleepRepository(database)
        val sharedPrefs = getSharedPreferences("sleep_analyzer_prefs", MODE_PRIVATE)
        val settings = SharedPreferencesSettings(sharedPrefs)

        setContent {
            MyApplicationTheme {
                val viewModel: SleepViewModel = viewModel(
                    factory = sleepViewModelFactory(repository, settings)
                )

                App(viewModel = viewModel)
            }
        }
    }
}
