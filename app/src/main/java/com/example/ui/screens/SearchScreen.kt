package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PriceSortOrder
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.FilterState
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.PriceSortToggleChip
import com.example.ui.components.ProductCard
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooPrimaryFixed
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSecondaryFixed
import com.example.ui.theme.KidooSurfaceContainer
import com.example.ui.theme.KidooSurfaceContainerHigh
import com.example.ui.theme.KidooSurfaceContainerHighest
import com.example.ui.theme.KidooSurfaceContainerLow
import com.example.ui.theme.KidooSurfaceContainerLowest
import com.example.ui.theme.KidooTertiaryFixed
import com.example.ui.theme.KidooTertiaryFixedDim

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
  query: String,
  onQueryChange: (String) -> Unit,
  onSearchSubmit: (String) -> Unit,
  onClearQuery: () -> Unit,
  onCloseSearch: () -> Unit,
  recentSearches: List<String>,
  onClearRecentSearches: () -> Unit,
  products: List<Product>,
  wishlistIds: Set<String>,
  onWishlistToggle: (String) -> Unit,
  onAddToCart: (Product) -> Unit,
  filterState: FilterState,
  onToggleUnder5000: () -> Unit,
  onToggleInStock: () -> Unit,
  onToggleMinRating: () -> Unit,
  priceSortOrder: PriceSortOrder = PriceSortOrder.NONE,
  onTogglePriceSort: () -> Unit = {},
  onOpenReviews: (Product) -> Unit = {},
  onProductClick: (Product) -> Unit = {},
  modifier: Modifier = Modifier
) {
  val focusManager = LocalFocusManager.current

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    // Header & Search Input
    item(span = { GridItemSpan(2) }) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Search Input bar
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, KidooCardBorder, RoundedCornerShape(14.dp)),
          color = KidooSurfaceContainerLowest,
          shadowElevation = 2.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = onCloseSearch,
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
              )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = KidooPrimary,
              modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
              value = query,
              onValueChange = onQueryChange,
              modifier = Modifier
                .weight(1f)
                .testTag("kidoo_search_input_field"),
              textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
              ),
              singleLine = true,
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
              keyboardActions = KeyboardActions(onSearch = {
                focusManager.clearFocus()
                onSearchSubmit(query)
              }),
              decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                  Text(
                    text = "Search things you’ll love...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                innerTextField()
              }
            )

            if (query.isNotEmpty()) {
              IconButton(
                onClick = onClearQuery,
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Cancel,
                  contentDescription = "Clear",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            IconButton(
              onClick = { /* Voice search */ },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice search",
                tint = KidooPrimary,
                modifier = Modifier.size(20.dp)
              )
            }

            IconButton(
              onClick = { /* Visual search */ },
              modifier = Modifier.size(32.dp)
            ) {
              Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = "Camera search",
                tint = KidooPrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        // Recent Searches
        if (recentSearches.isNotEmpty()) {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                  imageVector = Icons.Default.History,
                  contentDescription = null,
                  tint = KidooPrimary,
                  modifier = Modifier.size(18.dp)
                )
                Text(
                  text = "Recent Searches",
                  style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                  ),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              Text(
                text = "Clear All",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = KidooSecondaryContainer,
                modifier = Modifier
                  .clickable(onClick = onClearRecentSearches)
                  .testTag("clear_recents_btn")
              )
            }

            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              recentSearches.forEach { searchTag ->
                Surface(
                  shape = RoundedCornerShape(20.dp),
                  color = KidooSurfaceContainer,
                  modifier = Modifier.clickable {
                    onQueryChange(searchTag)
                    onSearchSubmit(searchTag)
                  }
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Schedule,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.onSurfaceVariant,
                      modifier = Modifier.size(14.dp)
                    )
                    Text(
                      text = searchTag,
                      style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                }
              }
            }
          }
        }

        // Trending Now Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.LocalFireDepartment,
              contentDescription = null,
              tint = KidooSecondaryContainer,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "Trending Now",
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(KidooSecondaryContainer.copy(alpha = 0.15f))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "Hot",
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = KidooSecondaryContainer
              )
            }
          }

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            val trendingItems = listOf(
              "Wireless Mechanical Keyboards",
              "Botanical LEGO Bricks",
              "Astronomy Telescopes",
              "Duo Cable Pals"
            )
            trendingItems.forEach { trend ->
              Surface(
                shape = RoundedCornerShape(20.dp),
                color = KidooSurfaceContainerLowest,
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
                modifier = Modifier.clickable {
                  onQueryChange(trend)
                  onSearchSubmit(trend)
                }
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp),
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(KidooSecondaryContainer)
                  )
                  Text(
                    text = trend,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Icon(
                    imageVector = Icons.Default.NorthEast,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(14.dp)
                  )
                }
              }
            }
          }
        }

        // Explore by Top Categories 4-Column Grid
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Explore by Top Categories",
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "View Hub",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              ),
              color = KidooPrimary
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Category 1: Stationery
            TopCategoryTile(
              label = "Stationery",
              count = "1.2k items",
              icon = Icons.Default.Edit,
              iconBg = KidooPrimaryFixed,
              iconTint = KidooPrimary,
              onClick = {
                onQueryChange("Stationery")
                onSearchSubmit("Stationery")
              },
              modifier = Modifier.weight(1f)
            )
            // Category 2: Smart Tech
            TopCategoryTile(
              label = "Smart Tech",
              count = "850+ items",
              icon = Icons.Default.SmartToy,
              iconBg = KidooSecondaryFixed,
              iconTint = KidooSecondaryContainer,
              onClick = {
                onQueryChange("Tech")
                onSearchSubmit("Tech")
              },
              modifier = Modifier.weight(1f)
            )
            // Category 3: Toys
            TopCategoryTile(
              label = "Toys",
              count = "620 items",
              icon = Icons.Default.RocketLaunch,
              iconBg = KidooTertiaryFixed,
              iconTint = KidooTertiaryFixedDim,
              onClick = {
                onQueryChange("Toys")
                onSearchSubmit("Toys")
              },
              modifier = Modifier.weight(1f)
            )
            // Category 4: Hobbies
            TopCategoryTile(
              label = "Hobbies",
              count = "940 items",
              icon = Icons.Default.SportsEsports,
              iconBg = KidooSurfaceContainerHighest,
              iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
              onClick = {
                onQueryChange("Hobbies")
                onSearchSubmit("Hobbies")
              },
              modifier = Modifier.weight(1f)
            )
          }
        }

        // Quick Category Filter Chips
        CategoryFilterChips(
          selectedCategoryId = when {
            query.contains("Stationery", ignoreCase = true) -> ProductCategory.STATIONERY.id
            query.contains("Gadget", ignoreCase = true) || query.contains("Tech", ignoreCase = true) || query.contains("Keyboard", ignoreCase = true) -> ProductCategory.GADGETS.id
            query.contains("Hobbi", ignoreCase = true) || query.contains("Toy", ignoreCase = true) || query.contains("STEM", ignoreCase = true) -> ProductCategory.HOBBIES.id
            query.contains("Gaming", ignoreCase = true) -> ProductCategory.GAMING.id
            query.contains("Art", ignoreCase = true) || query.contains("Craft", ignoreCase = true) -> ProductCategory.ART_CRAFT.id
            query.isBlank() -> ProductCategory.ALL.id
            else -> ""
          },
          onCategorySelected = { cat ->
            if (cat.id == ProductCategory.ALL.id) {
              onQueryChange("")
              onSearchSubmit("")
            } else {
              onQueryChange(cat.name)
              onSearchSubmit(cat.name)
            }
          },
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 2.dp)
        )

        // Live Results Header & Filters
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Showing results for ",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontSize = 15.sp,
                  fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "\"$query\"",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontSize = 15.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = KidooPrimary
              )
            }
            Text(
              text = "${products.size} Found",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // Filter & Sort Horizontal Scroll Row
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Filter (3) button
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = KidooPrimary,
              shadowElevation = 1.dp
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Tune,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "Filter (3)",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color.White
                )
              }
            }

            // Price Sort Toggle Chip (Low to High / High to Low)
            PriceSortToggleChip(
              sortOrder = priceSortOrder,
              onToggleSort = onTogglePriceSort
            )

            // Under ₹5,000 Filter Chip
            val isUnder5k = filterState.maxPrice == 5000
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (isUnder5k) KidooPrimaryFixed else KidooSurfaceContainerLowest,
              border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
              modifier = Modifier.clickable(onClick = onToggleUnder5000)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
              ) {
                Text(
                  text = "Under ₹5,000",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.outline,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            // In Stock Toggle Chip
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (filterState.inStockOnly) KidooPrimaryFixed else KidooSurfaceContainerLowest,
              border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
              modifier = Modifier.clickable(onClick = onToggleInStock)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(KidooSecondaryContainer)
                )
                Text(
                  text = "In Stock",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }

            // Rating Filter Chip
            Surface(
              shape = RoundedCornerShape(20.dp),
              color = if (filterState.minRating >= 4.0) KidooPrimaryFixed else KidooSurfaceContainerLowest,
              border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
              modifier = Modifier.clickable(onClick = onToggleMinRating)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Star,
                  contentDescription = null,
                  tint = KidooTertiaryFixedDim,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = "4★+",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }
    }

    // 2-Column Product Grid Items
    items(products) { product ->
      ProductCard(
        product = product,
        isWishlisted = wishlistIds.contains(product.id),
        onWishlistToggle = { onWishlistToggle(product.id) },
        onAddToCart = { onAddToCart(product) },
        onOpenReviews = onOpenReviews,
        onClick = { onProductClick(product) }
      )
    }

    // Bottom spacing
    item(span = { GridItemSpan(2) }) {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
fun TopCategoryTile(
  label: String,
  count: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconBg: Color,
  iconTint: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .clickable(onClick = onClick),
    color = KidooSurfaceContainerLowest,
    shadowElevation = 1.dp,
    border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 10.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(iconBg),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = iconTint,
          modifier = Modifier.size(22.dp)
        )
      }
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
          fontSize = 11.sp,
          fontWeight = FontWeight.SemiBold
        ),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 6.dp)
      )
      Text(
        text = count,
        style = MaterialTheme.typography.labelSmall.copy(
          fontSize = 9.sp,
          fontWeight = FontWeight.Normal
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
