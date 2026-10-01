package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.OrderData
import com.example.ui.OrdersTab
import com.example.ui.components.LiveMapPreview
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSecondary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSecondaryFixed
import com.example.ui.theme.KidooSurfaceContainer
import com.example.ui.theme.KidooSurfaceContainerHigh
import com.example.ui.theme.KidooSurfaceContainerHighest
import com.example.ui.theme.KidooSurfaceContainerLow
import com.example.ui.theme.KidooSurfaceContainerLowest
import com.example.ui.theme.KidooTertiaryFixed
import com.example.ui.theme.KidooTertiaryFixedDim

@Composable
fun OrdersScreen(
  ordersTab: OrdersTab,
  onSelectOrdersTab: (OrdersTab) -> Unit,
  activeOrders: List<OrderData>,
  pastOrders: List<OrderData>,
  onBack: () -> Unit,
  onSearchOrders: () -> Unit,
  onHelpClick: () -> Unit,
  onTrackOnLiveMap: () -> Unit,
  onOrderDetails: (OrderData) -> Unit,
  onBuyAgain: (OrderData) -> Unit,
  onRateAndReview: (OrderData) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Sub-Header control bar: Back, "My Orders", Search & Help
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = KidooSurfaceContainerLowest,
            shadowElevation = 2.dp,
            modifier = Modifier.size(40.dp)
          ) {
            IconButton(onClick = onBack) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Text(
            text = "My Orders",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          Surface(
            shape = CircleShape,
            color = KidooSurfaceContainerLowest,
            shadowElevation = 2.dp,
            modifier = Modifier.size(40.dp)
          ) {
            IconButton(onClick = onSearchOrders) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search orders",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          Surface(
            shape = CircleShape,
            color = KidooSurfaceContainerLowest,
            shadowElevation = 2.dp,
            modifier = Modifier.size(40.dp)
          ) {
            IconButton(onClick = onHelpClick) {
              Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = "Help and support",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }
      }
    }

    // Segmented Tabs: [Active Orders (1)] | [Past Orders (8)]
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp),
        shape = RoundedCornerShape(24.dp),
        color = KidooSurfaceContainer
      ) {
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          // Active Tab
          val isActiveSelected = ordersTab == OrdersTab.ACTIVE
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .clickable { onSelectOrdersTab(OrdersTab.ACTIVE) }
              .testTag("tab_active_orders"),
            color = if (isActiveSelected) KidooPrimary else Color.Transparent,
            shadowElevation = if (isActiveSelected) 2.dp else 0.dp
          ) {
            Row(
              modifier = Modifier.fillMaxSize(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Active Orders",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isActiveSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(CircleShape)
                  .background(if (isActiveSelected) KidooSurfaceContainerLowest else KidooSurfaceContainerHighest),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "${activeOrders.size}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = if (isActiveSelected) KidooPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          // Past Tab
          val isPastSelected = ordersTab == OrdersTab.PAST
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(20.dp))
              .clickable { onSelectOrdersTab(OrdersTab.PAST) }
              .testTag("tab_past_orders"),
            color = if (isPastSelected) KidooPrimary else Color.Transparent,
            shadowElevation = if (isPastSelected) 2.dp else 0.dp
          ) {
            Row(
              modifier = Modifier.fillMaxSize(),
              horizontalArrangement = Arrangement.Center,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Past Orders",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isPastSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .size(20.dp)
                  .clip(CircleShape)
                  .background(if (isPastSelected) KidooSurfaceContainerLowest else KidooSurfaceContainerHighest),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "8",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = if (isPastSelected) KidooPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // Active Orders List / Featured Card
    if (ordersTab == OrdersTab.ACTIVE) {
      if (activeOrders.isEmpty()) {
        item {
          Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = KidooSurfaceContainerLowest
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "No active orders right now",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(activeOrders) { activeOrder ->
          ActiveOrderCard(
            order = activeOrder,
            onTrackOnLiveMap = onTrackOnLiveMap,
            onOrderDetails = { onOrderDetails(activeOrder) },
            onCallHero = {
              val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
              context.startActivity(intent)
            }
          )
        }
      }
    }

    // Recent Orders Feed Header (Oct)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "Recent Orders",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(KidooSurfaceContainer)
              .padding(horizontal = 8.dp, vertical = 2.dp)
          ) {
            Text(
              text = "October",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { onSelectOrdersTab(OrdersTab.PAST) }
        ) {
          Text(
            text = "View All",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              color = KidooPrimary
            )
          )
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = KidooPrimary,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }

    // Past Orders Cards
    items(pastOrders) { pastOrder ->
      PastOrderCard(
        order = pastOrder,
        onBuyAgain = { onBuyAgain(pastOrder) },
        onRateAndReview = { onRateAndReview(pastOrder) }
      )
    }

    // KIDOO Trust & Delivery Promise Banner
    item {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = KidooSurfaceContainerHigh,
        shadowElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
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
                .size(40.dp)
                .clip(CircleShape)
                .background(KidooPrimary.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.VerifiedUser,
                contentDescription = null,
                tint = KidooPrimary,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = "KIDOO Trust & Delivery Promise",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "7-day easy returns & 100% genuine products",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun ActiveOrderCard(
  order: OrderData,
  onTrackOnLiveMap: () -> Unit,
  onOrderDetails: () -> Unit,
  onCallHero: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(1.dp, KidooCardBorder, RoundedCornerShape(18.dp))
      .testTag("active_order_card_${order.orderId}"),
    color = KidooSurfaceContainerLowest,
    shadowElevation = 3.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Top Metadata Row: Order ID & "Out for Delivery"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          Text(
            text = order.orderNumber,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = order.dateText,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KidooSecondaryContainer.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocalShipping,
              contentDescription = null,
              tint = KidooSecondaryContainer,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = order.status,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              ),
              color = KidooSecondaryContainer
            )
          }
        }
      }

      // Estimated Arrival Banner
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(KidooPrimary.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = KidooPrimary,
                modifier = Modifier.size(18.dp)
              )
            }
            Column {
              Text(
                text = "Estimated Arrival",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = order.estimatedArrival,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(KidooSecondaryFixed.copy(alpha = 0.5f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "On Schedule",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              ),
              color = KidooSecondary
            )
          }
        }
      }

      // Live Step Progress Tracker
      OrderStepTracker(currentStep = order.currentStep)

      // Live Interactive Map Tile Preview
      LiveMapPreview(
        distanceText = "0.8 km away",
        onClick = onTrackOnLiveMap
      )

      // 2 Items in Package Row
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = onOrderDetails),
        shape = RoundedCornerShape(14.dp),
        color = KidooSurfaceContainerLow
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
          ) {
            // Overlapping item thumbnails
            Box(modifier = Modifier.width(68.dp).height(46.dp)) {
              if (order.images.size > 1) {
                AsyncImage(
                  model = order.images[1],
                  contentDescription = null,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.5.dp, Color.White, RoundedCornerShape(10.dp))
                )
              }
              if (order.images.isNotEmpty()) {
                AsyncImage(
                  model = order.images[0],
                  contentDescription = null,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                    .padding(start = 22.dp)
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.5.dp, Color.White, RoundedCornerShape(10.dp))
                )
              }
            }

            Column {
              Text(
                text = order.itemsTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "₹${order.totalAmount} • ${order.paymentInfo}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Box(
            modifier = Modifier
              .size(30.dp)
              .clip(CircleShape)
              .background(KidooSurfaceContainerHighest),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = "View items",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Assigned Delivery Partner Card
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = KidooSurfaceContainerHigh
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box {
              AsyncImage(
                model = order.deliveryHeroAvatar,
                contentDescription = order.deliveryHeroName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(Color.White)
              )
              Box(
                modifier = Modifier
                  .align(Alignment.BottomEnd)
                  .size(14.dp)
                  .clip(CircleShape)
                  .background(KidooSecondaryContainer)
                  .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "✓",
                  color = Color.White,
                  fontSize = 8.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Column {
              Text(
                text = order.deliveryHeroName,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Delivery Hero • ${order.deliveryHeroRating} ★",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = "DELIVERY OTP",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              )
              Text(
                text = order.deliveryOtp,
                style = MaterialTheme.typography.headlineMedium.copy(
                  fontWeight = FontWeight.Black,
                  fontSize = 18.sp,
                  letterSpacing = 1.sp,
                  color = KidooPrimary
                )
              )
            }

            Surface(
              shape = CircleShape,
              color = KidooSurfaceContainerLowest,
              shadowElevation = 2.dp,
              modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onCallHero)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Call,
                  contentDescription = "Call partner",
                  tint = KidooPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      // Action CTAs
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Button(
          onClick = onTrackOnLiveMap,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = KidooPrimary,
            contentColor = Color.White
          ),
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .testTag("track_on_live_map_btn")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Navigation,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Track on Live Map",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = KidooSurfaceContainer,
          modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .clickable(onClick = onOrderDetails)
            .testTag("order_details_btn")
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Order Details",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }
  }
}

@Composable
fun OrderStepTracker(currentStep: Int) {
  val infiniteTransition = rememberInfiniteTransition(label = "stepPulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  val steps = listOf("Placed", "Confirmed", "Packed", "On the way", "Delivered")

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp)
  ) {
    // Background connecting line
    Box(
      modifier = Modifier
        .align(Alignment.Center)
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .fillMaxWidth()
        .height(3.dp)
        .background(KidooSurfaceContainerHighest)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(fraction = 0.78f)
          .height(3.dp)
          .background(KidooPrimary)
      )
    }

    // Step Nodes
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.Top
    ) {
      steps.forEachIndexed { index, stepLabel ->
        val stepNum = index + 1
        val isCompleted = stepNum < currentStep
        val isCurrent = stepNum == currentStep

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Box(
            modifier = Modifier.size(26.dp),
            contentAlignment = Alignment.Center
          ) {
            if (isCurrent) {
              Box(
                modifier = Modifier
                  .size(26.dp * pulseScale)
                  .clip(CircleShape)
                  .background(KidooPrimary.copy(alpha = 0.25f))
              )
            }

            Box(
              modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                  when {
                    isCompleted || isCurrent -> KidooPrimary
                    else -> KidooSurfaceContainerHighest
                  }
                ),
              contentAlignment = Alignment.Center
            ) {
              if (isCompleted) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(13.dp)
                )
              } else if (isCurrent) {
                Box(
                  modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                )
              } else {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline)
                )
              }
            }
          }

          Text(
            text = stepLabel,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
            ),
            color = when {
              isCurrent -> KidooPrimary
              isCompleted -> MaterialTheme.colorScheme.onSurface
              else -> MaterialTheme.colorScheme.outline
            },
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
    }
  }
}

