package com.bootabledrive.app.ui.screens.connectionwizard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bootabledrive.app.data.model.BootImage
import com.bootabledrive.app.data.model.BootMode
import com.bootabledrive.app.data.model.DeviceCompatibility
import com.bootabledrive.app.data.model.ImageStatus
import com.bootabledrive.app.data.model.ImageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class WizardStep {
    PREPARATION_CHECK,
    IMAGE_SELECTION,
    CONNECTION_MODE,
    BOOT_INSTRUCTIONS
}

data class ConnectionWizardUiState(
    val currentStep: WizardStep = WizardStep.PREPARATION_CHECK,
    val deviceCompatibility: DeviceCompatibility? = null,
    val isCheckingDevice: Boolean = false,
    val availableImages: List<BootImage> = emptyList(),
    val selectedImage: BootImage? = null,
    val selectedBootMode: BootMode = BootMode.USB,
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ConnectionWizardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ConnectionWizardUiState())
    val uiState: StateFlow<ConnectionWizardUiState> = _uiState.asStateFlow()

    init {
        checkDeviceCompatibility()
        loadAvailableImages()
    }

    private fun checkDeviceCompatibility() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingDevice = true) }

            delay(1500) // Simulate device check

            val compatibility = DeviceCompatibility(
                isCompatible = true,
                hasRootAccess = true,
                supportsUSBGadget = true,
                kernelVersion = "5.10.0-android12"
            )

            _uiState.update {
                it.copy(
                    isCheckingDevice = false,
                    deviceCompatibility = compatibility
                )
            }
        }
    }

    private fun loadAvailableImages() {
        viewModelScope.launch {
            val images = listOf(
                BootImage(
                    id = "1",
                    name = "Ubuntu 22.04 LTS",
                    description = "Ubuntu Desktop",
                    filePath = "/storage/emulated/0/BootImages/ubuntu-22.04.iso",
                    fileSize = 4_700_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.USB,
                    status = ImageStatus.READY
                ),
                BootImage(
                    id = "2",
                    name = "Windows 11 PE",
                    description = "Windows PE",
                    filePath = "/storage/emulated/0/BootImages/win11pe.iso",
                    fileSize = 5_200_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.CD,
                    status = ImageStatus.READY
                ),
                BootImage(
                    id = "3",
                    name = "Hiren's Boot CD",
                    description = "Recovery tools",
                    filePath = "/storage/emulated/0/BootImages/hirens.iso",
                    fileSize = 1_500_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.CD,
                    status = ImageStatus.READY
                )
            )

            _uiState.update { it.copy(availableImages = images) }
        }
    }

    fun nextStep() {
        _uiState.update { state ->
            val nextStep = when (state.currentStep) {
                WizardStep.PREPARATION_CHECK -> WizardStep.IMAGE_SELECTION
                WizardStep.IMAGE_SELECTION -> WizardStep.CONNECTION_MODE
                WizardStep.CONNECTION_MODE -> WizardStep.BOOT_INSTRUCTIONS
                WizardStep.BOOT_INSTRUCTIONS -> WizardStep.BOOT_INSTRUCTIONS
            }
            state.copy(currentStep = nextStep)
        }
    }

    fun previousStep() {
        _uiState.update { state ->
            val prevStep = when (state.currentStep) {
                WizardStep.PREPARATION_CHECK -> WizardStep.PREPARATION_CHECK
                WizardStep.IMAGE_SELECTION -> WizardStep.PREPARATION_CHECK
                WizardStep.CONNECTION_MODE -> WizardStep.IMAGE_SELECTION
                WizardStep.BOOT_INSTRUCTIONS -> WizardStep.CONNECTION_MODE
            }
            state.copy(currentStep = prevStep)
        }
    }

    fun selectImage(image: BootImage) {
        _uiState.update {
            it.copy(
                selectedImage = image,
                selectedBootMode = image.bootMode
            )
        }
    }

    fun selectBootMode(mode: BootMode) {
        _uiState.update { it.copy(selectedBootMode = mode) }
    }

    fun startConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true) }

            delay(2000) // Simulate USB gadget setup

            _uiState.update {
                it.copy(
                    isConnecting = false,
                    isConnected = true
                )
            }

            // Move to instructions step
            nextStep()
        }
    }

    fun retryCheck() {
        checkDeviceCompatibility()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    val canProceed: Boolean
        get() = when (_uiState.value.currentStep) {
            WizardStep.PREPARATION_CHECK -> {
                _uiState.value.deviceCompatibility?.isCompatible == true
            }
            WizardStep.IMAGE_SELECTION -> {
                _uiState.value.selectedImage != null
            }
            WizardStep.CONNECTION_MODE -> true
            WizardStep.BOOT_INSTRUCTIONS -> false
        }
}
