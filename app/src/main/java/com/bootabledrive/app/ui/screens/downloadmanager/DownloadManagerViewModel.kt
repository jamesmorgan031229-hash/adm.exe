package com.bootabledrive.app.ui.screens.downloadmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bootabledrive.app.data.model.DownloadProgress
import com.bootabledrive.app.data.model.DownloadStatus
import com.bootabledrive.app.data.model.DownloadableOS
import com.bootabledrive.app.data.model.OSCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DownloadManagerUiState(
    val searchQuery: String = "",
    val selectedCategory: OSCategory? = null,
    val availableOS: List<DownloadableOS> = emptyList(),
    val filteredOS: List<DownloadableOS> = emptyList(),
    val downloads: Map<String, DownloadProgress> = emptyMap(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class DownloadManagerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DownloadManagerUiState())
    val uiState: StateFlow<DownloadManagerUiState> = _uiState.asStateFlow()

    init {
        loadAvailableOS()
    }

    private fun loadAvailableOS() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            delay(500)

            val osList = listOf(
                // Linux Distributions
                DownloadableOS(
                    id = "ubuntu-22.04",
                    name = "Ubuntu",
                    version = "22.04 LTS",
                    description = "Popular Linux distribution with great hardware support and user-friendly interface.",
                    category = OSCategory.LINUX_DISTRIBUTION,
                    fileSize = 4_700_000_000L,
                    downloadUrl = "https://releases.ubuntu.com/22.04/ubuntu-22.04-desktop-amd64.iso",
                    sha256Checksum = "a1b2c3d4..."
                ),
                DownloadableOS(
                    id = "fedora-39",
                    name = "Fedora Workstation",
                    version = "39",
                    description = "Cutting-edge Linux distribution with latest technologies and GNOME desktop.",
                    category = OSCategory.LINUX_DISTRIBUTION,
                    fileSize = 2_100_000_000L,
                    downloadUrl = "https://download.fedoraproject.org/pub/fedora/linux/releases/39/Workstation/x86_64/iso/"
                ),
                DownloadableOS(
                    id = "linuxmint-21.3",
                    name = "Linux Mint",
                    version = "21.3 Virginia",
                    description = "Elegant, easy to use Linux distribution based on Ubuntu.",
                    category = OSCategory.LINUX_DISTRIBUTION,
                    fileSize = 2_800_000_000L,
                    downloadUrl = "https://linuxmint.com/edition.php?id=311"
                ),
                DownloadableOS(
                    id = "arch-latest",
                    name = "Arch Linux",
                    version = "Latest",
                    description = "Lightweight and flexible Linux distribution for advanced users.",
                    category = OSCategory.LINUX_DISTRIBUTION,
                    fileSize = 850_000_000L,
                    downloadUrl = "https://archlinux.org/download/"
                ),
                DownloadableOS(
                    id = "debian-12",
                    name = "Debian",
                    version = "12 Bookworm",
                    description = "Universal operating system known for stability and security.",
                    category = OSCategory.LINUX_DISTRIBUTION,
                    fileSize = 3_700_000_000L,
                    downloadUrl = "https://www.debian.org/download"
                ),

                // Windows Installation
                DownloadableOS(
                    id = "win11-pe",
                    name = "Windows 11 PE",
                    version = "Build 22H2",
                    description = "Windows Preinstallation Environment for system deployment and recovery.",
                    category = OSCategory.WINDOWS_INSTALLATION,
                    fileSize = 5_200_000_000L,
                    downloadUrl = "custom_build"
                ),
                DownloadableOS(
                    id = "win10-pe",
                    name = "Windows 10 PE",
                    version = "Build 21H2",
                    description = "Windows 10 Preinstallation Environment for legacy systems.",
                    category = OSCategory.WINDOWS_INSTALLATION,
                    fileSize = 4_800_000_000L,
                    downloadUrl = "custom_build"
                ),

                // Recovery Tools
                DownloadableOS(
                    id = "hirens-boot",
                    name = "Hiren's Boot CD PE",
                    version = "1.0.2",
                    description = "Comprehensive utility toolkit for system recovery and diagnostics.",
                    category = OSCategory.RECOVERY_TOOLS,
                    fileSize = 1_500_000_000L,
                    downloadUrl = "https://www.hirensbootcd.org/download/"
                ),
                DownloadableOS(
                    id = "systemrescue",
                    name = "SystemRescue",
                    version = "10.02",
                    description = "Linux-based system rescue toolkit for repairing systems and data recovery.",
                    category = OSCategory.RECOVERY_TOOLS,
                    fileSize = 750_000_000L,
                    downloadUrl = "https://www.system-rescue.org/Download/"
                ),
                DownloadableOS(
                    id = "clonezilla",
                    name = "Clonezilla",
                    version = "3.1.1-27",
                    description = "Disk imaging and cloning solution for backups and deployments.",
                    category = OSCategory.RECOVERY_TOOLS,
                    fileSize = 450_000_000L,
                    downloadUrl = "https://clonezilla.org/downloads.php"
                ),
                DownloadableOS(
                    id = "gparted",
                    name = "GParted Live",
                    version = "1.5.0-6",
                    description = "Partition editor for managing disk partitions.",
                    category = OSCategory.RECOVERY_TOOLS,
                    fileSize = 500_000_000L,
                    downloadUrl = "https://gparted.org/download.php"
                ),

                // Antivirus/Security
                DownloadableOS(
                    id = "kaspersky-rescue",
                    name = "Kaspersky Rescue Disk",
                    version = "18",
                    description = "Bootable antivirus solution for scanning infected systems.",
                    category = OSCategory.ANTIVIRUS_SECURITY,
                    fileSize = 600_000_000L,
                    downloadUrl = "https://support.kaspersky.com/krd18"
                ),
                DownloadableOS(
                    id = "bitdefender-rescue",
                    name = "Bitdefender Rescue CD",
                    version = "4.1.1",
                    description = "Free bootable antivirus scanner from Bitdefender.",
                    category = OSCategory.ANTIVIRUS_SECURITY,
                    fileSize = 700_000_000L,
                    downloadUrl = "https://www.bitdefender.com/support/how-to-create-a-bitdefender-rescue-cd-627.html"
                ),
                DownloadableOS(
                    id = "tails",
                    name = "Tails",
                    version = "6.0",
                    description = "Privacy-focused OS that routes all traffic through Tor network.",
                    category = OSCategory.ANTIVIRUS_SECURITY,
                    fileSize = 1_200_000_000L,
                    downloadUrl = "https://tails.net/install/"
                ),

                // Custom/Beta
                DownloadableOS(
                    id = "ventoy",
                    name = "Ventoy",
                    version = "1.0.96",
                    description = "Create bootable USB drive for multiple ISO files.",
                    category = OSCategory.CUSTOM_BETA,
                    fileSize = 20_000_000L,
                    downloadUrl = "https://www.ventoy.net/en/download.html"
                ),
                DownloadableOS(
                    id = "memtest86",
                    name = "MemTest86+",
                    version = "7.0",
                    description = "Memory testing utility for diagnosing RAM issues.",
                    category = OSCategory.CUSTOM_BETA,
                    fileSize = 30_000_000L,
                    downloadUrl = "https://www.memtest.org/"
                )
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    availableOS = osList,
                    filteredOS = osList
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            val filtered = filterOS(state.availableOS, query, state.selectedCategory)
            state.copy(searchQuery = query, filteredOS = filtered)
        }
    }

    fun selectCategory(category: OSCategory?) {
        _uiState.update { state ->
            val filtered = filterOS(state.availableOS, state.searchQuery, category)
            state.copy(selectedCategory = category, filteredOS = filtered)
        }
    }

    private fun filterOS(
        osList: List<DownloadableOS>,
        query: String,
        category: OSCategory?
    ): List<DownloadableOS> {
        return osList.filter { os ->
            val matchesQuery = query.isEmpty() ||
                    os.name.contains(query, ignoreCase = true) ||
                    os.description.contains(query, ignoreCase = true) ||
                    os.version.contains(query, ignoreCase = true)

            val matchesCategory = category == null || os.category == category

            matchesQuery && matchesCategory
        }
    }

    fun startDownload(osId: String) {
        viewModelScope.launch {
            val os = _uiState.value.availableOS.find { it.id == osId } ?: return@launch

            _uiState.update { state ->
                state.copy(
                    downloads = state.downloads + (osId to DownloadProgress(
                        osId = osId,
                        status = DownloadStatus.DOWNLOADING,
                        totalBytes = os.fileSize
                    ))
                )
            }

            // Simulate download progress
            val totalSize = os.fileSize
            var downloaded = 0L
            val chunkSize = totalSize / 20

            while (downloaded < totalSize) {
                delay(500)
                downloaded = (downloaded + chunkSize).coerceAtMost(totalSize)
                val progress = downloaded.toFloat() / totalSize

                _uiState.update { state ->
                    state.copy(
                        downloads = state.downloads + (osId to DownloadProgress(
                            osId = osId,
                            status = if (downloaded >= totalSize) DownloadStatus.COMPLETED else DownloadStatus.DOWNLOADING,
                            progress = progress,
                            downloadedBytes = downloaded,
                            totalBytes = totalSize
                        ))
                    )
                }
            }
        }
    }

    fun pauseDownload(osId: String) {
        _uiState.update { state ->
            val current = state.downloads[osId] ?: return@update state
            state.copy(
                downloads = state.downloads + (osId to current.copy(status = DownloadStatus.PAUSED))
            )
        }
    }

    fun resumeDownload(osId: String) {
        startDownload(osId)
    }

    fun cancelDownload(osId: String) {
        _uiState.update { state ->
            state.copy(downloads = state.downloads - osId)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    val categories = OSCategory.entries.toList()
}
