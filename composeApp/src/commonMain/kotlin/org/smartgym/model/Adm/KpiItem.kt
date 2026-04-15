package org.smartgym.model.Adm

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class KpiItem(
    val title: String,
    val subtitle: String,
    val value: String,
    val badgeValue: String,
    val icon: ImageVector,
    val iconColor: Color,
    val isPositive: Boolean = true
)

data class GradientItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val gradient: Brush
)