package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.local.entity.WishlistItemEntity
import com.example.data.model.Product
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooError
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSurfaceContainerHigh
import com.example.ui.theme.KidooSurfaceContainerLow
import com.example.ui.theme.KidooSurfaceContainerLowest
import com.example.ui.theme.KidooTertiaryFixedDim

@Composable
fun WishlistDialog(
  wishlistItems: List<WishlistItemEntity>,
  onRemoveItem: (String) -> Unit,
  onAddToCart: (Product) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.94f)
        .fillMaxHeight(0.85f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("wishlist_dialog"),
      color = KidooSurfaceContainerLowest,
      shadowElevation = 8.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(KidooError.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = KidooError,
                modifier = Modifier.size(20.dp)
              )
            }

            Column {
              Text(
                text = "Saved Wishlist",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${wishlistItems.size} items bookmarked in Room DB",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier
              .size(36.dp)
              .testTag("close_wishlist_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        if (wishlistItems.isEmpty()) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(48.dp)
              )
              Text(
                text = "Your wishlist is empty",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Tap the heart icon on any product to bookmark it.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            items(wishlistItems, key = { it.productId }) { item ->
              WishlistRowItem(
                item = item,
                onRemove = { onRemoveItem(item.productId) },
                onAddToCart = { onAddToCart(item.toProduct()) }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun WishlistRowItem(
  item: WishlistItemEntity,
  onRemove: () -> Unit,
  onAddToCart: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = KidooSurfaceContainerLow,
    border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      AsyncImage(
        model = item.imageUrl,
        contentDescription = item.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(68.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color.White)
      )

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.brand.uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = item.title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
          ),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = KidooTertiaryFixedDim,
            modifier = Modifier.size(12.dp)
          )
          Text(
            text = "${item.rating}",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "• ₹${item.price}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = KidooPrimary
          )
        }
      }

      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        IconButton(
          onClick = onRemove,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DeleteOutline,
            contentDescription = "Remove bookmark",
            tint = KidooError,
            modifier = Modifier.size(18.dp)
          )
        }

        Button(
          onClick = onAddToCart,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = KidooPrimary,
            contentColor = Color.White
          ),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.height(30.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = null,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = "Add",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
            )
          }
        }
      }
    }
  }
}
