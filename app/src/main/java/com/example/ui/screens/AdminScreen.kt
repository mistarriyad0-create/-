package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RongdhonuRepository
import com.example.model.*
import com.example.ui.components.OFFICIAL_WHATSAPP
import com.example.ui.components.openWhatsApp
import com.example.ui.localization.Strings
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

enum class AdminTab(val labelEn: String, val labelBn: String) {
    DASHBOARD("Dashboard", "ড্যাশবোর্ড"),
    ORDERS("Orders", "অর্ডার"),
    PRODUCTS("Products", "পণ্য"),
    CUSTOMERS("Customers", "কাস্টমার"),
    MARKETING("Marketing", "মার্কেটিং"),
    FINANCE("Finance", "আয়-ব্যয়"),
    SERVICES("Digital Services", "ডিজিটাল সেবা"),
    SETTINGS("Settings & Logs", "সেটিংস ও লগ")
}

enum class TimeFilter(val labelEn: String, val labelBn: String) {
    TODAY("Today", "আজকের"),
    WEEKLY("Weekly", "সাপ্তাহিক"),
    MONTHLY("Monthly", "মাসিক"),
    YEARLY("Yearly", "বার্ষিক"),
    ALL("All Time", "সব সময়")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    language: Language,
    products: List<Product>,
    categories: List<Category>,
    orders: List<Order>,
    onBack: () -> Unit,
    onUpdateOrderStatus: (String, OrderStatus) -> Unit,
    onUpdatePaymentStatus: (String, PaymentStatus, String?) -> Unit,
    onUpdateCourier: (String, String, String) -> Unit,
    onAddProduct: (Product) -> Unit,
    onUpdateProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AdminTab.DASHBOARD) }

    val adminRole by RongdhonuRepository.currentAdminRole.collectAsStateWithLifecycle()
    val notifications by RongdhonuRepository.notifications.collectAsStateWithLifecycle()
    val unreadNotifCount = notifications.count { !it.isRead }
    var showNotifDrawer by remember { mutableStateOf(false) }

    // Dialog states
    var showProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var selectedOrderForManage by remember { mutableStateOf<Order?>(null) }
    var viewingInvoiceOrder by remember { mutableStateOf<Order?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rongdhonu Shop Admin",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = adminRole.name,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Rongdhonu Digital Studio Control Hub",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Store")
                    }
                },
                actions = {
                    // Notification Icon with Badge
                    IconButton(onClick = { showNotifDrawer = true }) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifCount > 0) {
                                    Badge(containerColor = Color.Red) {
                                        Text("$unreadNotifCount", fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }

                    TextButton(onClick = onBack) {
                        Text("Exit", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Horizontal Admin Navigation Tabs
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                ScrollableTabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    edgePadding = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    AdminTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    text = if (language == Language.BN) tab.labelBn else tab.labelEn,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }

            // Tab Panels
            when (selectedTab) {
                AdminTab.DASHBOARD -> AdminDashboardTab(products, orders, language)
                AdminTab.ORDERS -> AdminOrdersTab(
                    orders = orders,
                    language = language,
                    onManageOrder = { selectedOrderForManage = it },
                    onViewInvoice = { viewingInvoiceOrder = it }
                )
                AdminTab.PRODUCTS -> AdminProductsTab(
                    products = products,
                    categories = categories,
                    language = language,
                    onAddNewProduct = {
                        editingProduct = null
                        showProductDialog = true
                    },
                    onEditProduct = { prod ->
                        editingProduct = prod
                        showProductDialog = true
                    },
                    onDeleteProduct = onDeleteProduct
                )
                AdminTab.CUSTOMERS -> AdminCustomersTab(language)
                AdminTab.MARKETING -> AdminMarketingTab(language, categories)
                AdminTab.FINANCE -> AdminFinanceTab(orders, language)
                AdminTab.SERVICES -> AdminDigitalServicesTab(language)
                AdminTab.SETTINGS -> AdminSettingsAndLogsTab(language)
            }
        }
    }

    // Product Add/Edit Dialog
    if (showProductDialog) {
        AdvancedProductFormDialog(
            categories = categories,
            existingProduct = editingProduct,
            onDismiss = { showProductDialog = false },
            onSave = { product ->
                if (editingProduct != null) {
                    onUpdateProduct(product)
                } else {
                    onAddProduct(product)
                }
                showProductDialog = false
            }
        )
    }

    // Order Management Dialog
    selectedOrderForManage?.let { order ->
        AdvancedOrderManagementDialog(
            order = order,
            language = language,
            onDismiss = { selectedOrderForManage = null },
            onUpdateStatus = { newStatus ->
                onUpdateOrderStatus(order.id, newStatus)
                selectedOrderForManage = null
            },
            onUpdatePayment = { newPayment, trxId ->
                onUpdatePaymentStatus(order.id, newPayment, trxId)
                selectedOrderForManage = null
            },
            onAssignCourier = { courier, tracking, charge ->
                RongdhonuRepository.updateCourier(order.id, courier, tracking, charge)
                selectedOrderForManage = null
            },
            onToggleCod = {
                RongdhonuRepository.toggleCodConfirmed(order.id)
                selectedOrderForManage = null
            },
            onProcessRefund = { amt, reason, isReturn ->
                RongdhonuRepository.processRefundOrReturn(order.id, amt, reason, isReturn)
                selectedOrderForManage = null
            }
        )
    }

    // Invoice Dialog
    viewingInvoiceOrder?.let { order ->
        InvoicePrintDialog(
            order = order,
            language = language,
            onDismiss = { viewingInvoiceOrder = null }
        )
    }

    // Notifications Dialog
    if (showNotifDrawer) {
        AdminNotificationsDialog(
            notifications = notifications,
            onDismiss = { showNotifDrawer = false },
            onMarkRead = { RongdhonuRepository.markNotificationRead(it) },
            onClearAll = { RongdhonuRepository.clearAllNotifications() }
        )
    }
}

