package com.example.data.model

/**
 * Represents the sort order for products based on price.
 */
enum class PriceSortOrder(val label: String) {
  NONE("Sort by Price"),
  ASCENDING("Price: Low to High"),
  DESCENDING("Price: High to Low");

  /**
   * Toggles the order between ascending (Low to High) and descending (High to Low).
   * If currently NONE, starts with ASCENDING.
   */
  fun toggle(): PriceSortOrder = when (this) {
    NONE -> ASCENDING
    ASCENDING -> DESCENDING
    DESCENDING -> ASCENDING
  }
}

/**
 * Extension function to sort a list of products by price according to [PriceSortOrder].
 */
fun List<Product>.sortByPrice(order: PriceSortOrder): List<Product> {
  return when (order) {
    PriceSortOrder.ASCENDING -> sortedBy { it.price }
    PriceSortOrder.DESCENDING -> sortedByDescending { it.price }
    PriceSortOrder.NONE -> this
  }
}
