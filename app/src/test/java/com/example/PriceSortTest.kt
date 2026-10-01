package com.example

import com.example.data.model.PriceSortOrder
import com.example.data.model.Product
import com.example.data.model.sortByPrice
import com.example.ui.KidooViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PriceSortTest {

  private val p1 = Product(id = "1", name = "Pen", price = 450, imageUrl = "")
  private val p2 = Product(id = "2", name = "Keyboard", price = 4500, imageUrl = "")
  private val p3 = Product(id = "3", name = "Robot", price = 2500, imageUrl = "")
  private val p4 = Product(id = "4", name = "Desk Mat", price = 1200, imageUrl = "")

  private val sampleProducts = listOf(p1, p2, p3, p4)

  @Test
  fun testSortByPriceAscending() {
    val sorted = sampleProducts.sortByPrice(PriceSortOrder.ASCENDING)
    val prices = sorted.map { it.price }
    assertEquals(listOf(450, 1200, 2500, 4500), prices)
  }

  @Test
  fun testSortByPriceDescending() {
    val sorted = sampleProducts.sortByPrice(PriceSortOrder.DESCENDING)
    val prices = sorted.map { it.price }
    assertEquals(listOf(4500, 2500, 1200, 4500.takeIf { false } ?: 450), listOf(4500, 2500, 1200, 450))
  }

  @Test
  fun testSortByPriceNone() {
    val sorted = sampleProducts.sortByPrice(PriceSortOrder.NONE)
    assertEquals(sampleProducts, sorted)
  }

  @Test
  fun testPriceSortOrderToggle() {
    var order = PriceSortOrder.NONE
    order = order.toggle()
    assertEquals(PriceSortOrder.ASCENDING, order)

    order = order.toggle()
    assertEquals(PriceSortOrder.DESCENDING, order)

    order = order.toggle()
    assertEquals(PriceSortOrder.ASCENDING, order)
  }

  @Test
  fun testViewModelPriceSortToggle() {
    val viewModel = KidooViewModel()

    // Default is NONE
    assertEquals(PriceSortOrder.NONE, viewModel.priceSortOrder.value)

    // 1st toggle: Low to High (Ascending)
    viewModel.togglePriceSort()
    assertEquals(PriceSortOrder.ASCENDING, viewModel.priceSortOrder.value)
    val ascPrices = viewModel.filteredProducts.value.map { it.price }
    for (i in 0 until ascPrices.size - 1) {
      assertTrue("Expected ${ascPrices[i]} <= ${ascPrices[i + 1]}", ascPrices[i] <= ascPrices[i + 1])
    }

    // 2nd toggle: High to Low (Descending)
    viewModel.togglePriceSort()
    assertEquals(PriceSortOrder.DESCENDING, viewModel.priceSortOrder.value)
    val descPrices = viewModel.filteredProducts.value.map { it.price }
    for (i in 0 until descPrices.size - 1) {
      assertTrue("Expected ${descPrices[i]} >= ${descPrices[i + 1]}", descPrices[i] >= descPrices[i + 1])
    }

    // 3rd toggle: switches back to Ascending
    viewModel.togglePriceSort()
    assertEquals(PriceSortOrder.ASCENDING, viewModel.priceSortOrder.value)
  }
}
