package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KidooSecondaryContainer

val StarGoldColor = Color(0xFFFFB800)

/**
 * Visual display of a 5-star rating with optional rating score and review count.
 */
@Composable
fun DisplayStarRating(
  rating: Double,
  modifier: Modifier = Modifier,
  reviewCount: Int? = null,
  starSize: Dp = 13.dp,
  starColor: Color = StarGoldColor,
  showNumericScore: Boolean = true
) {
  Row(
    modifier = modifier.semantics(mergeDescendants = true) {
      contentDescription = "$rating stars out of 5${if (reviewCount != null) ", $reviewCount reviews" else ""}"
    },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    for (i in 1..5) {
      val icon = when {
        rating >= i -> Icons.Default.Star
        rating >= i - 0.75 -> Icons.Default.StarHalf
        else -> Icons.Outlined.StarOutline
      }
      val tint = if (rating >= i - 0.75) starColor else MaterialTheme.colorScheme.outlineVariant

      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(starSize)
      )
    }

    if (showNumericScore) {
      Text(
        text = String.format("%.1f", rating),
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(start = 3.dp)
      )
    }

    if (reviewCount != null) {
      Text(
        text = "($reviewCount)",
        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

/**
 * Interactive 5-star rating picker for submitting product reviews.
 * Adheres to Android accessibility standards with minimum 48dp touch targets.
 */
@Composable
fun InteractiveStarRating(
  selectedRating: Int,
  onRatingChanged: (Int) -> Unit,
  modifier: Modifier = Modifier,
  starSize: Dp = 32.dp,
  activeColor: Color = StarGoldColor
) {
  Row(
    modifier = modifier.testTag("interactive_star_rating_bar"),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    for (star in 1..5) {
      val isFilled = star <= selectedRating
      val icon = if (isFilled) Icons.Default.Star else Icons.Outlined.StarOutline
      val tint = if (isFilled) activeColor else MaterialTheme.colorScheme.outlineVariant

      Row(
        modifier = Modifier
          .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp) // Android 48dp minimum touch target
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            role = Role.Button,
            onClick = { onRatingChanged(star) }
          )
          .semantics {
            contentDescription = "Rate $star of 5 stars"
          }
          .testTag("star_rate_button_$star"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = tint,
          modifier = Modifier.size(starSize)
        )
      }
    }
  }
}
