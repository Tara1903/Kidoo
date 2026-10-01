package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CategoryData
import com.example.ui.theme.KidooCardBorder
import com.example.ui.theme.KidooPrimary
import com.example.ui.theme.KidooSecondaryContainer
import com.example.ui.theme.KidooSurfaceContainer
import com.example.ui.theme.KidooSurfaceContainerHigh
import com.example.ui.theme.KidooSurfaceContainerLow
import com.example.ui.theme.KidooSurfaceContainerLowest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesScreen(
  categories: List<CategoryData>,
  onSearchClick: () -> Unit,
  onExploreCategory: (String) -> Unit,
  onClaimSpotlight: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Spacing
    item {
      Spacer(modifier = Modifier.height(4.dp))
    }

    // Interactive Search Bar Mock
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .clip(RoundedCornerShape(12.dp))
          .border(1.dp, KidooCardBorder, RoundedCornerShape(12.dp))
          .clickable(onClick = onSearchClick)
          .testTag("categories_search_input_mock"),
        color = KidooSurfaceContainerLowest,
        shadowElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = KidooPrimary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Search in all 8 categories, brands, ...",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "Voice search",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
            Icon(
              imageVector = Icons.Default.QrCodeScanner,
              contentDescription = "Scan code",
              tint = KidooPrimary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }

    // Top Curated Badges Row
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(KidooSurfaceContainerLow)
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(KidooSecondaryContainer)
          )
          Text(
            text = "Curated Collections",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(KidooSurfaceContainerLow)
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = KidooPrimary,
            modifier = Modifier.size(13.dp)
          )
          Text(
            text = "3,400+ Lifestyle & Tech Goods",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    }

    // Categories Accordion-Style Rich Cards
    items(categories) { cat ->
      CategoryCard(
        category = cat,
        onExplore = { onExploreCategory(cat.name) }
      )
    }

    // Trending Subcategories Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
              text = "Trending Subcategories",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Text(
            text = "Updated daily",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = MaterialTheme.colorScheme.outline
          )
        }

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Button(
            onClick = { onExploreCategory("Pens") },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = KidooPrimary,
              contentColor = Color.White
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Text("Gel Pens & Inks", style = MaterialTheme.typography.labelMedium)
          }

          Surface(
            shape = RoundedCornerShape(20.dp),
            color = KidooSurfaceContainerLowest,
            border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
            modifier = Modifier.clickable { onExploreCategory("Bricks") }
          ) {
            Text(
              text = "Botanical Bricks",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(20.dp),
            color = KidooSurfaceContainerLowest,
            border = androidx.compose.foundation.BorderStroke(1.dp, KidooCardBorder),
            modifier = Modifier.clickable { onExploreCategory("Keyboards") }
          ) {
            Text(
              text = "Custom ...",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // KIDOO Spotlight Card
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(20.dp))
          .background(KidooSurfaceContainerLow)
          .padding(bottom = 24.dp)
          .testTag("kidoo_spotlight_card"),
        color = KidooSurfaceContainer,
        shadowElevation = 1.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "KIDOO SPOTLIGHT",
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              ),
              color = KidooPrimary
            )
            Text(
              text = "Build Your Dream Desk Studio",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              ),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(top = 2.dp)
            )
            Text(
              text = "Combine stationery & smart desk gear for a bundled 15% reward credit.",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            Button(
              onClick = onClaimSpotlight,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = KidooPrimary,
                contentColor = Color.White
              ),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
              Text(
                text = "Claim Bundle",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
            }
          }

          // Decorative modern geometric squares
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(KidooPrimary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(KidooPrimary.copy(alpha = 0.35f))
            )
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryCard(
  category: CategoryData,
  onExplore: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .border(1.dp, KidooCardBorder, RoundedCornerShape(18.dp)),
    color = KidooSurfaceContainerLowest,
    shadowElevation = 2.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header: Title, Item Count & Expand Arrow
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = category.name,
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(KidooSurfaceContainerHigh)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
              Text(
                text = category.itemCount,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                ),
                color = KidooPrimary
              )
            }
          }
          Text(
            text = category.description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp)
          )
        }

        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(KidooSurfaceContainerLow),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "Expand",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // 3-Image Collage Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val displayImages = category.images.take(3)
        displayImages.forEachIndexed { index, imgUrl ->
          Box(
            modifier = Modifier
              .weight(1f)
              .height(84.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(KidooSurfaceContainerLow)
          ) {
            AsyncImage(
              model = imgUrl,
              contentDescription = "${category.name} item $index",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )

            // If last image of Stationery, overlay +92 more
            if (category.name == "Stationery" && index == 2) {
              Box(
                modifier = Modifier
                  .fillMaxSize()
                  .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "+92 more",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                  ),
                  color = Color.White
                )
              }
            }
          }
        }
      }

      // Tag chips
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        category.tags.forEach { tag ->
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(KidooSurfaceContainerLow)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = tag,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
              ),
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }

      // Bottom Row: Status badge & "Explore Collection" Button
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (category.badgeText != null) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            val badgeIcon = when (category.badgeIcon) {
              "local_shipping" -> Icons.Default.LocalShipping
              "bolt" -> Icons.Default.Bolt
              "stars" -> Icons.Default.Stars
              "verified" -> Icons.Default.Verified
              "eco" -> Icons.Default.Eco
              else -> Icons.Default.AutoAwesome
            }
            val badgeColor = when {
              category.isStaffPick -> KidooSecondaryContainer
              category.isTrending -> KidooSecondaryContainer
              else -> KidooPrimary
            }
            Icon(
              imageVector = badgeIcon,
              contentDescription = null,
              tint = badgeColor,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = category.badgeText,
              style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              ),
              color = badgeColor
            )
          }
        } else {
          Spacer(modifier = Modifier.width(4.dp))
        }

        // Button: Toys has Orange button, others have Blue button as in screenshot!
        val buttonColor = if (category.isStaffPick || category.name.contains("Toys")) {
          KidooSecondaryContainer
        } else {
          KidooPrimary
        }

        Button(
          onClick = onExplore,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White
          ),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 7.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text(
              text = "Explore Collection",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowForward,
              contentDescription = null,
              modifier = Modifier.size(15.dp)
            )
          }
        }
      }
    }
  }
}
