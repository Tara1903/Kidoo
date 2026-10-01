package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.KidooDatabase
import com.example.data.local.dao.WishlistDao
import com.example.data.local.entity.WishlistItemEntity
import com.example.data.model.Product
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WishlistRoomTest {

  private lateinit var db: KidooDatabase
  private lateinit var dao: WishlistDao

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, KidooDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    dao = db.wishlistDao()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun testProductDataClassAndDatabaseRoundTrip() = runBlocking {
    // Testing the requested Product data class with fields: id, name, description, price, imageUrl
    val product = Product(
      id = "item-101",
      name = "Minimalist Desk Pad",
      description = "Anti-slip waterproof vegan leather desk mat for workspace setup",
      price = 1499,
      imageUrl = "https://example.com/desk-pad.jpg"
    )

    // Verify fields
    assertEquals("item-101", product.id)
    assertEquals("Minimalist Desk Pad", product.name)
    assertEquals("Anti-slip waterproof vegan leather desk mat for workspace setup", product.description)
    assertEquals(1499, product.price)
    assertEquals("https://example.com/desk-pad.jpg", product.imageUrl)

    // Save to Wishlist database
    val entity = WishlistItemEntity.fromProduct(product)
    dao.insertWishlistItem(entity)

    // Retrieve from Wishlist database
    val list = dao.getAllWishlistItems().first()
    assertEquals(1, list.size)
    val retrievedProduct = list[0].toProduct()

    assertEquals("item-101", retrievedProduct.id)
    assertEquals("Minimalist Desk Pad", retrievedProduct.name)
    assertEquals("Anti-slip waterproof vegan leather desk mat for workspace setup", retrievedProduct.description)
    assertEquals(1499, retrievedProduct.price)
    assertEquals("https://example.com/desk-pad.jpg", retrievedProduct.imageUrl)
    assertTrue(retrievedProduct.isWishlisted)
  }

  @Test
  fun insertAndRetrieveWishlistItem() = runBlocking {
    val item = WishlistItemEntity(
      productId = "test-prod-1",
      name = "Mechanical Keyboard",
      description = "Wireless mechanical keyboard with RGB backlighting",
      price = 4299,
      imageUrl = "https://example.com/keyboard.jpg",
      category = "Tech Accessories",
      brand = "NuPhy",
      originalPrice = 5999,
      discountPercent = 28,
      rating = 4.7,
      reviewCount = 520
    )

    dao.insertWishlistItem(item)
    val list = dao.getAllWishlistItems().first()
    assertEquals(1, list.size)
    assertEquals("test-prod-1", list[0].productId)
    assertEquals("Mechanical Keyboard", list[0].name)
    assertEquals("Wireless mechanical keyboard with RGB backlighting", list[0].description)

    val isWishlisted = dao.isProductWishlisted("test-prod-1").first()
    assertTrue(isWishlisted)
  }

  @Test
  fun deleteWishlistItem() = runBlocking {
    val item = WishlistItemEntity(
      productId = "test-prod-2",
      name = "Gel Pens Set",
      description = "Smooth writing quick-dry pastel gel pens",
      price = 449,
      imageUrl = "https://example.com/pens.jpg",
      category = "Stationery",
      brand = "KIDOO",
      originalPrice = 699,
      discountPercent = 35,
      rating = 4.8,
      reviewCount = 890
    )

    dao.insertWishlistItem(item)
    assertEquals(1, dao.getWishlistCount().first())

    dao.deleteWishlistItemById("test-prod-2")
    assertEquals(0, dao.getWishlistCount().first())
    assertFalse(dao.isProductWishlisted("test-prod-2").first())
  }
}
