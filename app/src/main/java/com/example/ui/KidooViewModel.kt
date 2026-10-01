package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cart.CartSummary
import com.example.data.cart.ShoppingCartManager
import com.example.data.local.entity.WishlistItemEntity
import com.example.data.model.CartItem
import com.example.data.model.CategoryData
import com.example.data.model.OrderData
import com.example.data.model.PriceSortOrder
import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.data.model.ProductReview
import com.example.data.model.UserProfile
import com.example.data.model.sortByPrice
import com.example.data.repository.KidooRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class KidooTab {
  HOME,
  CATEGORIES,
  CART,
  ORDERS,
  ME
}

enum class OrdersTab {
  ACTIVE,
  PAST
}

data class FilterState(
  val maxPrice: Int = 10000,
  val minRating: Double = 0.0,
  val inStockOnly: Boolean = false,
  val sortBy: String = "Popularity"
)

class KidooViewModel : ViewModel() {

  private val repository = KidooRepository

  private val _currentTab = MutableStateFlow(KidooTab.CATEGORIES)
  val currentTab: StateFlow<KidooTab> = _currentTab.asStateFlow()

  private val _ordersTab = MutableStateFlow(OrdersTab.ACTIVE)
  val ordersTab: StateFlow<OrdersTab> = _ordersTab.asStateFlow()

  private val _isSearching = MutableStateFlow(false)
  val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

  private val _searchQuery = MutableStateFlow("Keyboards")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _filterState = MutableStateFlow(FilterState(maxPrice = 5000))
  val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

  private val _selectedCategory = MutableStateFlow(ProductCategory.ALL)
  val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

  private val _priceSortOrder = MutableStateFlow(PriceSortOrder.NONE)
  val priceSortOrder: StateFlow<PriceSortOrder> = _priceSortOrder.asStateFlow()

  private val _homeSearchQuery = MutableStateFlow("")
  val homeSearchQuery: StateFlow<String> = _homeSearchQuery.asStateFlow()

  private val _filteredProducts = MutableStateFlow<List<Product>>(repository.getProducts())
  val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

  fun updateHomeSearchQuery(query: String) {
    _homeSearchQuery.value = query
    applyProductFilters()
  }

  fun clearHomeSearchQuery() {
    _homeSearchQuery.value = ""
    applyProductFilters()
  }

  fun selectCategory(category: ProductCategory) {
    _selectedCategory.value = category
    applyProductFilters()
  }

  fun togglePriceSort() {
    _priceSortOrder.value = _priceSortOrder.value.toggle()
    applyProductFilters()
  }

  fun setPriceSort(order: PriceSortOrder) {
    _priceSortOrder.value = order
    applyProductFilters()
  }

  private fun applyProductFilters() {
    val category = _selectedCategory.value
    val query = _homeSearchQuery.value.trim().lowercase()
    val filtered = repository.getProducts().filter { product ->
      val matchesCategory = category.matches(product)
      val matchesName = query.isEmpty() || product.name.lowercase().contains(query)
      matchesCategory && matchesName
    }
    _filteredProducts.value = filtered.sortByPrice(_priceSortOrder.value)
  }

  private val _showLiveMapSheet = MutableStateFlow(false)
  val showLiveMapSheet: StateFlow<Boolean> = _showLiveMapSheet.asStateFlow()

  private val _showSpinWheelDialog = MutableStateFlow(false)
  val showSpinWheelDialog: StateFlow<Boolean> = _showSpinWheelDialog.asStateFlow()

  private val _showOrderSuccessDialog = MutableStateFlow(false)
  val showOrderSuccessDialog: StateFlow<Boolean> = _showOrderSuccessDialog.asStateFlow()

  private val _latestPlacedOrder = MutableStateFlow<OrderData?>(null)
  val latestPlacedOrder: StateFlow<OrderData?> = _latestPlacedOrder.asStateFlow()

