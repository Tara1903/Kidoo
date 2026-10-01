package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AssignmentReturn
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.UserProfile
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooError
import com.example.ui.theme.KidooErrorContainer
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooPrimaryFixed
import com.example.ui.theme.KidooSecondary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSecondaryFixed
import com.example.ui.theme.KidooSurfaceContainer
import com.example.ui.theme.KidooSurfaceContainerHigh
import com.example.ui.theme.KidooSurfaceContainerLow
import com.example.ui.theme.KidooSurfaceContainerLowest
import com.example.ui.theme.KidooTertiary
import com.example.ui.theme.KidooTertiaryFixed
import com.example.ui.theme.KidooTertiaryFixedDim

@Composable
fun AccountScreen(
  userProfile: UserProfile,
  onEditProfile: () -> Unit,
  onNavigateToOrders: () -> Unit,
  onNavigateToWishlist: () -> Unit,
  onOpenSpinWheel: () -> Unit,
  onOpenWallet: () -> Unit,
  onOpenCoupons: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "primePulse")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(900, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "primePulseAlpha"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Row: Title & Notification/Settings
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "My Account",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Surface(
            shape = CircleShape,
            color = KidooSurfaceContainer,
            modifier = Modifier.size(40.dp)
          ) {
            IconButton(onClick = { /* Notification */ }) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Surface(
            shape = CircleShape,
            color = KidooSurfaceContainer,
            modifier = Modifier.size(40.dp)
          ) {
            IconButton(onClick = { /* Settings */ }) {
              Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    // Profile Card (Aarav Sharma)
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = KidooSurfaceContainerLowest,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
          ) {
            // Avatar with verified badge
            Box(modifier = Modifier.size(64.dp)) {
              AsyncImage(
                model = userProfile.avatarUrl,
                contentDescription = userProfile.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .border(2.dp, KidooPrimary, CircleShape)
              )
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .size(22.dp)
                  .clip(CircleShape)
                  .background(KidooPrimary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = "Verified Member",
                  tint = Color.White,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            // Name & Membership details
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = userProfile.name,
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )

              Surface(
                shape = RoundedCornerShape(12.dp),
                color = KidooTertiaryFixed,
                modifier = Modifier.padding(top = 4.dp)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Stars,
                    contentDescription = null,
                    tint = KidooTertiary,
                    modifier = Modifier.size(13.dp)
                  )
                  Text(
                    text = userProfile.membershipBadge,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }

              Text(
                text = "${userProfile.phone} • ${userProfile.email}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp)
              )
            }
          }

          // Edit Profile & Prime Pass pill
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(18.dp),
              color = KidooSurfaceContainerLow,
              modifier = Modifier.clickable(onClick = onEditProfile)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Edit,
                  contentDescription = null,
                  tint = KidooPrimary,
                  modifier = Modifier.size(15.dp)
                )
                Text(
                  text = "Edit Profile",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = KidooPrimary
                  )
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(18.dp),
              color = KidooSurfaceContainerLow
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(KidooSecondaryContainer.copy(alpha = pulseAlpha))
                )
                Text(
                  text = "Active Prime Pass",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                  ),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // Stats Row: Orders Placed, Saved Wishlist, Reward Coins
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = KidooSurfaceContainerLowest,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Orders Placed
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clickable(onClick = onNavigateToOrders)
              .padding(horizontal = 8.dp)
          ) {
            Text(
              text = "${userProfile.ordersCount}",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Orders Placed",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(top = 2.dp)
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(32.dp)
              .background(KidooSurfaceContainer)
          )

          // Saved Wishlist
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clickable(onClick = onNavigateToWishlist)
              .padding(horizontal = 8.dp)
          ) {
            Text(
              text = "${userProfile.wishlistCount}",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
              ),
              color = KidooPrimary
            )
            Text(
              text = "Saved Wishlist",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(top = 2.dp)
            )
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(32.dp)
              .background(KidooSurfaceContainer)
          )

          // Reward Coins
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .clickable(onClick = onOpenSpinWheel)
              .padding(horizontal = 8.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = KidooTertiaryFixedDim,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "${userProfile.rewardCoins}",
                style = MaterialTheme.typography.headlineMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "Reward Coins",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(top = 2.dp)
            )
          }
        }
      }
    }

    // Section 1: My Shopping
    item {
      SectionCard(
        title = "My Shopping",
        dotColor = KidooPrimary
      ) {
        AccountRowItem(
          icon = Icons.Default.LocalShipping,
          iconBg = KidooPrimaryFixed,
          iconTint = KidooPrimary,
          title = "My Orders",
          subtitle = "Track, return & buy again",
          badgeText = "1 Active",
          badgeBg = KidooSecondaryContainer,
          badgeColor = Color.White,
          onClick = onNavigateToOrders
        )
        AccountRowItem(
          icon = Icons.Default.Favorite,
          iconBg = KidooSurfaceContainerHigh,
          iconTint = KidooPrimary,
          title = "Wishlist & Saved Items",
          subtitle = "5 creative stationery picks",
          trailingText = "${userProfile.wishlistCount} items",
          onClick = onNavigateToWishlist
        )
        AccountRowItem(
          icon = Icons.Default.History,
          iconBg = KidooSurfaceContainerHigh,
          iconTint = MaterialTheme.colorScheme.onSurface,
          title = "Recently Viewed Products",
          subtitle = "Smart desk lamps, sketchbooks",
          onClick = { /* Recently viewed */ }
        )
      }
    }

    // Section 2: KIDOO Perks & Rewards
    item {
      SectionCard(
        title = "KIDOO Perks & Rewards",
        dotColor = KidooSecondaryContainer
      ) {
        AccountRowItem(
          icon = Icons.Default.AccountBalanceWallet,
          iconBg = KidooTertiaryFixed,
          iconTint = KidooTertiary,
          title = "KIDOO Coins & Cash Wallet",
          subtitle = "Instant checkout credits",
          trailingText = "₹${userProfile.walletCredits} available",
          trailingTextColor = KidooPrimary,
          onClick = onOpenWallet
        )
        AccountRowItem(
          icon = Icons.Default.ConfirmationNumber,
          iconBg = KidooSecondaryFixed,
          iconTint = KidooSecondary,
          title = "Coupons & Vouchers",
          subtitle = "Up to 40% off tech accessories",
          badgeText = "${userProfile.activeCouponsCount} Active",
          badgeBg = KidooTertiaryFixedDim,
          badgeColor = MaterialTheme.colorScheme.onSurface,
          onClick = onOpenCoupons
        )
        AccountRowItem(
          icon = Icons.Default.Casino,
          iconBg = KidooPrimaryFixed,
          iconTint = KidooPrimary,
          title = "Spin the Wonder Wheel",
          subtitle = "Daily reward game unlocked",
          hasDot = true,
          trailingText = "Ready",
          trailingTextColor = KidooSecondary,
          onClick = onOpenSpinWheel
        )
      }
    }

    // Section 3: Saved Details & Preferences
    item {
      SectionCard(
        title = "Saved Details & Preferences",
        dotColor = KidooTertiary
      ) {
        AccountRowItem(
          icon = Icons.Default.PinDrop,
          iconBg = KidooSurfaceContainerHigh,
          iconTint = MaterialTheme.colorScheme.onSurface,
          title = "Saved Addresses",
          subtitle = "Home (Indiranagar) & Campus Lab",
          trailingText = "2 Saved",
          onClick = { /* Saved addresses */ }
        )
        AccountRowItem(
          icon = Icons.Default.CreditCard,
          iconBg = KidooSurfaceContainerHigh,
          iconTint = MaterialTheme.colorScheme.onSurface,
          title = "Payment Methods",
          subtitle = "UPI ID & 2 Cards saved securely",
          onClick = { /* Payment */ }
        )
        AccountRowItem(
          icon = Icons.Default.Tune,
          iconBg = KidooSurfaceContainerHigh,
          iconTint = MaterialTheme.colorScheme.onSurface,
          title = "Language & Notifications",
          subtitle = "English • Push & WhatsApp Enabled",
          onClick = { /* Language */ }
        )
      }
    }

    // Section 4: Support & Legal
    item {
      SectionCard(
        title = "Support & Legal",
        dotColor = MaterialTheme.colorScheme.outline
      ) {
        AccountRowItem(
          icon = Icons.Default.SupportAgent,
          iconBg = KidooSurfaceContainerHigh,
          iconTint = KidooPrimary,
          title = "Help & Support Center",
          subtitle = "24/7 Live chat & ticket tracking",
          onClick = { /* Help */ }
        )
        AccountRowItem(
          icon = Icons.Default.AssignmentReturn,
          iconBg = KidooSurfaceContainerHigh,
          iconTint = MaterialTheme.colorScheme.onSurface,
          title = "Return & Refund Policy",
          subtitle = "Easy 7-day doorstep replacement",
          onClick = { /* Returns */ }
        )
        AccountRowItem(
          icon = Icons.Default.Info,
          iconBg = KidooSurfaceContainerHigh,
          iconTint = MaterialTheme.colorScheme.onSurface,
          title = "About KIDOO",
          subtitle = "'Things You'll Love' • v2.4.0",
          onClick = { /* About */ }
        )
      }
    }

    // Log Out Button
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp, bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onLogout),
          color = KidooErrorContainer.copy(alpha = 0.4f)
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = null,
              tint = KidooError,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Log Out from KIDOO",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = KidooError
              )
            )
          }
        }

        Text(
          text = "Signed in as Aarav • Bengaluru, India",
          style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.outline,
          modifier = Modifier.padding(top = 10.dp)
        )
      }
    }
  }
}

@Composable
fun SectionCard(
  title: String,
  dotColor: Color,
  content: @Composable () -> Unit
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    color = KidooSurfaceContainerLowest,
    shadowElevation = 1.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(dotColor)
        )
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          ),
          color = MaterialTheme.colorScheme.onSurface
        )
      }

      content()
    }
  }
}

@Composable
fun AccountRowItem(
  icon: ImageVector,
  iconBg: Color,
  iconTint: Color,
  title: String,
  subtitle: String,
  badgeText: String? = null,
  badgeBg: Color = Color.Transparent,
  badgeColor: Color = Color.White,
  hasDot: Boolean = false,
  trailingText: String? = null,
  trailingTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
  onClick: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick),
    color = KidooSurfaceContainerLow
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(iconBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
          )
        }

        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = title,
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            if (hasDot) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(KidooError)
              )
            }
          }
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        if (badgeText != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(badgeBg)
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = badgeText,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              ),
              color = badgeColor
            )
          }
        }
        if (trailingText != null) {
          Text(
            text = trailingText,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold
            ),
            color = trailingTextColor
          )
        }
        Icon(
          imageVector = Icons.Default.ChevronRight,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
