package com.example

import com.example.data.model.ProductCategory
import com.example.ui.KidooViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProductSearchTest {

  @Test
  fun testSearchFilteringByName() {
    val viewModel = KidooViewModel()

    val totalCount = viewModel.getProducts().size
    assertEquals(totalCount, viewModel.filteredProducts.value.size)
    assertEquals("", viewModel.homeSearchQuery.value)

    // Search by name "Keyboard"
    viewModel.updateHomeSearchQuery("Keyboard")
    assertEquals("Keyboard", viewModel.homeSearchQuery.value)
    val keyboardItems = viewModel.filteredProducts.value
    assertTrue(keyboardItems.isNotEmpty())
    assertTrue(keyboardItems.all { it.name.contains("Keyboard", ignoreCase = true) })

    // Search by name "Gel Pens"
    viewModel.updateHomeSearchQuery("Gel Pens")
    val penItems = viewModel.filteredProducts.value
    assertTrue(penItems.isNotEmpty())
    assertTrue(penItems.all { it.name.contains("Gel Pens", ignoreCase = true) })

    // Non-existent search query
    viewModel.updateHomeSearchQuery("ZzZzNonExistentProduct999")
    assertTrue(viewModel.filteredProducts.value.isEmpty())

    // Clear search query
    viewModel.clearHomeSearchQuery()
    assertEquals("", viewModel.homeSearchQuery.value)
    assertEquals(totalCount, viewModel.filteredProducts.value.size)
  }

  @Test
  fun testCombinedSearchAndCategoryFiltering() {
    val viewModel = KidooViewModel()

    // Select Stationery category
    viewModel.selectCategory(ProductCategory.STATIONERY)

    // Search "Pen" within Stationery
    viewModel.updateHomeSearchQuery("Pen")
    val stationeryPens = viewModel.filteredProducts.value
    assertTrue(stationeryPens.isNotEmpty())
    assertTrue(stationeryPens.all {
      ProductCategory.STATIONERY.matches(it) && it.name.contains("Pen", ignoreCase = true)
    })

    // Search "Keyboard" within Stationery -> Should be empty
    viewModel.updateHomeSearchQuery("Keyboard")
    assertTrue(viewModel.filteredProducts.value.isEmpty())

    // Switch to Gadgets category with "Keyboard" search query
    viewModel.selectCategory(ProductCategory.GADGETS)
    val gadgetKeyboards = viewModel.filteredProducts.value
    assertTrue(gadgetKeyboards.isNotEmpty())
    assertTrue(gadgetKeyboards.all {
      ProductCategory.GADGETS.matches(it) && it.name.contains("Keyboard", ignoreCase = true)
    })
  }
}
