package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import coil.compose.AsyncImage
import com.example.data.cart.CartSummary
import com.example.data.cart.ShoppingCartManager
import com.example.data.model.CartItem
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooError
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooPrimaryContainer
import com.example.ui.theme.KidooPrimaryFixed
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSurfaceContainer
import com.example.ui.theme.KidooSurfaceContainerHigh
import com.example.ui.theme.KidooSurfaceContainerLow
import com.example.ui.theme.KidooSurfaceContainerLowest
import com.example.ui.theme.KidooTertiaryFixed

@Composable
fun CartScreen(
  cartItems: List<CartItem>,
  appliedCoupon: String?,
  onUpdateQty: (String, Int) -> Unit,
  onRemoveItem: (String) -> Unit,
  onRemoveCoupon: () -> Unit,
  onApplyCoupon: (String) -> Unit,
  onProceedToCheckout: () -> Unit,
  onSaveForLater: (String) -> Unit,
  modifier: Modifier = Modifier,
  cartSummary: CartSummary? = null,
  onClearCart: (() -> Unit)? = null
) {
  val summary = cartSummary ?: remember(cartItems, appliedCoupon) {
    ShoppingCartManager.calculateSummary(cartItems, appliedCoupon)
  }
  val totalItemsCount = summary.itemCount
  val itemsTotal = summary.subtotal
  val bagDiscount = summary.bagDiscount
  val couponDiscount = summary.couponSavings
  val totalPayable = summary.totalPayable
  val totalSavings = summary.totalSavings

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Top Header Row
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "Your Cart",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(KidooPrimaryFixed)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(
                text = "($totalItemsCount items)",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = KidooPrimary
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (cartItems.isNotEmpty() && onClearCart != null) {
              Text(
                text = "Clear All",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = KidooError,
                  fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                  .clickable(onClick = onClearCart)
                  .padding(horizontal = 4.dp, vertical = 2.dp)
                  .testTag("clear_cart_button")
              )
            }

            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp),
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(KidooSurfaceContainer)
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Icon(
                imageVector = Icons.Default.PinDrop,
                contentDescription = null,
                tint = KidooSecondaryContainer,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "560034",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }

      // Deliver to banner
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
              .padding(12.dp),
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
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(KidooPrimaryContainer.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.LocalShipping,
                  contentDescription = null,
                  tint = KidooPrimary,
                  modifier = Modifier.size(20.dp)
                )
              }
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "Deliver to: ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = "Home",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
                Text(
                  text = "Indiranagar, 560034",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Text(
              text = "Change",
              style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = KidooPrimary
              ),
              modifier = Modifier
                .clickable { /* Change address */ }
                .padding(4.dp)
            )
          }
        }
      }

      // Express Next-Day Delivery Unlocked Banner
      item {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          color = KidooSurfaceContainerLowest,
          border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
          shadowElevation = 1.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
                  imageVector = Icons.Default.Bolt,
                  contentDescription = null,
                  tint = KidooPrimary,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "Express Next-Day Delivery Unlocked!",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              Text(
                text = "100%",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = KidooPrimary
              )
            }

            LinearProgressIndicator(
              progress = { 1f },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = KidooPrimaryContainer,
              trackColor = KidooSurfaceContainer
            )

            Text(
              text = "Add ₹0 more for guaranteed next-day dispatch.",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Cart Items List
      if (cartItems.isEmpty()) {
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
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Explore categories and find things you’ll love!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(cartItems, key = { it.id }) { cartItem ->
          CartItemCard(
            item = cartItem,
            onIncrease = { onUpdateQty(cartItem.id, 1) },
            onDecrease = { onUpdateQty(cartItem.id, -1) },
            onRemove = { onRemoveItem(cartItem.id) },
            onSaveForLater = { onSaveForLater(cartItem.id) }
          )
        }
      }

      // Coupon Section
      item {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          color = KidooSurfaceContainerLowest,
          border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
          shadowElevation = 1.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(KidooTertiaryFixed),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Sell,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = appliedCoupon ?: "KIDOO10",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    if (appliedCoupon != null) {
                      Box(
                        modifier = Modifier
                          .clip(RoundedCornerShape(10.dp))
                          .background(KidooSurfaceContainerHigh)
                          .padding(horizontal = 6.dp, vertical = 2.dp)
                      ) {
                        Row(
                          verticalAlignment = Alignment.CenterVertically,
                          horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                          Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = KidooPrimary,
                            modifier = Modifier.size(12.dp)
                          )
                          Text(
                            text = "Applied",
                            style = MaterialTheme.typography.labelSmall.copy(
                              fontSize = 10.sp,
                              fontWeight = FontWeight.Bold
                            ),
                            color = KidooPrimary
                          )
                        }
                      }
                    }
                  }
                  Text(
                    text = if (appliedCoupon != null) "₹350 Instant discount unlocked!" else "Apply coupon for savings",
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 11.sp,
                      color = KidooPrimary,
                      fontWeight = FontWeight.Medium
                    )
                  )
                }
              }

              if (appliedCoupon != null) {
                Text(
                  text = "Remove",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = KidooSecondaryContainer
                  ),
                  modifier = Modifier
                    .clickable(onClick = onRemoveCoupon)
                    .testTag("remove_coupon_btn")
                )
              } else {
                Text(
                  text = "Apply",
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = KidooPrimary
                  ),
                  modifier = Modifier
                    .clickable { onApplyCoupon("KIDOO10") }
                    .testTag("apply_coupon_btn")
                )
              }
            }

            Surface(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp),
              color = KidooSurfaceContainerLow
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Have another promo code?",
                  style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.clickable { /* View coupons */ }
                ) {
                  Text(
                    text = "View All Coupons",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = KidooPrimary
                    )
                  )
                  Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = KidooPrimary,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }
        }
      }

      // Price Details Section
      item {
        Surface(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          color = KidooSurfaceContainerLowest,
          border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
          shadowElevation = 1.dp
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Text(
              text = "Price Details",
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Items Total ($totalItemsCount items)",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "₹$itemsTotal",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Bag Discount",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "-₹$bagDiscount",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = KidooError
              )
            }

            if (couponDiscount > 0) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Coupon Savings",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "-₹$couponDiscount",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = KidooPrimary
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Delivery Fee",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "FREE",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = KidooPrimary
              )
            }

            HorizontalDivider(
              color = KidooSurfaceContainer,
              modifier = Modifier.padding(vertical = 4.dp)
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Total Amount",
                  style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                  ),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Inclusive of all taxes",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Text(
                text = "₹$totalPayable",
                style = MaterialTheme.typography.headlineLarge.copy(
                  fontWeight = FontWeight.Black,
                  fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            if (totalSavings > 0) {
              Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = KidooSurfaceContainerHigh
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 10.dp),
                  horizontalArrangement = Arrangement.Center,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = KidooPrimary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "You are saving ₹$totalSavings on this order!",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        }
      }

      // Bottom Spacer so content is not cut off by sticky checkout footer
      item {
        Spacer(modifier = Modifier.height(84.dp))
      }
    }

    // Sticky Bottom Action Bar
    Surface(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth(),
      color = KidooSurfaceContainerLowest.copy(alpha = 0.98f),
      shadowElevation = 12.dp,
      border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "TOTAL PAYABLE",
            style = MaterialTheme.typography.labelSmall.copy(
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "₹$totalPayable",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Button(
          onClick = onProceedToCheckout,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = KidooPrimaryContainer,
            contentColor = Color.White
          ),
          modifier = Modifier
            .height(48.dp)
            .testTag("proceed_to_checkout_btn")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "Proceed to Checkout",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun CartItemCard(
  item: CartItem,
  onIncrease: () -> Unit,
  onDecrease: () -> Unit,
  onRemove: () -> Unit,
  onSaveForLater: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(1.dp, KidooCardBorder, RoundedCornerShape(18.dp))
      .testTag("cart_item_${item.id}"),
    color = KidooSurfaceContainerLowest,
    shadowElevation = 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Thumbnail with discount badge
        Box(
          modifier = Modifier
            .size(92.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(KidooSurfaceContainerLow)
        ) {
          AsyncImage(
            model = item.product.imageUrl,
            contentDescription = item.product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          if (item.product.discountPercent > 0) {
            Box(
              modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(KidooError)
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text(
                text = "${item.product.discountPercent}% OFF",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = Color.White
              )
            }
          }
        }

        // Title and price column
        Column(
          modifier = Modifier
            .weight(1f)
            .height(92.dp),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = item.product.category.uppercase(),
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
              ),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = item.product.title,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
              ),
              color = MaterialTheme.colorScheme.onSurface,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            if (item.selectedVariant.isNotEmpty()) {
              Text(
                text = item.selectedVariant,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "₹${item.product.price}",
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            if (item.product.originalPrice > item.product.price) {
              Text(
                text = "₹${item.product.originalPrice}",
                style = MaterialTheme.typography.bodySmall.copy(
                  fontSize = 12.sp,
                  textDecoration = TextDecoration.LineThrough
                ),
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
        }
      }

      // Bottom Row: Save for Later, Delete, Quantity Stepper
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .clickable(onClick = onSaveForLater)
              .padding(horizontal = 6.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.BookmarkBorder,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "Save for later",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Remove item",
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Stepper: [-  qty  +]
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(KidooSurfaceContainerLow)
            .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(KidooSurfaceContainerLowest)
              .clickable(onClick = onDecrease),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = "Decrease",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(14.dp)
            )
          }

          Text(
            text = "${item.quantity}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 10.dp)
          )

          Box(
            modifier = Modifier
              .size(26.dp)
              .clip(CircleShape)
              .background(KidooPrimaryContainer)
              .clickable(onClick = onIncrease),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Increase",
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }
    }
  }
}
