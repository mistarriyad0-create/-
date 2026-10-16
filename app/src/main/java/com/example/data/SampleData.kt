package com.example.data

import com.example.model.*

object SampleData {
    val categories = listOf(
        Category("cat_tshirt", "T-shirt Printing", "টি-শার্ট প্রিন্টিং", "tshirt", 14),
        Category("cat_mug", "Custom Mug Printing", "কাস্টম মগ প্রিন্টিং", "mug", 10),
        Category("cat_printing", "Digital Printing Services", "ডিজিটাল প্রিন্টিং সেবা", "print", 18),
        Category("cat_gifts", "Gift Items", "উপহার সামগ্রী", "gift", 25),
        Category("cat_servicing", "Mobile Servicing", "মোবাইল সার্ভিসিং", "build", 8),
        Category("cat_online_svc", "Online Digital Services", "অনলাইন ডিজিটাল সার্ভিস", "computer", 12),
        Category("cat_mob_acc", "Mobile Accessories", "মোবাইল এক্সেসরিজ", "headphones", 32),
        Category("cat_electronics", "Electronics & Electrical", "ইলেকট্রনিক্স পণ্য", "bolt", 20),
        Category("cat_mens", "Men's Clothing", "পুরুষদের পোশাক", "male", 40),
        Category("cat_womens", "Women's Clothing", "নারীদের পোশাক", "female", 35),
        Category("cat_cosmetics", "Cosmetics & Beauty", "প্রসাধন ও রূপচর্চা", "face", 28),
        Category("cat_mobile", "Mobile Phones", "মোবাইল ফোন", "phone", 15),
        Category("cat_banking", "Mobile Banking Services", "মোবাইল ব্যাংকিং সেবা", "account_balance", 6),
        Category("cat_imported", "Foreign & Imported Gadgets", "আমদানি করা গ্যাজেট", "public", 16),
        Category("cat_grocery", "Grocery & Essentials", "মুদি ও নিত্যপ্রয়োজনীয়", "shopping_cart", 50)
    )

