package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.components.DisplayStarRating
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooPrimaryFixed
import com.example.ui.theme.KidooSecondary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSurfaceContainerLow
import com.example.ui.theme.KidooSurfaceContainerLowest

/**
 * High-fidelity, detailed product view screen displaying:
 * - High-resolution hero image gallery with interactive thumbnail switching
 * - Brand, title, stock status, ratings, and price discounts
 * - Interactive variant and color theme selector
 * - Value proposition highlights (Shipping, Warranty, Returns, Safety)
 * - Full multi-paragraph product description
 * - Comprehensive technical specifications table
 * - Customer ratings breakdown & reviews preview with write review trigger
 * - Sticky bottom purchase bar with quantity selector, Add to Cart, and Buy Now actions
 */
@Composable
fun ProductDetailScreen(
  product: Product,
  isWishlisted: Boolean,
  cartCount: Int,
  onBack: () -> Unit,
  onWishlistToggle: () -> Unit,
  onAddToCart: (quantity: Int, variant: String) -> Unit,
  onBuyNow: (quantity: Int, variant: String) -> Unit,
  onOpenReviews: (Product) -> Unit,
  onNavigateToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler(onBack = onBack)

  val context = LocalContext.current
  val listState = rememberLazyListState()

  // Gallery state
  val gallery = remember(product) { product.allGalleryImages }
  var selectedImageIndex by remember(product) { mutableIntStateOf(0) }

  // Variant & Quantity state
  val availableVariants = remember(product) { product.allVariants }
  var selectedVariant by remember(product) {
    mutableStateOf(availableVariants.firstOrNull() ?: product.themeVariant)
  }
  var quantity by remember { mutableIntStateOf(1) }

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .testTag("product_detail_screen"),
    topBar = {
      ProductDetailTopBar(
        brand = product.brand,
        title = product.title,
        isWishlisted = isWishlisted,
        cartCount = cartCount,
        onBack = onBack,
        onWishlistToggle = onWishlistToggle,
        onShare = {
          val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check out ${product.title} on KIDOO")
            putExtra(
              Intent.EXTRA_TEXT,
              "Check out ${product.title} by ${product.brand} for only ₹${product.price} on KIDOO!\nhttps://kidoo.app/products/${product.id}"
            )
          }
          context.startActivity(Intent.createChooser(shareIntent, "Share product via"))
        },
        onNavigateToCart = onNavigateToCart
      )
    },
    bottomBar = {
      StickyProductBottomBar(
        price = product.price,
        quantity = quantity,
        onIncrement = { if (quantity < 10) quantity++ },
        onDecrement = { if (quantity > 1) quantity-- },
        onAddToCart = { onAddToCart(quantity, selectedVariant) },
        onBuyNow = { onBuyNow(quantity, selectedVariant) }
      )
    }
  ) { innerPadding ->
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Large Hero Image with Gallery Thumbnails & Badges
      item {
        HeroImageGallerySection(
          gallery = gallery,
          selectedIndex = selectedImageIndex,
          onSelectIndex = { selectedImageIndex = it },
          discountPercent = product.discountPercent,
          isTopRated = product.isTopRated,
          isStaffPick = product.isStaffPick
        )
      }

      // 2. Product Title, Brand, Rating & Pricing
      item {
        ProductHeaderInfoSection(
          product = product,
          onOpenReviews = { onOpenReviews(product) }
        )
      }

      // 3. Variant Selector (if available)
      if (availableVariants.isNotEmpty()) {
        item {
          VariantSelectorSection(
            variants = availableVariants,
            selectedVariant = selectedVariant,
            onSelectVariant = { selectedVariant = it }
          )
        }
      }

      // 4. Value Proposition Trust Cards
      item {
        ValuePropositionCardsSection()
      }

      // 5. Full Product Description
      item {
        ProductDescriptionSection(description = product.description)
      }

      // 6. Comprehensive Technical Specifications
      if (product.specifications.isNotEmpty()) {
        item {
          ProductSpecificationsSection(specifications = product.specifications)
        }
      }

      // 7. Customer Ratings & Reviews Preview Section
      item {
        CustomerReviewsPreviewSection(
          product = product,
          onOpenAllReviews = { onOpenReviews(product) }
        )
      }

      // Bottom padding spacer to clear sticky bar comfortably
      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}

/**
 * Top App Bar with back navigation, product brand, share action, wishlist heart, and cart badge.
 */
