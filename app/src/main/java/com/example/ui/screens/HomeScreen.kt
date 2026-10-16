package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RongdhonuRepository
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleData
import com.example.model.Category
import com.example.model.Language
import com.example.model.Product
import com.example.model.PromoBanner
import com.example.ui.components.*
import com.example.ui.localization.Strings
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    language: Language,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    products: List<Product>,
    categories: List<Category>,
    banners: List<PromoBanner>,
    wishlist: Set<String>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    onCategoryClick: (Category) -> Unit,
    onOpenSocialMedia: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storeSettings by RongdhonuRepository.storeSettings.collectAsStateWithLifecycle()
    var activeBannerIndex by remember { mutableIntStateOf(0) }

    // Filter products if search is active
    val filteredProducts = remember(searchQuery, products) {
        if (searchQuery.isBlank()) products else {
            val q = searchQuery.trim().lowercase()
            products.filter {
                it.nameEn.lowercase().contains(q) ||
                it.nameBn.lowercase().contains(q) ||
                it.sku.lowercase().contains(q) ||
                it.categoryId.lowercase().contains(q)
            }
        }
    }

    val flashSaleProducts = remember(products) {
        products.filter { it.isFlashSale }
    }

    val popularProducts = remember(products) {
        products.filter { it.isFeatured || it.rating >= 4.8f }
    }

    val studioServices = remember(products) {
        products.filter { it.categoryId in listOf("cat_tshirt", "cat_mug", "cat_printing", "cat_servicing") }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Sticky/Top Search Bar & Quick Navigation Options (Home & Social Media)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                if (storeSettings.showAnnouncement && storeSettings.announcementText.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Campaign,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = storeSettings.announcementText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                        }
                    }
                }

                SearchBarWidget(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    language = language
                )

                Spacer(modifier = Modifier.height(8.dp))


                // Primary Quick Option Switcher: [ 🏠 হোম ]  [ 🌐 কোম্পানির সোশ্যাল মিডিয়া ]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = RongdhonuPrimary,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.navHome(language),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1.3f)
                            .clickable { onOpenSocialMedia() },
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, RongdhonuPrimary.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = RongdhonuPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.companySocialMedia(language),
                                color = RongdhonuPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // Search Results Mode
        if (searchQuery.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == Language.BN) "অনুসন্ধানের ফলাফল (${filteredProducts.size})" else "Search Results (${filteredProducts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = "No Results",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (language == Language.BN) "কোনো পণ্য পাওয়া যায়নি" else "No products found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            } else {
                items(filteredProducts.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ProductCard(
                            product = pair[0],
                            language = language,
                            isWishlisted = wishlist.contains(pair[0].id),
                            onProductClick = onProductClick,
                            onAddToCart = onAddToCart,
                            onToggleWishlist = onToggleWishlist,
                            modifier = Modifier.weight(1f)
                        )
                        if (pair.size > 1) {
                            ProductCard(
                                product = pair[1],
                                language = language,
                                isWishlisted = wishlist.contains(pair[1].id),
                                onProductClick = onProductClick,
                                onAddToCart = onAddToCart,
                                onToggleWishlist = onToggleWishlist,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // Standard Homepage flow

            // 1. Promo Banner Slider
            item {
                val banner = banners.getOrNull(activeBannerIndex) ?: banners.first()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(170.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    when (activeBannerIndex % 3) {
                                        0 -> listOf(Color(0xFF4F46E5), Color(0xFF7C3AED))
                                        1 -> listOf(Color(0xFFFF6B00), Color(0xFFEA580C))
                                        else -> listOf(Color(0xFF0D9488), Color(0xFF0284C7))
                                    }
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.75f),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = banner.discountTag,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = if (language == Language.BN) banner.titleBn else banner.titleEn,
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (language == Language.BN) banner.subtitleBn else banner.subtitleEn,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 12.sp,
                                    maxLines = 2
                                )
                            }

                            Button(
                                onClick = {
                                    val target = categories.find { it.id == banner.targetCategory }
                                    if (target != null) onCategoryClick(target)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = if (language == Language.BN) "অফার দেখুন" else "Explore Now",
                                    color = Color(0xFF1E293B),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Banner switcher dots
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            banners.forEachIndexed { index, _ ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == activeBannerIndex) 8.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == activeBannerIndex) Color.White else Color.White.copy(alpha = 0.5f)
                                        )
                                        .clickable { activeBannerIndex = index }
                                )
                            }
                        }
                    }
                }
            }

            // 2. Announcement / Marquee Notice Bar
            item {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Notice",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == Language.BN)
                                "🚚 সারা দেশে ক্যাশ অন ডেলিভারি | বিকাশ/নগদে ১০০% নিরাপদ পেমেন্ট | স্টুডিও হেল্পলাইন: 01632042441"
                            else
                                "🚚 Nationwide Cash on Delivery | 100% Secure bKash/Nagad | Studio Hotline: 01632042441",
                            fontSize = 11.sp,
                            color = Color(0xFF92400E),
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // 2.5 Quick Company Social Media Options Strip (Clickable by everyone)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenSocialMedia() }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = RongdhonuPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.BN) "কোম্পানির সোশ্যাল মিডিয়া:" else "Company Social Media:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (language == Language.BN) "সবগুলো দেখুন ➔" else "View All ➔",
                                fontSize = 11.sp,
                                color = RongdhonuPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            SocialQuickChip(
                                label = "WhatsApp",
                                icon = Icons.Default.Chat,
                                bg = Color(0xFF25D366),
                                onClick = { openWhatsApp(context, "আসসালামু আলাইকুম, আমি রংধনু শপ থেকে যোগাযোগ করছি।") }
                            )
                        }
                        item {
                            SocialQuickChip(
                                label = "Facebook Page",
                                icon = Icons.Default.ThumbUp,
                                bg = Color(0xFF1877F2),
                                onClick = { openWebUrl(context, storeSettings.facebookUrl) }
                            )
                        }
                        item {
                            SocialQuickChip(
                                label = "Facebook Group",
                                icon = Icons.Default.Groups,
                                bg = Color(0xFF1E40AF),
                                onClick = { openWebUrl(context, storeSettings.facebookGroupUrl) }
                            )
                        }
                        item {
                            SocialQuickChip(
                                label = "YouTube",
                                icon = Icons.Default.PlayCircle,
                                bg = Color(0xFFFF0000),
                                onClick = { openWebUrl(context, storeSettings.youtubeUrl) }
                            )
                        }
                        item {
                            SocialQuickChip(
                                label = "Instagram",
                                icon = Icons.Default.CameraAlt,
                                bg = Color(0xFFE1306C),
                                onClick = { openWebUrl(context, storeSettings.instagramUrl) }
                            )
                        }
                        item {
                            SocialQuickChip(
                                label = "TikTok",
                                icon = Icons.Default.MusicNote,
                                bg = Color(0xFF0F172A),
                                onClick = { openWebUrl(context, storeSettings.tiktokUrl) }
                            )
                        }
                        item {
                            SocialQuickChip(
                                label = "Hotline Call",
                                icon = Icons.Default.Phone,
                                bg = Color(0xFF4F46E5),
                                onClick = { openDialer(context, "01632042441") }
                            )
                        }
                        item {
                            SocialQuickChip(
                                label = "Website",
                                icon = Icons.Default.Language,
                                bg = Color(0xFF10B981),
                                onClick = { openWebUrl(context, storeSettings.websiteUrl) }
                            )
                        }
                        item {
                            Surface(
                                color = RongdhonuPrimary,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.clickable { onOpenSocialMedia() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (language == Language.BN) "সব সোশ্যাল মিডিয়া" else "All Channels",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. Quick Categories Grid / Icons
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (language == Language.BN) "জনপ্রিয় ক্যাটাগরি" else "Featured Categories",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(categories) { cat ->
                            CategoryQuickItem(
                                category = cat,
                                language = language,
                                onClick = { onCategoryClick(cat) }
                            )
                        }
                    }
                }
            }

            // 4. Flash Sale Section (Daraz style with countdown pill)
            if (flashSaleProducts.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 4.dp)
                            .background(Color(0xFFFFF1F2))
                            .padding(vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = Strings.flashSale(language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFE11D48)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Timer pill
                                Surface(
                                    color = Color(0xFFE11D48),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "08 : 42 : 19",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(flashSaleProducts) { prod ->
                                Box(modifier = Modifier.width(170.dp)) {
                                    ProductCard(
                                        product = prod,
                                        language = language,
                                        isWishlisted = wishlist.contains(prod.id),
                                        onProductClick = onProductClick,
                                        onAddToCart = onAddToCart,
                                        onToggleWishlist = onToggleWishlist
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Digital Studio & Custom Printing Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Studio",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == Language.BN) "রংধনু ডিজিটাল স্টুডিও সেবা" else "Rongdhonu Studio Services",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = if (language == Language.BN) "টি-শার্ট ও মগ প্রিন্ট, পিভিসি আইডি কার্ড, মোবাইল রিপেয়ার" else "T-Shirt & Mug Print, PVC Cards, Phone Servicing",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF15803D),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = {
                                val msg = if (language == Language.BN)
                                    "রংধনু ডিজিটাল স্টুডিওর কাস্টম প্রিন্টিং ও সার্ভিসিং সম্পর্কে জানতে চাই।"
                                else
                                    "I want to know about Rongdhonu Studio custom printing services."
                                openWhatsApp(context, msg)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Contact Studio",
                                tint = WhatsAppGreen
                            )
                        }
                    }
                }
            }

            // 6. Popular Products Grid Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.popularProducts(language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Popular Products 2-column grid items
            items(popularProducts.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProductCard(
                        product = pair[0],
                        language = language,
                        isWishlisted = wishlist.contains(pair[0].id),
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onToggleWishlist = onToggleWishlist,
                        modifier = Modifier.weight(1f)
                    )
                    if (pair.size > 1) {
                        ProductCard(
                            product = pair[1],
                            language = language,
                            isWishlisted = wishlist.contains(pair[1].id),
                            onProductClick = onProductClick,
                            onAddToCart = onAddToCart,
                            onToggleWishlist = onToggleWishlist,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // 7. Customer Reviews Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 16.dp)
                ) {
                    Text(
                        text = Strings.customerReviews(language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(SampleData.customerReviews) { rev ->
                            Card(
                                modifier = Modifier.width(260.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = rev.author,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Row {
                                            repeat(rev.rating) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color(0xFFF59E0B),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (language == Language.BN) rev.commentBn else rev.commentEn,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "✓ Verified Purchase • ${rev.date}",
                                        fontSize = 10.sp,
                                        color = RongdhonuAccentGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 8. Company's Full Social Media & Helpline Hub (Clickable by everyone)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(RongdhonuPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (language == Language.BN)
                                        "কোম্পানির অফিসিয়াল সোশ্যাল মিডিয়া"
                                    else
                                        "Company Official Social Media",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (language == Language.BN)
                                        "যেকোনো অপশনে ক্লিক করে সরাসরি আমাদের পেজ বা চ্যানেলে যান"
                                    else
                                        "Click any option below to view our channels & connect",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Facebook Page Card
                        CompanySocialChannelCard(
                            platformName = if (language == Language.BN) "ফেসবুক অফিসিয়াল পেজ" else "Official Facebook Page",
                            handle = "facebook.com/rongdhonustudio",
                            description = if (language == Language.BN) "লাইক ও ফলো করে নতুন অফার ও কালেকশন দেখুন" else "Like & follow for latest product posts and updates",
                            actionLabel = if (language == Language.BN) "ফেসবুকে যান" else "Visit Facebook",
                            icon = Icons.Default.ThumbUp,
                            accentColor = Color(0xFF1877F2),
                            onClick = { openWebUrl(context, storeSettings.facebookUrl) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // WhatsApp Channel & Chat
                        CompanySocialChannelCard(
                            platformName = if (language == Language.BN) "অফিসিয়াল হোয়াটসঅ্যাপ সাপোর্ট ও অর্ডার" else "WhatsApp Helpline & Support",
                            handle = "01632042441 (২৪/৭ চ্যাট ও সরাসরি অর্ডার)",
                            description = if (language == Language.BN) "যেকোনো পণ্য বা কাস্টম প্রিন্টিং নিয়ে সরাসরি চ্যাট করুন" else "Chat directly with our support team anytime",
                            actionLabel = if (language == Language.BN) "হোয়াটসঅ্যাপে চ্যাট" else "Chat on WhatsApp",
                            icon = Icons.Default.Chat,
                            accentColor = Color(0xFF25D366),
                            onClick = { openWhatsApp(context, "আসসালামু আলাইকুম, আমি রংধনু শপ থেকে যোগাযোগ করছি।") }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // YouTube Channel
                        CompanySocialChannelCard(
                            platformName = if (language == Language.BN) "ইউটিউব অফিসিয়াল চ্যানেল" else "YouTube Official Channel",
                            handle = "youtube.com/@rongdhonustudio",
                            description = if (language == Language.BN) "টি-শার্ট প্রিন্টিং, ফটো মগ ও গ্যাজেটের আনবক্সিং ভিডিও দেখুন" else "Watch printing demos, product unboxings & reviews",
                            actionLabel = if (language == Language.BN) "সাবস্ক্রাইব করুন" else "Watch & Subscribe",
                            icon = Icons.Default.PlayCircle,
                            accentColor = Color(0xFFFF0000),
                            onClick = { openWebUrl(context, storeSettings.youtubeUrl) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Instagram
                        CompanySocialChannelCard(
                            platformName = if (language == Language.BN) "ইনস্টাগ্রাম প্রোফাইল" else "Instagram Profile",
                            handle = "instagram.com/rongdhonustudio",
                            description = if (language == Language.BN) "কাস্টম ডিজাইন ও কাস্টমার ফটো গ্যালারি" else "See our custom design portfolio and customer photos",
                            actionLabel = if (language == Language.BN) "ফলো করুন" else "Follow Instagram",
                            icon = Icons.Default.CameraAlt,
                            accentColor = Color(0xFFE1306C),
                            onClick = { openWebUrl(context, storeSettings.instagramUrl) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // TikTok
                        CompanySocialChannelCard(
                            platformName = if (language == Language.BN) "টিকটক অফিসিয়াল চ্যানেল" else "TikTok Official Channel",
                            handle = "tiktok.com/@rongdhonustudio",
                            description = if (language == Language.BN) "ট্রেন্ডিং শর্ট ভিডিও ও স্পেশাল ডিসকাউন্ট ক্লিপস" else "Watch short clips of custom prints & offers",
                            actionLabel = if (language == Language.BN) "টিকটক দেখুন" else "Watch on TikTok",
                            icon = Icons.Default.MusicNote,
                            accentColor = Color(0xFF0F172A),
                            onClick = { openWebUrl(context, storeSettings.tiktokUrl) }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Direct Hotline Call
                        CompanySocialChannelCard(
                            platformName = if (language == Language.BN) "সরাসরি হটলাইন কল" else "Direct Hotline Call",
                            handle = "01632042441 (সকাল ৯টা - রাত ১০টা)",
                            description = if (language == Language.BN) "অর্ডার ও কাস্টমার কেয়ারের জন্য সরাসরি ফোনে কথা বলুন" else "Call our customer service directly for any inquiry",
                            actionLabel = if (language == Language.BN) "কল করুন" else "Call Now",
                            icon = Icons.Default.Phone,
                            accentColor = Color(0xFF4F46E5),
                            onClick = { openDialer(context, "01632042441") }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Studio Physical Counter Info
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (language == Language.BN)
                                            "রংধনু ডিজিটাল স্টুডিও — বাংলাদেশ"
                                        else
                                            "Rongdhonu Digital Studio — Bangladesh",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = if (language == Language.BN)
                                            "ইমেইল: rongdhonustudio00@gmail.com | হটলাইন: 01632042441"
                                        else
                                            "Email: rongdhonustudio00@gmail.com | Hotline: 01632042441",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dedicated Button to open complete Social Media Screen
                        Button(
                            onClick = onOpenSocialMedia,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.BN)
                                    "কোম্পানির সব সোশ্যাল মিডিয়া পেইজ ও চ্যানেল দেখুন"
                                else
                                    "View All Company Social Media Pages & Channels",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryQuickItem(
    category: Category,
    language: Language,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(72.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            val icon = when (category.iconKey) {
                "tshirt" -> Icons.Default.Checkroom
                "mug" -> Icons.Default.Coffee
                "print" -> Icons.Default.Print
                "gift" -> Icons.Default.CardGiftcard
                "build" -> Icons.Default.Build
                "computer" -> Icons.Default.Computer
                "headphones" -> Icons.Default.Headphones
                "bolt" -> Icons.Default.Bolt
                "male" -> Icons.Default.Person
                "female" -> Icons.Default.Face
                "face" -> Icons.Default.AutoFixHigh
                "phone" -> Icons.Default.PhoneAndroid
                "account_balance" -> Icons.Default.AccountBalance
                "public" -> Icons.Default.Public
                else -> Icons.Default.Category
            }
            Icon(
                imageVector = icon,
                contentDescription = category.nameEn,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (language == Language.BN) category.nameBn else category.nameEn,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}