    val products = listOf(
        // Studio & Custom Printing
        Product(
            id = "prod_1",
            sku = "RD-TSH-001",
            nameEn = "Custom Photo T-Shirt (Premium Drop Shoulder)",
            nameBn = "কাস্টম ছবি প্রিন্ট টি-শার্ট (ড্রপ শোল্ডার)",
            categoryId = "cat_tshirt",
            descriptionEn = "High-definition DTF sublimation print on 220+ GSM 100% combed cotton fabric. Wash-proof vibrant color guarantee from Rongdhonu Digital Studio.",
            descriptionBn = "২২০+ জিএসএম ১০০% খাঁটি কটন ফ্যাব্রিকে হাই-ডেফিনিশন ডিটিএফ প্রিন্ট। রংধনু স্টুডিওর ওয়াশ-প্রুফ কালার গ্যারান্টি।",
            regularPrice = 650.0,
            discountPrice = 490.0,
            stock = 45,
            isFeatured = true,
            isFlashSale = true,
            isNewArrival = true,
            imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800&q=80",
            variants = listOf("Size: M", "Size: L", "Size: XL", "Size: XXL", "Color: Black", "Color: White"),
            rating = 4.9f,
            reviewCount = 38,
            viewCount = 612,
            productType = ProductType.PRINTING_SERVICE
        ),
        Product(
            id = "prod_2",
            sku = "RD-MUG-002",
            nameEn = "Custom Magic Color Changing Coffee Mug",
            nameBn = "কাস্টম ম্যাজিক ফটো কফি মগ",
            categoryId = "cat_mug",
            descriptionEn = "Heat-sensitive ceramic mug that reveals your custom photo or text when hot liquid is poured. Perfect personalized gift for birthdays and anniversaries.",
            descriptionBn = "গরম পানি বা চা ঢাললেই ফুটে উঠবে আপনার পছন্দমতো ছবি বা বার্তা। জন্মদিন ও উপহারের জন্য সেরা পছন্দ।",
            regularPrice = 450.0,
            discountPrice = 350.0,
            stock = 30,
            isFeatured = true,
            isFlashSale = false,
            isNewArrival = true,
            imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=800&q=80",
            variants = listOf("Type: Color Changing Magic", "Type: Pure White Glossy"),
            rating = 4.8f,
            reviewCount = 27,
            viewCount = 480,
            productType = ProductType.PRINTING_SERVICE
        ),
        Product(
            id = "prod_3",
            sku = "RD-PVC-003",
            nameEn = "Official PVC ID Card & Lanyard Printing",
            nameBn = "অফিসিয়াল পিভিসি আইডি কার্ড ও ফিতা প্রিন্টিং",
            categoryId = "cat_printing",
            descriptionEn = "Waterproof thick PVC identity card with barcode/QR integration and customized company printed lanyards. Suitable for offices, schools, and events.",
            descriptionBn = "ওয়াটারপ্রুফ টেকসই পিভিসি স্মার্ট আইডি কার্ড ও ডিজিটাল রিবন ফিতা প্রিন্ট। স্কুল, কলেজ ও অফিসিয়াল কাজের উপযোগী।",
            regularPrice = 180.0,
            discountPrice = 140.0,
            stock = 120,
            isFeatured = false,
            isFlashSale = false,
            isNewArrival = false,
            imageUrl = "https://images.unsplash.com/photo-1589829545856-d10d557cf95f?w=800&q=80",
            variants = listOf("Qty: Single Card", "Qty: Bulk 10+ Pack"),
            rating = 4.7f,
            reviewCount = 19,
            viewCount = 290,
            productType = ProductType.PRINTING_SERVICE
        ),
        Product(
            id = "prod_4",
            sku = "RD-SRV-004",
            nameEn = "Smartphone Display & Glass Replacement Service",
            nameBn = "স্মার্টফোন ডিসপ্লে ও গ্লাস পরিবর্তন সার্ভিস",
            categoryId = "cat_servicing",
            descriptionEn = "Expert technician display replacement for Samsung, Xiaomi, iPhone, Vivo, Realme with 30-day touch replacement warranty at Rongdhonu Digital Studio.",
            descriptionBn = "রংধনু ডিজিটাল স্টুডিওর দক্ষ টেকনিশিয়ান দ্বারা অরিজিনাল ও প্রিমিয়াম ডিসপ্লে রিপ্লেসমেন্ট সার্ভিস। ৩০ দিনের ওয়ারেন্টি সহ।",
            regularPrice = 2500.0,
            discountPrice = 2200.0,
            stock = 15,
            isFeatured = true,
            isFlashSale = false,
            isNewArrival = false,
            imageUrl = "https://images.unsplash.com/photo-1597740985671-2a8a3b80532e?w=800&q=80",
            variants = listOf("Model: AMOLED Original", "Model: OLED High Copy"),
            rating = 4.9f,
            reviewCount = 52,
            viewCount = 740,
            productType = ProductType.DIGITAL_SERVICE
        ),
        Product(
            id = "prod_5",
            sku = "RD-GFT-005",
            nameEn = "Customized Crystal Acrylic 3D Photo Lamp",
            nameBn = "কাস্টমাইজড ৩ডি এক্রিলিক ফটো নাইট ল্যাম্প",
            categoryId = "cat_gifts",
            descriptionEn = "Laser-engraved acrylic panel with wooden warm LED base. Custom portrait engraving from high-res photograph with personalized greeting text.",
            descriptionBn = "কাঠের স্টাইলিশ ওয়ার্ম এলইডি বেস সহ লেজার এনগ্রেভড ৩ডি এক্রিলিক ফ্রেম। প্রিয়জনের ছবি ও শুভেচ্ছা বার্তার চমৎকার উপহার।",
            regularPrice = 1200.0,
            discountPrice = 890.0,
            stock = 22,
            isFeatured = true,
            isFlashSale = true,
            isNewArrival = true,
            imageUrl = "https://images.unsplash.com/photo-1513519245088-0e12902e5a38?w=800&q=80",
            variants = listOf("Base: Warm Yellow Wood", "Base: 7 Color RGB Touch"),
            rating = 5.0f,
            reviewCount = 44,
            viewCount = 890,
            productType = ProductType.PRINTING_SERVICE
        ),

        // Mobile Accessories & Gadgets
        Product(
            id = "prod_6",
            sku = "RD-ACC-006",
            nameEn = "Remax 20000mAh 22.5W Fast Charging Power Bank",
            nameBn = "রিম্যাক্স ২০০০০ এমএএইচ ফাস্ট চার্জিং পাওয়ার ব্যাংক",
            categoryId = "cat_mob_acc",
            descriptionEn = "Dual USB-A and Type-C PD 22.5W two-way fast charging with digital LED battery percentage display and airline-safe certificate.",
            descriptionBn = "২২.৫ ওয়াট পিডি ফাস্ট চার্জিং এবং ডিজিটাল ডিসপ্লে সহ ২০,০০০ এমএএইচ ক্যাপাসিটি। মোবাইল ও গ্যাজেটের জন্য আদর্শ।",
            regularPrice = 2200.0,
            discountPrice = 1750.0,
            stock = 35,
            isFeatured = true,
            isFlashSale = true,
            isNewArrival = false,
            imageUrl = "https://images.unsplash.com/photo-1609592426815-58564177b8b4?w=800&q=80",
            variants = listOf("Color: Matte Black", "Color: Pure White"),
            rating = 4.8f,
            reviewCount = 65,
            viewCount = 930,
            productType = ProductType.PHYSICAL_PRODUCT
        ),
        Product(
            id = "prod_7",
            sku = "RD-ACC-007",
            nameEn = "TWS Wireless Bluetooth Earbuds with ANC",
            nameBn = "টিডব্লিউএস ওয়্যারলেস ব্লুটুথ ইয়ারবাডস (এএনসি সহ)",
            categoryId = "cat_mob_acc",
            descriptionEn = "Active Noise Cancellation, deep bass 13mm dynamic drivers, IPX5 water resistance, and 36-hour total battery life with Type-C case.",
            descriptionBn = "অ্যাক্টিভ নয়েজ ক্যান্সেলেশন, দুর্দান্ত ডিপ বেস ও ৩৬ ঘণ্টার লং ব্যাটারি ব্যাকআপ। গেমিং ও মিউজিকের জন্য পারফেক্ট।",
            regularPrice = 1850.0,
            discountPrice = 1390.0,
            stock = 28,
            isFeatured = true,
            isFlashSale = false,
            isNewArrival = true,
            imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800&q=80",
            variants = listOf("Color: Stealth Black", "Color: Pearl White"),
            rating = 4.7f,
            reviewCount = 31,
            viewCount = 540,
            productType = ProductType.PHYSICAL_PRODUCT
        ),

        // Men's & Women's Fashion
        Product(
            id = "prod_8",
            sku = "RD-MEN-008",
            nameEn = "Premium Semi-Fitting Embroidered Cotton Panjabi",
            nameBn = "প্রিমিয়াম সেমি-ফিটিং এমব্রয়ডারি কটন পাঞ্জাবি",
            categoryId = "cat_mens",
            descriptionEn = "Finely tailored Jacquard cotton fabric with subtle collar and placket thread embroidery. Comfortable all-day wear for Eid and festivals.",
            descriptionBn = "উন্নত জ্যাকার্ড সুতি কাপড়ে আরামদায়ক সেমি-ফিটিং ডিজাইনের পাঞ্জাবি। ঈদ, উৎসব ও জুম্মার জন্য চমৎকার।",
            regularPrice = 2400.0,
            discountPrice = 1850.0,
            stock = 18,
            isFeatured = true,
            isFlashSale = false,
            isNewArrival = true,
            imageUrl = "https://images.unsplash.com/photo-1617137984095-74e4e5e3613f?w=800&q=80",
            variants = listOf("Size: 40", "Size: 42", "Size: 44", "Color: Navy Blue", "Color: Maroon"),
            rating = 4.9f,
            reviewCount = 22,
            viewCount = 410,
            productType = ProductType.PHYSICAL_PRODUCT
        ),
        Product(
            id = "prod_9",
            sku = "RD-WMN-009",
            nameEn = "Authentic Tangail Soft Handloom Cotton Saree",
            nameBn = "খাঁটি টাঙ্গাইল সফট তাঁতের সুতি শাড়ি",
            categoryId = "cat_womens",
            descriptionEn = "100% pure organic cotton handloom woven saree with contrasting zari border. Lightweight, breathable, and exceptionally elegant.",
            descriptionBn = "১০০% খাঁটি সুতি সুতায় বোনা আরামদায়ক টাঙ্গাইল তাঁতের শাড়ি। আকর্ষণীয় কন্ট্রাস্ট আঁচল ও পাড় ডিজাইন।",
            regularPrice = 1950.0,
            discountPrice = 1450.0,
            stock = 14,
            isFeatured = false,
            isFlashSale = true,
            isNewArrival = true,
            imageUrl = "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=800&q=80",
            variants = listOf("Color: Royal Red & Gold", "Color: Mustard Yellow", "Color: Pastel Pink"),
            rating = 4.8f,
            reviewCount = 18,
            viewCount = 370,
            productType = ProductType.PHYSICAL_PRODUCT
        ),

        // Cosmetics & Beauty
        Product(
            id = "prod_10",
            sku = "RD-COS-010",
            nameEn = "Organic Herbal Aloe Vera & Vitamin C Brightening Serum",
            nameBn = "অর্গানিক অ্যালোভেরা ও ভিটামিন-সি ব্রাইটনিং সিরাম",
            categoryId = "cat_cosmetics",
            descriptionEn = "Pure natural extract serum for glowing skin tone, blemish reduction, and intense 24-hour hydration. Dermatologically tested.",
            descriptionBn = "প্রাকৃতিক উপাদান সমৃদ্ধ স্কিন ব্রাইটনিং সিরাম। ত্বকের দাগ দূর করে এবং ত্বককে করে মসৃণ ও উজ্জ্বল।",
            regularPrice = 850.0,
            discountPrice = 620.0,
            stock = 40,
            isFeatured = false,
            isFlashSale = false,
            isNewArrival = true,
            imageUrl = "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?w=800&q=80",
            variants = listOf("Volume: 30ml Bottle"),
            rating = 4.7f,
            reviewCount = 29,
            viewCount = 510,
            productType = ProductType.PHYSICAL_PRODUCT
        ),

        // Imported Gadgets
        Product(
            id = "prod_11",
            sku = "RD-IMP-011",
            nameEn = "Full HD Smart Portable WiFi LED Mini Projector",
            nameBn = "স্মার্ট পোর্টেবল ওয়াইফাই ফুল এইচডি মিনি প্রজেক্টর",
            categoryId = "cat_imported",
            descriptionEn = "Native 1080P supported, built-in Android OS, YouTube/Netflix streaming, screen mirroring from phone, and cinema sound speaker.",
            descriptionBn = "মোবাইল স্ক্রিন শেয়ারিং ও বিল্ট-ইন স্পিকার সহ হোম থিয়েটার মিনি প্রজেক্টর। ইউটিউব ও মুভি দেখার চমৎকার অভিজ্ঞতা।",
            regularPrice = 8500.0,
            discountPrice = 6900.0,
            stock = 6,
            isFeatured = true,
            isFlashSale = true,
            isNewArrival = true,
            imageUrl = "https://images.unsplash.com/photo-1535016120720-40c646be5580?w=800&q=80",
            variants = listOf("Model: Smart Android WiFi Edition"),
            rating = 4.9f,
            reviewCount = 15,
            viewCount = 880,
            productType = ProductType.PHYSICAL_PRODUCT
        ),

        // Grocery & Daily Essentials
        Product(
            id = "prod_12",
            sku = "RD-GRO-012",
            nameEn = "100% Pure Ghani Broken Mustard Oil 1 Litre",
            nameBn = "ঘানি ভাঙা ১০০% খাঁটি সরিষার তেল ১ লিটার",
            categoryId = "cat_grocery",
            descriptionEn = "Cold-pressed wood mill mustard oil without any chemicals or artificial scent. Natural pungent taste guaranteed.",
            descriptionBn = "কাঠের ঘানি ভাঙা শতভাগ খাঁটি সরিষার তেল। কোনো প্রকার কেমিক্যাল বা ভেজালমুক্ত খাঁটি ঝাঁজ ও স্বাদ।",
            regularPrice = 360.0,
            discountPrice = 310.0,
            stock = 60,
            isFeatured = false,
            isFlashSale = false,
            isNewArrival = false,
            imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=800&q=80",
            variants = listOf("Bottle: 1 Litre Food Grade PET"),
            rating = 5.0f,
            reviewCount = 41,
            viewCount = 630,
            productType = ProductType.PHYSICAL_PRODUCT
        )
    )