@Composable
private fun ProductDetailTopBar(
  brand: String,
  title: String,
  isWishlisted: Boolean,
  cartCount: Int,
  onBack: () -> Unit,
  onWishlistToggle: () -> Unit,
  onShare: () -> Unit,
  onNavigateToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .fillMaxWidth()
      .statusBarsPadding(),
    color = MaterialTheme.colorScheme.background,
    shadowElevation = 2.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier
          .size(48.dp)
          .testTag("product_detail_back_button")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back to store",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 8.dp)
      ) {
        Text(
          text = brand.uppercase(),
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontSize = 11.sp
          ),
          color = KidooPrimary,
          maxLines = 1
        )
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      // Share Button
      IconButton(
        onClick = onShare,
        modifier = Modifier
          .size(44.dp)
          .testTag("product_detail_share_button")
      ) {
        Icon(
          imageVector = Icons.Default.Share,
          contentDescription = "Share product",
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(20.dp)
        )
      }

      // Wishlist Heart Toggle
      IconButton(
        onClick = onWishlistToggle,
        modifier = Modifier
          .size(44.dp)
          .testTag("product_detail_wishlist_toggle")
      ) {
        Icon(
          imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
          contentDescription = if (isWishlisted) "Remove from wishlist" else "Add to wishlist",
          tint = if (isWishlisted) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(22.dp)
        )
      }

      // Cart Badge Button
      IconButton(
        onClick = onNavigateToCart,
        modifier = Modifier
          .size(44.dp)
          .testTag("product_detail_cart_button")
      ) {
        BadgedBox(
          badge = {
            if (cartCount > 0) {
              Badge(
                containerColor = KidooSecondaryContainer,
                contentColor = Color.White
              ) {
                Text(
                  text = "$cartCount",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = "Shopping Cart",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }
  }
}

/**
 * Large hero product image with zoomable presentation, discount tags, and horizontal gallery thumbnails.
 */
@Composable
private fun HeroImageGallerySection(
  gallery: List<String>,
  selectedIndex: Int,
  onSelectIndex: (Int) -> Unit,
  discountPercent: Int,
  isTopRated: Boolean,
  isStaffPick: Boolean,
  modifier: Modifier = Modifier
) {
  val currentImageUrl = gallery.getOrNull(selectedIndex) ?: gallery.firstOrNull() ?: ""

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Large Main Hero Image Box
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1.15f)
        .clip(RoundedCornerShape(24.dp))
        .border(1.dp, KidooCardBorder, RoundedCornerShape(24.dp))
        .testTag("product_detail_hero_image"),
      color = KidooSurfaceContainerLow,
      shadowElevation = 4.dp
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
          targetState = currentImageUrl,
          transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(250)) },
          label = "gallery_image_transition",
          modifier = Modifier.fillMaxSize()
        ) { imageUrl ->
          AsyncImage(
            model = imageUrl,
            contentDescription = "Product image preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
        }

        // Gradient overlay at bottom for depth
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .align(Alignment.BottomCenter)
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f))
              )
            )
        )

        // Top Badges (Discount, Staff Pick, Top Rated)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (discountPercent > 0) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = KidooSecondaryContainer,
                shadowElevation = 2.dp
              ) {
                Text(
                  text = "$discountPercent% OFF",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            if (isStaffPick) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = KidooPrimary,
                shadowElevation = 2.dp
              ) {
                Text(
                  text = "★ STAFF PICK",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            } else if (isTopRated) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF2E7D32),
                shadowElevation = 2.dp
              ) {
                Text(
                  text = "TOP RATED",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          // Image Counter Pill (e.g. 1 of 3)
          if (gallery.size > 1) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color.Black.copy(alpha = 0.6f)
            ) {
              Text(
                text = "${selectedIndex + 1} of ${gallery.size}",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 11.sp
                ),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }

    // Horizontal Thumbnail Selector Row
    if (gallery.size > 1) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        gallery.forEachIndexed { index, imageUrl ->
          val isSelected = index == selectedIndex
          Surface(
            modifier = Modifier
              .size(68.dp)
              .clip(RoundedCornerShape(14.dp))
              .border(
                width = if (isSelected) 2.5.dp else 1.dp,
                color = if (isSelected) KidooPrimary else KidooCardBorder,
                shape = RoundedCornerShape(14.dp)
              )
              .clickable { onSelectIndex(index) }
              .testTag("gallery_thumbnail_$index"),
            color = KidooSurfaceContainerLow
          ) {
            AsyncImage(
              model = imageUrl,
              contentDescription = "Thumbnail ${index + 1}",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          }
        }
      }
    }
  }
}

/**
 * Product Title, Verified Brand, 5-Star Rating summary, Large Pricing, and Stock Status.
 */
