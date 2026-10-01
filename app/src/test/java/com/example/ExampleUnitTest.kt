package com.example

import com.example.data.repository.KidooRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCartInitialValues() {
    val cart = KidooRepository.cartItems.value
    assertTrue(cart.isNotEmpty())
    val totalCount = KidooRepository.calculateTotalItems()
    assertEquals(4, totalCount) // 1 keyboard + 2 pens + 1 bot = 4 items
  }

  @Test
  fun testPriceCalculations() {
    val itemsTotal = KidooRepository.calculateItemsTotal()
    val bagDiscount = KidooRepository.calculateBagDiscount()
    val couponSavings = KidooRepository.calculateCouponSavings()
    val totalPayable = KidooRepository.calculateTotalPayable()

    assertEquals(7696, itemsTotal)
    assertEquals(2449, bagDiscount)
    assertEquals(350, couponSavings)
    assertEquals(4897, totalPayable)
  }

  @Test
  fun testCategoriesData() {
    val categories = KidooRepository.getCategories()
    assertEquals(8, categories.size)
    assertTrue(categories.any { it.name == "Stationery" })
    assertTrue(categories.any { it.name == "Smart Gadgets" })
    assertTrue(categories.any { it.name == "Toys & Building" })
    assertTrue(categories.any { it.name == "Tech Accessories" })
  }
}
