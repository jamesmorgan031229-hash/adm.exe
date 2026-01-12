package com.bootabledrive.app.data.model

data class AppSettings(
    // General
    val defaultStorageLocation: String = "",
    val autoConnectOnUSB: Boolean = false,
    val launchOnStartup: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM,

    // Boot Options
    val defaultEmulationMode: BootMode = BootMode.USB,
    val bootTimeoutSeconds: Int = 30,
    val autoSelectLastImage: Boolean = true,

    // Storage Management
    val defaultDownloadFolder: String = "",
    val autoVerifyImages: Boolean = true,
    val cacheSizeMB: Int = 500,

    // Advanced
    val enableKernelCheck: Boolean = true,
    val enableUSBDebugging: Boolean = false,
    val enableLogs: Boolean = true,
    val developerMode: Boolean = false,

    // First-time setup
    val hasCompletedOnboarding: Boolean = false
)

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
}
