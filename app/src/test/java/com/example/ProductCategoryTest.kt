package com.example

import com.example.data.model.Product
import com.example.data.model.ProductCategory
import com.example.ui.KidooViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProductCategoryTest {

  private val stationeryProduct = Product(
    id = "prod-pen",
    name = "Pastel Gel Pens",
    category = "Stationery",
    price = 449,
    imageUrl = "https://example.com/pens.jpg",
    description = "Pack of 10 pastel gel pens"
  )

  private val gadgetProduct = Product(
    id = "prod-keyboard",
    name = "NuPhy Mechanical Keyboard",
    category = "Tech Accessories",
    price = 4299,
    imageUrl = "https://example.com/keyboard.jpg",
    description = "Wireless mechanical keyboard"
  )

  private val hobbyProduct = Product(
    id = "prod-stem",
    name = "CyberBot STEM Robotics Kit",
    category = "Toys & Building",
    price = 2499,
    imageUrl = "https://example.com/robot.jpg",
    description = "Robotics kit for kids and hobbyists"
  )

  @Test
  fun testDefaultCategoriesExist() {
    val categories = ProductCategory.defaultCategories()
    assertTrue(categories.any { it.id == "all" && it.name == "All" })
    assertTrue(categories.any { it.id == "stationery" && it.name == "Stationery" })
    assertTrue(categories.any { it.id == "gadgets" && it.name == "Gadgets" })
    assertTrue(categories.any { it.id == "hobbies" && it.name == "Hobbies" })
  }

  @Test
  fun testProductCategoryMatching() {
    // Stationery
    assertTrue(ProductCategory.STATIONERY.matches(stationeryProduct))
    assertFalse(ProductCategory.STATIONERY.matches(gadgetProduct))
    assertFalse(ProductCategory.STATIONERY.matches(hobbyProduct))

    // Gadgets
    assertTrue(ProductCategory.GADGETS.matches(gadgetProduct))
    assertFalse(ProductCategory.GADGETS.matches(stationeryProduct))
    assertFalse(ProductCategory.GADGETS.matches(hobbyProduct))

    // Hobbies
    assertTrue(ProductCategory.HOBBIES.matches(hobbyProduct))
    assertFalse(ProductCategory.HOBBIES.matches(stationeryProduct))
    assertFalse(ProductCategory.HOBBIES.matches(gadgetProduct))

    // All matches everything
    assertTrue(ProductCategory.ALL.matches(stationeryProduct))
    assertTrue(ProductCategory.ALL.matches(gadgetProduct))
    assertTrue(ProductCategory.ALL.matches(hobbyProduct))
  }

  @Test
  fun testViewModelCategorySelection() {
    val viewModel = KidooViewModel()

    // Default is ALL
    assertEquals(ProductCategory.ALL.id, viewModel.selectedCategory.value.id)
    assertEquals(viewModel.getProducts().size, viewModel.filteredProducts.value.size)

    // Select Stationery
    viewModel.selectCategory(ProductCategory.STATIONERY)
    assertEquals(ProductCategory.STATIONERY.id, viewModel.selectedCategory.value.id)
    assertTrue(viewModel.filteredProducts.value.isNotEmpty())
    assertTrue(viewModel.filteredProducts.value.all { ProductCategory.STATIONERY.matches(it) })

    // Select Gadgets
    viewModel.selectCategory(ProductCategory.GADGETS)
    assertEquals(ProductCategory.GADGETS.id, viewModel.selectedCategory.value.id)
    assertTrue(viewModel.filteredProducts.value.isNotEmpty())
    assertTrue(viewModel.filteredProducts.value.all { ProductCategory.GADGETS.matches(it) })

    // Select Hobbies
    viewModel.selectCategory(ProductCategory.HOBBIES)
    assertEquals(ProductCategory.HOBBIES.id, viewModel.selectedCategory.value.id)
    assertTrue(viewModel.filteredProducts.value.isNotEmpty())
    assertTrue(viewModel.filteredProducts.value.all { ProductCategory.HOBBIES.matches(it) })

    // Reset to ALL
    viewModel.selectCategory(ProductCategory.ALL)
    assertEquals(ProductCategory.ALL.id, viewModel.selectedCategory.value.id)
    assertEquals(viewModel.getProducts().size, viewModel.filteredProducts.value.size)
  }
}
