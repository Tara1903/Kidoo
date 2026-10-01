package com.example

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.KidooDatabase
import com.example.data.local.dao.WishlistDao
import com.example.data.model.Product
import com.example.ui.WishlistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WishlistViewModelTest {

  private val testDispatcher = StandardTestDispatcher()
  private lateinit var db: KidooDatabase
  private lateinit var dao: WishlistDao
  private lateinit var viewModel: WishlistViewModel

  private val sampleProduct = Product(
    id = "prod-keyboard",
    name = "NuPhy Air75 Keyboard",
    description = "Low-profile mechanical keyboard with RGB backlighting",
    price = 4299,
    imageUrl = "https://example.com/keyboard.jpg",
    category = "Tech Accessories",
    brand = "NuPhy",
    originalPrice = 5999,
    discountPercent = 28,
    rating = 4.7,
    reviewCount = 520
  )

  @Before
  fun setUp() {
    Dispatchers.setMain(testDispatcher)
    val app = ApplicationProvider.getApplicationContext<Application>()
    db = Room.inMemoryDatabaseBuilder(app, KidooDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    dao = db.wishlistDao()
    viewModel = WishlistViewModel(app, dao)
  }

  @After
  fun tearDown() {
    db.close()
    Dispatchers.resetMain()
  }

  @Test
  fun testInitialWishlistIsEmpty() = runTest {
    advanceUntilIdle()
    val initial = viewModel.wishlistProductsFlow.first()
    assertTrue(initial.isEmpty())
    assertEquals(0, viewModel.wishlistCount.value)
  }

  @Test
  fun testAddToWishlistAndExposeFlow() = runTest {
    viewModel.addToWishlist(sampleProduct).join()

    val products = viewModel.wishlistProductsFlow.first()
    assertEquals(1, products.size)
    assertEquals("prod-keyboard", products[0].id)
    assertEquals("NuPhy Air75 Keyboard", products[0].title)

    val isWishlisted = viewModel.isProductWishlisted("prod-keyboard").first()
    assertTrue(isWishlisted)
  }

  @Test
  fun testRemoveFromWishlist() = runTest {
    viewModel.addToWishlist(sampleProduct).join()
    assertEquals(1, viewModel.wishlistProductsFlow.first().size)

    viewModel.removeFromWishlist("prod-keyboard").join()
    assertEquals(0, viewModel.wishlistProductsFlow.first().size)

    val isWishlisted = viewModel.isProductWishlisted("prod-keyboard").first()
    assertFalse(isWishlisted)
  }

  @Test
  fun testToggleWishlist() = runTest {
    // 1st toggle: adds to wishlist
    viewModel.toggleWishlist(sampleProduct).join()
    assertEquals(1, viewModel.wishlistProductsFlow.first().size)

    // 2nd toggle: removes from wishlist
    viewModel.toggleWishlist(sampleProduct).join()
    assertEquals(0, viewModel.wishlistProductsFlow.first().size)
  }

  @Test
  fun testClearWishlist() = runTest {
    viewModel.addToWishlist(sampleProduct).join()
    val product2 = sampleProduct.copy(id = "prod-pen", name = "Kaweco Pen")
    viewModel.addToWishlist(product2).join()
    assertEquals(2, viewModel.wishlistProductsFlow.first().size)

    viewModel.clearWishlist().join()
    assertEquals(0, viewModel.wishlistProductsFlow.first().size)
  }
}
