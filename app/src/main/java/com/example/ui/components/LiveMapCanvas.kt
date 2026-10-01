package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSecondaryContainer

@Composable
fun LiveMapPreview(
  distanceText: String = "0.8 km away",
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseAlpha"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(140.dp)
      .clip(RoundedCornerShape(16.dp))
      .background(Color(0xFFE2E7FD))
      .clickable(onClick = onClick)
      .testTag("live_map_preview_tile")
  ) {
    // Stylized Street Map Canvas
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Background parks/blocks
      drawRect(
        color = Color(0xFFD4DEF8),
        topLeft = Offset(0f, 0f),
        size = size
      )

      // City blocks
      drawRoundRect(
        color = Color(0xFFDFE6FA),
        topLeft = Offset(w * 0.08f, h * 0.12f),
        size = androidx.compose.ui.geometry.Size(w * 0.28f, h * 0.35f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
      )
      drawRoundRect(
        color = Color(0xFFDFE6FA),
        topLeft = Offset(w * 0.42f, h * 0.12f),
        size = androidx.compose.ui.geometry.Size(w * 0.50f, h * 0.28f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
      )
      drawRoundRect(
        color = Color(0xFFDFE6FA),
        topLeft = Offset(w * 0.08f, h * 0.55f),
        size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.38f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
      )
      drawRoundRect(
        color = Color(0xFFDFE6FA),
        topLeft = Offset(w * 0.46f, h * 0.48f),
        size = androidx.compose.ui.geometry.Size(w * 0.46f, h * 0.44f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
      )

      // Roads
      val roadColor = Color(0xFFFFFFFF)
      // Horizontal main road
      drawLine(
        color = roadColor,
        start = Offset(0f, h * 0.48f),
        end = Offset(w, h * 0.48f),
        strokeWidth = 14f,
        cap = StrokeCap.Round
      )
      // Vertical main road
      drawLine(
        color = roadColor,
        start = Offset(w * 0.38f, 0f),
        end = Offset(w * 0.38f, h),
        strokeWidth = 12f,
        cap = StrokeCap.Round
      )
      // Secondary road
      drawLine(
        color = roadColor,
        start = Offset(w * 0.75f, 0f),
        end = Offset(w * 0.75f, h),
        strokeWidth = 8f
      )

      // Active GPS delivery route path
      val routePath = Path().apply {
        moveTo(w * 0.12f, h * 0.48f)
        lineTo(w * 0.38f, h * 0.48f)
        lineTo(w * 0.38f, h * 0.25f)
        lineTo(w * 0.65f, h * 0.25f)
      }
      drawPath(
        path = routePath,
        color = Color(0xFF0052C1),
        style = Stroke(
          width = 5f,
          cap = StrokeCap.Round
        )
      )

      // Destination house point
      drawCircle(
        color = Color(0xFFFE8116),
        radius = 8f,
        center = Offset(w * 0.12f, h * 0.48f)
      )
    }

    // Vehicle marker located at (65%, 25%)
    Box(
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(start = 220.dp, top = 26.dp)
        .size(34.dp)
        .clip(CircleShape)
        .background(KidooPrimary)
        .border(2.dp, Color.White, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.TwoWheeler,
        contentDescription = "Delivery Courier",
        tint = Color.White,
        modifier = Modifier.size(18.dp)
      )
    }

    // Bottom dark gradient overlay with live info
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(54.dp)
        .align(Alignment.BottomCenter)
        .background(
          Brush.verticalGradient(
            listOf(
              Color.Transparent,
              Color(0xCC151B2B)
            )
          )
        )
        .padding(horizontal = 12.dp, vertical = 8.dp),
      contentAlignment = Alignment.BottomCenter
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(KidooSecondaryContainer.copy(alpha = pulseAlpha))
          )
          Text(
            text = "Live GPS Active • $distanceText",
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 11.sp
            ),
            color = Color.White
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = "Tap to enlarge",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Medium
            ),
            color = Color.White
          )
        }
      }
    }
  }
}