@Composable
internal fun ProductHeaderInfoSection(
  product: Product,
  onOpenReviews: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = KidooSurfaceContainerLowest),
    border = BorderStroke(1.dp, KidooCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Brand tag with verified badge & Category pill
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = KidooPrimary,
            modifier = Modifier.size(16.dp)
          )
          Text(
            text = product.brand,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = KidooPrimary
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = KidooPrimaryFixed
        ) {
          Text(
            text = product.category,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = KidooPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      // Product Title
      Text(
        text = product.title,
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 22.sp,
          lineHeight = 28.sp
        ),
        color = MaterialTheme.colorScheme.onSurface
      )

      // Interactive Rating summary row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .clickable(onClick = onOpenReviews)
          .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        DisplayStarRating(
          rating = product.averageRating,
          starSize = 18.dp,
          showNumericScore = true
        )

        Text(
          text = "•",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
          text = "${product.totalReviewsCount} customer ratings",
          style = MaterialTheme.typography.bodySmall.copy(
            color = KidooPrimary,
            fontWeight = FontWeight.SemiBold
          )
        )

        Text(
          text = "›",
          style = MaterialTheme.typography.titleMedium.copy(
            color = KidooPrimary,
            fontWeight = FontWeight.Bold
          )
        )
      }

      HorizontalDivider(color = KidooCardBorder.copy(alpha = 0.5f))

      // Price Row with Discount and Savings calculation
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "₹${product.price}",
              style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                fontSize = 28.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )

            if (product.originalPrice > product.price) {
              Text(
                text = "₹${product.originalPrice}",
                style = MaterialTheme.typography.bodyLarge.copy(
                  textDecoration = TextDecoration.LineThrough
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          if (product.originalPrice > product.price) {
            val savings = product.originalPrice - product.price
            Text(
              text = "You save ₹$savings (${product.discountPercent}% OFF)",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
              )
            )
          }
        }

        // Stock Status Pill
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFE8F5E9),
          border = BorderStroke(1.dp, Color(0xFF81C784))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Box(
              modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(Color(0xFF2E7D32))
            )
            Text(
              text = "In Stock (${product.stockLeft} left)",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20)
              )
            )
          }
        }
      }
    }
  }
}

/**
 * Interactive Variant / Edition selector.
 */
@Composable
internal fun VariantSelectorSection(
  variants: List<String>,
  selectedVariant: String,
  onSelectVariant: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = KidooSurfaceContainerLowest),
    border = BorderStroke(1.dp, KidooCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Select Edition / Variant",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Text(
          text = selectedVariant,
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = KidooPrimary
          )
        )
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        variants.forEach { variant ->
          val isSelected = variant == selectedVariant
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) KidooPrimaryFixed else KidooSurfaceContainerLow,
            border = BorderStroke(
              width = if (isSelected) 1.5.dp else 1.dp,
              color = if (isSelected) KidooPrimary else KidooCardBorder
            ),
            modifier = Modifier
              .clickable { onSelectVariant(variant) }
              .testTag("variant_chip_$variant")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = KidooPrimary,
                  modifier = Modifier.size(16.dp)
                )
              }
              Text(
                text = variant,
                style = MaterialTheme.typography.labelMedium.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) KidooPrimary else MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }
    }
  }
}

/**
 * Value Proposition cards: Free Express Shipping, Official Warranty, Easy Returns, Safety Guarantee.
 */
@Composable
private fun ValuePropositionCardsSection(modifier: Modifier = Modifier) {
  val benefits = listOf(
    Triple(Icons.Default.LocalShipping, "Express Delivery", "Dispatched within 24h"),
    Triple(Icons.Default.Security, "1-Year Warranty", "Official Brand Coverage"),
    Triple(Icons.Default.Sync, "7-Day Replacement", "Easy doorstep pickup"),
    Triple(Icons.Default.Verified, "100% Genuine", "BIS Certified & Tested")
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    Text(
      text = "KIDOO Assured Advantages",
      style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
      color = MaterialTheme.colorScheme.onSurface
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      benefits.take(2).forEach { (icon, title, desc) ->
        BenefitCard(icon = icon, title = title, subtitle = desc, modifier = Modifier.weight(1f))
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      benefits.takeLast(2).forEach { (icon, title, desc) ->
        BenefitCard(icon = icon, title = title, subtitle = desc, modifier = Modifier.weight(1f))
      }
    }
  }
}

@Composable
private fun BenefitCard(
  icon: ImageVector,
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = KidooSurfaceContainerLowest),
    border = BorderStroke(1.dp, KidooCardBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(KidooPrimaryFixed),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = KidooPrimary,
          modifier = Modifier.size(18.dp)
        )
      }
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

