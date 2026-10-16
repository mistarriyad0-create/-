package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.RongdhonuRepository
import com.example.model.Language
import com.example.model.StoreSettings
import com.example.ui.components.*
import com.example.ui.localization.Strings
import com.example.ui.theme.*

data class SocialChannelItem(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val handle: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val category: String, // "social", "chat", "web"
    val icon: ImageVector,
    val brandColor: Color,
    val url: String,
    val badgeEn: String = "Verified",
    val badgeBn: String = "ভেরিফায়েড",
    val actionLabelEn: String = "Click to View",
    val actionLabelBn: String = "ক্লিক করে দেখুন"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanySocialMediaScreen(
    language: Language,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val storeSettings by RongdhonuRepository.storeSettings.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }

    BackHandler {
        onBack()
    }

    val channels = remember(storeSettings) {
        listOf(
            SocialChannelItem(
                id = "facebook_page",
                nameEn = "Official Facebook Page",
                nameBn = "অফিসিয়াল ফেসবুক পেজ",
                handle = storeSettings.facebookUrl.removePrefix("https://"),
                descriptionEn = "Explore new product collections, photo reviews, customer photos, and live sessions.",
                descriptionBn = "আমাদের সব নতুন পণ্য, কাস্টমার ফটো রিভিউ, লাইভ সেশন এবং প্রতিদিনের বিশেষ অফার দেখুন।",
                category = "social",
                icon = Icons.Default.ThumbUp,
                brandColor = Color(0xFF1877F2),
                url = storeSettings.facebookUrl,
                badgeEn = "Official Page",
                badgeBn = "অফিসিয়াল পেজ",
                actionLabelEn = "Visit Facebook Page",
                actionLabelBn = "ফেসবুক পেজে যান"
            ),
            SocialChannelItem(
                id = "facebook_group",
                nameEn = "Facebook Customer Group",
                nameBn = "অফিসিয়াল ফেসবুক গ্রুপ ও কমিউনিটি",
                handle = storeSettings.facebookGroupUrl.removePrefix("https://"),
                descriptionEn = "Join thousands of customers sharing custom print ideas, feedback, and photos.",
                descriptionBn = "কাস্টমারদের সাথে যুক্ত হন, প্রিন্ট ডিজাইন শেয়ার করুন এবং সরাসরি মতামত জানান।",
                category = "social",
                icon = Icons.Default.Groups,
                brandColor = Color(0xFF1E40AF),
                url = storeSettings.facebookGroupUrl,
                badgeEn = "Community",
                badgeBn = "কমিউনিটি",
                actionLabelEn = "Join Group & View",
                actionLabelBn = "গ্রুপে যোগ দিন ও দেখুন"
            ),
            SocialChannelItem(
                id = "whatsapp",
                nameEn = "WhatsApp Official Helpline & Order",
                nameBn = "হোয়াটসঅ্যাপ হেল্পলাইন ও সরাসরি অর্ডার",
                handle = "${storeSettings.whatsappNumber} (24/7 Support)",
                descriptionEn = "Direct 24/7 instant chat for custom design submission, pricing quotation & instant ordering.",
                descriptionBn = "কাস্টম টি-শার্ট ও মগ ডিজাইনের ছবি পাঠান, দ্রুত দাম জানুন ও ২৪/৭ সরাসরি অর্ডার করুন।",
                category = "chat",
                icon = Icons.Default.Chat,
                brandColor = Color(0xFF25D366),
                url = "https://wa.me/8801632042441?text=${android.net.Uri.encode("আসসালামু আলাইকুম, আমি রংধনু শপ থেকে যোগাযোগ করছি।")}",
                badgeEn = "Instant Chat",
                badgeBn = "সরাসরি চ্যাট",
                actionLabelEn = "Chat on WhatsApp",
                actionLabelBn = "হোয়াটসঅ্যাপে চ্যাট করুন"
            ),
            SocialChannelItem(
                id = "youtube",
                nameEn = "Official YouTube Channel",
                nameBn = "অফিসিয়াল ইউটিউব চ্যানেল",
                handle = storeSettings.youtubeUrl.removePrefix("https://"),
                descriptionEn = "Watch DTF T-shirt printing demos, mug sublimation videos, product unboxings & reviews.",
                descriptionBn = "টি-শার্ট প্রিন্টিং ডেমো, ফটো মগ তৈরির ভিডিও, গ্যাজেট আনবক্সিং ও রিভিউ ভিডিও দেখুন।",
                category = "social",
                icon = Icons.Default.PlayCircle,
                brandColor = Color(0xFFFF0000),
                url = storeSettings.youtubeUrl,
                badgeEn = "Video Demos",
                badgeBn = "ভিডিও ও ডেমো",
                actionLabelEn = "Watch on YouTube",
                actionLabelBn = "ইউটিউব ভিডিও দেখুন"
            ),
            SocialChannelItem(
                id = "instagram",
                nameEn = "Official Instagram Gallery",
                nameBn = "অফিসিয়াল ইনস্টাগ্রাম প্রোফাইল",
                handle = storeSettings.instagramUrl.removePrefix("https://"),
                descriptionEn = "Browse high-resolution photos of custom printed apparel, gifts, and stylish reels.",
                descriptionBn = "কাস্টম প্রিন্ট করা টি-শার্ট, ফটো মগ, গিফট আইটেম ও ট্রেন্ডি রিলস ফটো দেখুন।",
                category = "social",
                icon = Icons.Default.CameraAlt,
                brandColor = Color(0xFFE1306C),
                url = storeSettings.instagramUrl,
                badgeEn = "Photo Gallery",
                badgeBn = "ফটো গ্যালারি",
                actionLabelEn = "View Instagram",
                actionLabelBn = "ইনস্টাগ্রামে দেখুন"
            ),
            SocialChannelItem(
                id = "tiktok",
                nameEn = "Official TikTok Channel",
                nameBn = "অফিসিয়াল টিকটক চ্যানেল",
                handle = storeSettings.tiktokUrl.removePrefix("https://"),
                descriptionEn = "Catch viral behind-the-scenes studio printing clips and exclusive flash sale videos.",
                descriptionBn = "স্টুডিও প্রিন্টিং কাজের ভাইরাল শর্ট ক্লিপস এবং এক্সক্লুসিভ অফারের ভিডিও দেখুন।",
                category = "social",
                icon = Icons.Default.MusicNote,
                brandColor = Color(0xFF0F172A),
                url = storeSettings.tiktokUrl,
                badgeEn = "Short Videos",
                badgeBn = "শর্ট ভিডিও",
                actionLabelEn = "Watch on TikTok",
                actionLabelBn = "টিকটক ভিডিও দেখুন"
            ),
            SocialChannelItem(
                id = "telegram",
                nameEn = "Official Telegram Channel",
                nameBn = "অফিসিয়াল টেলিগ্রাম চ্যানেল",
                handle = storeSettings.telegramUrl.removePrefix("https://"),
                descriptionEn = "Get instant coupon codes, flash discounts, and delivery notifications.",
                descriptionBn = "সবার আগে স্পেশাল কুপন কোড, ফ্ল্যাশ ডিসকাউন্ট ও অফারের নোটিফিকেশন পান।",
                category = "chat",
                icon = Icons.Default.Send,
                brandColor = Color(0xFF229ED9),
                url = storeSettings.telegramUrl,
                badgeEn = "Fast Alerts",
                badgeBn = "দ্রুত আপডেট",
                actionLabelEn = "Join Telegram",
                actionLabelBn = "টেলিগ্রামে যুক্ত হোন"
            ),
            SocialChannelItem(
                id = "hotline",
                nameEn = "Direct Customer Hotline Call",
                nameBn = "সরাসরি কাস্টমার কেয়ার হটলাইন কল",
                handle = "01632042441 (9:00 AM - 10:00 PM)",
                descriptionEn = "Speak directly with our customer representative for order status or bulk orders.",
                descriptionBn = "অর্ডার, পাইকারি কেনাকাটা বা যেকোনো তথ্যের জন্য সরাসরি ফোনে কথা বলুন।",
                category = "chat",
                icon = Icons.Default.Phone,
                brandColor = Color(0xFF4F46E5),
                url = "tel:01632042441",
                badgeEn = "Phone Support",
                badgeBn = "ফোন কল",
                actionLabelEn = "Call Helpline",
                actionLabelBn = "সরাসরি কল করুন"
            ),
            SocialChannelItem(
                id = "email",
                nameEn = "Official Email Support",
                nameBn = "অফিসিয়াল ইমেইল সাপোর্ট",
                handle = storeSettings.email,
                descriptionEn = "Official email communication for business inquiries, invoices & corporate orders.",
                descriptionBn = "ব্যবসায়িক তথ্য, করপোরেট অর্ডার বা ইনভয়েসের জন্য সরাসরি ইমেইল করুন।",
                category = "chat",
                icon = Icons.Default.Email,
                brandColor = Color(0xFFF59E0B),
                url = "mailto:${storeSettings.email}",
                badgeEn = "Email Support",
                badgeBn = "ইমেইল",
                actionLabelEn = "Send Email",
                actionLabelBn = "ইমেইল পাঠান"
            ),
            SocialChannelItem(
                id = "website",
                nameEn = "Official Web Store",
                nameBn = "অফিসিয়াল ই-কমার্স ওয়েবসাইট",
                handle = storeSettings.websiteUrl.removePrefix("https://"),
                descriptionEn = "Browse and shop directly from any web browser or desktop computer.",
                descriptionBn = "যেকোনো কম্পিউটার বা মোবাইল ব্রাউজার থেকে সরাসরি শপিং করুন।",
                category = "web",
                icon = Icons.Default.Language,
                brandColor = Color(0xFF10B981),
                url = storeSettings.websiteUrl,
                badgeEn = "Web Platform",
                badgeBn = "ওয়েব স্টোর",
                actionLabelEn = "Visit Web Store",
                actionLabelBn = "ওয়েবসাইটে যান"
            ),
            SocialChannelItem(
                id = "studio_location",
                nameEn = "Studio Location (Google Maps)",
                nameBn = "স্টুডিও অফিস ও গুগল ম্যাপস লোকেশন",
                handle = "Rongdhonu Digital Studio, Bangladesh",
                descriptionEn = "Find our physical studio counter on Google Maps for custom print pickups & servicing.",
                descriptionBn = "কাস্টম প্রিন্ট পার্সেল সংগ্রহ বা মুখোমুখি পরামর্শের জন্য স্টুডিওর অবস্থান দেখুন।",
                category = "web",
                icon = Icons.Default.LocationOn,
                brandColor = Color(0xFFEA4335),
                url = storeSettings.mapsLocationUrl,
                badgeEn = "Office Location",
                badgeBn = "স্টুডিও অফিস",
                actionLabelEn = "View on Maps",
                actionLabelBn = "ম্যাপসে লোকেশন দেখুন"
            )
        )
    }

    val filteredChannels = remember(channels, selectedFilter, searchQuery) {
        channels.filter { channel ->
            val matchesFilter = when (selectedFilter) {
                "social" -> channel.category == "social"
                "chat" -> channel.category == "chat"
                "web" -> channel.category == "web"
                else -> true
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                val q = searchQuery.trim().lowercase()
                channel.nameEn.lowercase().contains(q) ||
                channel.nameBn.lowercase().contains(q) ||
                channel.handle.lowercase().contains(q) ||
                channel.descriptionEn.lowercase().contains(q) ||
                channel.descriptionBn.lowercase().contains(q)
            }
            matchesFilter && matchesSearch
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (language == Language.BN) "কোম্পানির সোশ্যাল মিডিয়া" else "Company Social Media",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (language == Language.BN) "রংধনু ডিজিটাল স্টুডিও" else "Rongdhonu Digital Studio",
                            style = MaterialTheme.typography.bodySmall,
                            color = RongdhonuPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Quick Home button
                    FilledTonalButton(
                        onClick = onBack,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = Strings.navHome(language),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Rainbow Banner Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF4F46E5),
                                        Color(0xFF7C3AED),
                                        Color(0xFFDB2777)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (language == Language.BN)
                                            "রংধনু ডিজিটাল স্টুডিও"
                                        else
                                            "Rongdhonu Digital Studio",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    Surface(
                                        color = Color(0xFF10B981),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (language == Language.BN) "✓ ১০০% ভেরিফায়েড অফিসিয়াল চ্যানেল" else "✓ 100% Verified Official Channels",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = if (language == Language.BN)
                                    "আমাদের ফেসবুক পেজ, গ্রুপ, ইউটিউব, টিকটক, ইনস্টাগ্রাম ও হোয়াটসঅ্যাপে ক্লিক করে যুক্ত হোন। যেকোনো প্রশ্ন ও কাস্টম অর্ডারের জন্য সবাই সরাসরি ক্লিক করে দেখতে পারবেন।"
                                else
                                    "Connect with us on Facebook, YouTube, TikTok, Instagram, and WhatsApp. Everyone can click any option below to view our official pages, videos, and chat directly.",
                                color = Color.White.copy(alpha = 0.95f),
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick Action Chips Row inside Hero
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        openWhatsApp(context, "আসসালামু আলাইকুম, আমি রংধনু শপ থেকে যোগাযোগ করছি।")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        openWebUrl(context, storeSettings.facebookUrl)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Facebook", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1877F2))
                                }

                                Button(
                                    onClick = {
                                        openDialer(context, "01632042441")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Call BD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }

            // Quick Filter Tabs (All, Social, Chat & Support, Web & Maps)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "all",
                        onClick = { selectedFilter = "all" },
                        label = {
                            Text(if (language == Language.BN) "সবগুলো (${channels.size})" else "All (${channels.size})", fontSize = 11.sp)
                        }
                    )
                    FilterChip(
                        selected = selectedFilter == "social",
                        onClick = { selectedFilter = "social" },
                        label = {
                            Text(if (language == Language.BN) "সোশ্যাল ও ভিডিও" else "Social & Video", fontSize = 11.sp)
                        }
                    )
                    FilterChip(
                        selected = selectedFilter == "chat",
                        onClick = { selectedFilter = "chat" },
                        label = {
                            Text(if (language == Language.BN) "চ্যাট ও কল" else "Chat & Call", fontSize = 11.sp)
                        }
                    )
                    FilterChip(
                        selected = selectedFilter == "web",
                        onClick = { selectedFilter = "web" },
                        label = {
                            Text(if (language == Language.BN) "ওয়েব ও অফিস" else "Web & Office", fontSize = 11.sp)
                        }
                    )
                }
            }

            // Channels List
            items(filteredChannels, key = { it.id }) { channel ->
                SocialChannelDetailedCard(
                    channel = channel,
                    language = language,
                    onViewClick = {
                        when (channel.id) {
                            "whatsapp" -> openWhatsApp(context, "আসসালামু আলাইকুম, আমি রংধনু শপ থেকে যোগাযোগ করছি।")
                            "hotline" -> openDialer(context, "01632042441")
                            "email" -> openEmail(context, storeSettings.email)
                            else -> openWebUrl(context, channel.url)
                        }
                    },
                    onCopyLink = {
                        val textToCopy = when (channel.id) {
                            "hotline" -> "01632042441"
                            "email" -> storeSettings.email
                            else -> channel.url
                        }
                        copyToClipboard(context, textToCopy, channel.nameEn)
                    },
                    onShare = {
                        val textToShare = "${channel.nameEn} (${channel.nameBn})\nLink: ${channel.url}\nRongdhonu Digital Studio - Bangladesh"
                        shareText(context, textToShare, channel.nameEn)
                    }
                )
            }

            // Bottom Navigation Helper / Return to Home
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (language == Language.BN)
                                "কেনাকাটা চালিয়ে যেতে চান?"
                            else
                                "Ready to explore products?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (language == Language.BN)
                                "হোমে ফিরে গিয়ে আমাদের দারুণ কালেকশন ও স্পেশাল অফারগুলো দেখুন।"
                            else
                                "Go back to Home to browse categories, flash sales, and cart.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == Language.BN) "হোমে ফিরে যান (Back to Home)" else "Back to Home",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun SocialChannelDetailedCard(
    channel: SocialChannelItem,
    language: Language,
    onViewClick: () -> Unit,
    onCopyLink: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onViewClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Icon + Name + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(channel.brandColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = channel.icon,
                            contentDescription = channel.nameEn,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (language == Language.BN) channel.nameBn else channel.nameEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = channel.handle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = channel.brandColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Surface(
                    color = channel.brandColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (language == Language.BN) channel.badgeBn else channel.badgeEn,
                        color = channel.brandColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = if (language == Language.BN) channel.descriptionBn else channel.descriptionEn,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Main Click to View Button
                Button(
                    onClick = onViewClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = channel.brandColor)
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Open",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.BN) channel.actionLabelBn else channel.actionLabelEn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Copy Link Button
                OutlinedButton(
                    onClick = onCopyLink,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (language == Language.BN) "কপি" else "Copy",
                        fontSize = 11.sp
                    )
                }

                // Share Button
                IconButton(
                    onClick = onShare,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