  private val _snackbarMessage = MutableStateFlow<String?>(null)
  val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

  val cartItems: StateFlow<List<CartItem>> = ShoppingCartManager.cartItems
  val totalCartCount: StateFlow<Int> = ShoppingCartManager.totalItemsCount
  val cartSummary: StateFlow<CartSummary> = ShoppingCartManager.cartSummary
  val cartSubtotal: StateFlow<Int> = ShoppingCartManager.subtotalPrice
  val cartTotalPrice: StateFlow<Int> = ShoppingCartManager.totalPrice
  val cartDiscount: StateFlow<Int> = ShoppingCartManager.totalDiscount
  val cartCouponDiscount: StateFlow<Int> = ShoppingCartManager.couponDiscount
  val cartSavings: StateFlow<Int> = ShoppingCartManager.totalSavings
  val wishlistIds: StateFlow<Set<String>> = repository.wishlistIds
  val wishlistItems: StateFlow<List<WishlistItemEntity>> = repository.getWishlistFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
  val appliedCoupon: StateFlow<String?> = repository.appliedCoupon
  val userProfile: StateFlow<UserProfile> = repository.userProfile
  val activeOrders: StateFlow<List<OrderData>> = repository.activeOrders
  val pastOrders: StateFlow<List<OrderData>> = repository.pastOrders
  val recentSearches: StateFlow<List<String>> = repository.recentSearches

  private val _showWishlistSheet = MutableStateFlow(false)
  val showWishlistSheet: StateFlow<Boolean> = _showWishlistSheet.asStateFlow()

  private val _selectedProductForReviews = MutableStateFlow<Product?>(null)
  val selectedProductForReviews: StateFlow<Product?> = _selectedProductForReviews.asStateFlow()

  private val _selectedProductDetail = MutableStateFlow<Product?>(null)
  val selectedProductDetail: StateFlow<Product?> = _selectedProductDetail.asStateFlow()

  fun openProductDetail(product: Product) {
    _selectedProductDetail.value = repository.getProducts().find { it.id == product.id } ?: product
  }

  fun closeProductDetail() {
    _selectedProductDetail.value = null
  }

  fun openProductReviews(product: Product) {
    _selectedProductForReviews.value = repository.getProducts().find { it.id == product.id } ?: product
  }

  fun closeProductReviews() {
    _selectedProductForReviews.value = null
  }

  fun submitProductReview(productId: String, rating: Int, author: String, comment: String) {
    val review = ProductReview(
      author = author,
      rating = rating,
      date = "Just now",
      comment = comment,
      isVerifiedPurchase = true
    )
    repository.addProductReview(productId, review)
    val updated = repository.getProducts().find { it.id == productId }
    _selectedProductForReviews.value = updated
    if (_selectedProductDetail.value?.id == productId) {
      _selectedProductDetail.value = updated
    }
    applyProductFilters()
    showSnackbar("🎉 Thank you! Your $rating-star review was submitted.")
  }

  fun addToCartWithQuantity(product: Product, quantity: Int, variant: String = "") {
    ShoppingCartManager.addItem(product, quantity, variant)
    showSnackbar("Added ${quantity.coerceAtLeast(1)} x ${product.title} to your cart!")
  }

  fun openWishlist() {
    _showWishlistSheet.value = true
  }

  fun closeWishlist() {
    _showWishlistSheet.value = false
  }

  fun selectTab(tab: KidooTab) {
    _currentTab.value = tab
    _isSearching.value = false
  }

  fun setOrdersTab(tab: OrdersTab) {
    _ordersTab.value = tab
  }

  fun openSearch(initialQuery: String = "Keyboards") {
    _searchQuery.value = initialQuery
    _isSearching.value = true
  }

  fun closeSearch() {
    _isSearching.value = false
  }

  fun onSearchQueryChanged(query: String) {
    _searchQuery.value = query
  }

  fun submitSearch(query: String) {
    _searchQuery.value = query
    repository.addRecentSearch(query)
  }

