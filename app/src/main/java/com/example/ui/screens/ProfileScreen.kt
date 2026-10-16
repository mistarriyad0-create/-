package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Language
import com.example.ui.components.*
import com.example.ui.localization.Strings
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    language: Language,
    wishlistCount: Int,
    isAdminLoggedIn: Boolean,
    onToggleLanguage: () -> Unit,
    onOpenWishlist: () -> Unit,
    onOpenAdminPanel: () -> Unit,
    onOpenSocialMedia: () -> Unit,
    onOpenOrders: () -> Unit,
    onAdminLogin: (String) -> Boolean,
    onAdminLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var adminPasswordInput by remember { mutableStateOf("") }
    var adminPasswordError by remember { mutableStateOf<String?>(null) }

    var showPolicyDialog by remember { mutableStateOf<String?>(null) } // "about", "shipping", "refund", "terms"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Customer Profile Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (language == Language.BN) "সম্মানিত গ্রাহক" else "Valued Customer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Rongdhonu Shop Member",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Business Information Card (Rongdhonu Digital Studio)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == Language.BN) "প্রতিষ্ঠান ও যোগাযোগ" else "Studio & Contact Information",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ContactRowItem(
                        icon = Icons.Default.Store,
                        title = "Rongdhonu Digital Studio",
                        subtitle = "Official E-commerce Platform (BD)",
                        onClick = {}
                    )

                    ContactRowItem(
                        icon = Icons.Default.Phone,
                        title = OFFICIAL_WHATSAPP,
                        subtitle = "WhatsApp & Hotline (Call or Chat)",
                        iconTint = WhatsAppGreen,
                        onClick = { openWhatsApp(context, "Hello Rongdhonu Shop!") }
                    )

                    ContactRowItem(
                        icon = Icons.Default.Email,
                        title = OFFICIAL_EMAIL,
                        subtitle = "Customer Support & Inquiries",
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { openEmail(context) }
                    )

                    ContactRowItem(
                        icon = Icons.Default.Public,
                        title = if (language == Language.BN) "কোম্পানির সোশ্যাল মিডিয়া" else "Company Social Media",
                        subtitle = "Facebook, YouTube, TikTok, WhatsApp & Maps",
                        iconTint = RongdhonuPrimary,
                        onClick = onOpenSocialMedia
                    )

                    ContactRowItem(
                        icon = Icons.Default.LocationOn,
                        title = "Bangladesh",
                        subtitle = "Currency: Bangladeshi Taka (৳ BDT) | Asia/Dhaka",
                        onClick = onOpenSocialMedia
                    )
                }
            }
        }

        // Shopping & Channel Shortcuts (Social Media, Orders, Wishlist & Language)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.Public,
                        title = if (language == Language.BN) "কোম্পানির সোশ্যাল মিডিয়া চ্যানেলসমূহ" else "Company Official Social Media",
                        badge = if (language == Language.BN) "ক্লিক করে দেখুন" else "Click to View",
                        onClick = onOpenSocialMedia
                    )

                    ProfileMenuRow(
                        icon = Icons.Default.LocalShipping,
                        title = if (language == Language.BN) "আমার অর্ডারসমূহ (My Orders)" else "My Orders & Tracking",
                        onClick = onOpenOrders
                    )

                    ProfileMenuRow(
                        icon = Icons.Default.Favorite,
                        title = if (language == Language.BN) "আমার পছন্দের তালিকা (উইশলিস্ট)" else "My Wishlist",
                        badge = if (wishlistCount > 0) "$wishlistCount items" else null,
                        onClick = onOpenWishlist
                    )

                    ProfileMenuRow(
                        icon = Icons.Default.Translate,
                        title = if (language == Language.BN) "ভাষা পরিবর্তন (Language)" else "Change Language",
                        badge = if (language == Language.BN) "বাংলা" else "English",
                        onClick = onToggleLanguage
                    )
                }
            }
        }

        // Policies & Legal Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.Info,
                        title = if (language == Language.BN) "আমাদের সম্পর্কে (About Us)" else "About Us",
                        onClick = { showPolicyDialog = "about" }
                    )

                    ProfileMenuRow(
                        icon = Icons.Default.LocalShipping,
                        title = if (language == Language.BN) "শিপিং ও ডেলিভারি পলিসি" else "Shipping & Delivery Policy",
                        onClick = { showPolicyDialog = "shipping" }
                    )

                    ProfileMenuRow(
                        icon = Icons.Default.AssignmentReturn,
                        title = if (language == Language.BN) "রিটার্ন ও রিফান্ড পলিসি" else "Return & Refund Policy",
                        onClick = { showPolicyDialog = "refund" }
                    )

                    ProfileMenuRow(
                        icon = Icons.Default.Security,
                        title = if (language == Language.BN) "শর্তাবলী ও গোপনীয়তা নীতি" else "Terms & Privacy Policy",
                        onClick = { showPolicyDialog = "terms" }
                    )
                }
            }
        }

        // Admin Management Access
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAdminLoggedIn) Color(0xFFEFF6FF) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = Strings.adminPanel(language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAdminLoggedIn) "Logged in as Admin" else "Store Owner & Manager Login",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (isAdminLoggedIn) {
                            Row {
                                Button(
                                    onClick = onOpenAdminPanel,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Open Dashboard")
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                TextButton(onClick = onAdminLogout) {
                                    Text("Logout", color = Color.Red)
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    adminPasswordInput = ""
                                    adminPasswordError = null
                                    showAdminLoginDialog = true
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(if (language == Language.BN) "লগইন" else "Admin Login")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Admin Login Dialog
    if (showAdminLoginDialog) {
        AlertDialog(
            onDismissRequest = { showAdminLoginDialog = false },
            title = {
                Text(
                    text = if (language == Language.BN) "অ্যাডমিন প্যানেল লগইন" else "Admin Panel Login",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (language == Language.BN)
                            "অ্যাডমিন পাসওয়ার্ড লিখুন (ডিফল্ট: rongdhonu123)"
                        else
                            "Enter the admin passcode (default: rongdhonu123):",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = adminPasswordInput,
                        onValueChange = {
                            adminPasswordInput = it
                            adminPasswordError = null
                        },
                        label = { Text("Passcode") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    adminPasswordError?.let { err ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(err, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val ok = onAdminLogin(adminPasswordInput)
                        if (ok) {
                            showAdminLoginDialog = false
                            onOpenAdminPanel()
                        } else {
                            adminPasswordError = "Incorrect passcode. Use 'rongdhonu123'"
                        }
                    }
                ) {
                    Text(if (language == Language.BN) "প্রবেশ করুন" else "Unlock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminLoginDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Policy Dialog
    showPolicyDialog?.let { type ->
        val (title, body) = when (type) {
            "about" -> (
                if (language == Language.BN) "আমাদের সম্পর্কে" else "About Us"
            ) to (
                "Rongdhonu Shop is the official digital shopping and printing platform of Rongdhonu Digital Studio.\n\n" +
                "We provide high-quality custom DTF T-shirt printing, photo mugs, PVC ID cards, electronics, mobile servicing, fashion, and daily essentials across Bangladesh.\n\n" +
                "Hotline & WhatsApp: $OFFICIAL_WHATSAPP\nEmail: $OFFICIAL_EMAIL\nCountry: Bangladesh"
            )
            "shipping" -> (
                if (language == Language.BN) "শিপিং ও ডেলিভারি পলিসি" else "Shipping & Delivery Policy"
            ) to (
                "• Inside Dhaka: Standard delivery charge ৳60 (24 to 48 hours).\n" +
                "• Outside Dhaka: Delivery charge ৳120 (3 to 5 business days).\n" +
                "• Cash on Delivery (COD) is available across all 64 districts in Bangladesh.\n" +
                "• Express courier partners: Steadfast Courier, Pathao Courier, RedX, and Sundarban Courier."
            )
            "refund" -> (
                if (language == Language.BN) "রিটার্ন ও রিফান্ড পলিসি" else "Return & Refund Policy"
            ) to (
                "• Customers can request a product return or exchange within 7 days of receiving the package if the item is damaged, defective, or incorrect.\n" +
                "• For custom photo printing items (T-shirts, mugs), replacement is provided for any printing defects or material flaws.\n" +
                "• Refund processing takes 3-5 business days directly to bKash, Nagad, or bank accounts."
            )
            else -> (
                if (language == Language.BN) "শর্তাবলী ও নিরাপত্তা" else "Terms & Security Policy"
            ) to (
                "• Rongdhonu Digital Studio protects all customer personal data and delivery addresses.\n" +
                "• Every bKash and Nagad payment is validated with a unique transaction ID before shipping.\n" +
                "• All prices are in Bangladeshi Taka (৳ BDT) inclusive of applicable taxes."
            )
        }

        AlertDialog(
            onDismissRequest = { showPolicyDialog = null },
            title = { Text(title, fontWeight = FontWeight.Bold) },
            text = { Text(body, fontSize = 13.sp, lineHeight = 20.sp) },
            confirmButton = {
                Button(onClick = { showPolicyDialog = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ContactRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            badge?.let {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = it,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
