package com.bootabledrive.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bootabledrive.app.ui.theme.SuccessColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.Usb,
            title = "Turn Your Phone Into a Bootable Drive",
            description = "Use your Android phone to boot any computer. Install operating systems, run recovery tools, or test new systems - all from your pocket."
        ),
        OnboardingPage(
            icon = Icons.Default.Storage,
            title = "Manage Your Boot Images",
            description = "Import ISO and IMG files from your device or download popular operating systems directly. Organize and manage all your bootable images in one place."
        ),
        OnboardingPage(
            icon = Icons.Default.CloudDownload,
            title = "Download Popular OS Images",
            description = "Access a library of popular Linux distributions, Windows installers, recovery tools, and security utilities. Download directly to your device."
        ),
        OnboardingPage(
            icon = Icons.Default.PhoneAndroid,
            title = "Easy Connection Wizard",
            description = "Our step-by-step wizard guides you through connecting to your PC and booting from the selected image. Simple and straightforward."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size + 1 }) // +1 for setup page
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (pagerState.currentPage < pages.size) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pages.size)
                        }
                    }
                ) {
                    Text("Skip")
                }
            }
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            if (page < pages.size) {
                OnboardingPageContent(page = pages[page])
            } else {
                SetupPage(onComplete = onOnboardingComplete)
            }
        }

        // Page indicators and navigation
        if (pagerState.currentPage < pages.size) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (index == pagerState.currentPage) 24.dp else 8.dp, 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (index == pagerState.currentPage) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation button
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SetupPage(onComplete: () -> Unit) {
    var isCheckingRoot by remember { mutableStateOf(true) }
    var hasRootAccess by remember { mutableStateOf(false) }
    var isCheckingCompatibility by remember { mutableStateOf(false) }
    var isCompatible by remember { mutableStateOf(false) }
    var setupComplete by remember { mutableStateOf(false) }

    // Simulate checks
    LaunchedEffect(Unit) {
        delay(1500)
        isCheckingRoot = false
        hasRootAccess = true // Simulated result

        isCheckingCompatibility = true
        delay(1000)
        isCheckingCompatibility = false
        isCompatible = true // Simulated result

        setupComplete = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Setting Up",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Checking device requirements",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Root check
        SetupCheckItem(
            icon = Icons.Default.Security,
            title = "Root Access",
            isChecking = isCheckingRoot,
            isSuccess = hasRootAccess && !isCheckingRoot,
            description = when {
                isCheckingRoot -> "Checking root access..."
                hasRootAccess -> "Root access available"
                else -> "Root access not available"
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Compatibility check
        SetupCheckItem(
            icon = Icons.Default.PhoneAndroid,
            title = "Device Compatibility",
            isChecking = isCheckingCompatibility,
            isSuccess = isCompatible && !isCheckingCompatibility,
            description = when {
                isCheckingRoot -> "Waiting..."
                isCheckingCompatibility -> "Checking USB gadget support..."
                isCompatible -> "Your device is compatible"
                else -> "Device may not be fully compatible"
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Complete button
        AnimatedVisibility(
            visible = setupComplete,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (hasRootAccess && isCompatible) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = SuccessColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "You're all set!",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Complete Setup")
                }

                if (!hasRootAccess || !isCompatible) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Some features may be limited",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SetupCheckItem(
    icon: ImageVector,
    title: String,
    isChecking: Boolean,
    isSuccess: Boolean,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isChecking -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                isSuccess -> SuccessColor.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
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
                tint = when {
                    isChecking -> MaterialTheme.colorScheme.onSurfaceVariant
                    isSuccess -> SuccessColor
                    else -> MaterialTheme.colorScheme.error
                }
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

            when {
                isChecking -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
                isSuccess -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessColor
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