  fun clearSearch() {
    _searchQuery.value = ""
  }

  fun clearRecentSearches() {
    repository.clearRecentSearches()
  }

  fun updateCartQty(cartItemId: String, delta: Int) {
    ShoppingCartManager.changeQuantity(cartItemId, delta)
  }

  fun setItemQuantity(cartItemId: String, quantity: Int) {
    ShoppingCartManager.updateQuantity(cartItemId, quantity)
  }

  fun removeCartItem(cartItemId: String) {
    ShoppingCartManager.removeItem(cartItemId)
    showSnackbar("Item removed from cart")
  }

  fun clearCart() {
    ShoppingCartManager.clearCart()
    showSnackbar("Cart cleared")
  }

  fun addToCart(product: Product) {
    ShoppingCartManager.addItem(product)
    showSnackbar("Added ${product.title} to cart!")
  }

  fun toggleWishlist(productId: String) {
    val willBeAdded = !wishlistIds.value.contains(productId)
    repository.toggleWishlist(productId)
    showSnackbar(if (willBeAdded) "Added to wishlist ❤️" else "Removed from wishlist")
  }

  fun removeCoupon() {
    repository.removeCoupon()
    showSnackbar("Coupon removed")
  }

  fun applyCoupon(code: String) {
    repository.applyCoupon(code)
    showSnackbar("Coupon $code applied!")
  }

  fun proceedToCheckout() {
    if (cartItems.value.isEmpty()) {
      showSnackbar("Your cart is empty!")
      return
    }
    val order = repository.placeOrder()
    _latestPlacedOrder.value = order
    _showOrderSuccessDialog.value = true
  }

  fun dismissOrderSuccess() {
    _showOrderSuccessDialog.value = false
    _currentTab.value = KidooTab.ORDERS
    _ordersTab.value = OrdersTab.ACTIVE
  }

  fun openLiveMapTracking() {
    _showLiveMapSheet.value = true
  }

  fun closeLiveMapTracking() {
    _showLiveMapSheet.value = false
  }

  fun openSpinWheel() {
    _showSpinWheelDialog.value = true
  }

  fun closeSpinWheel() {
    _showSpinWheelDialog.value = false
  }

  fun claimSpinReward(coins: Int) {
    repository.addRewardCoins(coins)
    showSnackbar("🎉 You won $coins KIDOO Reward Coins!")
  }

  fun showSnackbar(message: String) {
    _snackbarMessage.value = message
  }

  fun dismissSnackbar() {
    _snackbarMessage.value = null
  }

  fun toggleUnder5000Filter() {
    val current = _filterState.value
    if (current.maxPrice == 5000) {
      _filterState.value = current.copy(maxPrice = 100000)
    } else {
      _filterState.value = current.copy(maxPrice = 5000)
    }
  }

  fun toggleInStockFilter() {
    _filterState.value = _filterState.value.copy(inStockOnly = !_filterState.value.inStockOnly)
  }

  fun toggleMinRatingFilter() {
    val current = _filterState.value.minRating
    _filterState.value = _filterState.value.copy(minRating = if (current >= 4.0) 0.0 else 4.0)
  }

  fun getFilteredProducts(): List<Product> {
    val all = repository.getProducts()
    val query = _searchQuery.value.trim().lowercase()
    val filter = _filterState.value

    return all.filter { p ->
      val matchesQuery = query.isEmpty() ||
          p.title.lowercase().contains(query) ||
          p.category.lowercase().contains(query) ||
          p.brand.lowercase().contains(query)
      val matchesPrice = p.price <= filter.maxPrice
      val matchesRating = p.rating >= filter.minRating
      matchesQuery && matchesPrice && matchesRating
    }.sortByPrice(_priceSortOrder.value)
  }

  fun getProducts(): List<Product> = repository.getProducts()

  fun getCategories(): List<CategoryData> = repository.getCategories()
}