    val promoBanners = listOf(
        PromoBanner(
            id = "ban_1",
            titleEn = "Mega Studio Eid Sale!",
            titleBn = "মেগা স্টুডিও ঈদ স্পেশাল সেল!",
            subtitleEn = "Up to 35% OFF on Custom T-Shirts & Photo Mugs",
            subtitleBn = "কাস্টম টি-শার্ট ও ফটো মগে ৩৫% পর্যন্ত ছাড়",
            discountTag = "FLAT 35% OFF",
            targetCategory = "cat_tshirt"
        ),
        PromoBanner(
            id = "ban_2",
            titleEn = "Fast & Reliable Mobile Servicing",
            titleBn = "নির্ভরযোগ্য মোবাইল সার্ভিসিং ও পার্টস",
            subtitleEn = "Original Displays & Battery Replacement with Warranty",
            subtitleBn = "ওয়ারেন্টি সহ অরিজিনাল ডিসপ্লে ও ব্যাটারি সার্ভিস",
            discountTag = "WARRANTY BACKED",
            targetCategory = "cat_servicing"
        ),
        PromoBanner(
            id = "ban_3",
            titleEn = "Fast Nationwide Delivery",
            titleBn = "সারা বাংলাদেশে দ্রুত হোম ডেলিভারি",
            subtitleEn = "Cash on Delivery Available across all 64 Districts",
            subtitleBn = "৬৪ জেলায় ক্যাশ অন ডেলিভারি এবং সহজ বিকাশ পেমেন্ট",
            discountTag = "64 DISTRICTS",
            targetCategory = null
        )
    )

