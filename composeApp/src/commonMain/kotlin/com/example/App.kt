package com.example

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.Screen
import com.example.ui.SleepDashboardScreen
import com.example.ui.ChatScreen
import com.example.ui.AddSleepEntryScreen
import com.example.viewmodel.SleepViewModel

@Composable
fun App(viewModel: SleepViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute?.contains("Dashboard") == true,
                    onClick = {
                        navController.navigate(Screen.Dashboard) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Métricas") },
                    label = { Text("Dashboard") },
                    modifier = Modifier.testTag("tab_dashboard")
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("Chat") == true,
                    onClick = {
                        navController.navigate(Screen.Chat) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Asistente") },
                    label = { Text("Asistente") },
                    modifier = Modifier.testTag("tab_chat")
                )
                NavigationBarItem(
                    selected = currentRoute?.contains("AddEntry") == true,
                    onClick = {
                        navController.navigate(Screen.AddEntry) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Registrar") },
                    label = { Text("Registrar") },
                    modifier = Modifier.testTag("tab_register")
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Screen.Dashboard> {
                SleepDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToChat = {
                        navController.navigate(Screen.Chat) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable<Screen.Chat> {
                ChatScreen(viewModel = viewModel)
            }
            composable<Screen.AddEntry> {
                AddSleepEntryScreen(
                    viewModel = viewModel,
                    onSuccess = {
                        navController.navigate(Screen.Dashboard) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
