package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PriceSortOrder
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.PriceSortToggleChip
import com.example.ui.components.ProductCard
import com.example.ui.components.ProductSearchBar
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooNavyDark
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooPrimaryContainer
import com.example.ui.theme.KidooPrimaryFixed
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
fun HomeScreen(
  products: List<Product>,
  wishlistIds: Set<String>,
  searchQuery: String = "",
  onSearchQueryChange: (String) -> Unit = {},
  onClearSearchQuery: () -> Unit = {},
  selectedCategory: ProductCategory = ProductCategory.ALL,
  onSelectCategory: (ProductCategory) -> Unit = {},
  priceSortOrder: PriceSortOrder = PriceSortOrder.NONE,
  onTogglePriceSort: () -> Unit = {},
  onOpenReviews: (Product) -> Unit = {},
  onProductClick: (Product) -> Unit = {},
  onWishlistToggle: (String) -> Unit,
  onAddToCart: (Product) -> Unit,
  onSearchClick: () -> Unit,
  onExploreCategories: () -> Unit,
  onOpenSpinWheel: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
    }

    // Search bar at the top of the product screen for state-based name filtering
    item {
      ProductSearchBar(
        query = searchQuery,
        onQueryChange = onSearchQueryChange,
        onClearQuery = onClearSearchQuery,
        modifier = Modifier.testTag("home_product_search_bar")
      )
    }

    // Hero Brand Showcase Banner
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .testTag("home_hero_banner"),
        color = KidooPrimary,
        shadowElevation = 4.dp
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                listOf(
                  Color(0xFF0052C1),
                  Color(0xFF0569F1),
                  Color(0xFF023E8A)
                )
              )
            )
            .padding(20.dp)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color.White.copy(alpha = 0.2f))
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Text(
                  text = "CURATED STORE",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  ),
                  color = Color.White
                )
              }
              Text(
                text = "• Express Delivery in 560034",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = Color.White.copy(alpha = 0.85f)
              )
            }

            Text(
              text = "Things You’ll Love",
              style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.02).sp
              ),
              color = Color.White
            )

            Text(
              text = "Explore tactile stationery, ambient desk gadgets, modular robotics and collectible hobby gear.",
              style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp
              ),
              color = Color.White.copy(alpha = 0.9f),
              modifier = Modifier.fillMaxWidth(0.85f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Button(
                onClick = onExploreCategories,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color.White,
                  contentColor = KidooPrimary
                )
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Text(
                    text = "Explore Catalog",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }

              Button(
                onClick = onOpenSpinWheel,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = KidooSecondaryContainer,
                  contentColor = Color.White
                )
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Casino,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "Spin & Win",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        }
      }
    }

    // Quick Top Category Icons
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Top Collections",
          style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "All 8 Categories",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = KidooPrimary
          ),
          modifier = Modifier.clickable(onClick = onExploreCategories)
        )
      }
    }

    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        QuickCategoryChip(
          name = "Stationery",
          icon = Icons.Default.Edit,
          color = KidooPrimaryFixed,
          tint = KidooPrimary,
          onClick = { onSelectCategory(ProductCategory.STATIONERY) }
        )
        QuickCategoryChip(
          name = "Gadgets",
          icon = Icons.Default.SmartToy,
          color = KidooSecondaryFixed,
          tint = KidooSecondaryContainer,
          onClick = { onSelectCategory(ProductCategory.GADGETS) }
        )
        QuickCategoryChip(
          name = "Hobbies",
          icon = Icons.Default.RocketLaunch,
          color = KidooTertiaryFixed,
          tint = KidooTertiaryFixedDim,
          onClick = { onSelectCategory(ProductCategory.HOBBIES) }
        )
        QuickCategoryChip(
          name = "Gaming",
          icon = Icons.Default.SportsEsports,
          color = KidooSurfaceContainerHighest,
          tint = MaterialTheme.colorScheme.onSurfaceVariant,
          onClick = { onSelectCategory(ProductCategory.GAMING) }
        )
      }
    }

    // Category Filter Chips
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 2.dp),
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
              tint = KidooSecondaryContainer,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = if (selectedCategory == ProductCategory.ALL) "Featured & Trending" else "${selectedCategory.name} Collection",
              style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          if (selectedCategory != ProductCategory.ALL) {
            Text(
              text = "Show All",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = KidooPrimary
              ),
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSelectCategory(ProductCategory.ALL) }
                .padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        // Filter chips for product categories (Stationery, Gadgets, Hobbies, etc.)
        CategoryFilterChips(
          selectedCategoryId = selectedCategory.id,
          onCategorySelected = onSelectCategory,
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 4.dp)
        )

        // Price Sort Toggle Chip
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          PriceSortToggleChip(
            sortOrder = priceSortOrder,
            onToggleSort = onTogglePriceSort
          )

          Text(
            text = "${products.size} products",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // 2-Column Product Grid in Home or Empty Search State
    if (products.isEmpty()) {
      item {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .testTag("empty_products_search_state"),
          shape = RoundedCornerShape(20.dp),
          color = KidooSurfaceContainerLowest,
          border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(KidooPrimaryFixed),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = KidooPrimary,
                modifier = Modifier.size(30.dp)
              )
            }

            Text(
              text = if (searchQuery.isNotBlank()) "No products matching \"$searchQuery\"" else "No products found",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
              text = "Try searching with a different name or keyword like \"Keyboard\", \"Pens\", or reset filters.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Button(
              onClick = {
                onClearSearchQuery()
                onSelectCategory(ProductCategory.ALL)
              },
              colors = ButtonDefaults.buttonColors(containerColor = KidooPrimary),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.testTag("reset_search_filters_button")
            ) {
              Text("Clear Search & Reset")
            }
          }
        }
      }
    } else {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          val pairs = products.chunked(2)
          pairs.forEach { rowProducts ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              rowProducts.forEach { product ->
                Box(modifier = Modifier.weight(1f)) {
                  ProductCard(
                    product = product,
                    isWishlisted = wishlistIds.contains(product.id),
                    onWishlistToggle = { onWishlistToggle(product.id) },
                    onAddToCart = { onAddToCart(product) },
                    onOpenReviews = onOpenReviews,
                    onClick = { onProductClick(product) }
                  )
                }
              }
              if (rowProducts.size == 1) {
                Spacer(modifier = Modifier.weight(1f))
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}

@Composable
fun QuickCategoryChip(
  name: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  tint: Color,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = KidooSurfaceContainerLowest,
    shadowElevation = 1.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(color),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = name,
          tint = tint,
          modifier = Modifier.size(16.dp)
        )
      }
      Text(
        text = name,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}
