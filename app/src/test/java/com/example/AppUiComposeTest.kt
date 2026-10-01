package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import com.example.data.model.PriceSortOrder
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.ui.components.DisplayStarRating
import com.example.ui.components.InteractiveStarRating
import com.example.ui.components.PriceSortToggleChip
import com.example.ui.components.ProductCard
import com.example.ui.components.ProductReviewsContent
import com.example.ui.components.ProductReviewsDialog
import com.example.ui.components.ProductSearchBar
import com.example.ui.theme.KidooTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppUiComposeTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private val sampleProduct = Product(
    id = "prod-sample",
    name = "NuPhy Air75 Keyboard",
    price = 4299,
    originalPrice = 5999,
    discountPercent = 28,
    rating = 4.8,
    reviewCount = 3,
    imageUrl = "https://example.com/keyboard.jpg",
    category = "Smart Workspace",
    brand = "NuPhy Studio",
    reviews = listOf(
      ProductReview(author = "Aarav S.", rating = 5, date = "Oct 15", comment = "Best mechanical keyboard!")
    )
  )

  @Test
  fun testProductCardRenderingAndInteractions() {
    var openedReviews = false
    var addedToCart = false
    var wishlisted = false

    composeTestRule.setContent {
      KidooTheme {
        ProductCard(
          product = sampleProduct,
          isWishlisted = wishlisted,
          onWishlistToggle = { wishlisted = !wishlisted },
          onAddToCart = { addedToCart = true },
          onOpenReviews = { openedReviews = true }
        )
      }
    }

    composeTestRule.waitForIdle()

    // Verify product title, brand, price and discount are displayed
    composeTestRule.onNodeWithText("NuPhy Air75 Keyboard").assertIsDisplayed()
    composeTestRule.onNodeWithText("NUPHY STUDIO").assertIsDisplayed()
    composeTestRule.onNodeWithText("₹4299").assertIsDisplayed()
    composeTestRule.onNodeWithText("28% OFF").assertIsDisplayed()

    // Verify rating stars row is present and click triggers review dialog callback
    val ratingNode = composeTestRule.onNodeWithTag("product_rating_stars_prod-sample")
    ratingNode.assertIsDisplayed()
    ratingNode.performClick()
    assertTrue(openedReviews)

    // Verify Add to Cart button
    val addToCartBtn = composeTestRule.onNodeWithTag("add_to_cart_btn_prod-sample")
    addToCartBtn.assertIsDisplayed()
    addToCartBtn.performClick()
    assertTrue(addedToCart)
  }

  @Test
  fun testProductSearchBarFilteringInput() {
    var queryText = ""

    composeTestRule.setContent {
      KidooTheme {
        ProductSearchBar(
          query = queryText,
          onQueryChange = { queryText = it },
          onClearQuery = { queryText = "" }
        )
      }
    }

    composeTestRule.waitForIdle()

    // Check placeholder
    composeTestRule.onNodeWithText("Search products by name...").assertIsDisplayed()

    // Type in input field
    val inputField = composeTestRule.onNodeWithTag("product_search_input_field")
    inputField.performTextInput("Gel Pens")
    assertEquals("Gel Pens", queryText)
  }

  @Test
  fun testProductSearchBarClearButton() {
    var queryText by mutableStateOf("Keyboard")

    composeTestRule.setContent {
      KidooTheme {
        ProductSearchBar(
          query = queryText,
          onQueryChange = { queryText = it },
          onClearQuery = { queryText = "" }
        )
      }
    }

    composeTestRule.waitForIdle()

    // Clear button should be visible when text is not empty
    val clearBtn = composeTestRule.onNodeWithTag("product_search_clear_button")
    clearBtn.assertIsDisplayed()
    clearBtn.performClick()

    assertEquals("", queryText)
  }

  @Test
  fun testPriceSortToggleChip() {
    var sortOrder by mutableStateOf(PriceSortOrder.NONE)

    composeTestRule.setContent {
      KidooTheme {
        PriceSortToggleChip(
          sortOrder = sortOrder,
          onToggleSort = { sortOrder = sortOrder.toggle() }
        )
      }
    }

    composeTestRule.waitForIdle()

    val chip = composeTestRule.onNodeWithTag("price_sort_toggle_chip")
    chip.assertIsDisplayed()
    chip.performClick()

    assertEquals(PriceSortOrder.ASCENDING, sortOrder)

    chip.performClick()
    assertEquals(PriceSortOrder.DESCENDING, sortOrder)
  }

  @Test
  fun testInteractiveStarRatingBar() {
    var selectedStars by mutableStateOf(3)

    composeTestRule.setContent {
      KidooTheme {
        InteractiveStarRating(
          selectedRating = selectedStars,
          onRatingChanged = { selectedStars = it }
        )
      }
    }

    composeTestRule.waitForIdle()

    // Click 5th star
    composeTestRule.onNodeWithTag("star_rate_button_5").performClick()
    assertEquals(5, selectedStars)

    // Click 1st star
    composeTestRule.onNodeWithTag("star_rate_button_1").performClick()
    assertEquals(1, selectedStars)
  }

  @Test
  fun testProductReviewsDialogDisplayAndSubmit() {
    composeTestRule.setContent {
      KidooTheme {
        Box(modifier = Modifier.size(400.dp, 800.dp)) {
          ProductReviewsContent(
            product = sampleProduct,
            onDismiss = {},
            onSubmitReview = { _, _, _ -> }
          )
        }
      }
    }

    composeTestRule.waitForIdle()

    // Assert dialog content container is displayed
    composeTestRule.onNodeWithTag("product_reviews_dialog").assertIsDisplayed()

    // Toggle Write Review
    val writeReviewToggle = composeTestRule.onNodeWithTag("write_review_toggle_button")
    writeReviewToggle.assertIsDisplayed()
    writeReviewToggle.performClick()
    composeTestRule.waitForIdle()

    // Verify review form card exists upon toggle
    composeTestRule.onNodeWithTag("write_review_form_card").assertExists()
  }
}
