package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.model.Order
import com.example.model.OrderStatus
import com.example.ui.components.OFFICIAL_WHATSAPP
import com.example.ui.components.openWhatsApp
import com.example.ui.localization.Strings
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun OrdersScreen(
    language: Language,
    orders: List<Order>,
    onCancelOrder: (String) -> Boolean,
    onStartShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var trackSearchQuery by remember { mutableStateOf("") }

    val displayedOrders = remember(orders, trackSearchQuery) {
        if (trackSearchQuery.isBlank()) orders else {
            val q = trackSearchQuery.trim().lowercase()
            orders.filter {
                it.id.lowercase().contains(q) || it.customerPhone.contains(q)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Track Search Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = Strings.trackOrder(language),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = trackSearchQuery,
                    onValueChange = { trackSearchQuery = it },
                    placeholder = {
                        Text(
                            text = if (language == Language.BN) "অর্ডার নম্বর (যেমন: RS-98210) বা ফোন নম্বর দিন" else "Enter Order ID (e.g. RS-98210) or phone",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    trailingIcon = {
                        if (trackSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { trackSearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (displayedOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = "No Orders",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (language == Language.BN) "কোনো অর্ডার পাওয়া যায়নি" else "No orders found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (language == Language.BN)
                            "সঠিক অর্ডার আইডি অনুসন্ধান করুন অথবা কেনাকাটা শুরু করুন।"
                        else
                            "Check your Order ID or start shopping our latest collection.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(onClick = onStartShopping, shape = RoundedCornerShape(10.dp)) {
                        Text(Strings.startShopping(language))
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayedOrders) { order ->
                    OrderCardItem(
                        order = order,
                        language = language,
                        onCancel = { onCancelOrder(order.id) },
                        onContactSupport = {
                            val msg = if (language == Language.BN) {
                                "আসসালামু আলাইকুম, আমি আমার অর্ডার (${order.id}) সম্পর্কে জানতে চাচ্ছি।"
                            } else {
                                "Hello Rongdhonu Shop, I want an update regarding my order ${order.id}."
                            }
                            openWhatsApp(context, msg)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun OrderCardItem(
    order: Order,
    language: Language,
    onCancel: () -> Unit,
    onContactSupport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateString = remember(order.createdAt) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(order.createdAt))
    }

    val (statusColor, statusBg) = when (order.orderStatus) {
        OrderStatus.PENDING -> Color(0xFFD97706) to Color(0xFFFEF3C7)
        OrderStatus.CONFIRMED -> Color(0xFF4F46E5) to Color(0xFFEEF2FF)
        OrderStatus.PROCESSING -> Color(0xFF2563EB) to Color(0xFFDBEAFE)
        OrderStatus.SHIPPED -> Color(0xFF0D9488) to Color(0xFFCCFBF1)
        OrderStatus.DELIVERED -> Color(0xFF16A34A) to Color(0xFFDCFCE7)
        OrderStatus.CANCELLED -> Color(0xFFDC2626) to Color(0xFFFEE2E2)
        OrderStatus.RETURNED -> Color(0xFF7C3AED) to Color(0xFFEDE9FE)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Order ID & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.id,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (language == Language.BN) order.orderStatus.labelBn else order.orderStatus.labelEn,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Order Progress Stepper Bar
            OrderProgressStepper(status = order.orderStatus, language = language)

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Items List
            Text(
                text = if (language == Language.BN) "অর্ডারের পণ্যসমূহ:" else "Ordered Items:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "• ${if (language == Language.BN) item.product.nameBn else item.product.nameEn} (x${item.quantity})",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = Strings.formatPrice(item.totalPrice),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Courier / Tracking Info if assigned
            order.courierName?.let { courier ->
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Courier: $courier (${order.courierTrackingNumber ?: "Tracking active"})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            // Customer details and payment
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Customer: ${order.customerName}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Payment: ${order.paymentMethod.name} (${order.paymentStatus.name})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = Strings.grandTotal(language),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = Strings.formatPrice(order.grandTotal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions: WhatsApp Inquiry & Cancel button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onContactSupport,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp Support",
                        tint = WhatsAppGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.BN) "হোয়াটসঅ্যাপ সাপোর্ট" else "WhatsApp Support",
                        fontSize = 12.sp
                    )
                }

                if (order.orderStatus == OrderStatus.PENDING) {
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text(
                            text = if (language == Language.BN) "বাতিল করুন" else "Cancel",
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderProgressStepper(
    status: OrderStatus,
    language: Language,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        OrderStatus.PENDING to (if (language == Language.BN) "অর্ডার হয়েছে" else "Placed"),
        OrderStatus.CONFIRMED to (if (language == Language.BN) "নিশ্চিত" else "Confirmed"),
        OrderStatus.SHIPPED to (if (language == Language.BN) "শিপিংয়ে" else "Shipped"),
        OrderStatus.DELIVERED to (if (language == Language.BN) "সম্পন্ন" else "Delivered")
    )

    val currentStepIndex = when (status) {
        OrderStatus.PENDING -> 0
        OrderStatus.CONFIRMED -> 1
        OrderStatus.PROCESSING -> 1
        OrderStatus.SHIPPED -> 2
        OrderStatus.DELIVERED -> 3
        OrderStatus.CANCELLED, OrderStatus.RETURNED -> -1
    }

    if (status == OrderStatus.CANCELLED || status == OrderStatus.RETURNED) {
        Surface(
            color = Color(0xFFFEE2E2),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (status == OrderStatus.RETURNED) {
                    if (language == Language.BN) "এই অর্ডারটি রিটার্ন/এক্সচেঞ্জ করা হয়েছে।" else "This order was returned/exchanged."
                } else {
                    if (language == Language.BN) "এই অর্ডারটি বাতিল করা হয়েছে।" else "This order was cancelled."
                },
                color = Color(0xFF991B1B),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(8.dp)
            )
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, pair ->
                val isCompleted = index <= currentStepIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isCompleted) RongdhonuAccentGreen else Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = pair.second,
                        fontSize = 9.sp,
                        color = if (isCompleted) RongdhonuAccentGreen else Color.Gray,
                        fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
                    )
                }

                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .weight(0.6f)
                            .background(if (index < currentStepIndex) RongdhonuAccentGreen else Color.LightGray)
                    )
                }
            }
        }
    }
}
