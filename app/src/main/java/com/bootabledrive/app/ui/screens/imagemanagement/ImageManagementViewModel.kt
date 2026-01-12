package com.bootabledrive.app.ui.screens.imagemanagement

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bootabledrive.app.data.model.BootImage
import com.bootabledrive.app.data.model.BootMode
import com.bootabledrive.app.data.model.ImageStatus
import com.bootabledrive.app.data.model.ImageType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class ImageManagementUiState(
    val image: BootImage? = null,
    val isLoading: Boolean = false,
    val isVerifying: Boolean = false,
    val verificationResult: VerificationResult? = null,
    val showDeleteConfirmation: Boolean = false,
    val showAdvancedOptions: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class VerificationResult(
    val isValid: Boolean,
    val expectedChecksum: String?,
    val actualChecksum: String
)

@HiltViewModel
class ImageManagementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val imageId: String = savedStateHandle["imageId"] ?: ""

    private val _uiState = MutableStateFlow(ImageManagementUiState())
    val uiState: StateFlow<ImageManagementUiState> = _uiState.asStateFlow()

    init {
        loadImage()
    }

    private fun loadImage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            delay(300)

            // Sample data - in real app, this would come from repository
            val image = when (imageId) {
                "1" -> BootImage(
                    id = "1",
                    name = "Ubuntu 22.04 LTS",
                    description = "Ubuntu Desktop 22.04 Long Term Support - A complete desktop operating system with excellent hardware support.",
                    filePath = "/storage/emulated/0/BootImages/ubuntu-22.04.iso",
                    fileSize = 4_700_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.USB,
                    status = ImageStatus.READY,
                    sha256Checksum = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6",
                    creationDate = System.currentTimeMillis() - 86400000 * 7
                )
                "2" -> BootImage(
                    id = "2",
                    name = "Windows 11 PE",
                    description = "Windows 11 Preinstallation Environment for system recovery and installation.",
                    filePath = "/storage/emulated/0/BootImages/win11pe.iso",
                    fileSize = 5_200_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.CD,
                    status = ImageStatus.READY,
                    creationDate = System.currentTimeMillis() - 86400000 * 14
                )
                "3" -> BootImage(
                    id = "3",
                    name = "Hiren's Boot CD",
                    description = "Recovery and diagnostic tools collection.",
                    filePath = "/storage/emulated/0/BootImages/hirens.iso",
                    fileSize = 1_500_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.CD,
                    status = ImageStatus.READY,
                    creationDate = System.currentTimeMillis() - 86400000 * 30
                )
                "4" -> BootImage(
                    id = "4",
                    name = "Arch Linux",
                    description = "Arch Linux installation media - A lightweight and flexible Linux distribution.",
                    filePath = "/storage/emulated/0/BootImages/archlinux.iso",
                    fileSize = 850_000_000L,
                    type = ImageType.ISO,
                    bootMode = BootMode.USB,
                    status = ImageStatus.READY,
                    creationDate = System.currentTimeMillis() - 86400000 * 3
                )
                else -> null
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    image = image,
                    errorMessage = if (image == null) "Image not found" else null
                )
            }
        }
    }

    fun setAsBootImage() {
        viewModelScope.launch {
            _uiState.update { it.copy(successMessage = "Image set as boot target") }
        }
    }

    fun verifyIntegrity() {
        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true, verificationResult = null) }

            delay(2000) // Simulate verification

            val image = _uiState.value.image
            val result = VerificationResult(
                isValid = true,
                expectedChecksum = image?.sha256Checksum,
                actualChecksum = image?.sha256Checksum ?: "computed_checksum_here"
            )

            _uiState.update {
                it.copy(
                    isVerifying = false,
                    verificationResult = result
                )
            }
        }
    }

    fun updateBootMode(mode: BootMode) {
        _uiState.update { state ->
            state.copy(
                image = state.image?.copy(bootMode = mode)
            )
        }
    }

    fun updateBootParameters(parameters: String) {
        _uiState.update { state ->
            state.copy(
                image = state.image?.copy(bootParameters = parameters)
            )
        }
    }

    fun togglePersistence(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(
                image = state.image?.copy(persistenceEnabled = enabled)
            )
        }
    }

    fun toggleAdvancedOptions() {
        _uiState.update { it.copy(showAdvancedOptions = !it.showAdvancedOptions) }
    }

    fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    fun deleteImage(onDeleted: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            delay(500)
            onDeleted()
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
