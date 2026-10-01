package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data structure representing a product store category for filtering and browsing.
 */
data class ProductCategory(
  val id: String,
  val name: String,
  val description: String = "",
  val icon: ImageVector = Icons.Default.Category,
  val count: Int = 0
) {
  /**
   * Evaluates if a given product belongs to this category.
   */
  fun matches(product: Product): Boolean {
    if (id == ALL.id) return true
    val cat = product.category.lowercase()
    val title = product.name.lowercase()
    return when (id) {
      STATIONERY.id -> {
        cat.contains("stationery") || title.contains("pen") || title.contains("journal") || title.contains("notebook")
      }
      GADGETS.id -> {
        cat.contains("tech") || cat.contains("gadget") || cat.contains("smart") || title.contains("keyboard") || title.contains("typing")
      }
      HOBBIES.id -> {
        cat.contains("toys") || cat.contains("building") || cat.contains("stem") || cat.contains("robotics") || cat.contains("hobby")
      }
      GAMING.id -> {
        cat.contains("gaming") || title.contains("rgb") || title.contains("cyber")
      }
      ART_CRAFT.id -> {
        cat.contains("art") || cat.contains("craft") || title.contains("paint") || title.contains("color")
      }
      else -> cat.contains(name.lowercase())
    }
  }

  companion object {
    val ALL = ProductCategory(
      id = "all",
      name = "All",
      description = "Browse all store essentials",
      icon = Icons.Default.AllInclusive
    )
    val STATIONERY = ProductCategory(
      id = "stationery",
      name = "Stationery",
      description = "Pens, journals, desk pads & notebooks",
      icon = Icons.Default.Edit
    )
    val GADGETS = ProductCategory(
      id = "gadgets",
      name = "Gadgets",
      description = "Keyboards, chargers, audio & smart gear",
      icon = Icons.Default.Devices
    )
    val HOBBIES = ProductCategory(
      id = "hobbies",
      name = "Hobbies",
      description = "STEM robotics, building kits, RC & puzzles",
      icon = Icons.Default.Extension
    )
    val GAMING = ProductCategory(
      id = "gaming",
      name = "Gaming",
      description = "Mechanical decks, desk mats & RGB gear",
      icon = Icons.Default.SportsEsports
    )
    val ART_CRAFT = ProductCategory(
      id = "art_craft",
      name = "Art & Craft",
      description = "Coloring, origami, DIY craft materials",
      icon = Icons.Default.Palette
    )

    fun defaultCategories(): List<ProductCategory> = listOf(
      ALL,
      STATIONERY,
      GADGETS,
      HOBBIES,
      GAMING,
      ART_CRAFT
    )
  }
}
