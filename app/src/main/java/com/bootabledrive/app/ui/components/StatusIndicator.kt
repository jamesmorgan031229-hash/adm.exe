package com.bootabledrive.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bootabledrive.app.data.model.ConnectionStatus
import com.bootabledrive.app.data.model.ImageStatus
import com.bootabledrive.app.data.model.RootStatus
import com.bootabledrive.app.ui.theme.ErrorColor
import com.bootabledrive.app.ui.theme.SuccessColor
import com.bootabledrive.app.ui.theme.WarningColor
import com.bootabledrive.app.ui.theme.SecondaryMain

@Composable
fun ConnectionStatusIndicator(
    status: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status) {
        ConnectionStatus.DISCONNECTED -> ErrorColor to "Disconnected"
        ConnectionStatus.CONNECTING -> WarningColor to "Connecting..."
        ConnectionStatus.CONNECTED -> SuccessColor to "Connected"
        ConnectionStatus.EMULATING -> SecondaryMain to "Emulating"
        ConnectionStatus.ERROR -> ErrorColor to "Error"
    }

    StatusChip(
        color = color,
        text = text,
        modifier = modifier
    )
}

@Composable
fun RootStatusIndicator(
    status: RootStatus,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status) {
        RootStatus.NOT_CHECKED -> Color.Gray to "Root: Unknown"
        RootStatus.CHECKING -> WarningColor to "Checking Root..."
        RootStatus.ROOTED -> SuccessColor to "Root: Granted"
        RootStatus.NOT_ROOTED -> ErrorColor to "Root: Denied"
    }

    StatusChip(
        color = color,
        text = text,
        modifier = modifier
    )
}

@Composable
fun ImageStatusIndicator(
    status: ImageStatus,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status) {
        ImageStatus.READY -> SuccessColor to "Ready"
        ImageStatus.VERIFYING -> WarningColor to "Verifying..."
        ImageStatus.ACTIVE -> SecondaryMain to "Active"
        ImageStatus.ERROR -> ErrorColor to "Error"
        ImageStatus.IMPORTING -> WarningColor to "Importing..."
    }

    StatusChip(
        color = color,
        text = text,
        modifier = modifier
    )
}

@Composable
fun StatusChip(
    color: Color,
    text: String,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(300),
        label = "status_color"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(animatedColor.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(animatedColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = animatedColor
        )
    }
}

@Composable
fun BatteryIndicator(
    percentage: Int,
    isCharging: Boolean,
    modifier: Modifier = Modifier
) {
    val color = when {
        percentage <= 20 -> ErrorColor
        percentage <= 50 -> WarningColor
        else -> SuccessColor
    }

    val text = if (isCharging) "$percentage% (Charging)" else "$percentage%"

    StatusChip(
        color = color,
        text = text,
        modifier = modifier
    )
}
