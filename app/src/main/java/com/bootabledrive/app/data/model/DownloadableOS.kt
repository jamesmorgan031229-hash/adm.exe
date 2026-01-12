package com.bootabledrive.app.data.model

import java.util.UUID

enum class OSCategory {
    LINUX_DISTRIBUTION,
    WINDOWS_INSTALLATION,
    RECOVERY_TOOLS,
    ANTIVIRUS_SECURITY,
    CUSTOM_BETA
}

enum class DownloadStatus {
    NOT_STARTED,
    DOWNLOADING,
    PAUSED,
    COMPLETED,
    FAILED
}

data class DownloadableOS(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val version: String,
    val description: String,
    val category: OSCategory,
    val fileSize: Long,
    val downloadUrl: String,
    val iconUrl: String? = null,
    val sha256Checksum: String? = null
) {
    val fileSizeFormatted: String
        get() = BootImage.formatFileSize(fileSize)

    val categoryDisplayName: String
        get() = when (category) {
            OSCategory.LINUX_DISTRIBUTION -> "Linux Distributions"
            OSCategory.WINDOWS_INSTALLATION -> "Windows Installation"
            OSCategory.RECOVERY_TOOLS -> "Recovery Tools"
            OSCategory.ANTIVIRUS_SECURITY -> "Antivirus/Security"
            OSCategory.CUSTOM_BETA -> "Custom/Beta"
        }
}

data class DownloadProgress(
    val osId: String,
    val status: DownloadStatus = DownloadStatus.NOT_STARTED,
    val progress: Float = 0f,
    val downloadedBytes: Long = 0,
    val totalBytes: Long = 0,
    val errorMessage: String? = null
)