    val customerReviews = listOf(
        CustomerReview(
            id = "rev_1",
            author = "MD Shakil Hossain",
            rating = 5,
            commentEn = "Ordered 5 customized drop-shoulder t-shirts for our reunion. The DTF print clarity and fabric quality are top tier!",
            commentBn = "রিইউনিয়নের জন্য ৫টি কাস্টম টি-শার্ট নিয়েছিলাম। ডিটিএফ প্রিন্ট কোয়ালিটি এবং কাপড় অসাধারণ!",
            date = "12 Oct 2026"
        ),
        CustomerReview(
            id = "rev_2",
            author = "Farhana Akter",
            rating = 5,
            commentEn = "Got the magic photo mug for my husband's birthday. It arrived in secure packaging within 24 hours in Dhaka.",
            commentBn = "স্বামীর জন্মদিনের জন্য ম্যাজিক মগ অর্ডার করেছিলাম। নিরাপদ প্যাকেজিং সহ ঢাকায় মাত্র ২৪ ঘণ্টায় পেয়েছি।",
            date = "08 Oct 2026"
        ),
        CustomerReview(
            id = "rev_3",
            author = "Tanvir Ahmed",
            rating = 5,
            commentEn = "Repaired my Samsung AMOLED display at Rongdhonu Digital Studio. Clean work, original touch feel, highly recommended!",
            commentBn = "রংধনু ডিজিটাল স্টুডিও থেকে স্যামসাং ডিসপ্লে পরিবর্তন করিয়েছি। একদম অরিজিনাল ফিনিশিং এবং স্মুথ কাজ।",
            date = "02 Oct 2026"
        )
    )

