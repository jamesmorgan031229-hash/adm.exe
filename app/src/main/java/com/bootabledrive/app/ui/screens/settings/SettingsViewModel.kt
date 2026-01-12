package com.bootabledrive.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bootabledrive.app.data.model.AppSettings
import com.bootabledrive.app.data.model.AppTheme
import com.bootabledrive.app.data.model.BootMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val settings: AppSettings = AppSettings(),
    val storageUsedMB: Long = 2500,
    val storageTotalMB: Long = 10000,
    val cacheUsedMB: Long = 150,
    val isLoading: Boolean = false,
    val showClearCacheDialog: Boolean = false,
    val showResetDialog: Boolean = false,
    val successMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    // General Settings
    fun updateTheme(theme: AppTheme) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(theme = theme))
        }
    }

    fun updateAutoConnect(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(autoConnectOnUSB = enabled))
        }
    }

    fun updateLaunchOnStartup(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(launchOnStartup = enabled))
        }
    }

    // Boot Options
    fun updateDefaultEmulationMode(mode: BootMode) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(defaultEmulationMode = mode))
        }
    }

    fun updateBootTimeout(seconds: Int) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(bootTimeoutSeconds = seconds))
        }
    }

    fun updateAutoSelectLastImage(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(autoSelectLastImage = enabled))
        }
    }

    // Storage Management
    fun updateAutoVerifyImages(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(autoVerifyImages = enabled))
        }
    }

    fun showClearCacheDialog() {
        _uiState.update { it.copy(showClearCacheDialog = true) }
    }

    fun hideClearCacheDialog() {
        _uiState.update { it.copy(showClearCacheDialog = false) }
    }

    fun clearCache() {
        viewModelScope.launch {
            _uiState.update { it.copy(showClearCacheDialog = false, cacheUsedMB = 0) }
            _uiState.update { it.copy(successMessage = "Cache cleared successfully") }
        }
    }

    // Advanced
    fun updateKernelCheck(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(enableKernelCheck = enabled))
        }
    }

    fun updateUSBDebugging(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(enableUSBDebugging = enabled))
        }
    }

    fun updateLogs(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(enableLogs = enabled))
        }
    }

    fun updateDeveloperMode(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(settings = state.settings.copy(developerMode = enabled))
        }
    }

    // Reset
    fun showResetDialog() {
        _uiState.update { it.copy(showResetDialog = true) }
    }

    fun hideResetDialog() {
        _uiState.update { it.copy(showResetDialog = false) }
    }

    fun resetToDefaults() {
        _uiState.update {
            it.copy(
                settings = AppSettings(),
                showResetDialog = false,
                successMessage = "Settings reset to defaults"
            )
        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}
