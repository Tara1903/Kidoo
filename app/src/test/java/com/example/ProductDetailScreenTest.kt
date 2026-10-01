package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.example.data.model.Product
import com.example.data.model.ProductReview
import com.example.data.repository.KidooRepository
import com.example.ui.KidooViewModel
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProductHeaderInfoSection
import com.example.ui.screens.VariantSelectorSection
import com.example.ui.theme.KidooTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProductDetailScreenTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  private val testProduct = Product(
    id = "prod-test-detail",
    title = "Air75 Pro Wireless Keyboard",
    category = "Tech Accessories",
    brand = "NuPhy Studio",
    price = 4299,
    originalPrice = 5999,
    discountPercent = 28,
    rating = 4.8,
    reviewCount = 5,
    imageUrl = "https://example.com/img1.jpg",
    galleryImages = listOf(
      "https://example.com/img1.jpg",
      "https://example.com/img2.jpg",
      "https://example.com/img3.jpg"
    ),
    themeVariant = "Cyber Mint",
    variants = listOf("Cyber Mint", "Twilight Gray", "Retro White"),
    description = "Ultra-slim low-profile mechanical keyboard with lubricated Gateron switches and aluminum frame.",
    highlights = listOf(
      "Tri-Mode Connectivity",
      "Hot-Swappable Switches",
      "48-Hour Battery Life"
    ),
    specifications = mapOf(
      "Brand" to "NuPhy Studio",
      "Model" to "Air75 Pro",
      "Switch Type" to "Gateron Low-Profile Mechanical",
      "Battery Capacity" to "2500 mAh",
      "Weight" to "523 g",
      "Warranty" to "1 Year Official Warranty"
    ),
    reviews = listOf(
      ProductReview(author = "Aarav S.", rating = 5, date = "Oct 24", comment = "Amazing tactile typing experience!"),
      ProductReview(author = "Tanvi P.", rating = 5, date = "Oct 20", comment = "Clean and aesthetic finish.")
    ),
    stockLeft = 14
  )

  @Test
  fun testProductDetailScreenHeaderAndPricingDisplay() {
    composeTestRule.setContent {
      KidooTheme {
        ProductHeaderInfoSection(
          product = testProduct,
          onOpenReviews = {}
        )
      }
    }

    composeTestRule.waitForIdle()

    // Assert product header and pricing metadata are displayed
    composeTestRule.onNodeWithText("Air75 Pro Wireless Keyboard").assertIsDisplayed()
    composeTestRule.onNodeWithText("NuPhy Studio").assertIsDisplayed()
    composeTestRule.onAllNodesWithText("₹4299").onFirst().assertExists()
    composeTestRule.onNodeWithText("In Stock (14 left)").assertExists()
    composeTestRule.onNodeWithText("You save ₹1700 (28% OFF)").assertExists()
  }

  @Test
  fun testGalleryThumbnailsInteraction() {
    composeTestRule.setContent {
      KidooTheme {
        ProductDetailScreen(
          product = testProduct,
          isWishlisted = false,
          cartCount = 0,
          onBack = {},
          onWishlistToggle = {},
          onAddToCart = { _, _ -> },
          onBuyNow = { _, _ -> },
          onOpenReviews = {},
          onNavigateToCart = {}
        )
      }
    }

    composeTestRule.waitForIdle()

    // Verify thumbnails exist for the 3 gallery photos
    composeTestRule.onNodeWithTag("gallery_thumbnail_0").assertIsDisplayed()
    composeTestRule.onNodeWithTag("gallery_thumbnail_1").assertIsDisplayed()
    composeTestRule.onNodeWithTag("gallery_thumbnail_2").assertIsDisplayed()

    // Click 2nd thumbnail
    composeTestRule.onNodeWithTag("gallery_thumbnail_1").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("2 of 3").assertIsDisplayed()
  }

  @Test
  fun testVariantSelectorInteraction() {
    var selectedVariant = "Cyber Mint"

    composeTestRule.setContent {
      KidooTheme {
        VariantSelectorSection(
          variants = testProduct.variants,
          selectedVariant = selectedVariant,
          onSelectVariant = { selectedVariant = it }
        )
      }
    }

    composeTestRule.waitForIdle()

    // Verify variants chips exist
    composeTestRule.onNodeWithTag("variant_chip_Cyber Mint").assertExists()
    composeTestRule.onNodeWithTag("variant_chip_Twilight Gray").assertExists()
    composeTestRule.onNodeWithTag("variant_chip_Retro White").assertExists()

    // Click Twilight Gray
    composeTestRule.onNodeWithTag("variant_chip_Twilight Gray").performClick()
    composeTestRule.waitForIdle()

    assertEquals("Twilight Gray", selectedVariant)
  }

  @Test
  fun testStickyBottomBarQuantityAndAddToCartCallbacks() {
    var addedQty = 0
    var addedVariant = ""

    composeTestRule.setContent {
      KidooTheme {
        ProductDetailScreen(
          product = testProduct,
          isWishlisted = false,
          cartCount = 0,
          onBack = {},
          onWishlistToggle = {},
          onAddToCart = { qty, variant ->
            addedQty = qty
            addedVariant = variant
          },
          onBuyNow = { _, _ -> },
          onOpenReviews = {},
          onNavigateToCart = {}
        )
      }
    }

    composeTestRule.waitForIdle()

    // Increment quantity from 1 to 3
    val plusBtn = composeTestRule.onNodeWithTag("product_detail_qty_plus")
    plusBtn.performClick()
    plusBtn.performClick()
    composeTestRule.waitForIdle()

    // Assert quantity text 3 is displayed
    composeTestRule.onNodeWithText("3").assertIsDisplayed()

    // Click Add to Cart
    composeTestRule.onNodeWithTag("product_detail_add_to_cart_button").performClick()
    composeTestRule.waitForIdle()

    assertEquals(3, addedQty)
    assertEquals("Cyber Mint", addedVariant)
  }

  @Test
  fun testBuyNowCallback() {
    var boughtQty = 0
    var boughtVariant = ""

    composeTestRule.setContent {
      KidooTheme {
        ProductDetailScreen(
          product = testProduct,
          isWishlisted = false,
          cartCount = 0,
          onBack = {},
          onWishlistToggle = {},
          onAddToCart = { _, _ -> },
          onBuyNow = { qty, variant ->
            boughtQty = qty
            boughtVariant = variant
          },
          onOpenReviews = {},
          onNavigateToCart = {}
        )
      }
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("product_detail_buy_now_button").performClick()
    composeTestRule.waitForIdle()

    assertEquals(1, boughtQty)
    assertEquals("Cyber Mint", boughtVariant)
  }

  @Test
  fun testTopBarNavigationAndWishlistCallbacks() {
    var backPressed = false
    var wishlistToggled = false
    var cartNavigated = false

    composeTestRule.setContent {
      KidooTheme {
        ProductDetailScreen(
          product = testProduct,
          isWishlisted = true,
          cartCount = 3,
          onBack = { backPressed = true },
          onWishlistToggle = { wishlistToggled = true },
          onAddToCart = { _, _ -> },
          onBuyNow = { _, _ -> },
          onOpenReviews = {},
          onNavigateToCart = { cartNavigated = true }
        )
      }
    }

    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("product_detail_back_button").performClick()
    assertTrue(backPressed)

    composeTestRule.onNodeWithTag("product_detail_wishlist_toggle").performClick()
    assertTrue(wishlistToggled)

    composeTestRule.onNodeWithTag("product_detail_cart_button").performClick()
    assertTrue(cartNavigated)
  }

  @Test
  fun testViewModelProductDetailLifecycle() {
    val viewModel = KidooViewModel()

    // Initially null
    assertNull(viewModel.selectedProductDetail.value)

    // Open detail
    viewModel.openProductDetail(testProduct)
    assertNotNull(viewModel.selectedProductDetail.value)
    assertEquals("prod-test-detail", viewModel.selectedProductDetail.value?.id)

    // Close detail
    viewModel.closeProductDetail()
    assertNull(viewModel.selectedProductDetail.value)
  }
}
