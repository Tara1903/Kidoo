package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooError
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSurfaceContainerLow
import com.example.ui.theme.KidooSurfaceContainerLowest
import com.example.ui.theme.KidooTertiaryFixedDim

@Composable
fun ProductCard(
  product: Product,
  isWishlisted: Boolean,
  onWishlistToggle: () -> Unit,
  onAddToCart: () -> Unit,
  onOpenReviews: (Product) -> Unit = {},
  onClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .border(1.dp, KidooCardBorder, RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag("product_card_${product.id}"),
    color = KidooSurfaceContainerLowest,
    shadowElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      // Image Container with Badges & Wishlist Heart
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(KidooSurfaceContainerLow)
      ) {
        AsyncImage(
          model = product.imageUrl,
          contentDescription = product.title,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Deal Badge (Top Left)
        if (product.discountPercent > 0 || product.isTopRated || product.isStaffPick) {
          val badgeText = when {
            product.isTopRated -> "Top Rated"
            product.isStaffPick -> "Staff Pick"
            else -> "${product.discountPercent}% OFF"
          }
          val badgeBg = when {
            product.isTopRated -> KidooSecondaryContainer
            product.isStaffPick -> KidooPrimary
            else -> KidooError
          }
          Box(
            modifier = Modifier
              .padding(6.dp)
              .align(Alignment.TopStart)
              .clip(RoundedCornerShape(6.dp))
              .background(badgeBg)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = badgeText,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              ),
              color = Color.White
            )
          }
        }

        // Wishlist Button (Top Right)
        Box(
          modifier = Modifier
            .padding(6.dp)
            .align(Alignment.TopEnd)
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.85f))
            .clickable(onClick = onWishlistToggle),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Wishlist",
            tint = if (isWishlisted) KidooError else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Brand / Subtitle
      Text(
        text = product.brand.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 10.sp,
          letterSpacing = 0.5.sp
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      // Title
      Text(
        text = product.title,
        style = MaterialTheme.typography.titleMedium.copy(
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          lineHeight = 18.sp
        ),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.height(36.dp)
      )

      // Star-Based Rating System (Clickable to view and interact with reviews)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
          .padding(top = 2.dp)
          .clip(RoundedCornerShape(6.dp))
          .clickable { onOpenReviews(product) }
          .testTag("product_rating_stars_${product.id}")
      ) {
        DisplayStarRating(
          rating = product.averageRating,
          reviewCount = product.totalReviewsCount,
          starSize = 11.dp
        )
      }

      // Price & Add to Cart Action
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            text = "₹${product.price}",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontSize = 16.sp,
              fontWeight = FontWeight.ExtraBold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          if (product.originalPrice > product.price) {
            Text(
              text = "₹${product.originalPrice}",
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                textDecoration = TextDecoration.LineThrough
              ),
              color = MaterialTheme.colorScheme.outline
            )
          }
        }

        // Plus quick add button
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(KidooPrimary)
            .clickable(onClick = onAddToCart)
            .testTag("add_to_cart_btn_${product.id}"),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add to cart",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}
