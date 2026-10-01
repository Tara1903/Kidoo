package com.example

import com.example.ui.KidooTab
import com.example.ui.KidooViewModel
import com.example.ui.OrdersTab
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
class NavigationAndFeaturesTest {

  private lateinit var viewModel: KidooViewModel

  @Before
  fun setUp() {
    viewModel = KidooViewModel()
  }

  @Test
  fun testBottomNavigationTabs() {
    assertEquals(KidooTab.CATEGORIES, viewModel.currentTab.value)

    viewModel.selectTab(KidooTab.HOME)
    assertEquals(KidooTab.HOME, viewModel.currentTab.value)

    viewModel.selectTab(KidooTab.CATEGORIES)
    assertEquals(KidooTab.CATEGORIES, viewModel.currentTab.value)

    viewModel.selectTab(KidooTab.ORDERS)
    assertEquals(KidooTab.ORDERS, viewModel.currentTab.value)

    viewModel.selectTab(KidooTab.CART)
    assertEquals(KidooTab.CART, viewModel.currentTab.value)

    viewModel.selectTab(KidooTab.ME)
    assertEquals(KidooTab.ME, viewModel.currentTab.value)
  }

  @Test
  fun testOrdersTabSwitching() {
    assertEquals(OrdersTab.ACTIVE, viewModel.ordersTab.value)

    viewModel.setOrdersTab(OrdersTab.PAST)
    assertEquals(OrdersTab.PAST, viewModel.ordersTab.value)

    viewModel.setOrdersTab(OrdersTab.ACTIVE)
    assertEquals(OrdersTab.ACTIVE, viewModel.ordersTab.value)
  }

  @Test
  fun testGamificationSpinWheel() {
    val initialCoins = viewModel.userProfile.value.rewardCoins

    assertFalse(viewModel.showSpinWheelDialog.value)
    viewModel.openSpinWheel()
    assertTrue(viewModel.showSpinWheelDialog.value)

    // Claim reward of 200 coins
    viewModel.claimSpinReward(200)
    assertEquals(initialCoins + 200, viewModel.userProfile.value.rewardCoins)

    viewModel.closeSpinWheel()
    assertFalse(viewModel.showSpinWheelDialog.value)
  }

  @Test
  fun testLiveMapTrackingDialog() {
    assertFalse(viewModel.showLiveMapSheet.value)

    viewModel.openLiveMapTracking()
    assertTrue(viewModel.showLiveMapSheet.value)

    viewModel.closeLiveMapTracking()
    assertFalse(viewModel.showLiveMapSheet.value)
  }

  @Test
  fun testSearchFlowLifecycle() {
    assertFalse(viewModel.isSearching.value)

    viewModel.openSearch("Keyboard")
    assertTrue(viewModel.isSearching.value)
    assertEquals("Keyboard", viewModel.searchQuery.value)

    viewModel.onSearchQueryChanged("Desk Mat")
    assertEquals("Desk Mat", viewModel.searchQuery.value)

    viewModel.submitSearch("Robot")
    assertTrue(viewModel.recentSearches.value.contains("Robot"))

    viewModel.clearSearch()
    assertEquals("", viewModel.searchQuery.value)

    viewModel.closeSearch()
    assertFalse(viewModel.isSearching.value)
  }

  @Test
  fun testWishlistDialogLifecycle() {
    assertFalse(viewModel.showWishlistSheet.value)

    viewModel.openWishlist()
    assertTrue(viewModel.showWishlistSheet.value)

    viewModel.closeWishlist()
    assertFalse(viewModel.showWishlistSheet.value)
  }
}
