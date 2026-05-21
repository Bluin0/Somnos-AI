package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.SleepDatabase
import com.example.data.SleepRepository
import com.example.ui.AddSleepEntryScreen
import com.example.ui.ChatScreen
import com.example.ui.SleepDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.SleepViewModel
import com.example.viewmodel.SleepViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar persistencia Room y SharedPreferences para retención personalizable
        val database = SleepDatabase.getDatabase(applicationContext)
        val repository = SleepRepository(database)
        val sharedPrefs = getSharedPreferences("sleep_analyzer_prefs", MODE_PRIVATE)

        setContent {
            MyApplicationTheme {
                val viewModel: SleepViewModel = viewModel(
                    factory = SleepViewModelFactory(repository, sharedPrefs)
                )

                MainAppContainer(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContainer(
    viewModel: SleepViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Métricas") },
                    label = { Text("Dashboard") },
                    modifier = Modifier.testTag("tab_dashboard")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Asistente") },
                    label = { Text("Asistente") },
                    modifier = Modifier.testTag("tab_chat")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Registrar") },
                    label = { Text("Registrar") },
                    modifier = Modifier.testTag("tab_register")
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> SleepDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToChat = { selectedTab = 1 }
                )
                1 -> ChatScreen(viewModel = viewModel)
                2 -> AddSleepEntryScreen(
                    viewModel = viewModel,
                    onSuccess = { selectedTab = 0 }
                )
            }
        }
    }
}
