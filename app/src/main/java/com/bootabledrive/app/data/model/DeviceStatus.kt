package com.bootabledrive.app.data.model

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    EMULATING,
    ERROR
}

enum class RootStatus {
    NOT_CHECKED,
    CHECKING,
    ROOTED,
    NOT_ROOTED
}

data class DeviceStatus(
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val rootStatus: RootStatus = RootStatus.NOT_CHECKED,
    val batteryPercentage: Int = 100,
    val isCharging: Boolean = false,
    val currentBootImage: BootImage? = null,
    val emulationMode: BootMode? = null,
    val errorMessage: String? = null
)

data class DeviceCompatibility(
    val isCompatible: Boolean = false,
    val hasRootAccess: Boolean = false,
    val supportsUSBGadget: Boolean = false,
    val kernelVersion: String = "",
    val incompatibilityReasons: List<String> = emptyList()
)
