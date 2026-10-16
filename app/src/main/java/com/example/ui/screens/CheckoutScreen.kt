package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.model.*
import com.example.ui.components.OFFICIAL_WHATSAPP
import com.example.ui.components.openWhatsApp
import com.example.ui.localization.Strings
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    language: Language,
    cartItems: List<CartItem>,
    appliedCoupon: Coupon?,
    insideDhaka: Boolean,
    onBack: () -> Unit,
    onPlaceOrder: (
        customerName: String,
        phone: String,
        email: String,
        address: String,
        district: String,
        thana: String,
        deliveryMethod: String,
        orderNote: String,
        paymentMethod: PaymentMethod,
        transactionId: String?
    ) -> Order,
    onOrderPlaced: (Order) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var district by remember { mutableStateOf(if (insideDhaka) "Dhaka" else "Chittagong") }
    var thana by remember { mutableStateOf("") }
    var orderNote by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.COD) }
    var transactionId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val discount = if (appliedCoupon != null) (subtotal * appliedCoupon.percentOff / 100.0) else 0.0
    val deliveryCharge = if (insideDhaka) 60.0 else 120.0
    val grandTotal = (subtotal - discount + deliveryCharge).coerceAtLeast(0.0)

    val bangladeshDistricts = listOf(
        "Dhaka", "Gazipur", "Narayanganj", "Chittagong", "Cox's Bazar",
        "Sylhet", "Rajshahi", "Khulna", "Barishal", "Rangpur",
        "Mymensingh", "Comilla", "Bogra", "Jessore", "Dinajpur"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (language == Language.BN) "অর্ডার চেকআউট" else "Checkout Order",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Strings.grandTotal(language),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Strings.formatPrice(grandTotal),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Button(
                            onClick = {
                                if (name.isBlank()) {
                                    errorMessage = if (language == Language.BN) "অনুগ্রহ করে আপনার নাম লিখুন" else "Please enter your name"
                                    return@Button
                                }
                                if (phone.isBlank() || phone.length < 11) {
                                    errorMessage = if (language == Language.BN) "সঠিক মোবাইল নম্বর দিন (যেমন: 017XXXXXXXX)" else "Enter a valid 11-digit mobile number"
                                    return@Button
                                }
                                if (address.isBlank()) {
                                    errorMessage = if (language == Language.BN) "সম্পূর্ণ ডেলিভারি ঠিকানা দিন" else "Please enter your full delivery address"
                                    return@Button
                                }
                                if ((selectedPaymentMethod == PaymentMethod.BKASH || selectedPaymentMethod == PaymentMethod.NAGAD) && transactionId.isBlank()) {
                                    errorMessage = if (language == Language.BN) "পেমেন্টের পর ট্রানজেকশন আইডি (TrxID) দিন" else "Please enter your payment Transaction ID"
                                    return@Button
                                }

                                val order = onPlaceOrder(
                                    name,
                                    phone,
                                    email,
                                    address,
                                    district,
                                    thana.ifBlank { "Main Area" },
                                    if (insideDhaka) "Standard (Inside Dhaka)" else "Standard (Outside Dhaka)",
                                    orderNote,
                                    selectedPaymentMethod,
                                    transactionId.ifBlank { null }
                                )

                                if (selectedPaymentMethod == PaymentMethod.WHATSAPP) {
                                    val msg = "🛒 New Order on Rongdhonu Shop!\n" +
                                            "Order ID: ${order.id}\n" +
                                            "Customer: ${order.customerName} (${order.customerPhone})\n" +
                                            "Address: ${order.deliveryAddress}, ${order.district}\n" +
                                            "Total: ৳${order.grandTotal}\n" +
                                            "Items: ${order.items.joinToString { "${it.product.nameEn} x${it.quantity}" }}"
                                    openWhatsApp(context, msg)
                                }

                                onOrderPlaced(order)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RongdhonuSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(
                                text = if (language == Language.BN) "অর্ডার নিশ্চিত করুন" else "Confirm Order",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            errorMessage?.let { err ->
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "⚠️ $err",
                        color = Color(0xFF991B1B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Customer Contact Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.BN) "১. গ্রাহকের তথ্য" else "1. Customer Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = null },
                        label = { Text(if (language == Language.BN) "আপনার নাম *" else "Full Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it; errorMessage = null },
                        label = { Text(if (language == Language.BN) "মোবাইল নম্বর (যেমন: 017XXXXXXXX) *" else "Phone Number *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(if (language == Language.BN) "ইমেইল (ঐচ্ছিক)" else "Email Address (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Delivery Address
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.BN) "২. ডেলিভারি ঠিকানা" else "2. Delivery Address",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it; errorMessage = null },
                        label = { Text(if (language == Language.BN) "বাড়ি নম্বর, রোড, এলাকা *" else "Street Address, House/Flat No *") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // District Selector Chips
                    Text(
                        text = if (language == Language.BN) "জেলা নির্বাচন করুন:" else "Select District:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Dhaka", "Chittagong", "Sylhet", "Rajshahi").forEach { dist ->
                            FilterChip(
                                selected = district == dist,
                                onClick = { district = dist },
                                label = { Text(dist, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = thana,
                        onValueChange = { thana = it },
                        label = { Text(if (language == Language.BN) "থানা / উপজেলা" else "Thana / Upazila") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = orderNote,
                        onValueChange = { orderNote = it },
                        label = { Text(if (language == Language.BN) "অর্ডার নোট (যেমন: উপহারের মোড়ক)" else "Order Notes / Special Instructions") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Payment Methods
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.BN) "৩. মূল্য পরিশোধের মাধ্যম" else "3. Payment Method",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // COD option
                    PaymentOptionRow(
                        title = if (language == Language.BN) "ক্যাশ অন ডেলিভারি (পণ্য হাতে পেয়ে টাকা দিন)" else "Cash on Delivery (COD)",
                        subtitle = if (language == Language.BN) "ডেলিভারি ম্যানের হাতে পণ্য দেখে মূল্য পরিশোধ করুন" else "Pay in cash upon delivery",
                        selected = selectedPaymentMethod == PaymentMethod.COD,
                        onClick = { selectedPaymentMethod = PaymentMethod.COD }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // bKash option
                    PaymentOptionRow(
                        title = "bKash (বিকাশ সেন্ড মানি / পেমেন্ট)",
                        subtitle = "নম্বর: $OFFICIAL_WHATSAPP (Personal/Merchant)",
                        selected = selectedPaymentMethod == PaymentMethod.BKASH,
                        badgeColor = BkashPink,
                        onClick = { selectedPaymentMethod = PaymentMethod.BKASH }
                    )

                    // If bKash selected, show TrxID instructions
                    if (selectedPaymentMethod == PaymentMethod.BKASH) {
                        Surface(
                            color = Color(0xFFFDF2F8),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (language == Language.BN)
                                        "📌 বিকাশ নিয়ম: বিকাশ অ্যাপে গিয়ে $OFFICIAL_WHATSAPP নম্বরে ৳${grandTotal.toInt()} সেন্ড মানি করুন। এরপর নিচে প্রাপ্ত Transaction ID (TrxID) টি লিখুন।"
                                    else
                                        "📌 bKash Steps: Send ৳${grandTotal.toInt()} to $OFFICIAL_WHATSAPP. Then enter your Transaction ID below.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF831843)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = transactionId,
                                    onValueChange = { transactionId = it; errorMessage = null },
                                    label = { Text("bKash Transaction ID (যেমন: BKH9827361)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nagad option
                    PaymentOptionRow(
                        title = "Nagad (নগদ পেমেন্ট)",
                        subtitle = "নম্বর: $OFFICIAL_WHATSAPP",
                        selected = selectedPaymentMethod == PaymentMethod.NAGAD,
                        badgeColor = NagadOrange,
                        onClick = { selectedPaymentMethod = PaymentMethod.NAGAD }
                    )

                    if (selectedPaymentMethod == PaymentMethod.NAGAD) {
                        Surface(
                            color = Color(0xFFFFF7ED),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "📌 নগদ নিয়ম: $OFFICIAL_WHATSAPP নম্বরে ৳${grandTotal.toInt()} পাঠিয়ে Transaction ID টি লিখুন।",
                                    fontSize = 11.sp,
                                    color = Color(0xFF9A3412)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = transactionId,
                                    onValueChange = { transactionId = it; errorMessage = null },
                                    label = { Text("Nagad Transaction ID (TrxID)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // WhatsApp Direct Order
                    PaymentOptionRow(
                        title = if (language == Language.BN) "হোয়াটসঅ্যাপের মাধ্যমে সরাসরি অর্ডার" else "Direct WhatsApp Order",
                        subtitle = "সরাসরি প্রতিনিধির সাথে কথা বলে অর্ডার সম্পন্ন করুন",
                        selected = selectedPaymentMethod == PaymentMethod.WHATSAPP,
                        badgeColor = WhatsAppGreen,
                        onClick = { selectedPaymentMethod = PaymentMethod.WHATSAPP }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Order Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.BN) "মূল্য হিসাব" else "Order Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(Strings.subtotal(language), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(Strings.formatPrice(subtotal))
                    }
                    if (discount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(Strings.discount(language), color = RongdhonuAccentGreen)
                            Text("- ${Strings.formatPrice(discount)}", color = RongdhonuAccentGreen)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(Strings.deliveryCharge(language), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(Strings.formatPrice(deliveryCharge))
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(Strings.grandTotal(language), fontWeight = FontWeight.Bold)
                        Text(Strings.formatPrice(grandTotal), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    badgeColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = badgeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
