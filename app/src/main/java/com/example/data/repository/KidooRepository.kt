package com.example.data.repository

import com.example.data.cart.ShoppingCartManager
import com.example.data.local.dao.WishlistDao
import com.example.data.local.entity.WishlistItemEntity
import com.example.data.model.CartItem
import com.example.data.model.CategoryData
import com.example.data.model.OrderData
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

object KidooRepository {

  private var wishlistDao: WishlistDao? = null
  private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  val cartItems: StateFlow<List<CartItem>> = ShoppingCartManager.cartItems

  private val _wishlistIds = MutableStateFlow<Set<String>>(setOf("prod-1", "prod-3", "prod-5", "prod-8"))
  val wishlistIds: StateFlow<Set<String>> = _wishlistIds.asStateFlow()

  fun init(dao: WishlistDao) {
    wishlistDao = dao
    scope.launch {
      try {
        val initialCount = dao.getWishlistCount().first()
        if (initialCount == 0) {
          val defaults = allProducts().filter { _wishlistIds.value.contains(it.id) }
          defaults.forEach { p ->
            dao.insertWishlistItem(WishlistItemEntity.fromProduct(p))
          }
        }
        dao.getAllWishlistItems().collect { list ->
          val ids = list.map { it.productId }.toSet()
          _wishlistIds.value = ids
          _userProfile.value = _userProfile.value.copy(wishlistCount = ids.size)
        }
      } catch (e: Exception) {
        android.util.Log.e("KidooRepository", "Error in wishlist sync", e)
      }
    }
  }

  fun getWishlistFlow(): Flow<List<WishlistItemEntity>> {
    return wishlistDao?.getAllWishlistItems() ?: flow {
      val fallback = allProducts().filter { _wishlistIds.value.contains(it.id) }
        .map { WishlistItemEntity.fromProduct(it) }
      emit(fallback)
    }
  }

  val appliedCoupon: StateFlow<String?> = ShoppingCartManager.appliedCoupon

  private val _userProfile = MutableStateFlow(UserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _activeOrders = MutableStateFlow<List<OrderData>>(initialActiveOrders())
  val activeOrders: StateFlow<List<OrderData>> = _activeOrders.asStateFlow()

  private val _pastOrders = MutableStateFlow<List<OrderData>>(initialPastOrders())
  val pastOrders: StateFlow<List<OrderData>> = _pastOrders.asStateFlow()

  private val _recentSearches = MutableStateFlow(
    listOf("NuPhy Keyboard", "Pastel Gel Pens", "RC STEM Bot", "Desk Mat")
  )
  val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

  private val _products = MutableStateFlow<List<Product>>(seedProducts())
  val products: StateFlow<List<Product>> = _products.asStateFlow()

  fun getProducts(): List<Product> = _products.value

  fun addProductReview(productId: String, review: ProductReview) {
    _products.value = _products.value.map { p ->
      if (p.id == productId) {
        val updatedReviews = listOf(review) + p.reviews
        val newAvg = Math.round(updatedReviews.map { it.rating }.average() * 10.0) / 10.0
        p.copy(
          reviews = updatedReviews,
          rating = newAvg,
          reviewCount = updatedReviews.size
        )
      } else {
        p
      }
    }
  }

  fun updateCartQuantity(cartItemId: String, delta: Int) {
    ShoppingCartManager.changeQuantity(cartItemId, delta)
  }

  fun removeCartItem(cartItemId: String) {
    ShoppingCartManager.removeItem(cartItemId)
  }

  fun addToCart(product: Product) {
    ShoppingCartManager.addItem(product)
  }

  fun toggleWishlist(productId: String) {
    val current = _wishlistIds.value
    val willBeAdded = !current.contains(productId)
    if (!willBeAdded) {
      _wishlistIds.value = current - productId
    } else {
      _wishlistIds.value = current + productId
    }
    _userProfile.value = _userProfile.value.copy(wishlistCount = _wishlistIds.value.size)

    scope.launch {
      wishlistDao?.let { dao ->
        if (willBeAdded) {
          val product = allProducts().find { it.id == productId }
          if (product != null) {
            dao.insertWishlistItem(WishlistItemEntity.fromProduct(product))
          }
        } else {
          dao.deleteWishlistItemById(productId)
        }
      }
    }
  }

  fun removeCoupon() {
    ShoppingCartManager.removeCoupon()
  }

  fun applyCoupon(code: String) {
    ShoppingCartManager.applyCoupon(code)
  }

  fun clearRecentSearches() {
    _recentSearches.value = emptyList()
  }

  fun addRecentSearch(query: String) {
    if (query.isBlank()) return
    val current = _recentSearches.value.toMutableList()
    current.remove(query)
    current.add(0, query)
    _recentSearches.value = current.take(6)
  }

  fun addRewardCoins(coins: Int) {
    _userProfile.value = _userProfile.value.copy(
      rewardCoins = _userProfile.value.rewardCoins + coins
    )
  }

  fun placeOrder(): OrderData {
    val totalAmount = calculateTotalPayable()
    val items = ShoppingCartManager.cartItems.value
    val newOrder = OrderData(
      orderId = "order-${System.currentTimeMillis()}",
      orderNumber = "ORDER KD-${(10000..99999).random()}",
      dateText = "Placed Today • Just now",
      status = "Confirmed",
      isDelivered = false,
      estimatedArrival = "Tomorrow, by 1:00 PM",
      currentStep = 2,
      itemsTitle = "${items.sumOf { it.quantity }} Items in Package",
      paymentInfo = "Paid via UPI",
      totalAmount = totalAmount,
      images = items.map { it.product.imageUrl },
      deliveryHeroName = "Vikram S.",
      deliveryHeroRating = 4.8,
      deliveryHeroAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjiQDW_AaDj2JAOxtEuA-rIPJLxrvKCVnhZ2aPRxi-R0ZLVDIYqQ155cswvKXu8ihXuRgdOpHbOJoftTeqX-pbKnwME1t7faP8QNTWtFXSXYF8W0qtqwGG5UQ3R_xuHWXoykGkZPzZhQA9EGPNqK32-UxgOyfkyKhnAFkmM1SNnQ5FSUPqa3An3KoOAbchIv1uHvI4SaOKRg8wNBPJ-a40fCcPVr4ntmRJ83VBvTmy0niOlsPUhFXTzA",
      deliveryOtp = "${(1000..9999).random()}"
    )
    _activeOrders.value = listOf(newOrder) + _activeOrders.value
    ShoppingCartManager.clearCart()
    _userProfile.value = _userProfile.value.copy(ordersCount = _userProfile.value.ordersCount + 1)
    return newOrder
  }

  fun calculateTotalItems(): Int = ShoppingCartManager.totalItemsCount.value

  fun calculateItemsTotal(): Int = ShoppingCartManager.subtotalPrice.value

  fun calculateBagDiscount(): Int = ShoppingCartManager.totalDiscount.value

  fun calculateCouponSavings(): Int = ShoppingCartManager.couponDiscount.value

  fun calculateTotalPayable(): Int = ShoppingCartManager.totalPrice.value

  fun initialCartItems(): List<CartItem> = ShoppingCartManager.createInitialCartItems()

  private fun initialActiveOrders(): List<OrderData> {
    return listOf(
      OrderData(
        orderId = "order-active-1",
        orderNumber = "ORDER KD-98241",
        dateText = "Placed on Oct 24, 2026 • 11:20 AM",
        status = "Out for Delivery",
        isDelivered = false,
        estimatedArrival = "Today, by 4:30 PM",
        currentStep = 4, // On the way
        itemsTitle = "2 Items in Package",
        paymentInfo = "Paid via UPI",
        totalAmount = 4897,
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuAYRt26kaXPFuLw2P3YyMrSoJ0dlZY7HzEPoJy165IE85rk0pOxVHgDo4ipwcG_aBJXmGo8sSimi92YPMGcvr60Ud3AT27ZJVKReeMX3B_DKu2WL1qNenED8V9X8gAU_K5p6hTMHsdjOEq4Yzv79HkMKSVoL89p_gpPeaum0Hv2TBC-Qo2-j6ySjTq5Ry7hZ9qCIoJE2a9VpFd3Pke7uuJptW3rUBZgjNkwmhOUd1-CL9sRTfl0qWiU7Q",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuBxqo5_hkjnc209BeDSNXmFVcP1_hGHGeBoITGY8sj8yf7FF5xUGzGm3cmXDhzmFz8MIKJr1Nowns_GXJHd3RUexKfaR8MSU_6jWNAJyYr82ogJ29mttgE1oBDoCmQhn-IUkbYil9h1GsvnoX5_Wf1mO1fdmdH2B07btFymCIXkqa-5wVdsN6Wf8R0d93bhl32VXgdm5rh6mIpThX9oebMKMkRJLvztxdMcUa7gDnh4yFF_IaHHEIPh3w"
        ),
        deliveryHeroName = "Rajesh K.",
        deliveryHeroRating = 4.9,
        deliveryHeroAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuDjiQDW_AaDj2JAOxtEuA-rIPJLxrvKCVnhZ2aPRxi-R0ZLVDIYqQ155cswvKXu8ihXuRgdOpHbOJoftTeqX-pbKnwME1t7faP8QNTWtFXSXYF8W0qtqwGG5UQ3R_xuHWXoykGkZPzZhQA9EGPNqK32-UxgOyfkyKhnAFkmM1SNnQ5FSUPqa3An3KoOAbchIv1uHvI4SaOKRg8wNBPJ-a40fCcPVr4ntmRJ83VBvTmy0niOlsPUhFXTzA",
        deliveryOtp = "4921"
      )
    )
  }

