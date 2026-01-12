package com.bootabledrive.app.data.model

import java.util.UUID

enum class ImageType {
    ISO,
    IMG
}

enum class BootMode {
    USB,
    CD
}

enum class ImageStatus {
    READY,
    VERIFYING,
    ACTIVE,
    ERROR,
    IMPORTING
}

data class BootImage(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val filePath: String,
    val fileSize: Long,
    val type: ImageType,
    val bootMode: BootMode = BootMode.USB,
    val status: ImageStatus = ImageStatus.READY,
    val sha256Checksum: String? = null,
    val creationDate: Long = System.currentTimeMillis(),
    val thumbnailPath: String? = null,
    val bootParameters: String = "",
    val persistenceEnabled: Boolean = false
) {
    val fileSizeFormatted: String
        get() = formatFileSize(fileSize)

    val typeDisplayName: String
        get() = type.name

    val bootModeDisplayName: String
        get() = when (bootMode) {
            BootMode.USB -> "USB Drive"
            BootMode.CD -> "CD/DVD"
        }

    companion object {
        fun formatFileSize(size: Long): String {
            val kb = 1024.0
            val mb = kb * 1024
            val gb = mb * 1024

            return when {
                size >= gb -> String.format("%.2f GB", size / gb)
                size >= mb -> String.format("%.2f MB", size / mb)
                size >= kb -> String.format("%.2f KB", size / kb)
                else -> "$size B"
            }
        }
    }
}