@Composable
fun PastOrderCard(
  order: OrderData,
  onBuyAgain: () -> Unit,
  onRateAndReview: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(1.dp, KidooCardBorder, RoundedCornerShape(18.dp))
      .testTag("past_order_card_${order.orderId}"),
    color = KidooSurfaceContainerLowest,
    shadowElevation = 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header: Order ID & "Delivered"
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column {
          Text(
            text = order.orderNumber,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = order.dateText,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(KidooSurfaceContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = KidooTertiaryFixedDim,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Delivered",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }

      // Product presentation snippet
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (order.images.isNotEmpty()) {
          AsyncImage(
            model = order.images[0],
            contentDescription = order.primaryItemTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(64.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(KidooSurfaceContainerLow)
          )
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = order.categoryName.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            ),
            color = KidooPrimary
          )
          Text(
            text = order.primaryItemTitle,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 2.dp)
          ) {
            Text(
              text = "₹${order.totalAmount}",
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            if (order.originalPrice > 0) {
              Text(
                text = "₹${order.originalPrice}",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 12.sp,
                  textDecoration = TextDecoration.LineThrough
                ),
                color = MaterialTheme.colorScheme.outline
              )
            }
            if (order.discountPercent > 0) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(KidooSecondaryFixed.copy(alpha = 0.5f))
                  .padding(horizontal = 4.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "${order.discountPercent}% off",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = KidooSecondaryContainer
                )
              }
            } else if (order.paymentInfo.isNotEmpty()) {
              Text(
                text = order.paymentInfo,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Quick Actions Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = KidooSurfaceContainer,
          modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clickable(onClick = onBuyAgain)
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = Icons.Default.Replay,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Buy Again",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = KidooSurfaceContainer,
          modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clickable(onClick = onRateAndReview)
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Icon(
              imageVector = if (order.orderNumber.contains("87102")) Icons.Default.Star else Icons.Default.ChatBubbleOutline,
              contentDescription = null,
              tint = if (order.orderNumber.contains("87102")) KidooTertiaryFixedDim else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (order.orderNumber.contains("87102")) "Rate & Review" else "Feedback",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = KidooSurfaceContainer,
          modifier = Modifier
            .size(40.dp)
            .clickable { /* download invoice */ }
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Download,
              contentDescription = "Invoice",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}
