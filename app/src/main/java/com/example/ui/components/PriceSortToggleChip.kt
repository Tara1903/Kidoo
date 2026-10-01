package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PriceSortOrder
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooPrimaryFixed
import com.example.ui.theme.KidooSurfaceContainerLow

/**
 * Material 3 filter chip that allows users to toggle between ascending and descending price order.
 * Meets accessibility touch targets (>= 48dp).
 */
@Composable
fun PriceSortToggleChip(
  sortOrder: PriceSortOrder,
  onToggleSort: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isActive = sortOrder != PriceSortOrder.NONE

  val containerColor by animateColorAsState(
    targetValue = when (sortOrder) {
      PriceSortOrder.ASCENDING -> KidooPrimaryFixed
      PriceSortOrder.DESCENDING -> KidooPrimaryFixed
      PriceSortOrder.NONE -> KidooSurfaceContainerLow
    },
    animationSpec = tween(durationMillis = 200),
    label = "price_sort_bg"
  )

  val contentColor by animateColorAsState(
    targetValue = if (isActive) KidooPrimary else MaterialTheme.colorScheme.onSurface,
    animationSpec = tween(durationMillis = 200),
    label = "price_sort_fg"
  )

  val icon = when (sortOrder) {
    PriceSortOrder.ASCENDING -> Icons.Default.ArrowUpward
    PriceSortOrder.DESCENDING -> Icons.Default.ArrowDownward
    PriceSortOrder.NONE -> Icons.Default.SwapVert
  }

  Surface(
    shape = RoundedCornerShape(20.dp),
    color = containerColor,
    border = BorderStroke(1.dp, if (isActive) KidooPrimary else KidooCardBorder),
    shadowElevation = if (isActive) 2.dp else 0.dp,
    modifier = modifier
      .defaultMinSize(minHeight = 48.dp) // Accessibility standard: 48dp minimum touch target
      .clip(RoundedCornerShape(20.dp))
      .clickable(
        role = Role.Button,
        onClick = onToggleSort
      )
      .testTag("price_sort_toggle_chip")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = "Toggle price sort order, currently ${sortOrder.label}",
        tint = contentColor,
        modifier = Modifier.size(16.dp)
      )

      Text(
        text = sortOrder.label,
        style = MaterialTheme.typography.labelLarge.copy(
          fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
          fontSize = 13.sp
        ),
        color = contentColor
      )
    }
  }
}
