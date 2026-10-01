package com.example

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.KidooTab
import com.example.ui.KidooViewModel
import com.example.ui.OrdersTab
import com.example.ui.WishlistViewModel
import com.example.ui.components.KidooBottomNavigation
import com.example.ui.components.KidooHeader
import com.example.ui.components.ProductReviewsDialog
import com.example.ui.dialogs.CheckoutSuccessDialog
import com.example.ui.dialogs.LiveTrackingDialog
import com.example.ui.dialogs.WishlistDialog
import com.example.ui.dialogs.WonderWheelDialog
import com.example.ui.screens.AccountScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.KidooTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      KidooTheme {
        KidooApp()
      }
    }
  }
}

@Composable
fun KidooApp(
  viewModel: KidooViewModel = viewModel(),
  wishlistViewModel: WishlistViewModel = viewModel(
    factory = WishlistViewModel.provideFactory(LocalContext.current.applicationContext as Application)
  )
) {
  val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
  val ordersTab by viewModel.ordersTab.collectAsStateWithLifecycle()
  val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val recentSearches by viewModel.recentSearches.collectAsStateWithLifecycle()
  val filterState by viewModel.filterState.collectAsStateWithLifecycle()

  val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
  val cartBadgeCount by viewModel.totalCartCount.collectAsStateWithLifecycle()
  val cartSummary by viewModel.cartSummary.collectAsStateWithLifecycle()
  val wishlistIds by viewModel.wishlistIds.collectAsStateWithLifecycle()
  val wishlistItems by viewModel.wishlistItems.collectAsStateWithLifecycle()
  val showWishlistSheet by viewModel.showWishlistSheet.collectAsStateWithLifecycle()
  val appliedCoupon by viewModel.appliedCoupon.collectAsStateWithLifecycle()
  val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
  val activeOrders by viewModel.activeOrders.collectAsStateWithLifecycle()
  val pastOrders by viewModel.pastOrders.collectAsStateWithLifecycle()
  val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
  val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
  val priceSortOrder by viewModel.priceSortOrder.collectAsStateWithLifecycle()
  val homeSearchQuery by viewModel.homeSearchQuery.collectAsStateWithLifecycle()
  val selectedProductForReviews by viewModel.selectedProductForReviews.collectAsStateWithLifecycle()
  val selectedProductDetail by viewModel.selectedProductDetail.collectAsStateWithLifecycle()

  val showLiveMapSheet by viewModel.showLiveMapSheet.collectAsStateWithLifecycle()
  val showSpinWheelDialog by viewModel.showSpinWheelDialog.collectAsStateWithLifecycle()
  val showOrderSuccessDialog by viewModel.showOrderSuccessDialog.collectAsStateWithLifecycle()
  val latestPlacedOrder by viewModel.latestPlacedOrder.collectAsStateWithLifecycle()
  val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(snackbarMessage) {
    snackbarMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.dismissSnackbar()
    }
  }

  // Handle system back navigation
  BackHandler(enabled = selectedProductDetail != null) {
    viewModel.closeProductDetail()
  }

  BackHandler(enabled = selectedProductDetail == null && isSearching) {
    viewModel.closeSearch()
  }

  BackHandler(enabled = selectedProductDetail == null && !isSearching && currentTab != KidooTab.CATEGORIES) {
    viewModel.selectTab(KidooTab.CATEGORIES)
  }

  if (selectedProductDetail != null) {
    val currentProduct = selectedProductDetail!!
    ProductDetailScreen(
      product = currentProduct,
      isWishlisted = wishlistIds.contains(currentProduct.id),
      cartCount = cartBadgeCount,
      onBack = viewModel::closeProductDetail,
      onWishlistToggle = {
        viewModel.toggleWishlist(currentProduct.id)
      },
      onAddToCart = { quantity, variant ->
        viewModel.addToCartWithQuantity(currentProduct, quantity, variant)
      },
      onBuyNow = { quantity, variant ->
        viewModel.addToCartWithQuantity(currentProduct, quantity, variant)
        viewModel.closeProductDetail()
        viewModel.selectTab(KidooTab.CART)
      },
      onOpenReviews = viewModel::openProductReviews,
      onNavigateToCart = {
        viewModel.closeProductDetail()
        viewModel.selectTab(KidooTab.CART)
      }
    )
  } else {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      snackbarHost = { SnackbarHost(snackbarHostState) },
      topBar = {
        // Global header shown across main tabs (when not full search mode)
        if (!isSearching) {
          KidooHeader(
            avatarUrl = userProfile.avatarUrl,
            unreadNotifications = 2,
            onNotificationClick = { viewModel.showSnackbar("You have 2 delivery updates!") },
            onWishlistClick = { viewModel.openWishlist() },
            onProfileClick = { viewModel.selectTab(KidooTab.ME) }
          )
        }
      },
      bottomBar = {
        if (!isSearching) {
          KidooBottomNavigation(
            currentTab = currentTab,
            cartBadgeCount = cartBadgeCount,
            onTabSelected = { tab -> viewModel.selectTab(tab) }
          )
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .padding(innerPadding)
      ) {
        if (isSearching) {
          SearchScreen(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChanged,
            onSearchSubmit = viewModel::submitSearch,
            onClearQuery = viewModel::clearSearch,
            onCloseSearch = viewModel::closeSearch,
            recentSearches = recentSearches,
            onClearRecentSearches = viewModel::clearRecentSearches,
            products = viewModel.getFilteredProducts(),
            wishlistIds = wishlistIds,
            onWishlistToggle = viewModel::toggleWishlist,
            onAddToCart = viewModel::addToCart,
            filterState = filterState,
            onToggleUnder5000 = viewModel::toggleUnder5000Filter,
            onToggleInStock = viewModel::toggleInStockFilter,
            onToggleMinRating = viewModel::toggleMinRatingFilter,
            priceSortOrder = priceSortOrder,
            onTogglePriceSort = viewModel::togglePriceSort,
            onOpenReviews = viewModel::openProductReviews,
            onProductClick = viewModel::openProductDetail
          )
        } else {
          when (currentTab) {
            KidooTab.HOME -> {
              HomeScreen(
                products = filteredProducts,
                wishlistIds = wishlistIds,
                searchQuery = homeSearchQuery,
                onSearchQueryChange = viewModel::updateHomeSearchQuery,
                onClearSearchQuery = viewModel::clearHomeSearchQuery,
                selectedCategory = selectedCategory,
                onSelectCategory = viewModel::selectCategory,
                priceSortOrder = priceSortOrder,
                onTogglePriceSort = viewModel::togglePriceSort,
                onOpenReviews = viewModel::openProductReviews,
                onProductClick = viewModel::openProductDetail,
                onWishlistToggle = viewModel::toggleWishlist,
                onAddToCart = viewModel::addToCart,
                onSearchClick = { viewModel.openSearch("") },
                onExploreCategories = { viewModel.selectTab(KidooTab.CATEGORIES) },
                onOpenSpinWheel = viewModel::openSpinWheel
              )
            }

          KidooTab.CATEGORIES -> {
            CategoriesScreen(
              categories = viewModel.getCategories(),
              onSearchClick = { viewModel.openSearch("Keyboards") },
              onExploreCategory = { categoryName ->
                viewModel.openSearch(categoryName)
              },
              onClaimSpotlight = {
                viewModel.showSnackbar("🎉 15% Reward Credit Bundle added to your cart!")
                viewModel.selectTab(KidooTab.CART)
              }
            )
          }

          KidooTab.CART -> {
            CartScreen(
              cartItems = cartItems,
              appliedCoupon = appliedCoupon,
              onUpdateQty = viewModel::updateCartQty,
              onRemoveItem = viewModel::removeCartItem,
              onRemoveCoupon = viewModel::removeCoupon,
              onApplyCoupon = viewModel::applyCoupon,
              onProceedToCheckout = viewModel::proceedToCheckout,
              onSaveForLater = { id ->
                viewModel.removeCartItem(id)
                viewModel.showSnackbar("Saved for later")
              }
            )
          }

          KidooTab.ORDERS -> {
            OrdersScreen(
              ordersTab = ordersTab,
              onSelectOrdersTab = viewModel::setOrdersTab,
              activeOrders = activeOrders,
              pastOrders = pastOrders,
              onBack = { viewModel.selectTab(KidooTab.CATEGORIES) },
              onSearchOrders = { viewModel.openSearch("Orders") },
              onHelpClick = { viewModel.showSnackbar("KIDOO 24/7 Support: support@kidoo.com") },
              onTrackOnLiveMap = viewModel::openLiveMapTracking,
              onOrderDetails = { order ->
                viewModel.showSnackbar("${order.orderNumber} details: 2 items, ₹${order.totalAmount}")
              },
              onBuyAgain = { order ->
                viewModel.showSnackbar("Re-added ${order.primaryItemTitle} to cart!")
                viewModel.selectTab(KidooTab.CART)
              },
              onRateAndReview = { order ->
                viewModel.showSnackbar("Thank you for reviewing ${order.primaryItemTitle}! ⭐⭐⭐⭐⭐")
              }
            )
          }

          KidooTab.ME -> {
            AccountScreen(
              userProfile = userProfile,
              onEditProfile = { viewModel.showSnackbar("Profile updated!") },
              onNavigateToOrders = { viewModel.selectTab(KidooTab.ORDERS) },
              onNavigateToWishlist = { viewModel.openWishlist() },
              onOpenSpinWheel = viewModel::openSpinWheel,
              onOpenWallet = {
                viewModel.showSnackbar("Wallet balance: ₹${userProfile.walletCredits}. Applied at checkout!")
              },
              onOpenCoupons = {
                viewModel.showSnackbar("You have 3 active coupons: KIDOO10, TECH40, FREESHIP")
              },
              onLogout = {
                viewModel.showSnackbar("You are already securely logged in as Aarav.")
              }
            )
          }
        }
      }
    }
  }
}

  // Room Database Wishlist Dialog
  if (showWishlistSheet) {
    WishlistDialog(
      wishlistItems = wishlistItems,
      onRemoveItem = { productId ->
        wishlistViewModel.removeFromWishlist(productId)
      },
      onAddToCart = { product ->
        viewModel.addToCart(product)
      },
      onDismiss = viewModel::closeWishlist
    )
  }

  // Live Map Tracking Dialog
  if (showLiveMapSheet) {
    LiveTrackingDialog(
      onDismiss = viewModel::closeLiveMapTracking
    )
  }

  // Wonder Wheel Game Dialog
  if (showSpinWheelDialog) {
    WonderWheelDialog(
      onDismiss = viewModel::closeSpinWheel,
      onRewardClaimed = viewModel::claimSpinReward
    )
  }

  // Order Placement Success Dialog
  if (showOrderSuccessDialog) {
    CheckoutSuccessDialog(
      order = latestPlacedOrder,
      onViewOrder = viewModel::dismissOrderSuccess
    )
  }

  // Product Star Ratings & Reviews Dialog
  selectedProductForReviews?.let { product ->
    ProductReviewsDialog(
      product = product,
      onDismiss = viewModel::closeProductReviews,
      onSubmitReview = { rating, author, comment ->
        viewModel.submitProductReview(product.id, rating, author, comment)
      }
    )
  }
}
