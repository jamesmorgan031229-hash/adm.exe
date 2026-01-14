package com.bootabledrive.app.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bootabledrive.app.data.model.AppTheme
import com.bootabledrive.app.data.model.BootMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General Settings
            SettingsSection(title = "General") {
                // Theme
                SettingsItem(
                    icon = Icons.Default.DarkMode,
                    title = "Theme",
                    subtitle = "Choose your preferred appearance"
                ) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SegmentedButton(
                            selected = uiState.settings.theme == AppTheme.LIGHT,
                            onClick = { viewModel.updateTheme(AppTheme.LIGHT) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                        ) {
                            Text("Light")
                        }
                        SegmentedButton(
                            selected = uiState.settings.theme == AppTheme.DARK,
                            onClick = { viewModel.updateTheme(AppTheme.DARK) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3)
                        ) {
                            Text("Dark")
                        }
                        SegmentedButton(
                            selected = uiState.settings.theme == AppTheme.SYSTEM,
                            onClick = { viewModel.updateTheme(AppTheme.SYSTEM) },
                            shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3)
                        ) {
                            Text("System")
                        }
                    }
                }

                SettingsSwitchItem(
                    icon = Icons.Default.Usb,
                    title = "Auto-connect on USB",
                    subtitle = "Automatically start emulation when USB is connected",
                    checked = uiState.settings.autoConnectOnUSB,
                    onCheckedChange = viewModel::updateAutoConnect
                )

                SettingsSwitchItem(
                    icon = Icons.Default.RestartAlt,
                    title = "Launch on startup",
                    subtitle = "Start app when device boots",
                    checked = uiState.settings.launchOnStartup,
                    onCheckedChange = viewModel::updateLaunchOnStartup
                )
            }

            // Boot Options
            SettingsSection(title = "Boot Options") {
                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = "Default Emulation Mode",
                    subtitle = "Default mode for new images"
                ) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SegmentedButton(
                            selected = uiState.settings.defaultEmulationMode == BootMode.USB,
                            onClick = { viewModel.updateDefaultEmulationMode(BootMode.USB) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text("USB")
                        }
                        SegmentedButton(
                            selected = uiState.settings.defaultEmulationMode == BootMode.CD,
                            onClick = { viewModel.updateDefaultEmulationMode(BootMode.CD) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Text("CD/DVD")
                        }
                    }
                }

                SettingsItem(
                    icon = Icons.Default.Timer,
                    title = "Boot Timeout",
                    subtitle = "${uiState.settings.bootTimeoutSeconds} seconds"
                ) {
                    Slider(
                        value = uiState.settings.bootTimeoutSeconds.toFloat(),
                        onValueChange = { viewModel.updateBootTimeout(it.toInt()) },
                        valueRange = 10f..120f,
                        steps = 10,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                SettingsSwitchItem(
                    icon = Icons.Default.Refresh,
                    title = "Auto-select last image",
                    subtitle = "Remember and select the last used image",
                    checked = uiState.settings.autoSelectLastImage,
                    onCheckedChange = viewModel::updateAutoSelectLastImage
                )
            }

            // Storage Management
            SettingsSection(title = "Storage Management") {
                // Storage usage
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Storage Used",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${uiState.storageUsedMB} MB / ${uiState.storageTotalMB} MB",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { uiState.storageUsedMB.toFloat() / uiState.storageTotalMB },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                        )
                    }
                }

                SettingsSwitchItem(
                    icon = Icons.Default.Verified,
                    title = "Auto-verify images",
                    subtitle = "Verify checksums when importing images",
                    checked = uiState.settings.autoVerifyImages,
                    onCheckedChange = viewModel::updateAutoVerifyImages
                )

                SettingsClickableItem(
                    icon = Icons.Default.Delete,
                    title = "Clear Cache",
                    subtitle = "Free up ${uiState.cacheUsedMB} MB",
                    onClick = viewModel::showClearCacheDialog
                )

                SettingsClickableItem(
                    icon = Icons.Default.Folder,
                    title = "Download Folder",
                    subtitle = "/storage/emulated/0/BootImages",
                    onClick = { /* TODO: Open folder picker */ }
                )
            }

            // Advanced
            SettingsSection(title = "Advanced") {
                SettingsSwitchItem(
                    icon = Icons.Default.Memory,
                    title = "Kernel compatibility check",
                    subtitle = "Verify kernel supports USB gadget mode",
                    checked = uiState.settings.enableKernelCheck,
                    onCheckedChange = viewModel::updateKernelCheck
                )

                SettingsSwitchItem(
                    icon = Icons.Default.BugReport,
                    title = "USB Debugging",
                    subtitle = "Enable verbose USB logging",
                    checked = uiState.settings.enableUSBDebugging,
                    onCheckedChange = viewModel::updateUSBDebugging
                )

                SettingsSwitchItem(
                    icon = Icons.Default.Description,
                    title = "Enable Logs",
                    subtitle = "Collect diagnostic logs for troubleshooting",
                    checked = uiState.settings.enableLogs,
                    onCheckedChange = viewModel::updateLogs
                )

                SettingsSwitchItem(
                    icon = Icons.Default.Code,
                    title = "Developer Mode",
                    subtitle = "Enable advanced developer options",
                    checked = uiState.settings.developerMode,
                    onCheckedChange = viewModel::updateDeveloperMode
                )
            }

            // Reset button
            Button(
                onClick = viewModel::showResetDialog,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Defaults")
            }

            // App info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bootable Drive",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Clear cache dialog
        if (uiState.showClearCacheDialog) {
            AlertDialog(
                onDismissRequest = viewModel::hideClearCacheDialog,
                title = { Text("Clear Cache") },
                text = { Text("This will delete ${uiState.cacheUsedMB} MB of cached data. Downloaded images will not be affected.") },
                confirmButton = {
                    TextButton(onClick = viewModel::clearCache) {
                        Text("Clear")
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::hideClearCacheDialog) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Reset dialog
        if (uiState.showResetDialog) {
            AlertDialog(
                onDismissRequest = viewModel::hideResetDialog,
                title = { Text("Reset Settings") },
                text = { Text("This will reset all settings to their default values. Your images will not be affected.") },
                confirmButton = {
                    TextButton(
                        onClick = viewModel::resetToDefaults,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Reset")
                    }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::hideResetDialog) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
