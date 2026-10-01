package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.KidooTab
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSurfaceContainerLowest

data class NavItem(
  val tab: KidooTab,
  val label: String,
  val activeIcon: ImageVector,
  val inactiveIcon: ImageVector
)

@Composable
fun KidooBottomNavigation(
  currentTab: KidooTab,
  cartBadgeCount: Int,
  onTabSelected: (KidooTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavItem(KidooTab.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    NavItem(KidooTab.CATEGORIES, "Categories", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
    NavItem(KidooTab.CART, "Cart", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
    NavItem(KidooTab.ORDERS, "Orders", Icons.Filled.Inventory2, Icons.Outlined.Inventory2),
    NavItem(KidooTab.ME, "Me", Icons.Filled.Person, Icons.Outlined.Person)
  )

  Surface(
    modifier = modifier.fillMaxWidth(),
    color = KidooSurfaceContainerLowest.copy(alpha = 0.98f),
    shadowElevation = 8.dp,
    border = androidx.compose.foundation.BorderStroke(0.5.dp, KidooCardBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars)
        .height(64.dp)
        .padding(horizontal = 4.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      items.forEach { item ->
        val isSelected = currentTab == item.tab
        val interactionSource = remember { MutableInteractionSource() }

        Column(
          modifier = Modifier
            .weight(1f)
            .clickable(
              interactionSource = interactionSource,
              indication = null
            ) { onTabSelected(item.tab) }
            .testTag("nav_tab_${item.label.lowercase()}"),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .size(width = 44.dp, height = 28.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(if (isSelected) KidooPrimary.copy(alpha = 0.12f) else Color.Transparent),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isSelected) item.activeIcon else item.inactiveIcon,
              contentDescription = item.label,
              tint = if (isSelected) KidooPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(22.dp)
            )

            // Badge on Cart icon
            if (item.tab == KidooTab.CART && cartBadgeCount > 0) {
              Box(
                modifier = Modifier
                  .align(Alignment.TopEnd)
                  .size(16.dp)
                  .clip(CircleShape)
                  .background(KidooSecondaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "$cartBadgeCount",
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 11.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) KidooPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp)
          )
        }
      }
    }
  }
}
