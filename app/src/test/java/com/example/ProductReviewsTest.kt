package com.example

import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.ui.KidooViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProductReviewsTest {

  @Test
  fun testProductReviewCreationAndAverageCalculation() {
    val review1 = ProductReview(author = "User A", rating = 5, date = "Oct 10", comment = "Amazing product!")
    val review2 = ProductReview(author = "User B", rating = 3, date = "Oct 12", comment = "Decent, works fine.")
    val review3 = ProductReview(author = "User C", rating = 4, date = "Oct 14", comment = "Good value.")

    val product = Product(
      id = "test-prod",
      name = "Test Mechanical Keyboard",
      price = 3999,
      imageUrl = "https://example.com/kb.jpg",
      rating = 4.0,
      reviewCount = 0,
      reviews = listOf(review1, review2, review3)
    )

    assertEquals(3, product.totalReviewsCount)
    // (5 + 3 + 4) / 3 = 4.0
    assertEquals(4.0, product.averageRating, 0.05)
  }

  @Test
  fun testProductReviewFallbackWhenEmpty() {
    val product = Product(
      id = "test-empty",
      name = "Test Item",
      price = 999,
      imageUrl = "",
      rating = 4.7,
      reviewCount = 50,
      reviews = emptyList()
    )

    assertEquals(4.7, product.averageRating, 0.05)
    assertEquals(50, product.totalReviewsCount)
  }

  @Test
  fun testViewModelReviewInteraction() {
    val viewModel = KidooViewModel()
    val products = viewModel.getProducts()
    assertTrue(products.isNotEmpty())

    val targetProduct = products.first()

    // Dialog initial state
    assertNull(viewModel.selectedProductForReviews.value)

    // Open reviews
    viewModel.openProductReviews(targetProduct)
    assertNotNull(viewModel.selectedProductForReviews.value)
    assertEquals(targetProduct.id, viewModel.selectedProductForReviews.value?.id)

    val initialReviewCount = viewModel.selectedProductForReviews.value?.reviews?.size ?: 0

    // Submit a 5-star review
    viewModel.submitProductReview(
      productId = targetProduct.id,
      rating = 5,
      author = "Quality Tester",
      comment = "Exceptional tactile keys and durable build!"
    )

    val updatedProduct = viewModel.selectedProductForReviews.value
    assertNotNull(updatedProduct)
    assertEquals(initialReviewCount + 1, updatedProduct?.reviews?.size)
    assertEquals("Quality Tester", updatedProduct?.reviews?.first()?.author)
    assertEquals(5, updatedProduct?.reviews?.first()?.rating)

    // Close reviews
    viewModel.closeProductReviews()
    assertNull(viewModel.selectedProductForReviews.value)
  }
}
