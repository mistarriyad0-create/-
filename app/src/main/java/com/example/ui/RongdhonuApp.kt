package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RongdhonuRepository
import com.example.model.*
import com.example.ui.components.RongdhonuTopAppBar
import com.example.ui.localization.Strings
import com.example.ui.screens.*
import com.example.ui.theme.RongdhonuSecondary

enum class AppScreen {
    HOME,
    SOCIAL_MEDIA,
    CATEGORIES,
    CART,
    ORDERS,
    PROFILE,
    PRODUCT_DETAIL,
    CHECKOUT,
    ORDER_SUCCESS,
    ADMIN_PANEL
}

@Composable
fun RongdhonuApp(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var previousScreen by remember { mutableStateOf(AppScreen.HOME) }

    val language by RongdhonuRepository.language.collectAsStateWithLifecycle()
    val products by RongdhonuRepository.products.collectAsStateWithLifecycle()
    val categories by RongdhonuRepository.categories.collectAsStateWithLifecycle()
    val cart by RongdhonuRepository.cart.collectAsStateWithLifecycle()
    val wishlist by RongdhonuRepository.wishlist.collectAsStateWithLifecycle()
    val orders by RongdhonuRepository.orders.collectAsStateWithLifecycle()
    val banners by RongdhonuRepository.banners.collectAsStateWithLifecycle()
    val appliedCoupon by RongdhonuRepository.appliedCoupon.collectAsStateWithLifecycle()
    val isAdminLoggedIn by RongdhonuRepository.isAdminLoggedIn.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var lastCreatedOrder by remember { mutableStateOf<Order?>(null) }
    var insideDhaka by remember { mutableStateOf(true) }

    fun navigateTo(screen: AppScreen) {
        previousScreen = currentScreen
        currentScreen = screen
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (currentScreen != AppScreen.PRODUCT_DETAIL &&
                currentScreen != AppScreen.CHECKOUT &&
                currentScreen != AppScreen.ADMIN_PANEL &&
                currentScreen != AppScreen.ORDER_SUCCESS
            ) {
                RongdhonuTopAppBar(
                    language = language,
                    cartCount = cart.sumOf { it.quantity },
                    wishlistCount = wishlist.size,
                    onLanguageToggle = { RongdhonuRepository.toggleLanguage() },
                    onCartClick = { navigateTo(AppScreen.CART) },
                    onWishlistClick = {
                        // Filter search or show wishlist in categories
                        navigateTo(AppScreen.CATEGORIES)
                    },
                    onSocialMediaClick = { navigateTo(AppScreen.SOCIAL_MEDIA) }
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.PRODUCT_DETAIL &&
                currentScreen != AppScreen.CHECKOUT &&
                currentScreen != AppScreen.ADMIN_PANEL &&
                currentScreen != AppScreen.ORDER_SUCCESS
            ) {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    // 1. Home (হোম)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = {
                            searchQuery = ""
                            navigateTo(AppScreen.HOME)
                        },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = Strings.navHome(language)
                            )
                        },
                        label = { Text(Strings.navHome(language), fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.HOME) FontWeight.Bold else FontWeight.Normal) }
                    )

                    // 2. Company Social Media (কোম্পানির সোশ্যাল মিডিয়া - ক্লিক করে দেখুন)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SOCIAL_MEDIA,
                        onClick = { navigateTo(AppScreen.SOCIAL_MEDIA) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.SOCIAL_MEDIA) Icons.Filled.Public else Icons.Outlined.Public,
                                contentDescription = Strings.companySocialMedia(language)
                            )
                        },
                        label = { Text(Strings.navSocialMedia(language), fontSize = 11.sp, fontWeight = if (currentScreen == AppScreen.SOCIAL_MEDIA) FontWeight.Bold else FontWeight.Normal) }
                    )

                    // 3. Categories (ক্যাটাগরি)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.CATEGORIES,
                        onClick = { navigateTo(AppScreen.CATEGORIES) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.CATEGORIES) Icons.Filled.GridView else Icons.Outlined.GridView,
                                contentDescription = Strings.navCategories(language)
                            )
                        },
                        label = { Text(Strings.navCategories(language), fontSize = 11.sp) }
                    )

                    // 4. Cart with badge (কার্ট)
                    val cartCount = cart.sumOf { it.quantity }
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.CART,
                        onClick = { navigateTo(AppScreen.CART) },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (cartCount > 0) {
                                        Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                            Text("$cartCount", fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    if (currentScreen == AppScreen.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                    contentDescription = Strings.navCart(language)
                                )
                            }
                        },
                        label = { Text(Strings.navCart(language), fontSize = 11.sp) }
                    )

                    // 5. Profile & Orders (প্রোফাইল)
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PROFILE || currentScreen == AppScreen.ORDERS,
                        onClick = { navigateTo(AppScreen.PROFILE) },
                        icon = {
                            Icon(
                                if (currentScreen == AppScreen.PROFILE || currentScreen == AppScreen.ORDERS) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = Strings.navProfile(language)
                            )
                        },
                        label = { Text(Strings.navProfile(language), fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.HOME -> {
                    HomeScreen(
                        language = language,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        products = products,
                        categories = categories,
                        banners = banners,
                        wishlist = wishlist,
                        onProductClick = { prod ->
                            selectedProduct = prod
                            navigateTo(AppScreen.PRODUCT_DETAIL)
                        },
                        onAddToCart = { prod ->
                            RongdhonuRepository.addToCart(prod)
                        },
                        onToggleWishlist = { prod ->
                            RongdhonuRepository.toggleWishlist(prod.id)
                        },
                        onCategoryClick = { cat ->
                            selectedCategory = cat
                            navigateTo(AppScreen.CATEGORIES)
                        },
                        onOpenSocialMedia = {
                            navigateTo(AppScreen.SOCIAL_MEDIA)
                        }
                    )
                }

                AppScreen.SOCIAL_MEDIA -> {
                    BackHandler {
                        navigateTo(AppScreen.HOME)
                    }
                    CompanySocialMediaScreen(
                        language = language,
                        onBack = { navigateTo(AppScreen.HOME) }
                    )
                }

                AppScreen.CATEGORIES -> {
                    BackHandler {
                        navigateTo(AppScreen.HOME)
                    }
                    CategoriesScreen(
                        language = language,
                        categories = categories,
                        products = products,
                        selectedCategory = selectedCategory,
                        onSelectCategory = { selectedCategory = it },
                        wishlist = wishlist,
                        onProductClick = { prod ->
                            selectedProduct = prod
                            navigateTo(AppScreen.PRODUCT_DETAIL)
                        },
                        onAddToCart = { prod ->
                            RongdhonuRepository.addToCart(prod)
                        },
                        onToggleWishlist = { prod ->
                            RongdhonuRepository.toggleWishlist(prod.id)
                        }
                    )
                }

                AppScreen.CART -> {
                    BackHandler {
                        navigateTo(AppScreen.HOME)
                    }
                    CartScreen(
                        language = language,
                        cartItems = cart,
                        appliedCoupon = appliedCoupon,
                        insideDhaka = insideDhaka,
                        onToggleDeliveryLocation = { insideDhaka = it },
                        onUpdateQuantity = { id, qty -> RongdhonuRepository.updateCartQuantity(id, qty) },
                        onRemoveItem = { id -> RongdhonuRepository.removeFromCart(id) },
                        onApplyCoupon = { code ->
                            val sub = cart.sumOf { it.totalPrice }
                            RongdhonuRepository.applyCoupon(code, sub)
                        },
                        onRemoveCoupon = { RongdhonuRepository.removeCoupon() },
                        onProceedToCheckout = { navigateTo(AppScreen.CHECKOUT) },
                        onStartShopping = { navigateTo(AppScreen.HOME) }
                    )
                }

                AppScreen.CHECKOUT -> {
                    CheckoutScreen(
                        language = language,
                        cartItems = cart,
                        appliedCoupon = appliedCoupon,
                        insideDhaka = insideDhaka,
                        onBack = { navigateTo(AppScreen.CART) },
                        onPlaceOrder = { name, phone, email, addr, dist, thana, method, note, paymentMethod, trxId ->
                            RongdhonuRepository.placeOrder(
                                customerName = name,
                                phone = phone,
                                email = email,
                                address = addr,
                                district = dist,
                                thana = thana,
                                deliveryMethod = method,
                                orderNote = note,
                                paymentMethod = paymentMethod,
                                transactionId = trxId,
                                insideDhaka = insideDhaka
                            )
                        },
                        onOrderPlaced = { order ->
                            lastCreatedOrder = order
                            navigateTo(AppScreen.ORDER_SUCCESS)
                        }
                    )
                }

                AppScreen.ORDER_SUCCESS -> {
                    BackHandler {
                        navigateTo(AppScreen.HOME)
                    }
                    OrderSuccessView(
                        order = lastCreatedOrder,
                        language = language,
                        onViewOrders = { navigateTo(AppScreen.ORDERS) },
                        onContinueShopping = { navigateTo(AppScreen.HOME) }
                    )
                }

                AppScreen.ORDERS -> {
                    BackHandler {
                        navigateTo(AppScreen.HOME)
                    }
                    OrdersScreen(
                        language = language,
                        orders = orders,
                        onCancelOrder = { id -> RongdhonuRepository.cancelOrder(id) },
                        onStartShopping = { navigateTo(AppScreen.HOME) }
                    )
                }

                AppScreen.PROFILE -> {
                    BackHandler {
                        navigateTo(AppScreen.HOME)
                    }
                    ProfileScreen(
                        language = language,
                        wishlistCount = wishlist.size,
                        isAdminLoggedIn = isAdminLoggedIn,
                        onToggleLanguage = { RongdhonuRepository.toggleLanguage() },
                        onOpenWishlist = { navigateTo(AppScreen.CATEGORIES) },
                        onOpenAdminPanel = { navigateTo(AppScreen.ADMIN_PANEL) },
                        onOpenSocialMedia = { navigateTo(AppScreen.SOCIAL_MEDIA) },
                        onOpenOrders = { navigateTo(AppScreen.ORDERS) },
                        onAdminLogin = { pass -> RongdhonuRepository.loginAdmin(pass) },
                        onAdminLogout = { RongdhonuRepository.logoutAdmin() }
                    )
                }

                AppScreen.PRODUCT_DETAIL -> {
                    selectedProduct?.let { prod ->
                        ProductDetailScreen(
                            product = prod,
                            language = language,
                            isWishlisted = wishlist.contains(prod.id),
                            onBack = { navigateTo(previousScreen) },
                            onAddToCart = { p, variant, qty ->
                                RongdhonuRepository.addToCart(p, variant, qty)
                            },
                            onBuyNow = { p, variant, qty ->
                                RongdhonuRepository.addToCart(p, variant, qty)
                                navigateTo(AppScreen.CART)
                            },
                            onToggleWishlist = { p ->
                                RongdhonuRepository.toggleWishlist(p.id)
                            }
                        )
                    }
                }

                AppScreen.ADMIN_PANEL -> {
                    AdminScreen(
                        language = language,
                        products = products,
                        categories = categories,
                        orders = orders,
                        onBack = { navigateTo(AppScreen.PROFILE) },
                        onUpdateOrderStatus = { id, st -> RongdhonuRepository.updateOrderStatus(id, st) },
                        onUpdatePaymentStatus = { id, ps, trx -> RongdhonuRepository.updatePaymentStatus(id, ps, trx) },
                        onUpdateCourier = { id, cr, tr -> RongdhonuRepository.updateCourier(id, cr, tr) },
                        onAddProduct = { p -> RongdhonuRepository.addProduct(p) },
                        onUpdateProduct = { p -> RongdhonuRepository.updateProduct(p) },
                        onDeleteProduct = { id -> RongdhonuRepository.deleteProduct(id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderSuccessView(
    order: Order?,
    language: Language,
    onViewOrders: () -> Unit,
    onContinueShopping: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (language == Language.BN) "অর্ডার সফলভাবে সম্পন্ন হয়েছে!" else "Order Placed Successfully!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                order?.let {
                    Text(
                        text = "Order ID: ${it.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total Amount: ৳${it.grandTotal} (${it.paymentMethod.name})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (language == Language.BN)
                        "রংধনু শপে কেনাকাটার জন্য ধন্যবাদ। আমাদের প্রতিনিধি দ্রুতই আপনার ঠিকানায় পার্সেল পাঠাতে যোগাযোগ করবেন।"
                    else
                        "Thank you for shopping at Rongdhonu Shop! We will contact you shortly to dispatch your parcel.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onViewOrders,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Strings.trackOrder(language), fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onContinueShopping,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(Strings.startShopping(language))
                }
            }
        }
    }
}
