package com.example.ui.localization

import com.example.model.Language

object Strings {
    fun appName(lang: Language) = when (lang) {
        Language.BN -> "রংধনু শপ"
        Language.EN -> "Rongdhonu Shop"
    }

    fun organization(lang: Language) = when (lang) {
        Language.BN -> "রংধনু ডিজিটাল স্টুডিও"
        Language.EN -> "Rongdhonu Digital Studio"
    }

    fun searchPlaceholder(lang: Language) = when (lang) {
        Language.BN -> "পণ্য খুঁজুন (নাম, ক্যাটাগরি, কোড)..."
        Language.EN -> "Search products, categories, SKU..."
    }

    fun navHome(lang: Language) = when (lang) {
        Language.BN -> "হোম"
        Language.EN -> "Home"
    }

    fun navCategories(lang: Language) = when (lang) {
        Language.BN -> "ক্যাটাগরি"
        Language.EN -> "Categories"
    }

    fun navCart(lang: Language) = when (lang) {
        Language.BN -> "কার্ট"
        Language.EN -> "Cart"
    }

    fun navOrders(lang: Language) = when (lang) {
        Language.BN -> "অর্ডার"
        Language.EN -> "Orders"
    }

    fun navSocialMedia(lang: Language) = when (lang) {
        Language.BN -> "সোশ্যাল মিডিয়া"
        Language.EN -> "Social Media"
    }

    fun companySocialMedia(lang: Language) = when (lang) {
        Language.BN -> "কোম্পানির সোশ্যাল মিডিয়া"
        Language.EN -> "Company Social Media"
    }

    fun clickToView(lang: Language) = when (lang) {
        Language.BN -> "ক্লিক করে দেখুন"
        Language.EN -> "Click to View"
    }

    fun navProfile(lang: Language) = when (lang) {
        Language.BN -> "প্রোফাইল"
        Language.EN -> "Profile"
    }

    fun flashSale(lang: Language) = when (lang) {
        Language.BN -> "⚡ ফ্ল্যাশ সেল ও বিশেষ অফার"
        Language.EN -> "⚡ Flash Sale & Hot Deals"
    }

    fun popularProducts(lang: Language) = when (lang) {
        Language.BN -> "জনপ্রিয় পণ্যসমূহ"
        Language.EN -> "Popular Products"
    }

    fun newArrivals(lang: Language) = when (lang) {
        Language.BN -> "নতুন আগমন"
        Language.EN -> "New Arrivals"
    }

    fun studioServices(lang: Language) = when (lang) {
        Language.BN -> "স্টুডিও ও কাস্টম প্রিন্টিং সেবা"
        Language.EN -> "Studio & Custom Printing"
    }

    fun customerReviews(lang: Language) = when (lang) {
        Language.BN -> "গ্রাহকদের প্রতিক্রিয়া"
        Language.EN -> "Customer Reviews"
    }

    fun addToCart(lang: Language) = when (lang) {
        Language.BN -> "কার্টে যোগ করুন"
        Language.EN -> "Add to Cart"
    }

    fun buyNow(lang: Language) = when (lang) {
        Language.BN -> "এখনই কিনুন"
        Language.EN -> "Buy Now"
    }

    fun orderViaWhatsApp(lang: Language) = when (lang) {
        Language.BN -> "হোয়াটসঅ্যাপে অর্ডার করুন"
        Language.EN -> "Order via WhatsApp"
    }

    fun checkout(lang: Language) = when (lang) {
        Language.BN -> "চেকআউট করুন"
        Language.EN -> "Proceed to Checkout"
    }

    fun emptyCart(lang: Language) = when (lang) {
        Language.BN -> "আপনার কার্ট বর্তমানে খালি!"
        Language.EN -> "Your shopping cart is empty!"
    }

    fun startShopping(lang: Language) = when (lang) {
        Language.BN -> "কেনাকাটা শুরু করুন"
        Language.EN -> "Start Shopping"
    }

    fun applyCoupon(lang: Language) = when (lang) {
        Language.BN -> "কুপন প্রয়োগ করুন"
        Language.EN -> "Apply Coupon"
    }

    fun couponPlaceholder(lang: Language) = when (lang) {
        Language.BN -> "কুপন কোড (যেমন: RONGDHONU10)"
        Language.EN -> "Coupon code (e.g. RONGDHONU10)"
    }

    fun subtotal(lang: Language) = when (lang) {
        Language.BN -> "মোট মূল্য:"
        Language.EN -> "Subtotal:"
    }

    fun discount(lang: Language) = when (lang) {
        Language.BN -> "ছাড়:"
        Language.EN -> "Discount:"
    }

    fun deliveryCharge(lang: Language) = when (lang) {
        Language.BN -> "ডেলিভারি চার্জ:"
        Language.EN -> "Delivery Charge:"
    }

    fun grandTotal(lang: Language) = when (lang) {
        Language.BN -> "সর্বমোট প্রদেয়:"
        Language.EN -> "Grand Total:"
    }

    fun trackOrder(lang: Language) = when (lang) {
        Language.BN -> "অর্ডার ট্র্যাক করুন"
        Language.EN -> "Track Order"
    }

    fun adminPanel(lang: Language) = when (lang) {
        Language.BN -> "অ্যাডমিন প্যানেল"
        Language.EN -> "Admin Panel"
    }

    fun formatPrice(amount: Double): String {
        return "৳ ${String.format(java.util.Locale.US, "%,.0f", amount)}"
    }
}
