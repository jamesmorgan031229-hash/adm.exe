package com.bootabledrive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.bootabledrive.app.navigation.NavGraph
import com.bootabledrive.app.navigation.NavRoutes
import com.bootabledrive.app.ui.theme.BootableDriveTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BootableDriveApp()
        }
    }
}

@Composable
fun BootableDriveApp() {
    // In a real app, this would be read from DataStore/SharedPreferences
    var hasCompletedOnboarding by remember { mutableStateOf(false) }

    BootableDriveTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()

            NavGraph(
                navController = navController,
                startDestination = if (hasCompletedOnboarding) {
                    NavRoutes.Dashboard.route
                } else {
                    NavRoutes.Onboarding.route
                }
            )
        }
    }
}
