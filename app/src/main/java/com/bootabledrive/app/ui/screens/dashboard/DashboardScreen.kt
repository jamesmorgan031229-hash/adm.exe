package com.bootabledrive.app.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bootabledrive.app.ui.components.BatteryIndicator
import com.bootabledrive.app.ui.components.ConnectionStatusIndicator
import com.bootabledrive.app.ui.components.CurrentBootSection
import com.bootabledrive.app.ui.components.ImageCard
import com.bootabledrive.app.ui.components.ImageGridCard
import com.bootabledrive.app.ui.components.QuickActionButton
import com.bootabledrive.app.ui.components.RootStatusIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToImageManagement: (String) -> Unit,
    onNavigateToDownloadManager: () -> Unit,
    onNavigateToConnectionWizard: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Bootable Drive",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::refreshData) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(onClick = viewModel::toggleViewMode) {
                        Icon(
                            imageVector = if (uiState.isGridView) Icons.Default.List else Icons.Default.GridView,
                            contentDescription = "Toggle view"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status indicators
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ConnectionStatusIndicator(
                            status = uiState.deviceStatus.connectionStatus
                        )
                        RootStatusIndicator(
                            status = uiState.deviceStatus.rootStatus
                        )
                        BatteryIndicator(
                            percentage = uiState.deviceStatus.batteryPercentage,
                            isCharging = uiState.deviceStatus.isCharging
                        )
                    }
                }

                // Quick Actions
                item {
                    Column {
                        Text(
                            text = "Quick Actions",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            QuickActionButton(
                                icon = Icons.Default.Add,
                                label = "Add",
                                onClick = { /* TODO: Implement file picker */ }
                            )
                            QuickActionButton(
                                icon = Icons.Default.CloudDownload,
                                label = "Download",
                                onClick = onNavigateToDownloadManager
                            )
                            QuickActionButton(
                                icon = Icons.Default.Usb,
                                label = "Connect",
                                onClick = onNavigateToConnectionWizard
                            )
                        }
                    }
                }

                // Current Boot Section
                item {
                    CurrentBootSection(
                        currentImage = uiState.deviceStatus.currentBootImage,
                        emulationMode = uiState.deviceStatus.emulationMode,
                        isEmulating = uiState.isEmulating,
                        onStartEmulation = viewModel::startEmulation,
                        onStopEmulation = viewModel::stopEmulation
                    )
                }

                // Image Library Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Image Library",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${uiState.images.size} images",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Image Library
                if (uiState.isGridView) {
                    item {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((uiState.images.size / 2 + uiState.images.size % 2) * 180.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.images, key = { it.id }) { image ->
                                ImageGridCard(
                                    image = image,
                                    isSelected = uiState.selectedImage?.id == image.id,
                                    onClick = {
                                        viewModel.selectImage(image)
                                    },
                                    onLongClick = {
                                        onNavigateToImageManagement(image.id)
                                    }
                                )
                            }
                        }
                    }
                } else {
                    items(uiState.images, key = { it.id }) { image ->
                        ImageCard(
                            image = image,
                            onClick = {
                                viewModel.selectImage(image)
                            },
                            onEditClick = {
                                onNavigateToImageManagement(image.id)
                            },
                            onDeleteClick = {
                                viewModel.deleteImage(image.id)
                            },
                            onVerifyClick = {
                                viewModel.verifyImage(image.id)
                            }
                        )
                    }
                }

                // Empty state
                if (uiState.images.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No bootable images yet",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add an image to get started",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
