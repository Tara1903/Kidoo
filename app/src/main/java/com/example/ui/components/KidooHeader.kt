package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.KidooError
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSurfaceContainerLowest

@Composable
fun KidooHeader(
  avatarUrl: String,
  unreadNotifications: Int = 2,
  onNotificationClick: () -> Unit = {},
  onWishlistClick: () -> Unit = {},
  onProfileClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = KidooSurfaceContainerLowest.copy(alpha = 0.95f),
    shadowElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
      // Status bar indicators row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "9:41",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.SignalCellular4Bar,
            contentDescription = "Signal",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Icon(
            imageVector = Icons.Default.Wifi,
            contentDescription = "Wi-Fi",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Icon(
            imageVector = Icons.Default.BatteryFull,
            contentDescription = "Battery",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Main header bar with Logo, Address, and Actions
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Logo
        Text(
          text = "KIDOO",
          style = MaterialTheme.typography.displayLarge.copy(
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.03).sp
          ),
          color = KidooPrimary,
          modifier = Modifier.testTag("kidoo_logo_text")
        )

        // Delivery location selector
        Column(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 10.dp)
            .clickable { /* Address selection */ },
          verticalArrangement = Arrangement.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Deliver to ",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "560034",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 1.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = "Location",
              tint = KidooSecondaryContainer,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "Indiranagar, Bengaluru",
              style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
              ),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Icon(
              imageVector = Icons.Default.KeyboardArrowDown,
              contentDescription = "Select address",
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(14.dp)
            )
          }
        }

        // Action Icons
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Notification with badge
          Box(contentAlignment = Alignment.TopEnd) {
            IconButton(
              onClick = onNotificationClick,
              modifier = Modifier
                .size(38.dp)
                .testTag("header_notifications_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(22.dp)
              )
            }
            if (unreadNotifications > 0) {
              Box(
                modifier = Modifier
                  .padding(top = 6.dp, end = 6.dp)
                  .size(15.dp)
                  .clip(CircleShape)
                  .background(KidooError),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "$unreadNotifications",
                  color = Color.White,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          // Wishlist
          IconButton(
            onClick = onWishlistClick,
            modifier = Modifier
              .size(38.dp)
              .testTag("header_wishlist_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Favorite,
              contentDescription = "Wishlist",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(22.dp)
            )
          }

          // Profile Avatar
          AsyncImage(
            model = avatarUrl,
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
              .clickable(onClick = onProfileClick)
              .testTag("header_profile_avatar")
          )
        }
      }
    }
  }
}
