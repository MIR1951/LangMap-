package com.example.langmap.screen.main

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.langmap.ui.theme.Blue
import com.example.langmap.viewmodel.PlanViewModel
import com.example.langmap.viewmodel.ProfileViewModel
import com.example.langmap.viewmodel.RecommendationViewModel

@Composable
fun MainScreen(onLogout: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val application = LocalContext.current.applicationContext as Application

    val recommendationViewModel: RecommendationViewModel = viewModel()
    val planViewModel: PlanViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(application)
    )
    val profileViewModel: ProfileViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(application)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Asosiy") },
                    label = { Text("Asosiy") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Blue,
                        selectedTextColor = Blue
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Reja") },
                    label = { Text("Reja") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Blue,
                        selectedTextColor = Blue
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Blue,
                        selectedTextColor = Blue
                    )
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> HomeScreen(
                viewModel = recommendationViewModel,
                modifier = Modifier.padding(paddingValues)
            )
            1 -> PlanScreen(
                viewModel = planViewModel,
                modifier = Modifier.padding(paddingValues)
            )
            2 -> ProfileScreen(
                viewModel = profileViewModel,
                modifier = Modifier.padding(paddingValues),
                onLogout = onLogout
            )
        }
    }
}
