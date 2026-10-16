package com.example.model

enum class Language {
    EN, BN
}

enum class ProductType {
    PHYSICAL_PRODUCT,
    PRINTING_SERVICE,
    DIGITAL_SERVICE
}

enum class ProductPublicationStatus(val label: String) {
    PUBLISHED("Published"),
    DRAFT("Draft"),
    SCHEDULED("Scheduled")
}

data class Category(
    val id: String,
    val nameEn: String,
    val nameBn: String,
    val iconKey: String,
    val itemCount: Int = 0
)

data class Product(
    val id: String,
    val sku: String,
    val nameEn: String,
    val nameBn: String,
    val categoryId: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val regularPrice: Double,
    val discountPrice: Double,
    val costPrice: Double = regularPrice * 0.65, // ক্রয়মূল্য / Cost Price for Profit Analytics
    val stock: Int,
    val barcode: String = "890${sku.filter { it.isDigit() }.take(6).padStart(6, '1')}",
    val brand: String = "Rongdhonu Studio",
    val videoUrl: String = "",
    val warranty: String = "7 Days Official Replacement",
    val expiryDate: String = "N/A",
    val publicationStatus: ProductPublicationStatus = ProductPublicationStatus.PUBLISHED,
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val isFlashSale: Boolean = false,
    val isNewArrival: Boolean = false,
    val imageUrl: String,
    val additionalImages: List<String> = emptyList(),
    val variants: List<String> = emptyList(),
    val rating: Float = 4.8f,
    val reviewCount: Int = 12,
    val viewCount: Int = 145,
    val soldCount: Int = 24,
    val deliveryInfoEn: String = "Delivery inside Dhaka: 24-48 Hours. Outside Dhaka: 3-5 Days.",
    val deliveryInfoBn: String = "ঢাকার ভিতরে ডেলিভারি: ২৪-৪৮ ঘণ্টা। ঢাকার বাইরে: ৩-৫ দিন।",
    val productType: ProductType = ProductType.PHYSICAL_PRODUCT
) {
    val effectivePrice: Double get() = if (discountPrice > 0 && discountPrice < regularPrice) discountPrice else regularPrice
    val discountPercent: Int get() = if (regularPrice > 0 && discountPrice > 0 && discountPrice < regularPrice) {
        (((regularPrice - discountPrice) / regularPrice) * 100).toInt()
    } else 0
}

data class CartItem(
    val id: String,
    val product: Product,
    val quantity: Int,
    val selectedVariant: String? = null
) {
    val totalPrice: Double get() = product.effectivePrice * quantity
}

enum class OrderStatus(val labelEn: String, val labelBn: String) {
    PENDING("Pending", "অপেক্ষমান"),
    CONFIRMED("Confirmed", "নিশ্চিত"),
    PROCESSING("Processing", "প্রক্রিয়াধীন"),
    SHIPPED("Shipped", "ডেলিভারিতে আছে"),
    DELIVERED("Delivered", "ডেলিভারি সম্পন্ন"),
    CANCELLED("Cancelled", "বাতিলকৃত"),
    RETURNED("Returned", "ফেরতকৃত")
}

enum class PaymentMethod(val labelEn: String, val labelBn: String) {
    COD("Cash on Delivery (COD)", "ক্যাশ অন ডেলিভারি (হাতে পেয়ে টাকা)"),
    BKASH("bKash (Send/Payment)", "বিকাশ পেমেন্ট"),
    NAGAD("Nagad (Send/Payment)", "নগদ পেমেন্ট"),
    WHATSAPP("WhatsApp Direct Order", "হোয়াটসঅ্যাপ সরাসরি অর্ডার")
}

enum class PaymentStatus(val labelEn: String, val labelBn: String) {
    UNPAID("Unpaid", "পরিশোধিত নয়"),
    PENDING_VERIFICATION("Pending Verification", "যাচাই অপেক্ষমান"),
    PAID("Paid", "পরিশোধিত"),
    REFUNDED("Refunded", "রিফান্ডকৃত")
}