// -------------------------------------------------------------
// 1. ADVANCED DASHBOARD TAB
// -------------------------------------------------------------
@Composable
fun AdminDashboardTab(
    products: List<Product>,
    orders: List<Order>,
    language: Language
) {
    var selectedTimeFilter by remember { mutableStateOf(TimeFilter.ALL) }
    val liveVisitors by RongdhonuRepository.liveVisitors.collectAsStateWithLifecycle()
    val expenses by RongdhonuRepository.expenses.collectAsStateWithLifecycle()

    val totalRevenue = remember(orders) {
        orders.filter { it.orderStatus != OrderStatus.CANCELLED }.sumOf { it.grandTotal }
    }
    val totalCostOfGoods = remember(orders) {
        orders.filter { it.orderStatus != OrderStatus.CANCELLED }.sumOf { order ->
            order.items.sumOf { it.product.costPrice * it.quantity }
        }
    }
    val totalExpenses = remember(expenses) { expenses.sumOf { it.amount } }
    val netProfit = (totalRevenue - totalCostOfGoods - totalExpenses).coerceAtLeast(0.0)

    val pendingCount = orders.count { it.orderStatus == OrderStatus.PENDING }
    val deliveredCount = orders.count { it.orderStatus == OrderStatus.DELIVERED }
    val lowStockList = remember(products) { products.filter { it.stock <= 5 } }

    val topViewedProducts = remember(products) { products.sortedByDescending { it.viewCount }.take(4) }
    val topSellingProducts = remember(products) { products.sortedByDescending { it.soldCount }.take(4) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Live Visitors & Time Filter Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(RongdhonuAccentGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.BN) "$liveVisitors জন ভিজিটর এখন ওয়েবসাইটে" else "$liveVisitors Live Visitors Online",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = RongdhonuAccentGreen
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Live Telemetry",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Time Filters
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(TimeFilter.values()) { tf ->
                            FilterChip(
                                selected = selectedTimeFilter == tf,
                                onClick = { selectedTimeFilter = tf },
                                label = {
                                    Text(
                                        if (language == Language.BN) tf.labelBn else tf.labelEn,
                                        fontSize = 11.sp
                                    )
                                },
                                modifier = Modifier.height(30.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4 KPI Cards (Total Sales, Net Profit, Orders, Expenses)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdvancedKpiCard(
                    title = if (language == Language.BN) "মোট বিক্রি" else "Total Revenue",
                    value = Strings.formatPrice(totalRevenue),
                    subtitle = if (language == Language.BN) "সর্বমোট অর্ডার পেমেন্ট" else "Gross Sales",
                    icon = Icons.Default.MonetizationOn,
                    color = RongdhonuPrimary,
                    modifier = Modifier.weight(1f)
                )
                AdvancedKpiCard(
                    title = if (language == Language.BN) "আনুমানিক নিট লাভ" else "Net Profit",
                    value = Strings.formatPrice(netProfit),
                    subtitle = if (language == Language.BN) "ক্রয় ও খরচ বাদে" else "After Cost & Ops",
                    icon = Icons.Default.TrendingUp,
                    color = RongdhonuAccentGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdvancedKpiCard(
                    title = if (language == Language.BN) "মোট অর্ডার" else "Total Orders",
                    value = "${orders.size}",
                    subtitle = if (language == Language.BN) "পেন্ডিং: $pendingCount" else "Pending: $pendingCount",
                    icon = Icons.Default.ShoppingBag,
                    color = RongdhonuSecondary,
                    modifier = Modifier.weight(1f)
                )
                AdvancedKpiCard(
                    title = if (language == Language.BN) "ব্যবসায়িক খরচ" else "Total Expenses",
                    value = Strings.formatPrice(totalExpenses),
                    subtitle = if (language == Language.BN) "${expenses.size} টি রেকর্ড" else "${expenses.size} entries",
                    icon = Icons.Default.Receipt,
                    color = Color(0xFFEF4444),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Low Stock & Out of Stock Warning
        if (lowStockList.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == Language.BN) "কম স্টক ও সতর্কতা (${lowStockList.size} টি পণ্য)" else "Low Stock Alerts (${lowStockList.size} Items)",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        lowStockList.forEach { p ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "• ${p.sku} - ${p.nameEn}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF7F1D1D),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = if (p.stock == 0) "Out of Stock" else "Stock: ${p.stock}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFDC2626)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top Selling Products & Analytics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.BN) "🔥 সবচেয়ে বেশি বিক্রি হওয়া পণ্য" else "🔥 Top Best-Selling Products",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    topSellingProducts.forEach { prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(prod.nameEn, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                                Text(prod.sku, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${prod.soldCount} sold", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RongdhonuSecondary)
                                Text("৳${prod.effectivePrice * prod.soldCount}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Top Viewed Products Analytics
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.BN) "👀 সবচেয়ে বেশি দেখা পণ্যসমূহ (Views)" else "👀 Most Viewed Products",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    topViewedProducts.forEach { prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(prod.nameEn, fontSize = 12.sp, modifier = Modifier.weight(1f), maxLines = 1)
                            Text("${prod.viewCount} views", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = RongdhonuPrimary)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun AdvancedKpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}

// -------------------------------------------------------------
// 2. ADVANCED ORDER MANAGEMENT TAB
// -------------------------------------------------------------
@Composable
fun AdminOrdersTab(
    orders: List<Order>,
    language: Language,
    onManageOrder: (Order) -> Unit,
    onViewInvoice: (Order) -> Unit
) {
    val context = LocalContext.current
    var statusFilter by remember { mutableStateOf<OrderStatus?>(null) }
    var orderSearchQuery by remember { mutableStateOf("") }

    val filteredOrders = remember(orders, statusFilter, orderSearchQuery) {
        orders.filter { order ->
            (statusFilter == null || order.orderStatus == statusFilter) &&
            (orderSearchQuery.isBlank() ||
             order.id.contains(orderSearchQuery, ignoreCase = true) ||
             order.customerPhone.contains(orderSearchQuery) ||
             order.customerName.contains(orderSearchQuery, ignoreCase = true))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filter header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = orderSearchQuery,
                    onValueChange = { orderSearchQuery = it },
                    placeholder = { Text("Search by Order #, phone, or name...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (orderSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { orderSearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = statusFilter == null,
                            onClick = { statusFilter = null },
                            label = { Text("All (${orders.size})", fontSize = 11.sp) }
                        )
                    }
                    items(OrderStatus.values()) { st ->
                        val count = orders.count { it.orderStatus == st }
                        FilterChip(
                            selected = statusFilter == st,
                            onClick = { statusFilter = st },
                            label = { Text("${st.name} ($count)", fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Orders List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredOrders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(order.id, fontWeight = FontWeight.Bold, color = RongdhonuPrimary, fontSize = 15.sp)
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    order.orderStatus.name,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Customer: ${order.customerName} (${order.customerPhone})", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                        Text("Delivery: ${order.deliveryAddress}, ${order.district}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Payment: ${order.paymentMethod.name} [${order.paymentStatus.name}]", fontSize = 11.sp)
                        order.transactionId?.let { trx ->
                            Text("TrxID: $trx", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RongdhonuSecondary)
                        }

                        // Courier info if any
                        order.courierName?.let { courier ->
                            Text("Courier: $courier | Tracking: ${order.courierTrackingNumber ?: "Active"}", fontSize = 11.sp, color = RongdhonuAccentGreen)
                        }

                        if (order.isCodConfirmed) {
                            Text("✓ COD Phone Confirmed", fontSize = 11.sp, color = RongdhonuAccentGreen, fontWeight = FontWeight.Bold)
                        }

                        if (order.refundAmount > 0) {
                            Text("⚠️ Refunded: ৳${order.refundAmount} (${order.returnReason ?: "N/A"})", fontSize = 11.sp, color = Color.Red)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons: Manage, Invoice, Send WhatsApp Update
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Button(
                                onClick = { onManageOrder(order) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Manage", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { onViewInvoice(order) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Invoice", fontSize = 11.sp)
                            }

                            // 1-click WhatsApp update to customer
                            IconButton(
                                onClick = {
                                    val msg = "Dear ${order.customerName},\n" +
                                            "Update on your Rongdhonu Shop Order #${order.id}:\n" +
                                            "Status: ${order.orderStatus.name}\n" +
                                            (order.courierName?.let { "Courier: $it\nTracking No: ${order.courierTrackingNumber}\n" } ?: "") +
                                            "Total: ৳${order.grandTotal}\n" +
                                            "Thank you for choosing Rongdhonu Digital Studio!"
                                    try {
                                        val url = "https://wa.me/88${order.customerPhone.trimStart('0')}?text=${Uri.encode(msg)}"
                                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Cannot open WhatsApp", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Chat, contentDescription = "Send WhatsApp Update", tint = WhatsAppGreen)
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// 3. SMART PRODUCT MANAGEMENT TAB (with CSV Export/Import)
// -------------------------------------------------------------
@Composable
fun AdminProductsTab(
    products: List<Product>,
    categories: List<Category>,
    language: Language,
    onAddNewProduct: () -> Unit,
    onEditProduct: (Product) -> Unit,
    onDeleteProduct: (String) -> Unit
) {
    val context = LocalContext.current
    var showCsvImportDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products else {
            products.filter {
                it.sku.contains(searchQuery, ignoreCase = true) ||
                it.nameEn.contains(searchQuery, ignoreCase = true) ||
                it.brand.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Actions Bar: Add Product, CSV Export, CSV Import
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Products (${products.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = {
                                val csv = RongdhonuRepository.exportProductsToCsv()
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, csv)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Export Products CSV"))
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export CSV", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { showCsvImportDialog = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Import CSV", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onAddNewProduct,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter products by SKU, name or brand...", fontSize = 11.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )
            }
        }

        // Product items
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filtered) { prod ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${prod.sku} • ${prod.nameEn}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (prod.publicationStatus == ProductPublicationStatus.PUBLISHED) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        prod.publicationStatus.label,
                                        fontSize = 9.sp,
                                        color = if (prod.publicationStatus == ProductPublicationStatus.PUBLISHED) Color(0xFF166534) else Color(0xFF92400E),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text("Brand: ${prod.brand} | Barcode: ${prod.barcode}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Selling: ৳${prod.effectivePrice} | Cost: ৳${prod.costPrice.toInt()} | Profit: ৳${(prod.effectivePrice - prod.costPrice).toInt()}", fontSize = 11.sp, color = RongdhonuPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Stock: ${prod.stock} units | Sold: ${prod.soldCount} units | Views: ${prod.viewCount}", fontSize = 11.sp)
                            Text("Warranty: ${prod.warranty}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        IconButton(onClick = { onEditProduct(prod) }) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = { onDeleteProduct(prod.id) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (showCsvImportDialog) {
        CsvImportDialog(
            onDismiss = { showCsvImportDialog = false },
            onImport = { csv ->
                val count = RongdhonuRepository.importProductsFromCsv(csv)
                Toast.makeText(context, "Successfully imported $count products", Toast.LENGTH_LONG).show()
                showCsvImportDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// 4. CUSTOMER MANAGEMENT TAB
// -------------------------------------------------------------
@Composable
fun AdminCustomersTab(language: Language) {
    val context = LocalContext.current
    val customers by RongdhonuRepository.customers.collectAsStateWithLifecycle()
    val tickets by RongdhonuRepository.tickets.collectAsStateWithLifecycle()

    var customerSubTab by remember { mutableStateOf("customers") } // "customers" or "tickets"
    var maskPhone by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = customerSubTab == "customers",
                        onClick = { customerSubTab = "customers" },
                        label = { Text("Customer Profiles (${customers.size})", fontSize = 11.sp) }
                    )
                    FilterChip(
                        selected = customerSubTab == "tickets",
                        onClick = { customerSubTab = "tickets" },
                        label = { Text("Support Tickets (${tickets.size})", fontSize = 11.sp) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Mask Data", fontSize = 10.sp)
                    Switch(
                        checked = maskPhone,
                        onCheckedChange = { maskPhone = it },
                        modifier = Modifier.scale(0.7f)
                    )
                }
            }
        }

        if (customerSubTab == "customers") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(customers) { cust ->
                    val displayPhone = if (maskPhone && cust.phone.length >= 8) {
                        cust.phone.take(3) + "****" + cust.phone.takeLast(4)
                    } else cust.phone

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cust.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Surface(
                                    color = Color(cust.tier.colorHex).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        cust.tier.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(cust.tier.colorHex),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Phone: $displayPhone | District: ${cust.district}", fontSize = 11.sp)
                            Text("Total Orders: ${cust.totalOrders} | Spent: ${Strings.formatPrice(cust.totalSpent)}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = RongdhonuPrimary)
                            Text("Loyalty Points: ${cust.loyaltyPoints} pts | Special Discount: ${cust.customDiscountPercent}%", fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        val newTier = if (cust.tier == CustomerTier.VIP) CustomerTier.REGULAR else CustomerTier.VIP
                                        RongdhonuRepository.updateCustomerTier(cust.id, newTier, if (newTier == CustomerTier.VIP) 10 else 0)
                                    },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(if (cust.tier == CustomerTier.VIP) "Downgrade Regular" else "Upgrade to VIP", fontSize = 10.sp)
                                }

                                IconButton(
                                    onClick = {
                                        val msg = "Dear ${cust.name}, greetings from Rongdhonu Digital Studio!"
                                        openWhatsApp(context, msg)
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = "WhatsApp", tint = WhatsAppGreen)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        } else {
            // Support Tickets view
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tickets) { ticket ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(ticket.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Surface(
                                    color = if (ticket.status == TicketStatus.RESOLVED) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        ticket.status.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("From: ${ticket.customerName} (${ticket.customerPhone})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(ticket.message, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (ticket.status != TicketStatus.RESOLVED) {
                                    TextButton(onClick = { RongdhonuRepository.updateTicketStatus(ticket.id, TicketStatus.RESOLVED) }) {
                                        Text("Mark Resolved", fontSize = 11.sp)
                                    }
                                }
                                IconButton(onClick = { openWhatsApp(context, "Regarding ticket ${ticket.id}: Hello ${ticket.customerName}") }) {
                                    Icon(Icons.Default.Chat, contentDescription = "Reply via WhatsApp", tint = WhatsAppGreen)
                                }
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. MARKETING & ADVERTISING TAB
// -------------------------------------------------------------
@Composable
fun AdminMarketingTab(language: Language, categories: List<Category>) {
    val banners by RongdhonuRepository.banners.collectAsStateWithLifecycle()
    val ads by RongdhonuRepository.ads.collectAsStateWithLifecycle()
    val coupons by RongdhonuRepository.allCoupons.collectAsStateWithLifecycle()

    var newCouponCode by remember { mutableStateOf("") }
    var newCouponPercent by remember { mutableStateOf("10") }
    var newCouponMin by remember { mutableStateOf("500") }
    var isBogo by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner Slider Management
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Homepage Hero Banners (${banners.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    banners.forEach { banner ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(banner.titleEn, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text(banner.discountTag, fontSize = 10.sp, color = RongdhonuSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Switch(
                                    checked = banner.isLive,
                                    onCheckedChange = { RongdhonuRepository.toggleBannerLive(banner.id) },
                                    modifier = Modifier.scale(0.7f)
                                )
                                IconButton(onClick = { RongdhonuRepository.deleteBanner(banner.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Coupon & Flash Deals Manager
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Coupons & Promotional Offers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    coupons.forEach { c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(c.code, fontWeight = FontWeight.Bold, color = RongdhonuPrimary, fontSize = 12.sp)
                                Text("${c.percentOff}% OFF | Min Spend: ৳${c.minSpend.toInt()}${if (c.isBogo) " [BOGO]" else ""}", fontSize = 10.sp)
                            }
                            IconButton(onClick = { RongdhonuRepository.deleteCoupon(c.code) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("Create New Coupon:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = newCouponCode,
                            onValueChange = { newCouponCode = it },
                            placeholder = { Text("Code", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1.5f).height(46.dp)
                        )
                        OutlinedTextField(
                            value = newCouponPercent,
                            onValueChange = { newCouponPercent = it },
                            placeholder = { Text("% Off", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).height(46.dp)
                        )
                        OutlinedTextField(
                            value = newCouponMin,
                            onValueChange = { newCouponMin = it },
                            placeholder = { Text("Min ৳", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f).height(46.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            if (newCouponCode.isNotBlank()) {
                                RongdhonuRepository.addCoupon(
                                    Coupon(
                                        code = newCouponCode.uppercase().trim(),
                                        percentOff = newCouponPercent.toIntOrNull() ?: 10,
                                        minSpend = newCouponMin.toDoubleOrNull() ?: 500.0,
                                        isBogo = isBogo
                                    )
                                )
                                newCouponCode = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Add Coupon")
                    }
                }
            }
        }

        // In-App Advertisement Slots
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Ad Banners & Impression Tracking", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    ads.forEach { ad ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ad.title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                Switch(
                                    checked = ad.isActive,
                                    onCheckedChange = { RongdhonuRepository.toggleAdActive(ad.id) },
                                    modifier = Modifier.scale(0.7f)
                                )
                            }
                            Text("Position: ${ad.position.label} | Impressions: ${ad.impressions} | Clicks: ${ad.clicks}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// -------------------------------------------------------------
// 6. PAYMENT & FINANCE TAB
// -------------------------------------------------------------
@Composable
fun AdminFinanceTab(orders: List<Order>, language: Language) {
    val expenses by RongdhonuRepository.expenses.collectAsStateWithLifecycle()
    var showExpenseDialog by remember { mutableStateOf(false) }

    val totalRevenue = orders.filter { it.orderStatus != OrderStatus.CANCELLED }.sumOf { it.grandTotal }
    val totalCost = orders.filter { it.orderStatus != OrderStatus.CANCELLED }.sumOf { order ->
        order.items.sumOf { it.product.costPrice * it.quantity }
    }
    val totalExpenses = expenses.sumOf { it.amount }
    val netProfit = (totalRevenue - totalCost - totalExpenses).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Financial Summary & Profitability", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    FinanceRow("Total Revenue (বিক্রি):", Strings.formatPrice(totalRevenue), RongdhonuPrimary)
                    FinanceRow("Cost of Goods Sold (পণ্য ক্রয়মূল্য):", "- ${Strings.formatPrice(totalCost)}", Color(0xFFDC2626))
                    FinanceRow("Operating Expenses (অন্যান্য খরচ):", "- ${Strings.formatPrice(totalExpenses)}", Color(0xFFDC2626))
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    FinanceRow("Estimated Net Profit (নিট লাভ):", Strings.formatPrice(netProfit), RongdhonuAccentGreen, isBold = true)
                }
            }
        }

        // Expense Records & Add Expense
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Business Expenses (${expenses.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Button(
                            onClick = { showExpenseDialog = true },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("Record Expense", fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    expenses.forEach { exp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(exp.title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text(exp.category, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(Strings.formatPrice(exp.amount), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Red)
                                IconButton(onClick = { RongdhonuRepository.deleteExpense(exp.id) }) {
                                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Payment Gateway Settings & Secrets Protection Info
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Payment Gateways & Security", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• bKash Merchant / Personal: $OFFICIAL_WHATSAPP (Active)", fontSize = 11.sp)
                    Text("• Nagad Personal: $OFFICIAL_WHATSAPP (Active)", fontSize = 11.sp)
                    Text("• Cash on Delivery (COD): Enabled across 64 Districts", fontSize = 11.sp)
                    Text("• Secret API Keys are securely managed server-side and never exposed in client bundles.", fontSize = 11.sp, color = RongdhonuAccentGreen)
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showExpenseDialog) {
        ExpenseInputDialog(
            onDismiss = { showExpenseDialog = false },
            onSave = { title, cat, amt ->
                RongdhonuRepository.addExpense(title, cat, amt)
                showExpenseDialog = false
            }
        )
    }
}

@Composable
fun FinanceRow(label: String, value: String, valueColor: Color, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal)
        Text(value, fontSize = 12.sp, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.SemiBold, color = valueColor)
    }
}

// -------------------------------------------------------------
// 7. DIGITAL SERVICE MANAGEMENT TAB
// -------------------------------------------------------------
@Composable
fun AdminDigitalServicesTab(language: Language) {
    val context = LocalContext.current
    val services by RongdhonuRepository.digitalServices.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = if (language == Language.BN) "স্টুডিও ও ডিজিটাল সার্ভিস রিকোয়েস্ট" else "Studio & Digital Service Requests",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        items(services) { svc ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(svc.id, fontWeight = FontWeight.Bold, color = RongdhonuPrimary)
                        Surface(
                            color = when (svc.status) {
                                ServiceStatus.PENDING -> Color(0xFFFEF3C7)
                                ServiceStatus.IN_PROGRESS -> Color(0xFFDBEAFE)
                                ServiceStatus.COMPLETED -> Color(0xFFDCFCE7)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                if (language == Language.BN) svc.status.labelBn else svc.status.labelEn,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(svc.serviceTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Customer: ${svc.customerName} (${svc.customerPhone})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Documents/Specs: ${svc.documentInfo}", fontSize = 11.sp)
                    Text("Quoted Price: ${Strings.formatPrice(svc.quotedPrice)}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RongdhonuPrimary)
                    if (svc.notes.isNotBlank()) {
                        Text("Internal Notes: ${svc.notes}", fontSize = 11.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            ServiceStatus.values().forEach { st ->
                                FilterChip(
                                    selected = svc.status == st,
                                    onClick = { RongdhonuRepository.updateDigitalService(svc.id, st, svc.notes) },
                                    label = { Text(st.name, fontSize = 9.sp) },
                                    modifier = Modifier.height(26.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val msg = "Hello ${svc.customerName},\n" +
                                        "Update on your digital service (${svc.id} - ${svc.serviceTitle}):\n" +
                                        "Status: ${svc.status.name}\n" +
                                        "Price: ৳${svc.quotedPrice}\n" +
                                        "From: Rongdhonu Digital Studio"
                                openWhatsApp(context, msg)
                            }
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Send WhatsApp Update", tint = WhatsAppGreen)
                        }
                    }
                }
            }
        }
        item { Spacer(modifier = Modifier.height(60.dp)) }
    }
}

// -------------------------------------------------------------
// 8. SETTINGS, LOGS & ROLE SECURITY TAB
// -------------------------------------------------------------
@Composable
fun AdminSettingsAndLogsTab(language: Language) {
    val settings by RongdhonuRepository.storeSettings.collectAsStateWithLifecycle()
    val auditLogs by RongdhonuRepository.auditLogs.collectAsStateWithLifecycle()
    val currentRole by RongdhonuRepository.currentAdminRole.collectAsStateWithLifecycle()

    var seoTitle by remember { mutableStateOf(settings.seoTitle) }
    var seoDesc by remember { mutableStateOf(settings.seoDescription) }
    var deliveryDhaka by remember { mutableStateOf(settings.deliveryDhaka.toString()) }
    var deliveryOutside by remember { mutableStateOf(settings.deliveryOutside.toString()) }
    var maintenanceMode by remember { mutableStateOf(settings.maintenanceMode) }

    var facebookUrl by remember { mutableStateOf(settings.facebookUrl) }
    var facebookGroupUrl by remember { mutableStateOf(settings.facebookGroupUrl) }
    var whatsappNumber by remember { mutableStateOf(settings.whatsappNumber) }
    var youtubeUrl by remember { mutableStateOf(settings.youtubeUrl) }
    var instagramUrl by remember { mutableStateOf(settings.instagramUrl) }
    var tiktokUrl by remember { mutableStateOf(settings.tiktokUrl) }
    var telegramUrl by remember { mutableStateOf(settings.telegramUrl) }
    var websiteUrl by remember { mutableStateOf(settings.websiteUrl) }

    var announcementText by remember { mutableStateOf(settings.announcementText) }
    var showAnnouncement by remember { mutableStateOf(settings.showAnnouncement) }
    var customThemeColor by remember { mutableStateOf(settings.customThemeColor) }
    var newCourierName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // App Design & Live Announcement Customization
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = RongdhonuPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == Language.BN) "অ্যাপ ডিজাইন ও অ্যানাউন্সমেন্ট কন্ট্রোল" else "App Design & Announcement Control",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == Language.BN)
                            "পরবর্তী নিজের পছন্দের ডিজাইন, ব্যানার টেক্সট এবং কালার দিয়ে পুরো অ্যাপ কন্ট্রোল করুন।"
                        else
                            "Customize app theme colors, announcements, and storefront experience live from admin panel.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = announcementText,
                        onValueChange = { announcementText = it },
                        label = { Text("Top Announcement / Notice Bar Text", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Show Top Announcement Bar", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("Display announcement marquee on Home Screen", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = showAnnouncement,
                            onCheckedChange = { showAnnouncement = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("App Brand Color Accent:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "#FF6B00" to "Rongdhonu Orange",
                            "#E11D48" to "Ruby Red",
                            "#2563EB" to "Royal Blue",
                            "#059669" to "Emerald",
                            "#7C3AED" to "Violet"
                        ).forEach { (colorHex, colorName) ->
                            FilterChip(
                                selected = customThemeColor == colorHex,
                                onClick = { customThemeColor = colorHex },
                                label = { Text(colorName, fontSize = 10.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            RongdhonuRepository.updateStoreDesign(
                                customThemeColor = customThemeColor,
                                announcementText = announcementText,
                                showAnnouncement = showAnnouncement
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (language == Language.BN) "ডিজাইন ও অ্যানাউন্সমেন্ট সেভ করুন" else "Save Design & Notice Settings")
                    }
                }
            }
        }

        // Courier Services Management (Add any courier service)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = RongdhonuSecondary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == Language.BN) "কোরিয়ার সার্ভিস ম্যানেজমেন্ট (Courier Management)" else "Courier Services Partner Management",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == Language.BN)
                            "অন্য যেকোনো কুরিয়ার সার্ভিস (যেমন: সুন্দরবন, করতোয়া, SA পরিবহন, পাঠাও, ইত্যাদি) এখানে যুক্ত করুন।"
                        else
                            "Add and manage courier services for order fulfillment and tracking.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Active Couriers (${settings.activeCouriers.size}):", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Chip list of all couriers with delete option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(settings.activeCouriers) { courier ->
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(courier, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.Red,
                                            modifier = Modifier
                                                .size(14.dp)
                                                .clickable { RongdhonuRepository.removeCourierPartner(courier) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Add custom courier input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCourierName,
                            onValueChange = { newCourierName = it },
                            placeholder = { Text("নতুন কুরিয়ারের নাম (e.g. Sundarban Courier)...", fontSize = 11.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = {
                                if (newCourierName.isNotBlank()) {
                                    RongdhonuRepository.addCourierPartner(newCourierName)
                                    newCourierName = ""
                                }
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (language == Language.BN) "যোগ করুন" else "Add")
                        }
                    }
                }
            }
        }

        // Admin Roles & Permissions
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Administrative Role & Access Level", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Select role to test access boundaries:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))

                    AdminRole.values().forEach { role ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { RongdhonuRepository.setAdminRole(role) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentRole == role,
                                onClick = { RongdhonuRepository.setAdminRole(role) }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(role.label, fontSize = 12.sp, fontWeight = if (currentRole == role) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }

        // Company Official Social Media & Contact Links Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == Language.BN) "কোম্পানির সোশ্যাল মিডিয়া লিংক সেটিংস" else "Official Social Media & Channel Links",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == Language.BN)
                            "এখান থেকে যেকোনো লিংক পরিবর্তন করলে অ্যাপের হোম ও সোশ্যাল মিডিয়া পেজে তাৎক্ষণিক আপডেট হবে।"
                        else
                            "Edit URLs here to instantly update Home and Social Media screens across the app.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = facebookUrl,
                        onValueChange = { facebookUrl = it },
                        label = { Text("Official Facebook Page URL", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = facebookGroupUrl,
                        onValueChange = { facebookGroupUrl = it },
                        label = { Text("Facebook Customer Group URL", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = whatsappNumber,
                        onValueChange = { whatsappNumber = it },
                        label = { Text("WhatsApp Helpline Number (e.g. 01632042441)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = youtubeUrl,
                        onValueChange = { youtubeUrl = it },
                        label = { Text("YouTube Official Channel URL", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = instagramUrl,
                        onValueChange = { instagramUrl = it },
                        label = { Text("Instagram Profile URL", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = tiktokUrl,
                        onValueChange = { tiktokUrl = it },
                        label = { Text("TikTok Channel URL", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = telegramUrl,
                        onValueChange = { telegramUrl = it },
                        label = { Text("Telegram Channel URL", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = websiteUrl,
                        onValueChange = { websiteUrl = it },
                        label = { Text("Official E-commerce Website URL", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            RongdhonuRepository.updateStoreSettings(
                                settings.copy(
                                    facebookUrl = facebookUrl,
                                    facebookGroupUrl = facebookGroupUrl,
                                    whatsappNumber = whatsappNumber,
                                    youtubeUrl = youtubeUrl,
                                    instagramUrl = instagramUrl,
                                    tiktokUrl = tiktokUrl,
                                    telegramUrl = telegramUrl,
                                    websiteUrl = websiteUrl
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (language == Language.BN) "সোশ্যাল মিডিয়া লিংকসমূহ সেভ করুন" else "Save Social Media Links")
                    }
                }
            }
        }

        // Store & SEO Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Store & SEO Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = seoTitle,
                        onValueChange = { seoTitle = it },
                        label = { Text("SEO Meta Title", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = seoDesc,
                        onValueChange = { seoDesc = it },
                        label = { Text("SEO Meta Description", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = deliveryDhaka,
                            onValueChange = { deliveryDhaka = it },
                            label = { Text("Dhaka ৳", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = deliveryOutside,
                            onValueChange = { deliveryOutside = it },
                            label = { Text("Outside ৳", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Maintenance Mode", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("Pause storefront for maintenance", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = maintenanceMode,
                            onCheckedChange = { maintenanceMode = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            RongdhonuRepository.updateStoreSettings(
                                settings.copy(
                                    seoTitle = seoTitle,
                                    seoDescription = seoDesc,
                                    deliveryDhaka = deliveryDhaka.toDoubleOrNull() ?: 60.0,
                                    deliveryOutside = deliveryOutside.toDoubleOrNull() ?: 120.0,
                                    maintenanceMode = maintenanceMode
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save System Settings")
                    }
                }
            }
        }

        // Audit Logs (Who did what)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Security Audit Logs (${auditLogs.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    auditLogs.take(8).forEach { log ->
                        val date = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(log.timestamp))
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("• ${log.action}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("By ${log.adminName} (${log.role.name}) • $date", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

// -------------------------------------------------------------
// DIALOGS & FORMS
// -------------------------------------------------------------

@Composable
fun AdvancedProductFormDialog(
    categories: List<Category>,
    existingProduct: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit
) {
    var sku by remember { mutableStateOf(existingProduct?.sku ?: "RD-${(100..999).random()}") }
    var nameEn by remember { mutableStateOf(existingProduct?.nameEn ?: "") }
    var nameBn by remember { mutableStateOf(existingProduct?.nameBn ?: "") }
    var brand by remember { mutableStateOf(existingProduct?.brand ?: "Rongdhonu Studio") }
    var regularPrice by remember { mutableStateOf(existingProduct?.regularPrice?.toString() ?: "500") }
    var discountPrice by remember { mutableStateOf(existingProduct?.discountPrice?.toString() ?: "450") }
    var costPrice by remember { mutableStateOf(existingProduct?.costPrice?.toString() ?: "320") }
    var stock by remember { mutableStateOf(existingProduct?.stock?.toString() ?: "20") }
    var warranty by remember { mutableStateOf(existingProduct?.warranty ?: "7 Days Replacement") }
    var videoUrl by remember { mutableStateOf(existingProduct?.videoUrl ?: "") }
    var selectedCategory by remember { mutableStateOf(existingProduct?.categoryId ?: categories.first().id) }
    var publicationStatus by remember { mutableStateOf(existingProduct?.publicationStatus ?: ProductPublicationStatus.PUBLISHED) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (existingProduct != null) "Edit Product" else "Add New Product", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("SKU / Barcode Code") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = nameEn, onValueChange = { nameEn = it }, label = { Text("Name (English)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = nameBn, onValueChange = { nameBn = it }, label = { Text("Name (বাংলা)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = brand, onValueChange = { brand = it }, label = { Text("Brand / Supplier") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = regularPrice, onValueChange = { regularPrice = it }, label = { Text("Sale ৳") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = discountPrice, onValueChange = { discountPrice = it }, label = { Text("Discount ৳") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = costPrice, onValueChange = { costPrice = it }, label = { Text("Cost ৳") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stock Qty") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = warranty, onValueChange = { warranty = it }, label = { Text("Warranty") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = videoUrl, onValueChange = { videoUrl = it }, label = { Text("Video Link (Optional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = (existingProduct ?: Product(
                        id = "prod_${System.currentTimeMillis()}",
                        sku = sku,
                        nameEn = nameEn.ifBlank { "New Product" },
                        nameBn = nameBn.ifBlank { "নতুন পণ্য" },
                        categoryId = selectedCategory,
                        descriptionEn = "Product description from Rongdhonu Shop",
                        descriptionBn = "রংধনু শপের পণ্যের বিবরণ",
                        regularPrice = regularPrice.toDoubleOrNull() ?: 500.0,
                        discountPrice = discountPrice.toDoubleOrNull() ?: 450.0,
                        costPrice = costPrice.toDoubleOrNull() ?: 320.0,
                        stock = stock.toIntOrNull() ?: 10,
                        imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800&q=80"
                    )).copy(
                        sku = sku,
                        nameEn = nameEn,
                        nameBn = nameBn,
                        brand = brand,
                        regularPrice = regularPrice.toDoubleOrNull() ?: 500.0,
                        discountPrice = discountPrice.toDoubleOrNull() ?: 450.0,
                        costPrice = costPrice.toDoubleOrNull() ?: 320.0,
                        stock = stock.toIntOrNull() ?: 10,
                        warranty = warranty,
                        videoUrl = videoUrl,
                        publicationStatus = publicationStatus
                    )
                    onSave(p)
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdvancedOrderManagementDialog(
    order: Order,
    language: Language,
    onDismiss: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit,
    onUpdatePayment: (PaymentStatus, String?) -> Unit,
    onAssignCourier: (String, String, Double) -> Unit,
    onToggleCod: () -> Unit,
    onProcessRefund: (Double, String, Boolean) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(order.orderStatus) }
    var selectedPayment by remember { mutableStateOf(order.paymentStatus) }
    var trxInput by remember { mutableStateOf(order.transactionId ?: "") }
    var courierInput by remember { mutableStateOf(order.courierName ?: "Steadfast Courier") }
    var trackingInput by remember { mutableStateOf(order.courierTrackingNumber ?: "") }
    var deliveryChargeInput by remember { mutableStateOf(order.deliveryCharge.toString()) }

    var refundInput by remember { mutableStateOf("") }
    var refundReason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Manage Order: ${order.id}", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Order Status:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(OrderStatus.values()) { st ->
                        FilterChip(
                            selected = selectedStatus == st,
                            onClick = { selectedStatus = st },
                            label = { Text(st.name, fontSize = 10.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Payment Status:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(PaymentStatus.PAID, PaymentStatus.PENDING_VERIFICATION, PaymentStatus.UNPAID).forEach { ps ->
                        FilterChip(
                            selected = selectedPayment == ps,
                            onClick = { selectedPayment = ps },
                            label = { Text(ps.name, fontSize = 9.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = trxInput,
                    onValueChange = { trxInput = it },
                    label = { Text("Payment TrxID", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                val activeCouriers = RongdhonuRepository.storeSettings.collectAsStateWithLifecycle().value.activeCouriers
                Text("Courier Partner:", fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(activeCouriers) { cName ->
                        FilterChip(
                            selected = courierInput == cName,
                            onClick = { courierInput = cName },
                            label = { Text(cName, fontSize = 9.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = courierInput,
                        onValueChange = { courierInput = it },
                        label = { Text("Courier", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = trackingInput,
                        onValueChange = { trackingInput = it },
                        label = { Text("Tracking #", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }


                Spacer(modifier = Modifier.height(8.dp))

                // COD Phone Confirmation Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("COD Phone Confirmed", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = order.isCodConfirmed,
                        onCheckedChange = { onToggleCod() },
                        modifier = Modifier.scale(0.7f)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Refund or Return Section
                Text("Refund / Return / Exchange:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = refundInput,
                        onValueChange = { refundInput = it },
                        label = { Text("Amount ৳", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = refundReason,
                        onValueChange = { refundReason = it },
                        label = { Text("Reason", fontSize = 10.sp) },
                        modifier = Modifier.weight(1.5f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = {
                            val amt = refundInput.toDoubleOrNull() ?: 0.0
                            if (amt > 0) onProcessRefund(amt, refundReason, false)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Process Refund", fontSize = 10.sp)
                    }
                    OutlinedButton(
                        onClick = {
                            val amt = refundInput.toDoubleOrNull() ?: 0.0
                            onProcessRefund(amt, refundReason, true)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Exchange/Return", fontSize = 10.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onUpdateStatus(selectedStatus)
                    onUpdatePayment(selectedPayment, trxInput.ifBlank { null })
                    val charge = deliveryChargeInput.toDoubleOrNull() ?: order.deliveryCharge
                    if (courierInput.isNotBlank()) {
                        onAssignCourier(courierInput, trackingInput, charge)
                    }
                }
            ) { Text("Save Changes") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun InvoicePrintDialog(
    order: Order,
    language: Language,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val date = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.createdAt))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Order Invoice", fontWeight = FontWeight.Bold)
                Text(order.id, color = RongdhonuPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Rongdhonu Digital Studio", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Hotline: $OFFICIAL_WHATSAPP | Email: rongdhonustudio00@gmail.com", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Date: $date", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Customer: ${order.customerName}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("Phone: ${order.customerPhone}", fontSize = 11.sp)
                Text("Delivery: ${order.deliveryAddress}, ${order.district}", fontSize = 11.sp)
                Text("Payment Method: ${order.paymentMethod.name} (${order.paymentStatus.name})", fontSize = 11.sp)

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Text("Ordered Items:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.product.nameEn} (x${item.quantity})", fontSize = 11.sp, modifier = Modifier.weight(1f))
                        Text(Strings.formatPrice(item.totalPrice), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal:", fontSize = 11.sp)
                    Text(Strings.formatPrice(order.subtotal), fontSize = 11.sp)
                }
                if (order.discount > 0) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Discount:", fontSize = 11.sp, color = RongdhonuAccentGreen)
                        Text("- ${Strings.formatPrice(order.discount)}", fontSize = 11.sp, color = RongdhonuAccentGreen)
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Delivery Charge:", fontSize = 11.sp)
                    Text(Strings.formatPrice(order.deliveryCharge), fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Grand Total:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(Strings.formatPrice(order.grandTotal), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = RongdhonuPrimary)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val invoiceText = "=== RONGDHONU SHOP INVOICE ===\n" +
                            "Order ID: ${order.id}\n" +
                            "Date: $date\n" +
                            "Customer: ${order.customerName} (${order.customerPhone})\n" +
                            "Address: ${order.deliveryAddress}, ${order.district}\n" +
                            "Items:\n" + order.items.joinToString("\n") { "• ${it.product.nameEn} x${it.quantity} = ৳${it.totalPrice}" } +
                            "\nSubtotal: ৳${order.subtotal}" +
                            "\nDelivery: ৳${order.deliveryCharge}" +
                            "\nGrand Total: ৳${order.grandTotal}\n" +
                            "Status: ${order.orderStatus.name}\n" +
                            "Hotline: $OFFICIAL_WHATSAPP\nRongdhonu Digital Studio"
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, invoiceText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, "Share Invoice"))
                }
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Share / Print")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun CsvImportDialog(
    onDismiss: () -> Unit,
    onImport: (String) -> Unit
) {
    var csvText by remember {
        mutableStateOf(
            "SKU,Name_EN,Name_BN,Category,RegularPrice,DiscountPrice,CostPrice,Stock,Brand\n" +
            "RD-IMP-901,\"Wireless Gaming RGB Mouse\",\"ওয়্যারলেস গেমিং আরজিবি মাউস\",cat_electronics,950,750,500,25,\"Rongdhonu\"\n" +
            "RD-IMP-902,\"Customized Acrylic Nameplate\",\"কাস্টম এক্রিলিক নেইমপ্লেট\",cat_gifts,650,550,350,15,\"Studio Custom\""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import Products CSV", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Paste CSV data with header columns (SKU, Name_EN, Name_BN, Category, RegularPrice, DiscountPrice, CostPrice, Stock, Brand):", fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = csvText,
                    onValueChange = { csvText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 10.sp)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onImport(csvText) }) { Text("Process Import") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ExpenseInputDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Packaging") }
    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Expense Title / Note") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (Packaging, Courier, Materials, Utility)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Amount ৳") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        onSave(title, category, amt)
                    }
                }
            ) { Text("Save Expense") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminNotificationsDialog(
    notifications: List<AdminNotification>,
    onDismiss: () -> Unit,
    onMarkRead: (String) -> Unit,
    onClearAll: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Notifications", fontWeight = FontWeight.Bold)
                if (notifications.isNotEmpty()) {
                    TextButton(onClick = onClearAll) { Text("Clear All", fontSize = 11.sp) }
                }
            }
        },
        text = {
            if (notifications.isEmpty()) {
                Text("No new notifications.", fontSize = 12.sp)
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications) { notif ->
                        Surface(
                            color = if (notif.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMarkRead(notif.id) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(notif.message, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}