  private fun initialPastOrders(): List<OrderData> {
    return listOf(
      OrderData(
        orderId = "order-past-1",
        orderNumber = "ORDER KD-87102",
        dateText = "Delivered on Sunday, Oct 12",
        status = "Delivered",
        isDelivered = true,
        categoryName = "STEM & ROBOTICS",
        primaryItemTitle = "Modular Marble Run STEM Toy",
        totalAmount = 1899,
        originalPrice = 2499,
        discountPercent = 24,
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCze8orD73HrKwx7nSyhaG-BQ_Ghhnaejc8vYqqKh_oWVWB7jmBRaieDJ5LzJFUdx4Deh5smgbyNuC1S4W4hEBo9jy33jG68M8vuyUQmM2HCpojG5jAMYdPmT5IJMQNsDirKocEhZ-2wcrcmfHdUrWfZAGrom0pZmpJTxxdHzUQEq0Ql45nlQN5TDvC4D2YTmhiYLcceumV9izdHYLbiwPofCEngsriuN8meztbPQmPG40K0LIIFotk3A"
        )
      ),
      OrderData(
        orderId = "order-past-2",
        orderNumber = "ORDER KD-86540",
        dateText = "Delivered on Sep 29",
        status = "Delivered",
        isDelivered = true,
        categoryName = "LIFESTYLE STATIONERY",
        primaryItemTitle = "Midori MD Dotted Journal & Pen Set",
        totalAmount = 1240,
        paymentInfo = "Paid via NetBanking",
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCzb8h39SDPjoXPxavbb6tHSp9en9m41NSvMKryVqgsUKg3HVrZsAek8ypstzhRKCuZGxIpW8bVPZS290PlMkZiRQFbiHbQcvxiiRZ55PS6ZXQR7aYPrnty-elOArlO-ALWf1dzCmP28Md17qIhN0Par_3y5DGGib4A07E7vLYiTUnjR2HiswUbxtlaVvemCvfj4-x4pgO94_yhm0gSyk71w2oDSlcJfIJl0mbt45bqSm_rLlbw58DHBw"
        )
      )
    )
  }

  fun allProducts(): List<Product> = _products.value

  private fun seedProducts(): List<Product> {
    return listOf(
      Product(
        id = "prod-1",
        title = "NuPhy Pop Wireless Keyboard",
        category = "Tech Accessories",
        brand = "NuPhy Studio",
        price = 4299,
        originalPrice = 5999,
        discountPercent = 28,
        rating = 4.8,
        reviewCount = 3,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBgjzlDiaqFIEdiqyDg8sEjbV3kmClcg7I8VHH8yarnkzcA4QGg7IEon5Aexe_QDRLC2ZoAIt2SQYS2YJKP7Kv_GBd5-6Vq8PtWEi6MLJhtyqdHc2-v4wnVAACsyDEDWBca0UYI9wRRUtzkqAbuIQIgZjlrLT472jw9fPjxipSqRnP2Vm9wLmVeALa7o2k764XYbpd_8n99G7uIPJUqUmlmTqFLCPERv0rTYTHAWxQEG4xbGeEmoByerw",
        galleryImages = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuBgjzlDiaqFIEdiqyDg8sEjbV3kmClcg7I8VHH8yarnkzcA4QGg7IEon5Aexe_QDRLC2ZoAIt2SQYS2YJKP7Kv_GBd5-6Vq8PtWEi6MLJhtyqdHc2-v4wnVAACsyDEDWBca0UYI9wRRUtzkqAbuIQIgZjlrLT472jw9fPjxipSqRnP2Vm9wLmVeALa7o2k764XYbpd_8n99G7uIPJUqUmlmTqFLCPERv0rTYTHAWxQEG4xbGeEmoByerw",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB0zpWBZOSPu_5gt1WsTlhNS0uAZXbgxzcmMYyWbV30KbGf3E5l5dpBOsFl64X3GHsiWpwnOhEcjL27-p14RTyUqrB9ydm6etYhSu_GygFi0pebhQQhNKoOATazz2OMhakydIZSi8IqL9wJwQLB7HFKaZPo5OxwZ0G5HRDVfHEbp8E0DZQ-qCyp5z5qavFken__HCkRGM54Ifx5vj2kZ7g67J0RfmB8FNkRSuoh3CZBhXVA2vFl2S2JSQ",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB6bI1cgo5HY1kAonSOSFMFJJi38LPQOxlirIbIZfOqVbbOyvOElqXExRy99ZfHgVTHhxi-2YV9F3F2riOskHZ5cmGjKORK94XvNXTfIQU5XjC-1Vug7os26EVuEsaIM6vxVzNYMJHanR9aq4ZviIHjzjvCEWsFCeOAL8vQ-GhvAEdQyft2mKgq0KvDMI7jbDL_boss-YTmxBLdElgiyHeCEicPcxfvU7DmUkgjKUoFE02y8qUrgF4bsw"
        ),
        themeVariant = "Theme: Cyber Mint",
        variants = listOf("Cyber Mint", "Twilight Gray", "Retro White"),
        description = "The NuPhy Pop Wireless Keyboard is an ultra-slim, low-profile mechanical keyboard designed for effortless productivity and aesthetic desktop setups. Engineered with custom lubricated Gateron low-profile switches, it offers satisfying tactile feedback with minimal noise. Features tri-mode connectivity (Bluetooth 5.0, 2.4GHz wireless dongle, and USB Type-C wired) allowing you to switch seamlessly across up to 4 devices simultaneously. An integrated aluminum top frame provides robust structural rigidity while keeping weight down for everyday portability.",
        highlights = listOf(
          "Tri-Mode Connectivity: Bluetooth 5.0, 2.4G & USB-C",
          "Hot-Swappable Low-Profile Gateron Mechanical Switches",
          "Ultra-Thin CNC Machined Aluminum Frame",
          "48-Hour Battery Life with RGB Ambient Side Lighting"
        ),
        specifications = mapOf(
          "Brand" to "NuPhy Studio",
          "Model" to "Air75 Pop Edition V2",
          "Connectivity" to "Bluetooth 5.0 / 2.4GHz RF / USB Type-C",
          "Layout" to "75% ANSI (84 Keys)",
          "Switch Type" to "Gateron Low-Profile Mechanical (Hot-Swappable)",
          "Keycap Material" to "Double-shot PBT (Coast Profile)",
          "Battery Capacity" to "2500 mAh Lithium-Polymer (Up to 48 hrs RGB)",
          "Dimensions" to "315.7 mm x 132.6 mm x 16.0 mm",
          "Weight" to "523 g",
          "OS Compatibility" to "macOS, iOS, Windows, Android, Linux",
          "Warranty" to "1 Year Official Manufacturer Warranty",
          "Box Contents" to "Keyboard, USB-C Cable, 2.4G Receiver, Extra Keycaps, Keycap Puller"
        ),
        reviews = listOf(
          ProductReview(author = "Aarav Sharma", rating = 5, date = "Oct 18, 2026", comment = "Incredible tactile feel and compact layout. Connects seamlessly with both iPad and Mac!"),
          ProductReview(author = "Tanvi Patel", rating = 5, date = "Oct 12, 2026", comment = "The RGB backlighting and keycaps color palette are so aesthetic on my desk setup."),
          ProductReview(author = "Karan M.", rating = 4, date = "Sep 28, 2026", comment = "Very solid battery life, slightly heavy for carrying daily but great quality.")
        )
      ),
      Product(
        id = "prod-4",
        title = "Retro Classic Typing Deck",
        category = "Tech Accessories",
        brand = "RetroType",
        price = 3199,
        originalPrice = 4499,
        discountPercent = 29,
        rating = 4.7,
        reviewCount = 2,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuAh-nI-tiHF0Zrjzo4gHcoXLhmE5X9VqxdBZaNbISsIvgLEmXsBNzjmgN-ve-JvisrUuxA-HkZir7O6DdlPsYGfW5Sg6VkjOnK559d5eyQDMpM05s3dX41A4IQUYlZExsA4kSiaaMGgRvxLurno1vm4Q5aN0ozpZunT6-4K1GOfnSWcI-4cU31gEkb3pzZ-b68n1qMPt1OqsexHhsxzif7SxBuZYr5cxC4VfRf2k2g-MIGZExUldujriQ",
        galleryImages = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuAh-nI-tiHF0Zrjzo4gHcoXLhmE5X9VqxdBZaNbISsIvgLEmXsBNzjmgN-ve-JvisrUuxA-HkZir7O6DdlPsYGfW5Sg6VkjOnK559d5eyQDMpM05s3dX41A4IQUYlZExsA4kSiaaMGgRvxLurno1vm4Q5aN0ozpZunT6-4K1GOfnSWcI-4cU31gEkb3pzZ-b68n1qMPt1OqsexHhsxzif7SxBuZYr5cxC4VfRf2k2g-MIGZExUldujriQ",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB6bI1cgo5HY1kAonSOSFMFJJi38LPQOxlirIbIZfOqVbbOyvOElqXExRy99ZfHgVTHhxi-2YV9F3F2riOskHZ5cmGjKORK94XvNXTfIQU5XjC-1Vug7os26EVuEsaIM6vxVzNYMJHanR9aq4ZviIHjzjvCEWsFCeOAL8vQ-GhvAEdQyft2mKgq0KvDMI7jbDL_boss-YTmxBLdElgiyHeCEicPcxfvU7DmUkgjKUoFE02y8qUrgF4bsw"
        ),
        themeVariant = "Edition: Vintage Ivory",
        variants = listOf("Vintage Ivory", "Olive Green", "Midnight Black"),
        description = "Channel nostalgia with the Retro Classic Typing Deck. Modeled after 1950s typewriter aesthetics, this keyboard pairs authentic clicky mechanical switches with round electroplated keycaps. Includes a tactile top volume knob and warm amber LED backlighting for evening typing sessions.",
        highlights = listOf(
          "Typewriter-Style Circular Electroplated Keycaps",
          "Tactile Clicky Mechanical Blue Switches",
          "Dedicated Analog Volume & Brightness Roller Knob",
          "Bluetooth 5.1 & Detachable Braided Cable"
        ),
        specifications = mapOf(
          "Brand" to "RetroType",
          "Model" to "Classic-83",
          "Switch Type" to "Outemu Clicky Blue Switches",
          "Connectivity" to "Bluetooth 5.1 & USB-C",
          "Battery" to "2000 mAh Rechargeable",
          "Backlight" to "Warm Ambient White / Amber (14 Modes)",
          "Warranty" to "1 Year Brand Warranty"
        ),
        reviews = listOf(
          ProductReview(author = "Meera Nair", rating = 5, date = "Oct 15, 2026", comment = "Sounds like an authentic vintage typewriter! Super fun for creative writing."),
          ProductReview(author = "Vikram R.", rating = 4, date = "Sep 30, 2026", comment = "Round keycaps take a day to get used to, but the look is unbeatable.")
        )
      ),
      Product(
        id = "prod-5",
        title = "Cyber RGB Mac/Win Keyboard",
        category = "Tech Accessories",
        brand = "CyberGamer",
        price = 5499,
        originalPrice = 7299,
        discountPercent = 25,
        rating = 5.0,
        reviewCount = 2,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB0zpWBZOSPu_5gt1WsTlhNS0uAZXbgxzcmMYyWbV30KbGf3E5l5dpBOsFl64X3GHsiWpwnOhEcjL27-p14RTyUqrB9ydm6etYhSu_GygFi0pebhQQhNKoOATazz2OMhakydIZSi8IqL9wJwQLB7HFKaZPo5OxwZ0G5HRDVfHEbp8E0DZQ-qCyp5z5qavFken__HCkRGM54Ifx5vj2kZ7g67J0RfmB8FNkRSuoh3CZBhXVA2vFl2S2JSQ",
        galleryImages = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB0zpWBZOSPu_5gt1WsTlhNS0uAZXbgxzcmMYyWbV30KbGf3E5l5dpBOsFl64X3GHsiWpwnOhEcjL27-p14RTyUqrB9ydm6etYhSu_GygFi0pebhQQhNKoOATazz2OMhakydIZSi8IqL9wJwQLB7HFKaZPo5OxwZ0G5HRDVfHEbp8E0DZQ-qCyp5z5qavFken__HCkRGM54Ifx5vj2kZ7g67J0RfmB8FNkRSuoh3CZBhXVA2vFl2S2JSQ",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuBgjzlDiaqFIEdiqyDg8sEjbV3kmClcg7I8VHH8yarnkzcA4QGg7IEon5Aexe_QDRLC2ZoAIt2SQYS2YJKP7Kv_GBd5-6Vq8PtWEi6MLJhtyqdHc2-v4wnVAACsyDEDWBca0UYI9wRRUtzkqAbuIQIgZjlrLT472jw9fPjxipSqRnP2Vm9wLmVeALa7o2k764XYbpd_8n99G7uIPJUqUmlmTqFLCPERv0rTYTHAWxQEG4xbGeEmoByerw"
        ),
        isTopRated = true,
        themeVariant = "Edition: Pro Gasket RGB",
        variants = listOf("Pro Gasket RGB", "Stealth Dark", "Cyber Neon"),
        description = "Engineered for enthusiasts and gamers alike, this custom gasket-mounted keyboard dampens vibration for a deep, creamy acoustic sound profile. Features per-key South-facing RGB lighting and full programmability with QMK/VIA support.",
        highlights = listOf(
          "Gasket Mounted Structure with Poron Foam Sound Dampening",
          "Pre-lubed Custom Linear Silver Switches (45gf)",
          "South-Facing Per-Key 16.8M Color RGB",
          "Mac & Windows Switchable Layout with Dedicated Keycaps"
        ),
        specifications = mapOf(
          "Brand" to "CyberGamer",
          "Model" to "CG-Pro98",
          "Mounting Type" to "Gasket Mounted (Dual Silicone Dampers)",
          "Polling Rate" to "1000 Hz in 2.4G & Wired Mode",
          "Battery" to "4000 mAh High-Capacity Li-Ion",
          "Warranty" to "2 Years Premium On-Site Warranty"
        ),
        reviews = listOf(
          ProductReview(author = "Rohan Verma", rating = 5, date = "Oct 20, 2026", comment = "Hot-swappable switches and gasket mount makes it feel like an expensive custom build."),
          ProductReview(author = "Siddharth K.", rating = 5, date = "Oct 14, 2026", comment = "Ultra-low latency over 2.4GHz wireless dongle. Zero dropped inputs during gaming.")
        )
      ),
      Product(
        id = "prod-6",
        title = "Minimalist Quiet Bluetooth Board",
        category = "Tech Accessories",
        brand = "Nordic Craft",
        price = 2899,
        originalPrice = 3499,
        discountPercent = 17,
        rating = 4.5,
        reviewCount = 2,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB6bI1cgo5HY1kAonSOSFMFJJi38LPQOxlirIbIZfOqVbbOyvOElqXExRy99ZfHgVTHhxi-2YV9F3F2riOskHZ5cmGjKORK94XvNXTfIQU5XjC-1Vug7os26EVuEsaIM6vxVzNYMJHanR9aq4ZviIHjzjvCEWsFCeOAL8vQ-GhvAEdQyft2mKgq0KvDMI7jbDL_boss-YTmxBLdElgiyHeCEicPcxfvU7DmUkgjKUoFE02y8qUrgF4bsw",
        themeVariant = "Color: Sand Stone",
        variants = listOf("Sand Stone", "Matte Slate", "Arctic White"),
        description = "Silent, featherlight, and exceptionally thin. The Nordic Craft Quiet Board was created for minimalists, writers, and remote professionals who value distraction-free quiet environments. Scissor-switch key mechanisms offer a soft, cushioned bottom-out feeling.",
        highlights = listOf(
          "Silent Scissor-Switch Key Action (< 20dB)",
          "Ultra-Slim 5.8mm Forward Profile",
          "Multi-Device Bluetooth 5.2 Switching (3 Devices)",
          "120 Days Standby Battery Life"
        ),
        specifications = mapOf(
          "Brand" to "Nordic Craft",
          "Model" to "NC-SlimQ",
          "Mechanism" to "Precision Scissor Switch",
          "Weight" to "380 g Ultra-Light",
          "Charging" to "USB-C Fast Charging (Full in 90 min)",
          "Warranty" to "1 Year Brand Warranty"
        ),
        reviews = listOf(
          ProductReview(author = "Ananya Sen", rating = 5, date = "Oct 19, 2026", comment = "Silent keys are perfect for quiet coffee shop work and late night study sessions."),
          ProductReview(author = "Deepak G.", rating = 4, date = "Oct 05, 2026", comment = "Very sleek profile, pairs instantly with my phone and tablet.")
        )
      ),
      Product(
        id = "prod-2",
        title = "Pastel Minimal Gel Pens Set",
        category = "Stationery",
        brand = "KIDOO Craft",
        price = 449,
        originalPrice = 699,
        discountPercent = 35,
        rating = 4.8,
        reviewCount = 3,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuA-QCep_J8k8mbdsSXfylFFpSBax4mofAri0XTVSMuCGae0_Nt3XMEHIWpRKwcNQW_V9xhsTxas6H3yHEyiaPVo-VKpKEezmg7qoWsu0d-2Yem1EECHzUGEZofmiTNIlW2fSIrJSFHDiJua_nPKW5tY3zDo2A4k5pPHPF8P_o2KO852opusKPh3-z_Fm77nds62oF4M03pDYK1EX1NVHPMKNlFccxwyX3N4QG38ifoUwKEWi6TZS9hE4Q",
        galleryImages = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuA-QCep_J8k8mbdsSXfylFFpSBax4mofAri0XTVSMuCGae0_Nt3XMEHIWpRKwcNQW_V9xhsTxas6H3yHEyiaPVo-VKpKEezmg7qoWsu0d-2Yem1EECHzUGEZofmiTNIlW2fSIrJSFHDiJua_nPKW5tY3zDo2A4k5pPHPF8P_o2KO852opusKPh3-z_Fm77nds62oF4M03pDYK1EX1NVHPMKNlFccxwyX3N4QG38ifoUwKEWi6TZS9hE4Q",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCzb8h39SDPjoXPxavbb6tHSp9en9m41NSvMKryVqgsUKg3HVrZsAek8ypstzhRKCuZGxIpW8bVPZS290PlMkZiRQFbiHbQcvxiiRZ55PS6ZXQR7aYPrnty-elOArlO-ALWf1dzCmP28Md17qIhN0Par_3y5DGGib4A07E7vLYiTUnjR2HiswUbxtlaVvemCvfj4-x4pgO94_yhm0gSyk71w2oDSlcJfIJl0mbt45bqSm_rLlbw58DHBw"
        ),
        themeVariant = "Pack: 10-Pack Assorted",
        variants = listOf("10-Pack Assorted", "Pastel Noir 5-Pack", "Vintage Retro 8-Pack"),
        description = "Designed for smooth note-taking, sketching, and bullet journaling. These 0.5mm Japanese quick-drying gel pens glide effortlessly without skips or smudges. The matte pastel barrels feature a soft silicone comfort grip that prevents finger fatigue during marathon study hours.",
        highlights = listOf(
          "Japanese 0.5mm Tungsten Carbide Roller Ball",
          "Instant-Dry Pigment Ink (No Smudging with Highlighters)",
          "Velvet Touch Ergonomic Silicone Grip",
          "Non-Toxic & Acid-Free Safe Formula"
        ),
        specifications = mapOf(
          "Brand" to "KIDOO Craft",
          "Tip Size" to "0.5 mm Extra Fine Needle Point",
          "Ink Type" to "Fast-Drying Water-Resistant Gel Ink",
          "Pack Size" to "10 Pens in Protective Acrylic Case",
          "Barrel Finish" to "Soft Matte Touch Silicone Grip",
          "Country of Origin" to "Japan"
        ),
        reviews = listOf(
          ProductReview(author = "Sneha Iyer", rating = 5, date = "Oct 21, 2026", comment = "The pastel shades are so soft and relaxing to journal with. Absolutely zero bleed-through!"),
          ProductReview(author = "Pooja Hegde", rating = 5, date = "Oct 16, 2026", comment = "0.5mm tip produces crisp, dry lines without smudging even when using highlighters."),
          ProductReview(author = "Rahul Joshi", rating = 4, date = "Oct 02, 2026", comment = "Comfortable rubber grip for long writing sessions during exams.")
        )
      ),
      Product(
        id = "prod-3",
        title = "CyberBot STEM Robotics Kit",
        category = "Toys & Building",
        brand = "CyberBot Lab",
        price = 2499,
        originalPrice = 3999,
        discountPercent = 38,
        rating = 4.9,
        reviewCount = 3,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuB2kPtI2OotgS8dganLjEiCDuW8Z4x3yRzmaGo_tQjHPHhzYnZOv4-MLXkSN89dUOJI4NldIhwbk2c9j34UXqiBKI2J7ZS2vtYW6Ju5PoWBICKTjuinu2ON-7zVHMv1pLXDnUVbpsO1G1Q8Dyb6t2RQOPg4RuydZI2FV6lC7cL_EZIw1qBEF9_nxWzijbBnQ7KwLzJxdf0-wXemDPisi7CRgYaq7Om6LLXwANMmPUFM5uW4pXwQlLDFKA",
        galleryImages = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB2kPtI2OotgS8dganLjEiCDuW8Z4x3yRzmaGo_tQjHPHhzYnZOv4-MLXkSN89dUOJI4NldIhwbk2c9j34UXqiBKI2J7ZS2vtYW6Ju5PoWBICKTjuinu2ON-7zVHMv1pLXDnUVbpsO1G1Q8Dyb6t2RQOPg4RuydZI2FV6lC7cL_EZIw1qBEF9_nxWzijbBnQ7KwLzJxdf0-wXemDPisi7CRgYaq7Om6LLXwANMmPUFM5uW4pXwQlLDFKA",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCze8orD73HrKwx7nSyhaG-BQ_Ghhnaejc8vYqqKh_oWVWB7jmBRaieDJ5LzJFUdx4Deh5smgbyNuC1S4W4hEBo9jy33jG68M8vuyUQmM2HCpojG5jAMYdPmT5IJMQNsDirKocEhZ-2wcrcmfHdUrWfZAGrom0pZmpJTxxdHzUQEq0Ql45nlQN5TDvC4D2YTmhiYLcceumV9izdHYLbiwPofCEngsriuN8meztbPQmPG40K0LIIFotk3A"
        ),
        isStaffPick = true,
        themeVariant = "Edition: Explorer V1",
        variants = listOf("Explorer V1", "AI Vision Kit", "Solar Power Bundle"),
        description = "Empower young minds with hands-on coding and mechanical assembly. The CyberBot STEM Robotics Kit contains over 420 snap-together precision pieces, dual DC electric motors, an ultrasonic obstacle-avoidance sensor, and an intuitive drag-and-drop block coding mobile app.",
        highlights = listOf(
          "Over 420 Snap-Together Modular Parts (10 Models in 1)",
          "Ultrasonic Distance & Line-Tracking Optical Sensors",
          "Scratch-Based Drag-and-Drop Coding Companion App",
          "Rechargeable Core Module (USB-C Included)"
        ),
        specifications = mapOf(
          "Brand" to "CyberBot Lab",
          "Age Recommendation" to "8+ Years",
          "Piece Count" to "428 Precision Components",
          "Sensors" to "Ultrasonic, Line Follower, Sound Sensor",
          "App Support" to "Android 8.0+ & iOS 12+",
          "Safety" to "EN71 & ASTM Child Toy Safety Certified",
          "Warranty" to "6 Months Warranty on Electronics"
        ),
        reviews = listOf(
          ProductReview(author = "Arjun Kapoor", rating = 5, date = "Oct 22, 2026", comment = "Assembled with my 10-year-old over the weekend. The companion app programming is fantastic!"),
          ProductReview(author = "Neha Chawla", rating = 5, date = "Oct 17, 2026", comment = "High-quality motors and sensors. Great introduction to robotics and logic."),
          ProductReview(author = "Devendra P.", rating = 4, date = "Sep 25, 2026", comment = "Clear illustrated instruction manual. Really stimulates curiosity.")
        )
      ),
      Product(
        id = "prod-7",
        title = "Modular Marble Run STEM Toy",
        category = "Toys & Building",
        brand = "KIDOO STEM",
        price = 1899,
        originalPrice = 2499,
        discountPercent = 24,
        rating = 4.8,
        reviewCount = 2,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCze8orD73HrKwx7nSyhaG-BQ_Ghhnaejc8vYqqKh_oWVWB7jmBRaieDJ5LzJFUdx4Deh5smgbyNuC1S4W4hEBo9jy33jG68M8vuyUQmM2HCpojG5jAMYdPmT5IJMQNsDirKocEhZ-2wcrcmfHdUrWfZAGrom0pZmpJTxxdHzUQEq0Ql45nlQN5TDvC4D2YTmhiYLcceumV9izdHYLbiwPofCEngsriuN8meztbPQmPG40K0LIIFotk3A",
        galleryImages = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCze8orD73HrKwx7nSyhaG-BQ_Ghhnaejc8vYqqKh_oWVWB7jmBRaieDJ5LzJFUdx4Deh5smgbyNuC1S4W4hEBo9jy33jG68M8vuyUQmM2HCpojG5jAMYdPmT5IJMQNsDirKocEhZ-2wcrcmfHdUrWfZAGrom0pZmpJTxxdHzUQEq0Ql45nlQN5TDvC4D2YTmhiYLcceumV9izdHYLbiwPofCEngsriuN8meztbPQmPG40K0LIIFotk3A",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB2kPtI2OotgS8dganLjEiCDuW8Z4x3yRzmaGo_tQjHPHhzYnZOv4-MLXkSN89dUOJI4NldIhwbk2c9j34UXqiBKI2J7ZS2vtYW6Ju5PoWBICKTjuinu2ON-7zVHMv1pLXDnUVbpsO1G1Q8Dyb6t2RQOPg4RuydZI2FV6lC7cL_EZIw1qBEF9_nxWzijbBnQ7KwLzJxdf0-wXemDPisi7CRgYaq7Om6LLXwANMmPUFM5uW4pXwQlLDFKA"
        ),
        themeVariant = "Edition: Kinetic 150-Piece",
        variants = listOf("Kinetic 150-Piece", "Mega Pro 250-Piece"),
        description = "Experience the thrill of kinetic physics and gravity engineering. Build elaborate winding tracks, vortex funnels, elevator lifts, and looping drops. Constructed with crystal-clear durable ABS plastic to watch high-velocity marbles roll through every curve.",
        highlights = listOf(
          "150 Kinetic Gravity Track Pieces & 30 Glass Marbles",
          "Motorized Spiral Marble Lift for Continuous Action",
          "Smooth Snapping Connectors (Compatible with Major Brands)",
          "BPA-Free Shatterproof Non-Toxic Materials"
        ),
        specifications = mapOf(
          "Brand" to "KIDOO STEM",
          "Age Group" to "5+ Years",
          "Components" to "150 Track Pieces + 30 High-Speed Marbles",
          "Material" to "High-Grade BPA-Free ABS Plastic",
          "Safety" to "CE & BIS Certified Child Safe"
        ),
        reviews = listOf(
          ProductReview(author = "Kavita Rao", rating = 5, date = "Oct 18, 2026", comment = "Gravity-powered loops and kinetic tracks keep kids engaged for hours screen-free."),
          ProductReview(author = "Amit Trivedi", rating = 4, date = "Oct 08, 2026", comment = "Sturdy BPA-free pieces that snap together securely without falling apart.")
        )
      ),
      Product(
        id = "prod-8",
        title = "Midori MD Dotted Journal & Pen Set",
        category = "Stationery",
        brand = "Midori Japan",
        price = 1240,
        originalPrice = 1600,
        discountPercent = 22,
        rating = 5.0,
        reviewCount = 2,
        imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCzb8h39SDPjoXPxavbb6tHSp9en9m41NSvMKryVqgsUKg3HVrZsAek8ypstzhRKCuZGxIpW8bVPZS290PlMkZiRQFbiHbQcvxiiRZ55PS6ZXQR7aYPrnty-elOArlO-ALWf1dzCmP28Md17qIhN0Par_3y5DGGib4A07E7vLYiTUnjR2HiswUbxtlaVvemCvfj4-x4pgO94_yhm0gSyk71w2oDSlcJfIJl0mbt45bqSm_rLlbw58DHBw",
        galleryImages = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCzb8h39SDPjoXPxavbb6tHSp9en9m41NSvMKryVqgsUKg3HVrZsAek8ypstzhRKCuZGxIpW8bVPZS290PlMkZiRQFbiHbQcvxiiRZ55PS6ZXQR7aYPrnty-elOArlO-ALWf1dzCmP28Md17qIhN0Par_3y5DGGib4A07E7vLYiTUnjR2HiswUbxtlaVvemCvfj4-x4pgO94_yhm0gSyk71w2oDSlcJfIJl0mbt45bqSm_rLlbw58DHBw",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuA-QCep_J8k8mbdsSXfylFFpSBax4mofAri0XTVSMuCGae0_Nt3XMEHIWpRKwcNQW_V9xhsTxas6H3yHEyiaPVo-VKpKEezmg7qoWsu0d-2Yem1EECHzUGEZofmiTNIlW2fSIrJSFHDiJua_nPKW5tY3zDo2A4k5pPHPF8P_o2KO852opusKPh3-z_Fm77nds62oF4M03pDYK1EX1NVHPMKNlFccxwyX3N4QG38ifoUwKEWi6TZS9hE4Q"
        ),
        themeVariant = "Size: A5 Dotted (176 Pages)",
        variants = listOf("A5 Dotted (176 Pages)", "A5 Grid", "A5 Blank Leatherette"),
        description = "Crafted for lovers of writing and stationery. The Midori MD Journal opens completely 180° flat with thread-stitched binding. Its proprietary MD paper prevents ink bleeding and feathering, providing the ultimate tactile sensory feedback with fountain pens, gel ink, or pencils.",
        highlights = listOf(
          "Authentic Japanese MD Bleed-Resistant 80gsm Paper",
          "180-Degree Lay-Flat Thread-Stitched Spine",
          "5mm Subtle Dot Grid for Neat Handwriting",
          "Includes Brass Metal Pen & Index Sticker Labels"
        ),
        specifications = mapOf(
          "Brand" to "Midori Japan",
          "Paper Weight" to "80 gsm Premium MD Paper",
          "Pages" to "176 Pages (Numbered)",
          "Binding" to "Thread-Stitched Lay-Flat Binding",
          "Dimensions" to "210 mm x 148 mm (A5 Size)",
          "Country of Origin" to "Japan"
        ),
        reviews = listOf(
          ProductReview(author = "Divya Saxena", rating = 5, date = "Oct 23, 2026", comment = "Premium Japanese paper that opens completely flat. Handles fountain pen ink like a dream!"),
          ProductReview(author = "Manish Bhatia", rating = 5, date = "Oct 11, 2026", comment = "The dotted grid spacing is just right. Includes bookmark ribbons and index labels.")
        )
      )
    )
  }

  fun getCategories(): List<CategoryData> {
    return listOf(
      CategoryData(
        id = "cat-stationery",
        name = "Stationery",
        itemCount = "480+ items",
        description = "Premium pens, tactile journals & desk setups",
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuAYRt26kaXPFuLw2P3YyMrSoJ0dlZY7HzEPoJy165IE85rk0pOxVHgDo4ipwcG_aBJXmGo8sSimi92YPMGcvr60Ud3AT27ZJVKReeMX3B_DKu2WL1qNenED8V9X8gAU_K5p6hTMHsdjOEq4Yzv79HkMKSVoL89p_gpPeaum0Hv2TBC-Qo2-j6ySjTq5Ry7hZ9qCIoJE2a9VpFd3Pke7uuJptW3rUBZgjNkwmhOUd1-CL9sRTfl0qWiU7Q",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuBxqo5_hkjnc209BeDSNXmFVcP1_hGHGeBoITGY8sj8yf7FF5xUGzGm3cmXDhzmFz8MIKJr1Nowns_GXJHd3RUexKfaR8MSU_6jWNAJyYr82ogJ29mttgE1oBDoCmQhn-IUkbYil9h1GsvnoX5_Wf1mO1fdmdH2B07btFymCIXkqa-5wVdsN6Wf8R0d93bhl32VXgdm5rh6mIpThX9oebMKMkRJLvztxdMcUa7gDnh4yFF_IaHHEIPh3w",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCzb8h39SDPjoXPxavbb6tHSp9en9m41NSvMKryVqgsUKg3HVrZsAek8ypstzhRKCuZGxIpW8bVPZS290PlMkZiRQFbiHbQcvxiiRZ55PS6ZXQR7aYPrnty-elOArlO-ALWf1dzCmP28Md17qIhN0Par_3y5DGGib4A07E7vLYiTUnjR2HiswUbxtlaVvemCvfj4-x4pgO94_yhm0gSyk71w2oDSlcJfIJl0mbt45bqSm_rLlbw58DHBw"
        ),
        tags = listOf("Fine Pens", "Notebooks", "Art Supplies", "School Essentials", "Desk Organizers"),
        badgeText = "Express Next-Day",
        badgeIcon = "local_shipping"
      ),
      CategoryData(
        id = "cat-gadgets",
        name = "Smart Gadgets",
        itemCount = "310+ items",
        description = "Ambient audio, desk lights & wireless gear",
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuDVC8qRxro_LMGEBiwBVjGFdjk3cgPyPiSxSPdv76IPEo_nJjdZ9YOt-w-gLE-tTVGYCCY-Z3uzENBrCbu9N8wkzxrBKlD8V5q6EqgImwiFQTM1oQ2Qi8uzWxeOrqp5FqLoT7tFf38kEUZY85xKc0njZlye595bSMoff7j04Jqxx77gie-oWKd5riafIyOpMCk6sCwkKPBZZ-X8zeA9NdzDOCJCHjel68Hb3WQDC2b9paZbjRengoAp8w",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB0zpWBZOSPu_5gt1WsTlhNS0uAZXbgxzcmMYyWbV30KbGf3E5l5dpBOsFl64X3GHsiWpwnOhEcjL27-p14RTyUqrB9ydm6etYhSu_GygFi0pebhQQhNKoOATazz2OMhakydIZSi8IqL9wJwQLB7HFKaZPo5OxwZ0G5HRDVfHEbp8E0DZQ-qCyp5z5qavFken__HCkRGM54Ifx5vj2kZ7g67J0RfmB8FNkRSuoh3CZBhXVA2vFl2S2JSQ",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB6bI1cgo5HY1kAonSOSFMFJJi38LPQOxlirIbIZfOqVbbOyvOElqXExRy99ZfHgVTHhxi-2YV9F3F2riOskHZ5cmGjKORK94XvNXTfIQU5XjC-1Vug7os26EVuEsaIM6vxVzNYMJHanR9aq4ZviIHjzjvCEWsFCeOAL8vQ-GhvAEdQyft2mKgq0KvDMI7jbDL_boss-YTmxBLdElgiyHeCEicPcxfvU7DmUkgjKUoFE02y8qUrgF4bsw"
        ),
        tags = listOf("Portable Audio", "Desk Lighting", "Smart Clock", "Bluetooth Trackers"),
        badgeText = "Trending +44%",
        badgeIcon = "bolt",
        isTrending = true
      ),
      CategoryData(
        id = "cat-toys",
        name = "Toys & Building",
        itemCount = "540+ items",
        description = "Modular brick architecture, robotics & RC",
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCze8orD73HrKwx7nSyhaG-BQ_Ghhnaejc8vYqqKh_oWVWB7jmBRaieDJ5LzJFUdx4Deh5smgbyNuC1S4W4hEBo9jy33jG68M8vuyUQmM2HCpojG5jAMYdPmT5IJMQNsDirKocEhZ-2wcrcmfHdUrWfZAGrom0pZmpJTxxdHzUQEq0Ql45nlQN5TDvC4D2YTmhiYLcceumV9izdHYLbiwPofCEngsriuN8meztbPQmPG40K0LIIFotk3A",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB2kPtI2OotgS8dganLjEiCDuW8Z4x3yRzmaGo_tQjHPHhzYnZOv4-MLXkSN89dUOJI4NldIhwbk2c9j34UXqiBKI2J7ZS2vtYW6Ju5PoWBICKTjuinu2ON-7zVHMv1pLXDnUVbpsO1G1Q8Dyb6t2RQOPg4RuydZI2FV6lC7cL_EZIw1qBEF9_nxWzijbBnQ7KwLzJxdf0-wXemDPisi7CRgYaq7Om6LLXwANMmPUFM5uW4pXwQlLDFKA",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuA-QCep_J8k8mbdsSXfylFFpSBax4mofAri0XTVSMuCGae0_Nt3XMEHIWpRKwcNQW_V9xhsTxas6H3yHEyiaPVo-VKpKEezmg7qoWsu0d-2Yem1EECHzUGEZofmiTNIlW2fSIrJSFHDiJua_nPKW5tY3zDo2A4k5pPHPF8P_o2KO852opusKPh3-z_Fm77nds62oF4M03pDYK1EX1NVHPMKNlFccxwyX3N4QG38ifoUwKEWi6TZS9hE4Q"
        ),
        tags = listOf("Modular Bricks", "Remote Control", "STEM Mechanics", "Designer Plush"),
        badgeText = "Staff Pick",
        badgeIcon = "stars",
        isStaffPick = true
      ),
      CategoryData(
        id = "cat-games",
        name = "Games & Hobbies",
        itemCount = "290+ items",
        description = "Board games, trading cards & precision puzzles",
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuAh-nI-tiHF0Zrjzo4gHcoXLhmE5X9VqxdBZaNbISsIvgLEmXsBNzjmgN-ve-JvisrUuxA-HkZir7O6DdlPsYGfW5Sg6VkjOnK559d5eyQDMpM05s3dX41A4IQUYlZExsA4kSiaaMGgRvxLurno1vm4Q5aN0ozpZunT6-4K1GOfnSWcI-4cU31gEkb3pzZ-b68n1qMPt1OqsexHhsxzif7SxBuZYr5cxC4VfRf2k2g-MIGZExUldujriQ",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuBgjzlDiaqFIEdiqyDg8sEjbV3kmClcg7I8VHH8yarnkzcA4QGg7IEon5Aexe_QDRLC2ZoAIt2SQYS2YJKP7Kv_GBd5-6Vq8PtWEi6MLJhtyqdHc2-v4wnVAACsyDEDWBca0UYI9wRRUtzkqAbuIQIgZjlrLT472jw9fPjxipSqRnP2Vm9wLmVeALa7o2k764XYbpd_8n99G7uIPJUqUmlmTqFLCPERv0rTYTHAWxQEG4xbGeEmoByerw",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCze8orD73HrKwx7nSyhaG-BQ_Ghhnaejc8vYqqKh_oWVWB7jmBRaieDJ5LzJFUdx4Deh5smgbyNuC1S4W4hEBo9jy33jG68M8vuyUQmM2HCpojG5jAMYdPmT5IJMQNsDirKocEhZ-2wcrcmfHdUrWfZAGrom0pZmpJTxxdHzUQEq0Ql45nlQN5TDvC4D2YTmhiYLcceumV9izdHYLbiwPofCEngsriuN8meztbPQmPG40K0LIIFotk3A"
        ),
        tags = listOf("Board Games", "Puzzles & Brainteasers", "Model Kits", "Trading Cards"),
        badgeText = "Multiplayer & Solo",
        badgeIcon = "groups"
      ),
      CategoryData(
        id = "cat-education",
        name = "Educational & Learning",
        itemCount = "410+ items",
        description = "Robotics coding, biology optics & astronomy",
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB2kPtI2OotgS8dganLjEiCDuW8Z4x3yRzmaGo_tQjHPHhzYnZOv4-MLXkSN89dUOJI4NldIhwbk2c9j34UXqiBKI2J7ZS2vtYW6Ju5PoWBICKTjuinu2ON-7zVHMv1pLXDnUVbpsO1G1Q8Dyb6t2RQOPg4RuydZI2FV6lC7cL_EZIw1qBEF9_nxWzijbBnQ7KwLzJxdf0-wXemDPisi7CRgYaq7Om6LLXwANMmPUFM5uW4pXwQlLDFKA",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCze8orD73HrKwx7nSyhaG-BQ_Ghhnaejc8vYqqKh_oWVWB7jmBRaieDJ5LzJFUdx4Deh5smgbyNuC1S4W4hEBo9jy33jG68M8vuyUQmM2HCpojG5jAMYdPmT5IJMQNsDirKocEhZ-2wcrcmfHdUrWfZAGrom0pZmpJTxxdHzUQEq0Ql45nlQN5TDvC4D2YTmhiYLcceumV9izdHYLbiwPofCEngsriuN8meztbPQmPG40K0LIIFotk3A",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuAYRt26kaXPFuLw2P3YyMrSoJ0dlZY7HzEPoJy165IE85rk0pOxVHgDo4ipwcG_aBJXmGo8sSimi92YPMGcvr60Ud3AT27ZJVKReeMX3B_DKu2WL1qNenED8V9X8gAU_K5p6hTMHsdjOEq4Yzv79HkMKSVoL89p_gpPeaum0Hv2TBC-Qo2-j6ySjTq5Ry7hZ9qCIoJE2a9VpFd3Pke7uuJptW3rUBZgjNkwmhOUd1-CL9sRTfl0qWiU7Q"
        ),
        tags = listOf("Coding Kits", "Optics & Microscopes", "Astronomy", "Language Flashcards"),
        badgeText = "STEM Certified",
        badgeIcon = "school"
      ),
      CategoryData(
        id = "cat-gifts",
        name = "Gifts & Novelties",
        itemCount = "375+ items",
        description = "Curated surprise bundles & quirky desk toys",
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuA-QCep_J8k8mbdsSXfylFFpSBax4mofAri0XTVSMuCGae0_Nt3XMEHIWpRKwcNQW_V9xhsTxas6H3yHEyiaPVo-VKpKEezmg7qoWsu0d-2Yem1EECHzUGEZofmiTNIlW2fSIrJSFHDiJua_nPKW5tY3zDo2A4k5pPHPF8P_o2KO852opusKPh3-z_Fm77nds62oF4M03pDYK1EX1NVHPMKNlFccxwyX3N4QG38ifoUwKEWi6TZS9hE4Q",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuBgjzlDiaqFIEdiqyDg8sEjbV3kmClcg7I8VHH8yarnkzcA4QGg7IEon5Aexe_QDRLC2ZoAIt2SQYS2YJKP7Kv_GBd5-6Vq8PtWEi6MLJhtyqdHc2-v4wnVAACsyDEDWBca0UYI9wRRUtzkqAbuIQIgZjlrLT472jw9fPjxipSqRnP2Vm9wLmVeALa7o2k764XYbpd_8n99G7uIPJUqUmlmTqFLCPERv0rTYTHAWxQEG4xbGeEmoByerw",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuBxqo5_hkjnc209BeDSNXmFVcP1_hGHGeBoITGY8sj8yf7FF5xUGzGm3cmXDhzmFz8MIKJr1Nowns_GXJHd3RUexKfaR8MSU_6jWNAJyYr82ogJ29mttgE1oBDoCmQhn-IUkbYil9h1GsvnoX5_Wf1mO1fdmdH2B07btFymCIXkqa-5wVdsN6Wf8R0d93bhl32VXgdm5rh6mIpThX9oebMKMkRJLvztxdMcUa7gDnh4yFF_IaHHEIPh3w"
        ),
        tags = listOf("Gift Boxes", "Surprise Bundles", "Desk Novelties", "DIY Craft Kits"),
        badgeText = "Gift Wrapping Avail.",
        badgeIcon = "redeem"
      ),
      CategoryData(
        id = "cat-tech",
        name = "Tech Accessories",
        itemCount = "620+ items",
        description = "Mechanical keyboards, braided cables & stands",
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuBgjzlDiaqFIEdiqyDg8sEjbV3kmClcg7I8VHH8yarnkzcA4QGg7IEon5Aexe_QDRLC2ZoAIt2SQYS2YJKP7Kv_GBd5-6Vq8PtWEi6MLJhtyqdHc2-v4wnVAACsyDEDWBca0UYI9wRRUtzkqAbuIQIgZjlrLT472jw9fPjxipSqRnP2Vm9wLmVeALa7o2k764XYbpd_8n99G7uIPJUqUmlmTqFLCPERv0rTYTHAWxQEG4xbGeEmoByerw",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB0zpWBZOSPu_5gt1WsTlhNS0uAZXbgxzcmMYyWbV30KbGf3E5l5dpBOsFl64X3GHsiWpwnOhEcjL27-p14RTyUqrB9ydm6etYhSu_GygFi0pebhQQhNKoOATazz2OMhakydIZSi8IqL9wJwQLB7HFKaZPo5OxwZ0G5HRDVfHEbp8E0DZQ-qCyp5z5qavFken__HCkRGM54Ifx5vj2kZ7g67J0RfmB8FNkRSuoh3CZBhXVA2vFl2S2JSQ",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuB6bI1cgo5HY1kAonSOSFMFJJi38LPQOxlirIbIZfOqVbbOyvOElqXExRy99ZfHgVTHhxi-2YV9F3F2riOskHZ5cmGjKORK94XvNXTfIQU5XjC-1Vug7os26EVuEsaIM6vxVzNYMJHanR9aq4ZviIHjzjvCEWsFCeOAL8vQ-GhvAEdQyft2mKgq0KvDMI7jbDL_boss-YTmxBLdElgiyHeCEicPcxfvU7DmUkgjKUoFE02y8qUrgF4bsw"
        ),
        tags = listOf("Keyboards", "Wireless Mice", "Braided Cables", "Aluminum Stands"),
        badgeText = "KIDOO Quality Tested",
        badgeIcon = "verified"
      ),
      CategoryData(
        id = "cat-lifestyle",
        name = "Lifestyle & Utility",
        itemCount = "390+ items",
        description = "Ergonomic insulated bottles, bags & cases",
        images = listOf(
          "https://lh3.googleusercontent.com/aida-public/AB6AXuDVC8qRxro_LMGEBiwBVjGFdjk3cgPyPiSxSPdv76IPEo_nJjdZ9YOt-w-gLE-tTVGYCCY-Z3uzENBrCbu9N8wkzxrBKlD8V5q6EqgImwiFQTM1oQ2Qi8uzWxeOrqp5FqLoT7tFf38kEUZY85xKc0njZlye595bSMoff7j04Jqxx77gie-oWKd5riafIyOpMCk6sCwkKPBZZ-X8zeA9NdzDOCJCHjel68Hb3WQDC2b9paZbjRengoAp8w",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuBxqo5_hkjnc209BeDSNXmFVcP1_hGHGeBoITGY8sj8yf7FF5xUGzGm3cmXDhzmFz8MIKJr1Nowns_GXJHd3RUexKfaR8MSU_6jWNAJyYr82ogJ29mttgE1oBDoCmQhn-IUkbYil9h1GsvnoX5_Wf1mO1fdmdH2B07btFymCIXkqa-5wVdsN6Wf8R0d93bhl32VXgdm5rh6mIpThX9oebMKMkRJLvztxdMcUa7gDnh4yFF_IaHHEIPh3w",
          "https://lh3.googleusercontent.com/aida-public/AB6AXuCzb8h39SDPjoXPxavbb6tHSp9en9m41NSvMKryVqgsUKg3HVrZsAek8ypstzhRKCuZGxIpW8bVPZS290PlMkZiRQFbiHbQcvxiiRZ55PS6ZXQR7aYPrnty-elOArlO-ALWf1dzCmP28Md17qIhN0Par_3y5DGGib4A07E7vLYiTUnjR2HiswUbxtlaVvemCvfj4-x4pgO94_yhm0gSyk71w2oDSlcJfIJl0mbt45bqSm_rLlbw58DHBw"
        ),
        tags = listOf("Thermal Bottles", "Commuter Bags", "Bento Lunchware", "Travel Cases"),
        badgeText = "Eco-Friendly Materials",
        badgeIcon = "eco"
      )
    )
  }
}