data class Order(
    val id: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String = "",
    val deliveryAddress: String,
    val district: String,
    val thana: String,
    val deliveryMethod: String = "Standard Delivery",
    val orderNote: String = "",
    val items: List<CartItem>,
    val subtotal: Double,
    val discount: Double = 0.0,
    val deliveryCharge: Double = 60.0,
    val grandTotal: Double,
    val couponCode: String? = null,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val transactionId: String? = null,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val isCodConfirmed: Boolean = false,
    val courierName: String? = null,
    val courierTrackingNumber: String? = null,
    val refundAmount: Double = 0.0,
    val returnReason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

data class PromoBanner(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val discountTag: String,
    val targetCategory: String? = null,
    val isLive: Boolean = true
)

data class Coupon(
    val code: String,
    val percentOff: Int,
    val minSpend: Double,
    val isBogo: Boolean = false,
    val expiryDate: String = "31 Dec 2026"
)

data class CustomerReview(
    val id: String,
    val author: String,
    val rating: Int,
    val commentEn: String,
    val commentBn: String,
    val date: String,
    val verifiedPurchase: Boolean = true
)

// --- ADVANCED ADMIN EXPANSIONS ---

enum class CustomerTier(val label: String, val colorHex: Long) {
    NEW("New Customer", 0xFF64748B),
    REGULAR("Regular", 0xFF3B82F6),
    VIP("VIP Gold", 0xFFEAB308)
}

data class CustomerProfile(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val district: String,
    val tier: CustomerTier = CustomerTier.NEW,
    val loyaltyPoints: Int = 50,
    val totalOrders: Int = 1,
    val totalSpent: Double = 0.0,
    val joinedDate: String = "Oct 2026",
    val customDiscountPercent: Int = 0
)

enum class TicketStatus(val label: String) {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved")
}

data class SupportTicket(
    val id: String,
    val customerName: String,
    val customerPhone: String,
    val orderId: String? = null,
    val subject: String,
    val message: String,
    val status: TicketStatus = TicketStatus.OPEN,
    val createdAt: Long = System.currentTimeMillis()
)

data class ExpenseRecord(
    val id: String,
    val title: String,
    val category: String, // e.g., "Packaging", "Raw Materials", "Courier Shipping", "Utility/Electric", "Marketing"
    val amount: Double,
    val date: Long = System.currentTimeMillis()
)

enum class ServiceStatus(val labelEn: String, val labelBn: String) {
    PENDING("Pending", "অপেক্ষমান"),
    IN_PROGRESS("In Progress", "কাজ চলছে"),
    COMPLETED("Completed", "সম্পন্ন")
}

data class DigitalServiceRequest(
    val id: String,
    val serviceTitle: String,
    val customerName: String,
    val customerPhone: String,
    val documentInfo: String,
    val quotedPrice: Double,
    val status: ServiceStatus = ServiceStatus.PENDING,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class AdPosition(val label: String) {
    TOP_BAR("Top Header Banner"),
    HOMEPAGE_MIDDLE("Homepage In-feed"),
    FOOTER_STICKY("Bottom Sticky Bar")
}

data class AdvertisementBanner(
    val id: String,
    val title: String,
    val position: AdPosition,
    val imageUrl: String,
    val redirectLink: String = "https://wa.me/8801632042441",
    val clicks: Int = 45,
    val impressions: Int = 1240,
    val isActive: Boolean = true
)

enum class AdminRole(val label: String) {
    OWNER("Store Owner (Full Access)"),
    MANAGER("Operations Manager"),
    ORDER_MANAGER("Order & Logistics Manager"),
    STAFF("Support Staff")
}

data class AdminAuditLog(
    val id: String,
    val adminName: String,
    val role: AdminRole,
    val action: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationType {
    NEW_ORDER,
    PAYMENT,
    LOW_STOCK,
    NEW_CUSTOMER
}

data class AdminNotification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class CourierPartner(
    val id: String,
    val name: String,
    val hotline: String = "",
    val trackingUrlPrefix: String = "",
    val isEnabled: Boolean = true
)

data class StoreSettings(
    val storeName: String = "Rongdhonu Shop",
    val businessName: String = "Rongdhonu Digital Studio",
    val whatsappNumber: String = "01632042441",
    val email: String = "rongdhonustudio00@gmail.com",
    val deliveryDhaka: Double = 60.0,
    val deliveryOutside: Double = 120.0,
    val seoTitle: String = "Rongdhonu Shop | Digital Studio & E-Commerce BD",
    val seoDescription: String = "Custom DTF T-shirt printing, photo mugs, electronics, mobile servicing, fashion, and daily essentials across Bangladesh.",
    val maintenanceMode: Boolean = false,
    val facebookUrl: String = "https://facebook.com/rongdhonustudio",
    val facebookGroupUrl: String = "https://facebook.com/groups/rongdhonustudio",
    val youtubeUrl: String = "https://youtube.com/@rongdhonustudio",
    val tiktokUrl: String = "https://tiktok.com/@rongdhonustudio",
    val instagramUrl: String = "https://instagram.com/rongdhonustudio",
    val telegramUrl: String = "https://t.me/rongdhonustudio",
    val websiteUrl: String = "https://rongdhonushop.com",
    val mapsLocationUrl: String = "https://maps.google.com/?q=Rongdhonu+Digital+Studio+Bangladesh",
    val twoFactorAuthEnabled: Boolean = false,
    val customThemeColor: String = "#FF6B00",
    val announcementText: String = "রংধনু শপে স্বাগতম! সারা বাংলাদেশে ক্যাশ অন ডেলিভারি এবং দ্রুত হোম ডেলিভারি!",
    val showAnnouncement: Boolean = true,
    val activeCouriers: List<String> = listOf("Steadfast Courier", "Pathao Courier", "RedX", "Sundarban Courier", "Paperfly", "SA Paribahan", "eCourier")
)