    val sampleOrders = listOf(
        Order(
            id = "RS-98210",
            customerName = "Arif Mahmud",
            customerPhone = "01711223344",
            customerEmail = "arif@gmail.com",
            deliveryAddress = "House 14, Road 5, Block C, Mirpur 2",
            district = "Dhaka",
            thana = "Mirpur",
            deliveryMethod = "Standard (Inside Dhaka)",
            orderNote = "Please call before delivery",
            items = listOf(
                CartItem(
                    id = "ci_1",
                    product = products[0],
                    quantity = 2,
                    selectedVariant = "Size: L"
                )
            ),
            subtotal = 980.0,
            discount = 98.0,
            deliveryCharge = 60.0,
            grandTotal = 942.0,
            couponCode = "RONGDHONU10",
            paymentMethod = PaymentMethod.BKASH,
            paymentStatus = PaymentStatus.PAID,
            transactionId = "BKH9281729A",
            orderStatus = OrderStatus.PROCESSING,
            courierName = "Steadfast Courier",
            courierTrackingNumber = "STF-8392194",
            createdAt = System.currentTimeMillis() - 86400000L * 2
        ),
        Order(
            id = "RS-98211",
            customerName = "Nusrat Jahan",
            customerPhone = "01822334455",
            customerEmail = "nusrat@gmail.com",
            deliveryAddress = "Zindabazar, Sylhet Sadar",
            district = "Sylhet",
            thana = "Sylhet Sadar",
            deliveryMethod = "Outside Dhaka",
            orderNote = "Handle with care - fragile mug",
            items = listOf(
                CartItem(
                    id = "ci_2",
                    product = products[1],
                    quantity = 1,
                    selectedVariant = "Type: Color Changing Magic"
                )
            ),
            subtotal = 350.0,
            discount = 0.0,
            deliveryCharge = 120.0,
            grandTotal = 470.0,
            couponCode = null,
            paymentMethod = PaymentMethod.COD,
            paymentStatus = PaymentStatus.UNPAID,
            transactionId = null,
            orderStatus = OrderStatus.PENDING,
            courierName = "Pathao Courier",
            courierTrackingNumber = "PTH-441209",
            createdAt = System.currentTimeMillis() - 3600000L * 5
        )
    )

