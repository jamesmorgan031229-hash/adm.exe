package com.bootabledrive.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bootabledrive.app.ui.screens.connectionwizard.ConnectionWizardScreen
import com.bootabledrive.app.ui.screens.dashboard.DashboardScreen
import com.bootabledrive.app.ui.screens.dashboard.DashboardViewModel
import com.bootabledrive.app.ui.screens.downloadmanager.DownloadManagerScreen
import com.bootabledrive.app.ui.screens.imagemanagement.ImageManagementScreen
import com.bootabledrive.app.ui.screens.onboarding.OnboardingScreen
import com.bootabledrive.app.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(NavRoutes.Dashboard.route) {
                        popUpTo(NavRoutes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Dashboard.route) {
            DashboardScreen(
                onNavigateToImageManagement = { imageId ->
                    navController.navigate(NavRoutes.ImageManagement.createRoute(imageId))
                },
                onNavigateToDownloadManager = {
                    navController.navigate(NavRoutes.DownloadManager.route)
                },
                onNavigateToConnectionWizard = {
                    navController.navigate(NavRoutes.ConnectionWizard.route)
                },
                onNavigateToSettings = {
                    navController.navigate(NavRoutes.Settings.route)
                }
            )
        }

        composable(
            route = NavRoutes.ImageManagement.route,
            arguments = listOf(
                navArgument("imageId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val imageId = backStackEntry.arguments?.getString("imageId") ?: ""
            ImageManagementScreen(
                imageId = imageId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.DownloadManager.route) {
            DownloadManagerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.ConnectionWizard.route) {
            ConnectionWizardScreen(
                onNavigateBack = { navController.popBackStack() },
                onConnectionComplete = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
