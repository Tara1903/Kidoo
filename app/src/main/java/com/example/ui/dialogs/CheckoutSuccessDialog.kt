package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.OrderData
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSuccessGreen
import com.example.ui.theme.KidooSurfaceContainerLow
import com.example.ui.theme.KidooSurfaceContainerLowest

@Composable
fun CheckoutSuccessDialog(
  order: OrderData?,
  onViewOrder: () -> Unit
) {
  Dialog(onDismissRequest = onViewOrder) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = KidooSurfaceContainerLowest,
      shadowElevation = 8.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("checkout_success_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Box(
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(KidooSuccessGreen.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(KidooSuccessGreen),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Success",
              tint = Color.White,
              modifier = Modifier.size(26.dp)
            )
          }
        }

        Text(
          text = "Order Placed Successfully!",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        if (order != null) {
          Text(
            text = order.orderNumber,
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = KidooPrimary
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = KidooSurfaceContainerLow,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocalShipping,
              contentDescription = null,
              tint = KidooSecondaryContainer,
              modifier = Modifier.size(20.dp)
            )
            Column {
              Text(
                text = "Guaranteed Next-Day Delivery",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Delivering to Indiranagar, Bengaluru (560034)",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Button(
          onClick = onViewOrder,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = KidooPrimary,
            contentColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("view_order_btn")
        ) {
          Text(
            text = "Track My Order",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}