    val sampleCustomers = listOf(
        CustomerProfile(
            id = "cust_1",
            name = "MD Shakil Hossain",
            phone = "01711223344",
            email = "shakil@gmail.com",
            district = "Dhaka",
            tier = CustomerTier.VIP,
            loyaltyPoints = 320,
            totalOrders = 6,
            totalSpent = 7850.0,
            joinedDate = "15 Jan 2026",
            customDiscountPercent = 5
        ),
        CustomerProfile(
            id = "cust_2",
            name = "Farhana Akter",
            phone = "01822334455",
            email = "farhana@gmail.com",
            district = "Sylhet",
            tier = CustomerTier.REGULAR,
            loyaltyPoints = 140,
            totalOrders = 3,
            totalSpent = 2450.0,
            joinedDate = "10 Mar 2026",
            customDiscountPercent = 0
        ),
        CustomerProfile(
            id = "cust_3",
            name = "Tanvir Ahmed",
            phone = "01933445566",
            email = "tanvir@gmail.com",
            district = "Chittagong",
            tier = CustomerTier.NEW,
            loyaltyPoints = 50,
            totalOrders = 1,
            totalSpent = 890.0,
            joinedDate = "05 Oct 2026",
            customDiscountPercent = 0
        )
    )

    val sampleTickets = listOf(
        SupportTicket(
            id = "TCK-101",
            customerName = "Tanvir Ahmed",
            customerPhone = "01933445566",
            orderId = "RS-98210",
            subject = "T-shirt size exchange request",
            message = "I ordered L size but need XL size. Can I exchange it at your studio counter?",
            status = TicketStatus.OPEN,
            createdAt = System.currentTimeMillis() - 1000 * 3600 * 4
        ),
        SupportTicket(
            id = "TCK-102",
            customerName = "Farhana Akter",
            customerPhone = "01822334455",
            orderId = "RS-98211",
            subject = "Delivery status query",
            message = "Please let me know when Steadfast courier will dispatch the parcel in Sylhet.",
            status = TicketStatus.RESOLVED,
            createdAt = System.currentTimeMillis() - 1000 * 3600 * 24
        )
    )

    val sampleExpenses = listOf(
        ExpenseRecord("exp_1", "Packaging Boxes & Bubble Wrap", "Packaging", 1250.0, System.currentTimeMillis() - 86400000L),
        ExpenseRecord("exp_2", "Raw Blank T-Shirt Cotton Stock (50 pcs)", "Raw Materials", 14500.0, System.currentTimeMillis() - 86400000L * 3),
        ExpenseRecord("exp_3", "Steadfast Courier Bulk Delivery Charges", "Courier Shipping", 2400.0, System.currentTimeMillis() - 86400000L * 2),
        ExpenseRecord("exp_4", "Digital Studio Electricity & Internet", "Utility/Electric", 3200.0, System.currentTimeMillis() - 86400000L * 5)
    )

