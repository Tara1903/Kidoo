package com.example

import com.example.data.model.Product
import com.example.data.repository.KidooRepository
import com.example.ui.KidooViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CartAndCheckoutTest {

  private lateinit var viewModel: KidooViewModel

  @Before
  fun setUp() {
    viewModel = KidooViewModel()
  }

  @Test
  fun testCartAddModifyAndRemove() {
    val initialCartCount = viewModel.cartItems.value.size
    val testProduct = Product(
      id = "test-cart-prod",
      name = "Test Mechanical Keyboard",
      price = 2999,
      imageUrl = ""
    )

    // Add to cart
    viewModel.addToCart(testProduct)
    val afterAdd = viewModel.cartItems.value
    assertEquals(initialCartCount + 1, afterAdd.size)
    val addedItem = afterAdd.find { it.product.id == "test-cart-prod" }
    assertNotNull(addedItem)
    assertEquals(1, addedItem?.quantity)

    // Increase quantity
    viewModel.updateCartQty(addedItem!!.id, 1)
    val afterIncrement = viewModel.cartItems.value.find { it.id == addedItem.id }
    assertEquals(2, afterIncrement?.quantity)

    // Decrease quantity
    viewModel.updateCartQty(addedItem.id, -1)
    val afterDecrement = viewModel.cartItems.value.find { it.id == addedItem.id }
    assertEquals(1, afterDecrement?.quantity)

    // Remove from cart
    viewModel.removeCartItem(addedItem.id)
    val afterRemove = viewModel.cartItems.value.find { it.id == addedItem.id }
    assertEquals(null, afterRemove)
  }

  @Test
  fun testCouponsApplicationAndCalculation() {
    // Add product to ensure cart is not empty
    val testProduct = Product(id = "p-coupon", name = "Pen", price = 450, imageUrl = "")
    viewModel.addToCart(testProduct)

    // Default coupon
    assertEquals("KIDOO10", viewModel.appliedCoupon.value)
    val savingsWithCoupon = KidooRepository.calculateCouponSavings()
    assertTrue(savingsWithCoupon > 0)

    // Remove coupon
    viewModel.removeCoupon()
    assertEquals(null, viewModel.appliedCoupon.value)
    assertEquals(0, KidooRepository.calculateCouponSavings())

    // Reapply coupon
    viewModel.applyCoupon("KIDOO10")
    assertEquals("KIDOO10", viewModel.appliedCoupon.value)
    assertTrue(KidooRepository.calculateCouponSavings() > 0)
  }

  @Test
  fun testOrderPlacementCUJ() {
    val initialActiveOrders = viewModel.activeOrders.value.size
    val initialOrdersCount = viewModel.userProfile.value.ordersCount

    // Ensure cart has items
    if (viewModel.cartItems.value.isEmpty()) {
      val product = viewModel.getProducts().first()
      viewModel.addToCart(product)
    }

    assertTrue(viewModel.cartItems.value.isNotEmpty())

    // Place order via checkout
    viewModel.proceedToCheckout()

    // Assert order success dialog triggered
    assertTrue(viewModel.showOrderSuccessDialog.value)
    val placedOrder = viewModel.latestPlacedOrder.value
    assertNotNull(placedOrder)
    assertFalse(placedOrder!!.isDelivered)
    assertTrue(placedOrder.orderNumber.startsWith("ORDER KD-"))
    assertTrue(placedOrder.deliveryOtp.isNotEmpty())

    // Cart is cleared after checkout
    assertEquals(0, viewModel.cartItems.value.size)

    // Active orders incremented
    assertEquals(initialActiveOrders + 1, viewModel.activeOrders.value.size)
    assertEquals(initialOrdersCount + 1, viewModel.userProfile.value.ordersCount)

    // Dismiss order dialog
    viewModel.dismissOrderSuccess()
    assertFalse(viewModel.showOrderSuccessDialog.value)
  }
}
