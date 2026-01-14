package com.bootabledrive.app.ui.screens.connectionwizard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bootabledrive.app.data.model.BootImage
import com.bootabledrive.app.data.model.BootMode
import com.bootabledrive.app.data.model.ImageType
import com.bootabledrive.app.ui.theme.SuccessColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionWizardScreen(
    onNavigateBack: () -> Unit,
    onConnectionComplete: () -> Unit,
    viewModel: ConnectionWizardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connection Wizard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress indicator
            WizardProgressIndicator(
                currentStep = uiState.currentStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (uiState.currentStep) {
                    WizardStep.PREPARATION_CHECK -> PreparationCheckStep(
                        isChecking = uiState.isCheckingDevice,
                        compatibility = uiState.deviceCompatibility,
                        onRetry = viewModel::retryCheck
                    )

                    WizardStep.IMAGE_SELECTION -> ImageSelectionStep(
                        images = uiState.availableImages,
                        selectedImage = uiState.selectedImage,
                        onSelectImage = viewModel::selectImage
                    )

                    WizardStep.CONNECTION_MODE -> ConnectionModeStep(
                        selectedImage = uiState.selectedImage,
                        selectedBootMode = uiState.selectedBootMode,
                        isConnecting = uiState.isConnecting,
                        onSelectBootMode = viewModel::selectBootMode,
                        onStartConnection = viewModel::startConnection
                    )

                    WizardStep.BOOT_INSTRUCTIONS -> BootInstructionsStep(
                        selectedImage = uiState.selectedImage,
                        bootMode = uiState.selectedBootMode,
                        onComplete = onConnectionComplete
                    )
                }
            }

            // Navigation buttons
            if (uiState.currentStep != WizardStep.BOOT_INSTRUCTIONS) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (uiState.currentStep != WizardStep.PREPARATION_CHECK) {
                        OutlinedButton(onClick = viewModel::previousStep) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    if (uiState.currentStep != WizardStep.CONNECTION_MODE) {
                        Button(
                            onClick = viewModel::nextStep,
                            enabled = viewModel.canProceed
                        ) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WizardProgressIndicator(
    currentStep: WizardStep,
    modifier: Modifier = Modifier
) {
    val steps = WizardStep.entries
    val currentIndex = steps.indexOf(currentStep)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, step ->
            val isCompleted = index < currentIndex
            val isCurrent = index == currentIndex

            // Step indicator
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> SuccessColor
                            isCurrent -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            // Connector line
            if (index < steps.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(
                            if (index < currentIndex) {
                                SuccessColor
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                )
            }
        }
    }
}

@Composable
fun PreparationCheckStep(
    isChecking: Boolean,
    compatibility: com.bootabledrive.app.data.model.DeviceCompatibility?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Preparation Check",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Verifying device compatibility and requirements",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (isChecking) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Checking device...")
        } else if (compatibility != null) {
            CheckItem(
                icon = Icons.Default.Security,
                title = "Root Access",
                isChecked = compatibility.hasRootAccess,
                description = if (compatibility.hasRootAccess) "Root access granted" else "Root access required"
            )

            Spacer(modifier = Modifier.height(12.dp))

            CheckItem(
                icon = Icons.Default.Usb,
                title = "USB Gadget Support",
                isChecked = compatibility.supportsUSBGadget,
                description = if (compatibility.supportsUSBGadget) "USB gadget mode supported" else "USB gadget not supported"
            )

            Spacer(modifier = Modifier.height(12.dp))

            CheckItem(
                icon = Icons.Default.Cable,
                title = "USB Cable Connection",
                isChecked = true,
                description = "Connect your phone to the PC via USB"
            )

            Spacer(modifier = Modifier.height(12.dp))

            CheckItem(
                icon = Icons.Default.PhoneAndroid,
                title = "Device Compatibility",
                isChecked = compatibility.isCompatible,
                description = "Kernel: ${compatibility.kernelVersion}"
            )

            if (!compatibility.isCompatible) {
                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Compatibility Issues",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        compatibility.incompatibilityReasons.forEach { reason ->
                            Text(
                                text = "• $reason",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Retry Check")
                }
            }
        }
    }
}

@Composable
fun CheckItem(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isChecked) {
                SuccessColor.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isChecked) SuccessColor else MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (isChecked) SuccessColor else MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun ImageSelectionStep(
    images: List<BootImage>,
    selectedImage: BootImage?,
    onSelectImage: (BootImage) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Select Boot Image",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose an image from your library to boot",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        images.forEach { image ->
            val isSelected = selectedImage?.id == image.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(
                            alpha = 0f
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectImage(image) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (image.type) {
                                ImageType.ISO -> Icons.Default.Album
                                ImageType.IMG -> Icons.Default.Storage
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = image.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${image.fileSizeFormatted} • ${image.typeDisplayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        if (images.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No bootable images available.\nAdd images first from the dashboard.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionModeStep(
    selectedImage: BootImage?,
    selectedBootMode: BootMode,
    isConnecting: Boolean,
    onSelectBootMode: (BootMode) -> Unit,
    onStartConnection: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Connection Mode",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Configure USB emulation settings",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Selected image preview
        if (selectedImage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Album,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = selectedImage.name,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = selectedImage.fileSizeFormatted,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Emulation mode selection
        Text(
            text = "Emulation Mode",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            SegmentedButton(
                selected = selectedBootMode == BootMode.USB,
                onClick = { onSelectBootMode(BootMode.USB) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Icon(
                    imageVector = Icons.Default.Usb,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("USB Drive")
            }
            SegmentedButton(
                selected = selectedBootMode == BootMode.CD,
                onClick = { onSelectBootMode(BootMode.CD) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("CD/DVD")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (selectedBootMode) {
                BootMode.USB -> "Emulates a USB flash drive. Best for modern systems and UEFI boot."
                BootMode.CD -> "Emulates a CD/DVD drive. Use for legacy BIOS systems or specific ISO requirements."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Connection instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Before connecting:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "1. Connect your phone to the PC via USB cable\n2. Make sure the PC is powered on\n3. Press 'Start Connection' when ready",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Start connection button
        Button(
            onClick = onStartConnection,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isConnecting && selectedImage != null
        ) {
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Starting Emulation...")
            } else {
                Icon(Icons.Default.Usb, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Connection")
            }
        }
    }
}

@Composable
fun BootInstructionsStep(
    selectedImage: BootImage?,
    bootMode: BootMode,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = SuccessColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "USB Emulation Active",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your phone is now emulating a ${if (bootMode == BootMode.USB) "USB drive" else "CD/DVD drive"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Boot instructions
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Boot your PC from this device:",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                InstructionItem(
                    number = 1,
                    icon = Icons.Default.Computer,
                    text = "Restart your PC or turn it on"
                )

                InstructionItem(
                    number = 2,
                    icon = Icons.Default.Computer,
                    text = "Press the boot menu key (usually F12, F2, Del, or Esc)"
                )

                InstructionItem(
                    number = 3,
                    icon = Icons.Default.Storage,
                    text = "Select your phone from the boot device list"
                )

                InstructionItem(
                    number = 4,
                    icon = Icons.Default.CheckCircle,
                    text = "Wait for ${selectedImage?.name ?: "the OS"} to boot"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Troubleshooting tips
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Troubleshooting Tips",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Device not showing? Try a different USB port\n• Check if UEFI/Legacy boot mode matches your image\n• Disable Secure Boot in BIOS if needed\n• Try both USB 2.0 and USB 3.0 ports",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}

@Composable
fun InstructionItem(
    number: Int,
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$number",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
