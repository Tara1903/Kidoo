package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Product

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
  @PrimaryKey
  val productId: String,
  val name: String,
  val description: String = "",
  val price: Int,
  val imageUrl: String,
  val category: String = "",
  val brand: String = "",
  val originalPrice: Int = price,
  val discountPercent: Int = 0,
  val rating: Double = 4.5,
  val reviewCount: Int = 100,
  val themeVariant: String = "",
  val isTopRated: Boolean = false,
  val isStaffPick: Boolean = false,
  val addedAt: Long = System.currentTimeMillis()
) {
  val title: String get() = name

  fun toProduct(): Product {
    return Product(
      id = productId,
      name = name,
      description = description,
      price = price,
      imageUrl = imageUrl,
      category = category,
      brand = brand,
      originalPrice = originalPrice,
      discountPercent = discountPercent,
      rating = rating,
      reviewCount = reviewCount,
      themeVariant = themeVariant,
      isTopRated = isTopRated,
      isStaffPick = isStaffPick,
      isWishlisted = true
    )
  }

  companion object {
    fun fromProduct(product: Product): WishlistItemEntity {
      return WishlistItemEntity(
        productId = product.id,
        name = product.name,
        description = product.description,
        price = product.price,
        imageUrl = product.imageUrl,
        category = product.category,
        brand = product.brand,
        originalPrice = product.originalPrice,
        discountPercent = product.discountPercent,
        rating = product.rating,
        reviewCount = product.reviewCount,
        themeVariant = product.themeVariant,
        isTopRated = product.isTopRated,
        isStaffPick = product.isStaffPick
      )
    }
  }
}
