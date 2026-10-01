package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.KidooDatabase
import com.example.data.local.dao.WishlistDao
import com.example.data.local.entity.WishlistItemEntity
import com.example.data.model.Product
import com.example.data.repository.KidooRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WishlistViewModel @JvmOverloads constructor(
  application: Application,
  private val wishlistDao: WishlistDao = KidooDatabase.getDatabase(application).wishlistDao()
) : AndroidViewModel(application) {

  companion object {
    fun provideFactory(application: Application): androidx.lifecycle.ViewModelProvider.Factory =
      object : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
          return WishlistViewModel(
            application = application,
            wishlistDao = KidooDatabase.getDatabase(application).wishlistDao()
          ) as T
        }
      }
  }

  /**
   * Reactive Flow / StateFlow of all bookmarked products from the Room database,
   * mapped to domain [Product] models.
   */
  val wishlistProducts: StateFlow<List<Product>> = wishlistDao.getAllWishlistItems()
    .map { entities -> entities.map { it.toProduct() } }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  /**
   * Flow of bookmarked products directly for standard Flow consumers.
   */
  val wishlistProductsFlow: Flow<List<Product>> = wishlistDao.getAllWishlistItems()
    .map { entities -> entities.map { it.toProduct() } }

  /**
   * Real-time count of bookmarked products in the Room database.
   */
  val wishlistCount: StateFlow<Int> = wishlistDao.getWishlistCount()
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = 0
    )

  /**
   * Check if a specific product is bookmarked in the Room database.
   */
  fun isProductWishlisted(productId: String): Flow<Boolean> {
    return wishlistDao.isProductWishlisted(productId)
  }

  /**
   * Adds a product to the Room database wishlist.
   */
  fun addToWishlist(product: Product): Job = viewModelScope.launch {
    wishlistDao.insertWishlistItem(WishlistItemEntity.fromProduct(product))
  }

  /**
   * Removes a product from the Room database wishlist by its ID.
   */
  fun removeFromWishlist(productId: String): Job = viewModelScope.launch {
    wishlistDao.deleteWishlistItemById(productId)
  }

  /**
   * Toggles bookmark state: adds if absent, removes if present.
   */
  fun toggleWishlist(product: Product): Job = viewModelScope.launch {
    val isBookmarked = wishlistDao.isProductWishlisted(product.id).first()
    if (isBookmarked) {
      wishlistDao.deleteWishlistItemById(product.id)
    } else {
      wishlistDao.insertWishlistItem(WishlistItemEntity.fromProduct(product))
    }
  }

  /**
   * Clears all bookmarked items from the Room database.
   */
  fun clearWishlist(): Job = viewModelScope.launch {
    wishlistDao.clearWishlist()
  }
}
