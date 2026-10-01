package com.example.ui.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSurfaceContainerLowest
import com.example.ui.theme.KidooTertiaryFixedDim
import kotlinx.coroutines.launch

@Composable
fun WonderWheelDialog(
  onDismiss: () -> Unit,
  onRewardClaimed: (Int) -> Unit
) {
  val scope = rememberCoroutineScope()
  val rotation = remember { Animatable(0f) }
  var isSpinning by remember { mutableStateOf(false) }
  var wonCoins by remember { mutableStateOf<Int?>(null) }

  val prizes = listOf(50, 100, 150, 75, 200, 120)
  val colors = listOf(
    Color(0xFF0052C1),
    Color(0xFFFE8116),
    Color(0xFFFABD14),
    Color(0xFF0CAB1C),
    Color(0xFF0569F1),
    Color(0xFF974800)
  )

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = KidooSurfaceContainerLowest,
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("wonder_wheel_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
            Icon(
              imageVector = Icons.Default.Casino,
              contentDescription = null,
              tint = KidooPrimary,
              modifier = Modifier.size(24.dp)
            )
            Text(
              text = "Spin the Wonder Wheel",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        }

        Text(
          text = "Spin to win daily KIDOO coins for checkout discounts!",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // The Wheel Visual
        Box(
          modifier = Modifier
            .size(220.dp)
            .padding(10.dp),
          contentAlignment = Alignment.Center
        ) {
          // Wheel Segments
          Canvas(
            modifier = Modifier
              .fillMaxSize()
              .rotate(rotation.value)
          ) {
            val sweep = 360f / prizes.size
            for (i in prizes.indices) {
              drawArc(
                color = colors[i],
                startAngle = i * sweep,
                sweepAngle = sweep,
                useCenter = true
              )
            }
          }

          // Center Hub
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(Color.White)
              .border(3.dp, KidooPrimary, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.MonetizationOn,
              contentDescription = null,
              tint = KidooTertiaryFixedDim,
              modifier = Modifier.size(28.dp)
            )
          }

          // Pointer at top
          Box(
            modifier = Modifier
              .align(Alignment.TopCenter)
              .size(16.dp)
              .clip(RoundedCornerShape(3.dp))
              .background(Color.Red)
          )
        }

        if (wonCoins != null) {
          Text(
            text = "🎉 You Won $wonCoins Coins!",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              color = KidooPrimary
            )
          )
        }

        Button(
          onClick = {
            if (!isSpinning) {
              isSpinning = true
              val targetRotation = rotation.value + 1440f + (0..360).random()
              scope.launch {
                rotation.animateTo(
                  targetValue = targetRotation,
                  animationSpec = tween(durationMillis = 2800, easing = FastOutSlowInEasing)
                )
                val prize = prizes.random()
                wonCoins = prize
                onRewardClaimed(prize)
                isSpinning = false
              }
            }
          },
          enabled = !isSpinning,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = KidooSecondaryContainer,
            contentColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("spin_wheel_button")
        ) {
          Text(
            text = if (isSpinning) "Spinning..." else if (wonCoins != null) "Spin Again!" else "SPIN NOW",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}