/**
 * Full, detailed product description explaining build quality, materials, and benefits.
 */
@Composable
private fun ProductDescriptionSection(
  description: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = KidooSurfaceContainerLowest),
    border = BorderStroke(1.dp, KidooCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(
        text = "Product Overview & Description",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Text(
        text = description.ifBlank {
          "Engineered with premium materials and precision manufacturing for outstanding longevity. Every component is rigorously tested to ensure ergonomic comfort and optimal performance."
        },
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

/**
 * Comprehensive Technical Specifications Table card.
 */
@Composable
private fun ProductSpecificationsSection(
  specifications: Map<String, String>,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp)
      .testTag("product_specifications_table"),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = KidooSurfaceContainerLowest),
    border = BorderStroke(1.dp, KidooCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text(
        text = "Technical Specifications",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .border(1.dp, KidooCardBorder, RoundedCornerShape(12.dp))
      ) {
        specifications.entries.forEachIndexed { index, (key, value) ->
          val isEven = index % 2 == 0
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(if (isEven) KidooSurfaceContainerLow.copy(alpha = 0.5f) else Color.Transparent)
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = key,
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.weight(0.42f)
            )

            Text(
              text = value,
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.weight(0.58f)
            )
          }

          if (index < specifications.size - 1) {
            HorizontalDivider(color = KidooCardBorder.copy(alpha = 0.5f), thickness = 0.5.dp)
          }
        }
      }
    }
  }
}

/**
 * Customer Ratings & Reviews Section preview with quote cards and button to open full dialog.
 */
@Composable
private fun CustomerReviewsPreviewSection(
  product: Product,
  onOpenAllReviews: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = KidooSurfaceContainerLowest),
    border = BorderStroke(1.dp, KidooCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Customer Feedback",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "${product.totalReviewsCount} verified customer reviews",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = String.format("%.1f", product.averageRating),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "★",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
            color = Color(0xFFFFB800)
          )
        }
      }

      // Display up to 2 latest customer reviews
      if (product.reviews.isNotEmpty()) {
        product.reviews.take(2).forEach { review ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = KidooSurfaceContainerLow.copy(alpha = 0.6f),
            border = BorderStroke(1.dp, KidooCardBorder)
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
                Text(
                  text = review.author,
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                DisplayStarRating(
                  rating = review.rating.toDouble(),
                  starSize = 14.dp,
                  showNumericScore = false
                )
              }

              Text(
                text = "\"${review.comment}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }

      // Button to open reviews dialog & write review
      OutlinedButton(
        onClick = onOpenAllReviews,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = KidooPrimary),
        border = BorderStroke(1.dp, KidooPrimary),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("product_detail_open_reviews_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.RateReview,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "View All Reviews & Write Feedback",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}

/**
 * Sticky Bottom Purchase Bar respecting WindowInsets.navigationBars.
 * Contains quantity stepper, subtotal price, and Add to Cart / Buy Now actions.
 */
@Composable
private fun StickyProductBottomBar(
  price: Int,
  quantity: Int,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  onAddToCart: () -> Unit,
  onBuyNow: () -> Unit,
  modifier: Modifier = Modifier
) {
  val totalPrice = price * quantity

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding(),
    color = MaterialTheme.colorScheme.background,
    shadowElevation = 12.dp,
    border = BorderStroke(1.dp, KidooCardBorder)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Quantity Stepper
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = KidooSurfaceContainerLow,
        border = BorderStroke(1.dp, KidooCardBorder)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
          IconButton(
            onClick = onDecrement,
            modifier = Modifier
              .size(34.dp)
              .testTag("product_detail_qty_minus")
          ) {
            Icon(
              imageVector = Icons.Default.Remove,
              contentDescription = "Decrease quantity",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(16.dp)
            )
          }

          Text(
            text = "$quantity",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp)
          )

          IconButton(
            onClick = onIncrement,
            modifier = Modifier
              .size(34.dp)
              .testTag("product_detail_qty_plus")
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Increase quantity",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Add to Cart Button
      Button(
        onClick = onAddToCart,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = KidooPrimary,
          contentColor = Color.White
        ),
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("product_detail_add_to_cart_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Column(horizontalAlignment = Alignment.Start) {
            Text(
              text = "Add to Cart",
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "₹$totalPrice",
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = Color.White.copy(alpha = 0.85f)
            )
          }
        }
      }

      // Buy Now Button
      Button(
        onClick = onBuyNow,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = KidooSecondaryContainer,
          contentColor = Color.White
        ),
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("product_detail_buy_now_button")
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Bolt,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Text(
            text = "Buy Now",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }
  }
}
