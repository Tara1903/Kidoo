package com.example.data.model

import java.util.UUID

data class ProductReview(
  val id: String = UUID.randomUUID().toString(),
  val author: String,
  val rating: Int, // 1 to 5 stars
  val date: String,
  val comment: String,
  val isVerifiedPurchase: Boolean = true
)

data class Product(
  val id: String,
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
  val isWishlisted: Boolean = false,
  val reviews: List<ProductReview> = emptyList(),
  val galleryImages: List<String> = emptyList(),
  val specifications: Map<String, String> = emptyMap(),
  val highlights: List<String> = emptyList(),
  val variants: List<String> = emptyList(),
  val stockLeft: Int = 12
) {
  val title: String get() = name

  val allGalleryImages: List<String>
    get() = if (galleryImages.isNotEmpty()) galleryImages else listOf(imageUrl)

  val allVariants: List<String>
    get() = if (variants.isNotEmpty()) variants else if (themeVariant.isNotBlank()) listOf(themeVariant) else emptyList()

  val averageRating: Double
    get() = if (reviews.isNotEmpty()) {
      val avg = reviews.map { it.rating }.average()
      Math.round(avg * 10.0) / 10.0
    } else {
      rating
    }

  val totalReviewsCount: Int
    get() = if (reviews.isNotEmpty()) reviews.size else reviewCount

  constructor(
    id: String,
    title: String,
    category: String,
    brand: String,
    price: Int,
    originalPrice: Int = price,
    discountPercent: Int = 0,
    rating: Double = 4.5,
    reviewCount: Int = 100,
    imageUrl: String,
    themeVariant: String = "",
    isTopRated: Boolean = false,
    isStaffPick: Boolean = false,
    isWishlisted: Boolean = false,
    description: String = "",
    reviews: List<ProductReview> = emptyList(),
    galleryImages: List<String> = emptyList(),
    specifications: Map<String, String> = emptyMap(),
    highlights: List<String> = emptyList(),
    variants: List<String> = emptyList(),
    stockLeft: Int = 12
  ) : this(
    id = id,
    name = title,
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
    isWishlisted = isWishlisted,
    reviews = reviews,
    galleryImages = galleryImages,
    specifications = specifications,
    highlights = highlights,
    variants = variants,
    stockLeft = stockLeft
  )
}

data class CartItem(
  val id: String,
  val product: Product,
  val quantity: Int = 1,
  val selectedVariant: String = ""
) {
  val totalPrice: Int get() = product.price * quantity
  val originalTotalPrice: Int get() = product.originalPrice * quantity
  val totalSavings: Int get() = (product.originalPrice - product.price).coerceAtLeast(0) * quantity
}

data class CategoryData(
  val id: String,
  val name: String,
  val itemCount: String,
  val description: String,
  val images: List<String>,
  val tags: List<String>,
  val badgeText: String? = null,
  val badgeIcon: String? = null,
  val isStaffPick: Boolean = false,
  val isTrending: Boolean = false
)

data class OrderData(
  val orderId: String,
  val orderNumber: String,
  val dateText: String,
  val status: String,
  val isDelivered: Boolean,
  val estimatedArrival: String = "",
  val currentStep: Int = 4, // 1: Placed, 2: Confirmed, 3: Packed, 4: On the way, 5: Delivered
  val itemsTitle: String = "",
  val paymentInfo: String = "",
  val totalAmount: Int = 0,
  val images: List<String> = emptyList(),
  val deliveryHeroName: String = "",
  val deliveryHeroRating: Double = 0.0,
  val deliveryHeroAvatar: String = "",
  val deliveryOtp: String = "",
  val categoryName: String = "",
  val primaryItemTitle: String = "",
  val originalPrice: Int = 0,
  val discountPercent: Int = 0
)

data class UserProfile(
  val name: String = "Aarav Sharma",
  val email: String = "aarav.sharma@email.com",
  val phone: String = "+91 98765 43210",
  val avatarUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuDWuLkD92sWXEjaXXgFXDH__d5LpjAjnQd5A2whbp7MqBYOWhr_W-QpOSlTwa_fOnB6i6ChjtXk5mCQWxVCBu0UPpne_MQOpA6rQcqbXLzBUVHXlMy5KpQb-65lWU2MLVfO6clDI_kU9Z-i_VH_4y8Z83sZtTTT-_xRL8imSUD7IvsTn5qeF9woKMmj-WG8trRvzGFmpNNl2DyyqBT0HA7p3pkwAHfSbbGN_vsbAWMxF4K3KkS2jIwHew",
  val membershipBadge: String = "KIDOO Club Pro Member",
  val ordersCount: Int = 12,
  val wishlistCount: Int = 5,
  val rewardCoins: Int = 1450,
  val walletCredits: Int = 250,
  val activeCouponsCount: Int = 3
)
