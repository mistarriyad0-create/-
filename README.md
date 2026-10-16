# Rongdhonu Shop — Complete E-Commerce Platform

**Brand Name:** Rongdhonu Shop  
**Parent Organization:** Rongdhonu Digital Studio  
**Official WhatsApp & Hotline:** `01632042441`  
**Official Email:** `rongdhonustudio00@gmail.com`  
**Target Market:** Bangladesh  
**Languages:** বাংলা ও English (Bilingual)  
**Currency:** Bangladeshi Taka (৳ BDT)  
**Timezone:** Asia/Dhaka  

---

## 🌟 Executive Summary & Features

Rongdhonu Shop is a high-performance, modern e-commerce mobile application and platform developed for **Rongdhonu Digital Studio**. It integrates custom printing services (photo T-shirts, mugs, PVC ID cards), electronics, mobile servicing, fashion, and daily essentials with native speed, Daraz-like visual hierarchy, and full administrative controls.

### 🛍️ Customer Shopping Experience
1. **Dynamic Homepage:**
   - Interactive hero banner carousel with real-time promo tags
   - 15 category quick-access pills with custom icons
   - Flash Sale (⚡ ফ্ল্যাশ সেল) with countdown timer badge and discount percentages
   - Digital Studio spotlight for custom printing & repair services
   - Verified customer reviews section
   - Marquee announcement ticker with hotline and delivery updates

2. **Full Product Details & Customization:**
   - Image display with discount badges (`SAVE %`) and low stock alerts
   - Dual-language names and descriptions (বাংলা ও English)
   - Variant selector (Sizes: M, L, XL; Colors; Models)
   - Quantity counters with real-time stock limits
   - Delivery time estimates and policy badges
   - **Direct WhatsApp Order:** Pre-fills order details (SKU, title, quantity, price) and opens chat with `01632042441`

3. **Cart & Checkout System:**
   - Variant management, item removal, and quantity adjustment
   - Location-based delivery charge: Inside Dhaka (`৳60`), Outside Dhaka (`৳120`)
   - Coupon codes support: `RONGDHONU10` (10% off) & `EID2026` (15% off)
   - Checkout form: Name, Phone (valid 11-digit check), Street Address, District, Thana, Special Notes
   - Payment methods:
     - **Cash on Delivery (COD)** across all 64 districts
     - **bKash:** Instructions to send money to `01632042441` with Transaction ID (TrxID) input
     - **Nagad:** Instructions with TrxID input
     - **WhatsApp Direct Order:** One-tap order submission

4. **Order Tracking & History:**
   - Real-time status badges: `Pending`, `Confirmed`, `Processing`, `Shipped`, `Delivered`, `Cancelled`
   - Visual 4-step progress stepper
   - Courier partner and tracking number display (Steadfast, Pathao, RedX)
   - WhatsApp Support integration directly linked to the specific Order ID

5. **Advanced Admin Panel (`/admin`):**
   - **Multi-Role Security:** Protected by admin passcode (`rongdhonu123`). Supports `OWNER`, `MANAGER`, `ORDER_MANAGER`, and `STAFF` permission levels with audit logging.
   - **Real-Time Dashboard:** Live online visitors counter, interactive period filters (`Today`, `Weekly`, `Monthly`, `Yearly`, `All Time`), Total Revenue, Net Profit calculation (Revenue - Cost of Goods - Operating Expenses), and Low Stock / Out of Stock warning center.
   - **Smart Product Control:** Full product attributes (Cost Price, Selling Price, SKU, Barcode, Brand, Video link, Warranty, Expiry Date, Publication Status: `Published`/`Draft`/`Scheduled`), alongside bulk CSV Import and Export capabilities.
   - **Logistics & Advanced Orders:** Comprehensive status pipeline (`Pending`, `Confirmed`, `Processing`, `Shipped`, `Delivered`, `Cancelled`, `Returned`), courier assignment (Steadfast, Pathao, RedX, Sundarban) with tracking numbers, formatted printable/shareable invoices, 1-click WhatsApp order update dispatch, COD phone confirmation toggle, and partial refund/exchange processing.
   - **Customer & Loyalty Management:** Customer profiles with tiers (`NEW`, `REGULAR`, `VIP Gold`), loyalty points, lifetime spend, customer-specific discounts, data privacy masking toggle, and customer support ticket resolution with WhatsApp replies.
   - **Marketing & Ads:** Banner slider control, Flash Sale & Coupon manager (with BOGO support), and dedicated Advertisement management tracking impressions, clicks, and placements.
   - **Finance & Expenses:** Detailed ledger recording operating expenses (Packaging, Raw Materials, Courier fees, Utility) and generating real-time profitability analytics.
   - **Digital Service Desk:** Processing custom DTF printing requests, personalized photo mugs, PVC ID card batches, and smartphone hardware servicing with WhatsApp client updates.
   - **Platform & System Settings:** Maintenance mode toggle, SEO Meta titles and descriptions, delivery fee tuning, and two-factor authentication support.

---

## 🗄️ Database Schema & Backend (Supabase)

The complete SQL database schema with Row Level Security (RLS) is provided in `supabase_schema.sql` at the root directory.

### Core Tables Included:
- `profiles` & `user_roles`: Customer and role-based admin access control
- `categories`: 15 categories with multilingual labels
- `products` & `product_variants`: SKU, barcode, cost price, stock, and variants
- `carts` & `cart_items`: User shopping sessions
- `wishlists` & `addresses`: Saved favorites and delivery addresses
- `orders` & `order_items`: Complete order lifecycle records
- `coupons` & `banners`: Promotional marketing tools
- `reviews` & `audit_logs`: Social proof and admin activity audit
- `expenses`: Business expense ledger for real-time net profit calculation
- `support_tickets`: Customer complaints, exchange requests, and resolutions
- `digital_services`: Custom printing and technical servicing workflows
- `advertisements`: In-app and web advertising tracking impressions and clicks
- `site_settings`: Global store settings, SEO, and maintenance mode

---

## 📱 Android App Architecture & Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Image Loading:** Coil Compose (`io.coil-kt:coil-compose`)
- **State Management:** MVVM with Kotlin Coroutines & `StateFlow`
- **Architecture:** Unidirectional Data Flow with Repository Pattern (`RongdhonuRepository`)
- **Edge-to-Edge:** Full immersive window insets support
- **Package Name:** `com.aistudio.rongdhonushop.kvmqzt`
- **Application Label:** `Rongdhonu Shop`
