package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.RongdhonuRepository
import com.example.data.SampleData
import com.example.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Rongdhonu Shop", appName)
  }

  @Test
  fun `cart and order placement test`() {
    val sampleProduct = SampleData.products.first()
    RongdhonuRepository.clearCart()
    RongdhonuRepository.addToCart(sampleProduct, "Size: L", 2)

    val cartItems = RongdhonuRepository.cart.value
    assertEquals(1, cartItems.size)
    assertEquals(2, cartItems.first().quantity)

    val order = RongdhonuRepository.placeOrder(
        customerName = "Test User",
        phone = "01632042441",
        email = "test@example.com",
        address = "Dhaka, Bangladesh",
        district = "Dhaka",
        thana = "Mirpur",
        deliveryMethod = "Standard",
        orderNote = "Test note",
        paymentMethod = PaymentMethod.COD,
        transactionId = null,
        insideDhaka = true
    )

    assertNotNull(order.id)
    assertTrue(order.id.startsWith("RS-"))
    assertEquals(0, RongdhonuRepository.cart.value.size)
  }
}
