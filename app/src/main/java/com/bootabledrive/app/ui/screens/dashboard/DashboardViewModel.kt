package com.bootabledrive.app.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bootabledrive.app.data.model.BootImage
import com.bootabledrive.app.data.model.BootMode
import com.bootabledrive.app.data.model.ConnectionStatus
import com.bootabledrive.app.data.model.DeviceStatus
import com.bootabledrive.app.data.model.ImageStatus
import com.bootabledrive.app.data.model.ImageType
import com.bootabledrive.app.data.model.RootStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val deviceStatus: DeviceStatus = DeviceStatus(),
    val images: List<BootImage> = emptyList(),
    val selectedImage: BootImage? = null,
    val isEmulating: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isGridView: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Simulate loading device status
            delay(500)

            // Sample data for demonstration
            val sampleImages = listOf(
                BootImage(
                    id = "1",
                    name = "Ubuntu 22.04 LTS",
                    description = "Ubuntu Desktop 22.04 Long Term Support",
                    filePath = "/storage/emulated/0/BootImages/ubuntu-22.04.iso",
                    fileSize = 4_700_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.USB,
                    status = ImageStatus.READY,
                    sha256Checksum = "a1b2c3d4e5f6..."
                ),
                BootImage(
                    id = "2",
                    name = "Windows 11 PE",
                    description = "Windows 11 Preinstallation Environment",
                    filePath = "/storage/emulated/0/BootImages/win11pe.iso",
                    fileSize = 5_200_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.CD,
                    status = ImageStatus.READY
                ),
                BootImage(
                    id = "3",
                    name = "Hiren's Boot CD",
                    description = "Recovery and diagnostic tools",
                    filePath = "/storage/emulated/0/BootImages/hirens.iso",
                    fileSize = 1_500_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.CD,
                    status = ImageStatus.READY
                ),
                BootImage(
                    id = "4",
                    name = "Arch Linux",
                    description = "Arch Linux installation media",
                    filePath = "/storage/emulated/0/BootImages/archlinux.iso",
                    fileSize = 850_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.USB,
                    status = ImageStatus.READY
                )
            )

            val deviceStatus = DeviceStatus(
                connectionStatus = ConnectionStatus.DISCONNECTED,
                rootStatus = RootStatus.ROOTED,
                batteryPercentage = 85,
                isCharging = false
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    deviceStatus = deviceStatus,
                    images = sampleImages
                )
            }
        }
    }

    fun selectImage(image: BootImage) {
        _uiState.update {
            it.copy(
                selectedImage = image,
                deviceStatus = it.deviceStatus.copy(
                    currentBootImage = image,
                    emulationMode = image.bootMode
                )
            )
        }
    }

    fun startEmulation() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    deviceStatus = it.deviceStatus.copy(
                        connectionStatus = ConnectionStatus.CONNECTING
                    )
                )
            }

            delay(1500) // Simulate connection

            _uiState.update {
                it.copy(
                    isEmulating = true,
                    deviceStatus = it.deviceStatus.copy(
                        connectionStatus = ConnectionStatus.EMULATING
                    )
                )
            }
        }
    }

    fun stopEmulation() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isEmulating = false,
                    deviceStatus = it.deviceStatus.copy(
                        connectionStatus = ConnectionStatus.DISCONNECTED
                    )
                )
            }
        }
    }

    fun deleteImage(imageId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                val newImages = state.images.filter { it.id != imageId }
                val newSelectedImage = if (state.selectedImage?.id == imageId) null else state.selectedImage
                state.copy(
                    images = newImages,
                    selectedImage = newSelectedImage,
                    deviceStatus = if (newSelectedImage == null) {
                        state.deviceStatus.copy(currentBootImage = null)
                    } else {
                        state.deviceStatus
                    }
                )
            }
        }
    }

    fun verifyImage(imageId: String) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    images = state.images.map { image ->
                        if (image.id == imageId) {
                            image.copy(status = ImageStatus.VERIFYING)
                        } else {
                            image
                        }
                    }
                )
            }

            delay(2000) // Simulate verification

            _uiState.update { state ->
                state.copy(
                    images = state.images.map { image ->
                        if (image.id == imageId) {
                            image.copy(status = ImageStatus.READY)
                        } else {
                            image
                        }
                    }
                )
            }
        }
    }

    fun toggleViewMode() {
        _uiState.update { it.copy(isGridView = !it.isGridView) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refreshData() {
        loadInitialData()
    }
}