    val sampleDigitalServices = listOf(
        DigitalServiceRequest(
            id = "DS-701",
            serviceTitle = "College Reunion Custom T-Shirt Sublimation Print (25 pcs)",
            customerName = "Kabir Hasan",
            customerPhone = "01755667788",
            documentInfo = "Vector Logo & Student Name List uploaded (PDF/AI)",
            quotedPrice = 11500.0,
            status = ServiceStatus.IN_PROGRESS,
            notes = "Printing batch in progress on DTF machine at studio."
        ),
        DigitalServiceRequest(
            id = "DS-702",
            serviceTitle = "Samsung Galaxy A52 AMOLED Display Replacement",
            customerName = "Zahid Iqbal",
            customerPhone = "01688990011",
            documentInfo = "Phone received at studio counter, glass cracked",
            quotedPrice = 3200.0,
            status = ServiceStatus.PENDING,
            notes = "Original service pack display ordered from supplier."
        ),
        DigitalServiceRequest(
            id = "DS-703",
            serviceTitle = "Official PVC ID Card & Digital Ribbon Lanyard (50 pcs)",
            customerName = "Apex IT Solutions",
            customerPhone = "01911223377",
            documentInfo = "Excel employee data & passport size photos",
            quotedPrice = 4500.0,
            status = ServiceStatus.COMPLETED,
            notes = "Printed, laminated, and delivered via courier."
        )
    )

    val sampleAds = listOf(
        AdvertisementBanner(
            id = "ad_1",
            title = "Eid Special Custom Mug & Frame Offer",
            position = AdPosition.HOMEPAGE_MIDDLE,
            imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=800&q=80",
            redirectLink = "https://wa.me/8801632042441",
            clicks = 78,
            impressions = 2150,
            isActive = true
        ),
        AdvertisementBanner(
            id = "ad_2",
            title = "100% Original Mobile Displays & Battery - 30 Day Warranty",
            position = AdPosition.FOOTER_STICKY,
            imageUrl = "https://images.unsplash.com/photo-1597740985671-2a8a3b80532e?w=800&q=80",
            redirectLink = "https://wa.me/8801632042441",
            clicks = 52,
            impressions = 1420,
            isActive = true
        )
    )

    val sampleCoupons = listOf(
        Coupon("RONGDHONU10", 10, 500.0, isBogo = false, expiryDate = "31 Dec 2026"),
        Coupon("EID2026", 15, 1000.0, isBogo = false, expiryDate = "15 Nov 2026"),
        Coupon("BOGO2026", 50, 2000.0, isBogo = true, expiryDate = "25 Oct 2026"),
        Coupon("STUDIOVIP", 20, 1500.0, isBogo = false, expiryDate = "31 Dec 2026")
    )

    val sampleNotifications = listOf(
        AdminNotification("notif_1", "New Order #RS-98211", "Nusrat Jahan placed a COD order of ৳470", NotificationType.NEW_ORDER, System.currentTimeMillis() - 1000 * 3600 * 2),
        AdminNotification("notif_2", "bKash Payment Received", "TrxID: BKH9281729A for Order #RS-98210 (৳942)", NotificationType.PAYMENT, System.currentTimeMillis() - 1000 * 3600 * 5),
        AdminNotification("notif_3", "Low Stock Alert", "Full HD Smart Mini Projector stock is down to 6 units!", NotificationType.LOW_STOCK, System.currentTimeMillis() - 1000 * 3600 * 12),
        AdminNotification("notif_4", "New VIP Customer", "MD Shakil Hossain upgraded to VIP Gold status", NotificationType.NEW_CUSTOMER, System.currentTimeMillis() - 1000 * 3600 * 24)
    )

    val sampleAuditLogs = listOf(
        AdminAuditLog("log_1", "Super Admin", AdminRole.OWNER, "Updated order #RS-98210 status to PROCESSING", System.currentTimeMillis() - 1000 * 3600 * 3),
        AdminAuditLog("log_2", "Super Admin", AdminRole.OWNER, "Verified bKash payment for #RS-98210 (TrxID: BKH9281729A)", System.currentTimeMillis() - 1000 * 3600 * 4),
        AdminAuditLog("log_3", "Manager", AdminRole.MANAGER, "Added new promotional coupon: BOGO2026", System.currentTimeMillis() - 1000 * 3600 * 8),
        AdminAuditLog("log_4", "Super Admin", AdminRole.OWNER, "Updated stock count for RD-TSH-001 (+20 units)", System.currentTimeMillis() - 1000 * 3600 * 26)
    )

    val defaultStoreSettings = StoreSettings()
}

