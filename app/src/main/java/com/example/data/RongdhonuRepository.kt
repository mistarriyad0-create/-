package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object RongdhonuRepository {
    private val _language = MutableStateFlow(Language.BN) // Default Bengali
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _products = MutableStateFlow(SampleData.products)
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow(SampleData.categories)
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cart: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _wishlist = MutableStateFlow<Set<String>>(emptySet())
    val wishlist: StateFlow<Set<String>> = _wishlist.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(SampleData.sampleOrders)
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _banners = MutableStateFlow(SampleData.promoBanners)
    val banners: StateFlow<List<PromoBanner>> = _banners.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _allCoupons = MutableStateFlow(SampleData.sampleCoupons)
    val allCoupons: StateFlow<List<Coupon>> = _allCoupons.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _currentAdminRole = MutableStateFlow(AdminRole.OWNER)
    val currentAdminRole: StateFlow<AdminRole> = _currentAdminRole.asStateFlow()

    // --- Advanced Admin Flows ---
    private val _customers = MutableStateFlow(SampleData.sampleCustomers)
    val customers: StateFlow<List<CustomerProfile>> = _customers.asStateFlow()

    private val _tickets = MutableStateFlow(SampleData.sampleTickets)
    val tickets: StateFlow<List<SupportTicket>> = _tickets.asStateFlow()

    private val _expenses = MutableStateFlow(SampleData.sampleExpenses)
    val expenses: StateFlow<List<ExpenseRecord>> = _expenses.asStateFlow()

    private val _digitalServices = MutableStateFlow(SampleData.sampleDigitalServices)
    val digitalServices: StateFlow<List<DigitalServiceRequest>> = _digitalServices.asStateFlow()

    private val _ads = MutableStateFlow(SampleData.sampleAds)
    val ads: StateFlow<List<AdvertisementBanner>> = _ads.asStateFlow()

    private val _notifications = MutableStateFlow(SampleData.sampleNotifications)
    val notifications: StateFlow<List<AdminNotification>> = _notifications.asStateFlow()

    private val _auditLogs = MutableStateFlow(SampleData.sampleAuditLogs)
    val auditLogs: StateFlow<List<AdminAuditLog>> = _auditLogs.asStateFlow()

    private val _storeSettings = MutableStateFlow(SampleData.defaultStoreSettings)
    val storeSettings: StateFlow<StoreSettings> = _storeSettings.asStateFlow()

    private val _liveVisitors = MutableStateFlow(18)
    val liveVisitors: StateFlow<Int> = _liveVisitors.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    // --- Personal Account & Digital Banking System ---
    private val _digitalWallets = MutableStateFlow(SampleData.sampleDigitalWallets)
    val digitalWallets: StateFlow<List<DigitalWalletAccount>> = _digitalWallets.asStateFlow()

    private val _personalTransactions = MutableStateFlow(SampleData.samplePersonalTransactions)
    val personalTransactions: StateFlow<List<PersonalTransaction>> = _personalTransactions.asStateFlow()

    private val _personalSummary = MutableStateFlow(PersonalAccountSummary())
    val personalSummary: StateFlow<PersonalAccountSummary> = _personalSummary.asStateFlow()

    private val _bankingAuditLogs = MutableStateFlow(SampleData.sampleBankingAuditLogs)
    val bankingAuditLogs: StateFlow<List<BankingAuditLog>> = _bankingAuditLogs.asStateFlow()


    fun toggleLanguage() {
        _language.value = if (_language.value == Language.BN) Language.EN else Language.BN
    }

    fun setLanguage(lang: Language) {
        _language.value = lang
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        addAuditLog("Theme changed to ${mode.name}")
    }

    fun toggleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.SYSTEM -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.LIGHT
            ThemeMode.LIGHT -> ThemeMode.SYSTEM
        }
    }

    fun loginAdmin(password: String): Boolean {
        if (password.trim() == "rongdhonu123" || password.trim() == "admin") {
            _isAdminLoggedIn.value = true
            addAuditLog("Admin logged in successfully")
            return true
        }
        return false
    }

    fun logoutAdmin() {
        addAuditLog("Admin logged out")
        _isAdminLoggedIn.value = false
    }

    fun setAdminRole(role: AdminRole) {
        _currentAdminRole.value = role
        addAuditLog("Switched active administrative role to ${role.name}")
    }

    fun addAuditLog(action: String) {
        val newLog = AdminAuditLog(
            id = "log_${System.currentTimeMillis()}",
            adminName = "Admin User",
            role = _currentAdminRole.value,
            action = action,
            timestamp = System.currentTimeMillis()
        )
        _auditLogs.value = listOf(newLog) + _auditLogs.value
    }

    fun addToCart(product: Product, variant: String? = null, qty: Int = 1) {
        val currentList = _cart.value.toMutableList()
        val existingIndex = currentList.indexOfFirst {
            it.product.id == product.id && it.selectedVariant == variant
        }
        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + qty)
        } else {
            currentList.add(
                CartItem(
                    id = "ci_${System.currentTimeMillis()}_${(100..999).random()}",
                    product = product,
                    quantity = qty,
                    selectedVariant = variant ?: product.variants.firstOrNull()
                )
            )
        }
        _cart.value = currentList
    }

    fun updateCartQuantity(cartItemId: String, newQty: Int) {
        if (newQty <= 0) {
            removeFromCart(cartItemId)
            return
        }
        _cart.value = _cart.value.map {
            if (it.id == cartItemId) it.copy(quantity = newQty) else it
        }
    }

    fun removeFromCart(cartItemId: String) {
        _cart.value = _cart.value.filterNot { it.id == cartItemId }
    }

    fun clearCart() {
        _cart.value = emptyList()
        _appliedCoupon.value = null
    }

    fun toggleWishlist(productId: String) {
        val current = _wishlist.value.toMutableSet()
        if (current.contains(productId)) {
            current.remove(productId)
        } else {
            current.add(productId)
        }
        _wishlist.value = current
    }

    fun applyCoupon(code: String, subtotal: Double): String? {
        val clean = code.trim().uppercase()
        val found = _allCoupons.value.find { it.code.uppercase() == clean }
        if (found != null) {
            if (subtotal >= found.minSpend) {
                _appliedCoupon.value = found
                return null
            } else {
                return "Minimum spend for ${found.code} is ৳${found.minSpend.toInt()}"
            }
        }
        return "Invalid coupon code. Try 'RONGDHONU10' or 'EID2026'"
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    fun addCoupon(coupon: Coupon) {
        _allCoupons.value = listOf(coupon) + _allCoupons.value
        addAuditLog("Created new promotional coupon: ${coupon.code} (${coupon.percentOff}%)")
    }

    fun deleteCoupon(code: String) {
        _allCoupons.value = _allCoupons.value.filterNot { it.code == code }
        addAuditLog("Removed coupon: $code")
    }

    fun placeOrder(
        customerName: String,
        phone: String,
        email: String,
        address: String,
        district: String,
        thana: String,
        deliveryMethod: String,
        orderNote: String,
        paymentMethod: PaymentMethod,
        transactionId: String?,
        insideDhaka: Boolean
    ): Order {
        val currentCart = _cart.value
        val subtotal = currentCart.sumOf { it.totalPrice }
        val coupon = _appliedCoupon.value
        val discount = if (coupon != null) (subtotal * coupon.percentOff / 100.0) else 0.0
        val deliveryCharge = if (insideDhaka) _storeSettings.value.deliveryDhaka else _storeSettings.value.deliveryOutside
        val grandTotal = (subtotal - discount + deliveryCharge).coerceAtLeast(0.0)

        val uniqueNumber = (10000..99999).random()
        val orderId = "RS-$uniqueNumber"

        val initialPaymentStatus = when (paymentMethod) {
            PaymentMethod.COD -> PaymentStatus.UNPAID
            PaymentMethod.BKASH, PaymentMethod.NAGAD -> {
                if (!transactionId.isNullOrBlank()) PaymentStatus.PENDING_VERIFICATION else PaymentStatus.UNPAID
            }
            PaymentMethod.WHATSAPP -> PaymentStatus.UNPAID
        }

        val newOrder = Order(
            id = orderId,
            customerName = customerName,
            customerPhone = phone,
            customerEmail = email,
            deliveryAddress = address,
            district = district,
            thana = thana,
            deliveryMethod = deliveryMethod,
            orderNote = orderNote,
            items = currentCart,
            subtotal = subtotal,
            discount = discount,
            deliveryCharge = deliveryCharge,
            grandTotal = grandTotal,
            couponCode = coupon?.code,
            paymentMethod = paymentMethod,
            paymentStatus = initialPaymentStatus,
            transactionId = transactionId,
            orderStatus = OrderStatus.PENDING,
            createdAt = System.currentTimeMillis()
        )

        // Decrement stock & increment sold count
        val updatedProducts = _products.value.map { prod ->
            val orderedQty = currentCart.filter { it.product.id == prod.id }.sumOf { it.quantity }
            if (orderedQty > 0) {
                val newStock = (prod.stock - orderedQty).coerceAtLeast(0)
                if (newStock <= 5) {
                    addNotification("Low Stock Alert!", "${prod.sku} stock reduced to $newStock units.", NotificationType.LOW_STOCK)
                }
                prod.copy(stock = newStock, soldCount = prod.soldCount + orderedQty)
            } else {
                prod
            }
        }
        _products.value = updatedProducts

        _orders.value = listOf(newOrder) + _orders.value
        addNotification("New Order Received ($orderId)", "$customerName placed an order for ৳${grandTotal.toInt()} via ${paymentMethod.name}", NotificationType.NEW_ORDER)
        addAuditLog("New order created: $orderId ($customerName, ৳${grandTotal.toInt()})")

        // Register / update customer profile
        updateOrAddCustomerFromOrder(customerName, phone, email, district, grandTotal)

        clearCart()
        return newOrder
    }

    private fun updateOrAddCustomerFromOrder(name: String, phone: String, email: String, district: String, spent: Double) {
        val existing = _customers.value.find { it.phone == phone }
        if (existing != null) {
            val updated = existing.copy(
                totalOrders = existing.totalOrders + 1,
                totalSpent = existing.totalSpent + spent,
                loyaltyPoints = existing.loyaltyPoints + (spent * 0.05).toInt(),
                tier = if (existing.totalSpent + spent > 5000) CustomerTier.VIP else CustomerTier.REGULAR
            )
            _customers.value = _customers.value.map { if (it.id == existing.id) updated else it }
        } else {
            val newCustomer = CustomerProfile(
                id = "cust_${System.currentTimeMillis()}",
                name = name,
                phone = phone,
                email = email.ifBlank { "customer@rongdhonushop.com" },
                district = district,
                tier = CustomerTier.NEW,
                loyaltyPoints = (spent * 0.05).toInt(),
                totalOrders = 1,
                totalSpent = spent
            )
            _customers.value = listOf(newCustomer) + _customers.value
            addNotification("New Customer Registered", "$name ($phone) joined from $district", NotificationType.NEW_CUSTOMER)
        }
    }

    fun cancelOrder(orderId: String): Boolean {
        var success = false
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId && order.orderStatus == OrderStatus.PENDING) {
                success = true
                order.copy(orderStatus = OrderStatus.CANCELLED)
            } else {
                order
            }
        }
        if (success) {
            addAuditLog("Cancelled order: $orderId")
        }
        return success
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) it.copy(orderStatus = newStatus) else it
        }
        addAuditLog("Updated order $orderId status to ${newStatus.name}")
    }

    fun updatePaymentStatus(orderId: String, newStatus: PaymentStatus, trxId: String? = null) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) {
                it.copy(
                    paymentStatus = newStatus,
                    transactionId = trxId ?: it.transactionId
                )
            } else it
        }
        addAuditLog("Updated payment status for $orderId to ${newStatus.name} (TrxID: $trxId)")
    }

    fun updateCourier(orderId: String, courier: String, trackingNumber: String, charge: Double? = null) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) {
                it.copy(
                    courierName = courier,
                    courierTrackingNumber = trackingNumber,
                    deliveryCharge = charge ?: it.deliveryCharge,
                    orderStatus = if (it.orderStatus == OrderStatus.PENDING || it.orderStatus == OrderStatus.CONFIRMED) OrderStatus.SHIPPED else it.orderStatus
                )
            } else it
        }
        addAuditLog("Assigned courier $courier ($trackingNumber) to order $orderId")
    }

    fun toggleCodConfirmed(orderId: String) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) it.copy(isCodConfirmed = !it.isCodConfirmed) else it
        }
    }

    fun processRefundOrReturn(orderId: String, refundAmount: Double, reason: String, isReturn: Boolean) {
        _orders.value = _orders.value.map {
            if (it.id == orderId) {
                it.copy(
                    refundAmount = refundAmount,
                    returnReason = reason,
                    orderStatus = if (isReturn) OrderStatus.RETURNED else it.orderStatus,
                    paymentStatus = if (refundAmount >= it.grandTotal) PaymentStatus.REFUNDED else it.paymentStatus
                )
            } else it
        }
        addAuditLog("Processed ${if (isReturn) "return & exchange" else "refund"} of ৳$refundAmount for $orderId (Reason: $reason)")
    }

    fun addProduct(product: Product) {
        _products.value = listOf(product) + _products.value
        addAuditLog("Added new product: ${product.sku} - ${product.nameEn}")
    }

    fun updateProduct(product: Product) {
        _products.value = _products.value.map {
            if (it.id == product.id) product else it
        }
        addAuditLog("Updated product details: ${product.sku} (${product.nameEn})")
    }

    fun deleteProduct(productId: String) {
        val p = _products.value.find { it.id == productId }
        _products.value = _products.value.filterNot { it.id == productId }
        addAuditLog("Deleted product: ${p?.sku ?: productId}")
    }

    // --- Bulk CSV Import & Export ---
    fun exportProductsToCsv(): String {
        val sb = StringBuilder()
        sb.append("SKU,Name_EN,Name_BN,Category,RegularPrice,DiscountPrice,CostPrice,Stock,Brand\n")
        _products.value.forEach { p ->
            sb.append("${p.sku},\"${p.nameEn.replace("\"", "\"\"")}\",\"${p.nameBn.replace("\"", "\"\"")}\",${p.categoryId},${p.regularPrice},${p.discountPrice},${p.costPrice},${p.stock},${p.brand}\n")
        }
        return sb.toString()
    }

    fun importProductsFromCsv(csvContent: String): Int {
        var count = 0
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.size <= 1) return 0
        val newProducts = mutableListOf<Product>()
        for (i in 1 until lines.size) {
            val parts = lines[i].split(",")
            if (parts.size >= 8) {
                val sku = parts[0].trim().replace("\"", "")
                val nameEn = parts[1].trim().replace("\"", "")
                val nameBn = parts[2].trim().replace("\"", "")
                val cat = parts[3].trim().replace("\"", "")
                val regPrice = parts[4].trim().toDoubleOrNull() ?: 500.0
                val discPrice = parts[5].trim().toDoubleOrNull() ?: regPrice
                val costPrice = parts[6].trim().toDoubleOrNull() ?: (regPrice * 0.7)
                val stock = parts[7].trim().toIntOrNull() ?: 10
                newProducts.add(
                    Product(
                        id = "prod_import_${System.currentTimeMillis()}_$i",
                        sku = sku,
                        nameEn = nameEn,
                        nameBn = nameBn,
                        categoryId = cat,
                        descriptionEn = "Imported product",
                        descriptionBn = "আমদানি করা পণ্য",
                        regularPrice = regPrice,
                        discountPrice = discPrice,
                        costPrice = costPrice,
                        stock = stock,
                        imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800&q=80"
                    )
                )
                count++
            }
        }
        if (newProducts.isNotEmpty()) {
            _products.value = newProducts + _products.value
            addAuditLog("Bulk imported $count products from CSV")
        }
        return count
    }

    // --- Customer Management ---
    fun updateCustomerTier(customerId: String, tier: CustomerTier, customDiscount: Int) {
        _customers.value = _customers.value.map {
            if (it.id == customerId) it.copy(tier = tier, customDiscountPercent = customDiscount) else it
        }
        addAuditLog("Updated customer ($customerId) to tier ${tier.name} with $customDiscount% discount")
    }

    // --- Support Tickets ---
    fun updateTicketStatus(ticketId: String, status: TicketStatus) {
        _tickets.value = _tickets.value.map {
            if (it.id == ticketId) it.copy(status = status) else it
        }
        addAuditLog("Updated support ticket $ticketId status to ${status.name}")
    }

    // --- Expenses ---
    fun addExpense(title: String, category: String, amount: Double) {
        val exp = ExpenseRecord(
            id = "exp_${System.currentTimeMillis()}",
            title = title,
            category = category,
            amount = amount
        )
        _expenses.value = listOf(exp) + _expenses.value
        addAuditLog("Recorded business expense: $title (৳$amount)")
    }

    fun deleteExpense(id: String) {
        _expenses.value = _expenses.value.filterNot { it.id == id }
    }

    // --- Digital Services ---
    fun updateDigitalService(id: String, status: ServiceStatus, notes: String, price: Double? = null) {
        _digitalServices.value = _digitalServices.value.map {
            if (it.id == id) {
                it.copy(
                    status = status,
                    notes = notes,
                    quotedPrice = price ?: it.quotedPrice
                )
            } else it
        }
        addAuditLog("Updated digital service $id to ${status.name}")
    }

    // --- Banners & Ads ---
    fun addBanner(banner: PromoBanner) {
        _banners.value = listOf(banner) + _banners.value
        addAuditLog("Added promo banner: ${banner.titleEn}")
    }

    fun toggleBannerLive(id: String) {
        _banners.value = _banners.value.map {
            if (it.id == id) it.copy(isLive = !it.isLive) else it
        }
    }

    fun deleteBanner(id: String) {
        _banners.value = _banners.value.filterNot { it.id == id }
    }

    fun toggleAdActive(id: String) {
        _ads.value = _ads.value.map {
            if (it.id == id) it.copy(isActive = !it.isActive) else it
        }
    }

    // --- Notifications ---
    fun addNotification(title: String, message: String, type: NotificationType) {
        val notif = AdminNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = title,
            message = message,
            type = type
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    // --- Settings, Couriers & Design Control ---
    fun updateStoreSettings(settings: StoreSettings) {
        _storeSettings.value = settings
        addAuditLog("Updated store & system settings (SEO, WhatsApp, Delivery fees, Social links, Design)")
    }

    fun addCourierPartner(name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotBlank() && !_storeSettings.value.activeCouriers.contains(trimmed)) {
            val updatedCouriers = _storeSettings.value.activeCouriers + trimmed
            _storeSettings.value = _storeSettings.value.copy(activeCouriers = updatedCouriers)
            addAuditLog("Added new Courier Partner service: $trimmed")
        }
    }

    fun removeCourierPartner(name: String) {
        val updatedCouriers = _storeSettings.value.activeCouriers.filterNot { it.equals(name, ignoreCase = true) }
        _storeSettings.value = _storeSettings.value.copy(activeCouriers = updatedCouriers)
        addAuditLog("Removed Courier Partner service: $name")
    }

    fun updateStoreDesign(customThemeColor: String, announcementText: String, showAnnouncement: Boolean) {
        _storeSettings.value = _storeSettings.value.copy(
            customThemeColor = customThemeColor,
            announcementText = announcementText,
            showAnnouncement = showAnnouncement
        )
        addAuditLog("Admin customized storefront appearance & announcements")
    }
}

