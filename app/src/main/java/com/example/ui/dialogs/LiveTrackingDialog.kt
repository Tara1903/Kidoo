package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSurfaceContainerHigh
import com.example.ui.theme.KidooSurfaceContainerLowest

@Composable
fun LiveTrackingDialog(
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val transition = rememberInfiniteTransition(label = "scooterMove")
  val progress by transition.animateFloat(
    initialValue = 0.1f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "progress"
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background),
      color = MaterialTheme.colorScheme.background
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Top app bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = CircleShape,
              color = KidooSurfaceContainerLowest,
              shadowElevation = 2.dp,
              modifier = Modifier.size(40.dp)
            ) {
              IconButton(onClick = onDismiss) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close",
                  tint = MaterialTheme.colorScheme.onSurface
                )
              }
            }
            Column {
              Text(
                text = "Live GPS Tracking",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Order KD-98241 • Indiranagar 100 Feet Rd",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Live Simulated Map Area
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(Color(0xFFE2E7FD))
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Background terrain & parks
            drawRect(Color(0xFFD3DEF8), Offset(0f, 0f), size)
            drawRoundRect(
              Color(0xFFC7D7F6),
              Offset(w * 0.1f, h * 0.1f),
              androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.25f),
              androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )
            drawRoundRect(
              Color(0xFFC7D7F6),
              Offset(w * 0.55f, h * 0.5f),
              androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.35f),
              androidx.compose.ui.geometry.CornerRadius(16f, 16f)
            )

            // Street lines
            drawLine(
              Color.White,
              Offset(0f, h * 0.45f),
              Offset(w, h * 0.45f),
              strokeWidth = 24f,
              cap = StrokeCap.Round
            )
            drawLine(
              Color.White,
              Offset(w * 0.45f, 0f),
              Offset(w * 0.45f, h),
              strokeWidth = 20f,
              cap = StrokeCap.Round
            )
            drawLine(
              Color.White,
              Offset(w * 0.8f, 0f),
              Offset(w * 0.8f, h),
              strokeWidth = 14f
            )

            // Route Polyline
            val route = Path().apply {
              moveTo(w * 0.15f, h * 0.45f)
              lineTo(w * 0.45f, h * 0.45f)
              lineTo(w * 0.45f, h * 0.75f)
              lineTo(w * 0.85f, h * 0.75f)
            }
            drawPath(
              path = route,
              color = Color(0xFF0052C1),
              style = Stroke(width = 8f, cap = StrokeCap.Round)
            )

            // Delivery destination (Home)
            drawCircle(Color(0xFFFE8116), radius = 14f, center = Offset(w * 0.15f, h * 0.45f))
            drawCircle(Color.White, radius = 6f, center = Offset(w * 0.15f, h * 0.45f))
          }

          // Animated scooter vehicle marker
          val posX = 80.dp + (200.dp * progress)
          val posY = 200.dp + (60.dp * progress)

          Box(
            modifier = Modifier
              .padding(start = posX, top = posY)
              .size(44.dp)
              .clip(CircleShape)
              .background(KidooPrimary)
              .border(3.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.TwoWheeler,
              contentDescription = "Courier",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          // Floating Status Card
          Surface(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .padding(top = 16.dp)
              .clip(RoundedCornerShape(20.dp)),
            color = KidooSurfaceContainerLowest,
            shadowElevation = 6.dp
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(KidooSecondaryContainer)
              )
              Text(
                text = "Rajesh is 0.8 km away • Arriving in 12 mins",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }

        // Bottom Details Card
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
          color = KidooSurfaceContainerLowest,
          shadowElevation = 8.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                AsyncImage(
                  model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjiQDW_AaDj2JAOxtEuA-rIPJLxrvKCVnhZ2aPRxi-R0ZLVDIYqQ155cswvKXu8ihXuRgdOpHbOJoftTeqX-pbKnwME1t7faP8QNTWtFXSXYF8W0qtqwGG5UQ3R_xuHWXoykGkZPzZhQA9EGPNqK32-UxgOyfkyKhnAFkmM1SNnQ5FSUPqa3An3KoOAbchIv1uHvI4SaOKRg8wNBPJ-a40fCcPVr4ntmRJ83VBvTmy0niOlsPUhFXTzA",
                  contentDescription = "Delivery Partner",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                )
                Column {
                  Text(
                    text = "Rajesh K.",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Delivery Hero • 4.9 ★ (1,420 deliveries)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "DELIVERY OTP",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "4921",
                  style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = KidooPrimary
                  )
                )
              }
            }

            Button(
              onClick = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                context.startActivity(intent)
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = KidooPrimary,
                contentColor = Color.White
              )
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(imageVector = Icons.Default.Call, contentDescription = null)
                Text(
                  text = "Call Delivery Partner (+91 98765 43210)",
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        }
      }
    }
  }
}
