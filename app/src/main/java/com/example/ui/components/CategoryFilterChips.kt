package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.data.model.ProductCategory
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSurfaceContainerLow

/**
 * Horizontal scrollable filter chips row allowing users to filter products by category.
 */
@Composable
fun CategoryFilterChips(
  categories: List<ProductCategory> = ProductCategory.defaultCategories(),
  selectedCategoryId: String,
  onCategorySelected: (ProductCategory) -> Unit,
  modifier: Modifier = Modifier,
  contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(contentPadding)
      .testTag("category_filter_chips_row"),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    categories.forEach { category ->
      val isSelected = category.id == selectedCategoryId
      CategoryFilterChip(
        category = category,
        isSelected = isSelected,
        onClick = { onCategorySelected(category) }
      )
    }
  }
}

/**
 * Individual Material 3 category filter chip with icon, label, and accessible 48dp minimum touch target.
 */
@Composable
fun CategoryFilterChip(
  category: ProductCategory,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val containerColor by animateColorAsState(
    targetValue = if (isSelected) KidooPrimary else KidooSurfaceContainerLow,
    animationSpec = tween(durationMillis = 200),
    label = "chip_container_color"
  )

  val contentColor by animateColorAsState(
    targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
    animationSpec = tween(durationMillis = 200),
    label = "chip_content_color"
  )

  val borderColor = if (isSelected) KidooPrimary else KidooCardBorder

  Surface(
    shape = RoundedCornerShape(20.dp),
    color = containerColor,
    border = BorderStroke(1.dp, borderColor),
    shadowElevation = if (isSelected) 3.dp else 0.dp,
    modifier = modifier
      .defaultMinSize(minHeight = 48.dp) // Accessibility standard: 48dp minimum touch target
      .clip(RoundedCornerShape(20.dp))
      .clickable(
        role = Role.RadioButton,
        onClick = onClick
      )
      .testTag("category_chip_${category.id}")
  ) {
    Row(
      modifier = Modifier
        .padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Box(
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = category.icon,
          contentDescription = "${category.name} category icon",
          tint = contentColor,
          modifier = Modifier.size(16.dp)
        )
      }

      Text(
        text = category.name,
        style = MaterialTheme.typography.labelLarge.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
          fontSize = 13.sp
        ),
        color = contentColor
      )

      if (category.count > 0) {
        Surface(
          shape = CircleShape,
          color = if (isSelected) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.padding(start = 2.dp)
        ) {
          Text(
            text = "${category.count}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            ),
            color = contentColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}
