package com.bootabledrive.app.navigation

sealed class NavRoutes(val route: String) {
    object Onboarding : NavRoutes("onboarding")
    object Dashboard : NavRoutes("dashboard")
    object ImageManagement : NavRoutes("image_management/{imageId}") {
        fun createRoute(imageId: String) = "image_management/$imageId"
    }
    object DownloadManager : NavRoutes("download_manager")
    object ConnectionWizard : NavRoutes("connection_wizard")
    object Settings : NavRoutes("settings")
}
