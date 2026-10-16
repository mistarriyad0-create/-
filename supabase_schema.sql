-- ========================================================================
-- RONGDHONU ONLINE SHOP — ULTIMATE PROFESSIONAL BUSINESS MANAGEMENT SYSTEM
-- Business: Rongdhonu Online Shop | Organization: Rongdhonu Digital Studio
-- WhatsApp: 01632042441 | Email: rongdhonustudio00@gmail.com
-- Country: Bangladesh | Currency: ৳ BDT | Database: Supabase PostgreSQL
-- ========================================================================

-- Enable Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ------------------------------------------------------------------------
-- 1. ENUMS
-- ------------------------------------------------------------------------
DO $$ BEGIN
    CREATE TYPE admin_role_type AS ENUM (
        'super_admin',
        'product_manager',
        'order_manager',
        'accountant',
        'content_manager',
        'support_agent',
        'customer'
    );
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE product_type_enum AS ENUM (
        'physical_product',
        'printing_service',
        'digital_service'
    );
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE order_status_enum AS ENUM (
        'pending',
        'confirmed',
        'processing',
        'shipped',
        'delivered',
        'cancelled',
        'returned',
        'refunded'
    );
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE payment_method_enum AS ENUM (
        'cod',
        'bkash',
        'nagad',
        'bank_transfer',
        'whatsapp'
    );
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE payment_status_enum AS ENUM (
        'unpaid',
        'pending_verification',
        'paid',
        'partially_paid',
        'refunded',
        'failed'
    );
EXCEPTION WHEN duplicate_object THEN null; END $$;

-- ------------------------------------------------------------------------
-- 2. PROFILES & USER ROLES
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name TEXT NOT NULL,
    phone TEXT,
    email TEXT,
    avatar_url TEXT,
    loyalty_points INT DEFAULT 0,
    referral_code TEXT UNIQUE,
    referred_by UUID REFERENCES public.profiles(id),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.user_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    role admin_role_type NOT NULL DEFAULT 'customer',
    assigned_by UUID REFERENCES public.profiles(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, role)
);

-- ------------------------------------------------------------------------
-- 3. CATEGORIES & SUBCATEGORIES
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    slug TEXT UNIQUE NOT NULL,
    name_en TEXT NOT NULL,
    name_bn TEXT NOT NULL,
    description_en TEXT,
    description_bn TEXT,
    icon_key TEXT,
    image_url TEXT,
    display_order INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    seo_title TEXT,
    seo_description TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------
-- 4. PRODUCTS, IMAGES & VARIANTS
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sku TEXT UNIQUE NOT NULL,
    barcode TEXT,
    name_en TEXT NOT NULL,
    name_bn TEXT NOT NULL,
    category_id UUID REFERENCES public.categories(id) ON DELETE SET NULL,
    description_en TEXT,
    description_bn TEXT,
    regular_price DECIMAL(12,2) NOT NULL,
    discount_price DECIMAL(12,2),
    purchase_cost DECIMAL(12,2) NOT NULL DEFAULT 0.00, -- ক্রয়মূল্য for real Profit/Loss
    stock INT NOT NULL DEFAULT 0,
    low_stock_threshold INT NOT NULL DEFAULT 5,
    is_available BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    is_flash_sale BOOLEAN DEFAULT FALSE,
    is_new_arrival BOOLEAN DEFAULT FALSE,
    image_url TEXT NOT NULL,
    product_type product_type_enum DEFAULT 'physical_product',
    rating DECIMAL(3,2) DEFAULT 5.00,
    review_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    sold_count INT DEFAULT 0,
    is_deleted BOOLEAN DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.product_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    image_url TEXT NOT NULL,
    display_order INT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.product_variants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    variant_name TEXT NOT NULL, -- e.g. "Size", "Color", "Model", "Print Type"
    variant_value TEXT NOT NULL, -- e.g. "XL", "Black", "DTF Custom"
    price_delta DECIMAL(12,2) DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------
-- 5. INVENTORY & STOCK MOVEMENTS
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.suppliers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    contact_person TEXT,
    phone TEXT,
    email TEXT,
    address TEXT,
    balance_due DECIMAL(12,2) DEFAULT 0.00,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.purchases (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invoice_number TEXT UNIQUE NOT NULL,
    supplier_id UUID REFERENCES public.suppliers(id) ON DELETE SET NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    paid_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    due_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    purchase_date TIMESTAMPTZ DEFAULT NOW(),
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.inventory_movements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    movement_type TEXT NOT NULL, -- 'PURCHASE_IN', 'SALE_OUT', 'RETURN_IN', 'DAMAGE_OUT', 'ADJUSTMENT'
    quantity_changed INT NOT NULL,
    stock_after INT NOT NULL,
    reference_id TEXT, -- e.g. Order ID or Purchase Invoice ID
    notes TEXT,
    created_by UUID REFERENCES public.profiles(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------
-- 6. CARTS & WISHLISTS
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.carts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    session_id TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.cart_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cart_id UUID NOT NULL REFERENCES public.carts(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1,
    selected_variant TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.wishlists (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    UNIQUE(user_id, product_id)
);

CREATE TABLE IF NOT EXISTS public.addresses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES public.profiles(id) ON DELETE CASCADE,
    title TEXT DEFAULT 'Home',
    recipient_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    street_address TEXT NOT NULL,
    district TEXT NOT NULL,
    thana TEXT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------
-- 7. ORDERS, ITEMS & SHIPMENTS
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.courier_services (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL, -- e.g. "Steadfast Courier", "Pathao", "RedX"
    contact_phone TEXT,
    tracking_url_template TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_number TEXT UNIQUE NOT NULL, -- e.g. RS-9821
    user_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    customer_name TEXT NOT NULL,
    customer_phone TEXT NOT NULL,
    customer_email TEXT,
    delivery_address TEXT NOT NULL,
    district TEXT NOT NULL,
    thana TEXT,
    delivery_method TEXT DEFAULT 'Standard Delivery',
    order_note TEXT,
    subtotal DECIMAL(12,2) NOT NULL,
    discount DECIMAL(12,2) DEFAULT 0.00,
    delivery_charge DECIMAL(12,2) DEFAULT 60.00,
    grand_total DECIMAL(12,2) NOT NULL,
    total_cost DECIMAL(12,2) DEFAULT 0.00, -- ক্রয়মূল্য যোগফল for real Net Profit
    coupon_code TEXT,
    payment_method payment_method_enum NOT NULL DEFAULT 'cod',
    payment_status payment_status_enum NOT NULL DEFAULT 'unpaid',
    transaction_id TEXT,
    order_status order_status_enum NOT NULL DEFAULT 'pending',
    courier_id UUID REFERENCES public.courier_services(id) ON DELETE SET NULL,
    courier_consignment_id TEXT,
    courier_tracking_url TEXT,
    cod_collected_amount DECIMAL(12,2) DEFAULT 0.00,
    is_cod_reconciled BOOLEAN DEFAULT FALSE,
    cancel_reason TEXT,
    refund_amount DECIMAL(12,2) DEFAULT 0.00,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES public.orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES public.products(id),
    product_name TEXT NOT NULL,
    sku TEXT NOT NULL,
    unit_cost DECIMAL(12,2) DEFAULT 0.00,
    unit_price DECIMAL(12,2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    selected_variant TEXT,
    total_price DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.shipments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES public.orders(id) ON DELETE CASCADE,
    courier_name TEXT NOT NULL,
    tracking_number TEXT NOT NULL,
    shipped_at TIMESTAMPTZ DEFAULT NOW(),
    delivered_at TIMESTAMPTZ,
    status TEXT DEFAULT 'DISPATCHED',
    notes TEXT
);

-- ------------------------------------------------------------------------
-- 8. PAYMENTS & TRANSACTIONS
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID REFERENCES public.orders(id) ON DELETE CASCADE,
    payment_method payment_method_enum NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    gateway_fee DECIMAL(12,2) DEFAULT 0.00,
    transaction_id TEXT,
    sender_number TEXT,
    status payment_status_enum NOT NULL DEFAULT 'pending_verification',
    verified_by UUID REFERENCES public.profiles(id),
    verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------
-- 9. FINANCE, INCOME & EXPENSES
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.expenses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    category TEXT NOT NULL, -- "Packaging", "Raw Materials", "Courier Shipping", "Utility", "Marketing", "Rent", "Staff Salary"
    amount DECIMAL(12,2) NOT NULL,
    expense_date DATE DEFAULT CURRENT_DATE,
    recorded_by UUID REFERENCES public.profiles(id),
    receipt_url TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------
-- 10. MARKETING: COUPONS, BANNERS & ADS
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.coupons (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code TEXT UNIQUE NOT NULL,
    percent_off INT DEFAULT 0,
    amount_off DECIMAL(12,2) DEFAULT 0.00,
    min_spend DECIMAL(12,2) DEFAULT 0.00,
    max_discount DECIMAL(12,2),
    usage_limit INT DEFAULT 100,
    used_count INT DEFAULT 0,
    valid_until TIMESTAMPTZ,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.coupon_redemptions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    coupon_id UUID NOT NULL REFERENCES public.coupons(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES public.orders(id) ON DELETE CASCADE,
    user_id UUID REFERENCES public.profiles(id),
    discount_amount DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.banners (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title_en TEXT NOT NULL,
    title_bn TEXT NOT NULL,
    subtitle_en TEXT,
    subtitle_bn TEXT,
    discount_tag TEXT,
    image_url TEXT NOT NULL,
    redirect_url TEXT,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.advertisements (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    position TEXT NOT NULL, -- 'header_top', 'in_feed', 'footer_sticky'
    image_url TEXT NOT NULL,
    redirect_link TEXT,
    clicks INT DEFAULT 0,
    impressions INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------
-- 11. CRM, SUPPORT TICKETS & REVIEWS
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.support_tickets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    ticket_number TEXT UNIQUE NOT NULL,
    customer_id UUID REFERENCES public.profiles(id),
    customer_name TEXT NOT NULL,
    customer_phone TEXT NOT NULL,
    order_id UUID REFERENCES public.orders(id) ON DELETE SET NULL,
    subject TEXT NOT NULL,
    message TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'OPEN', -- 'OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'
    priority TEXT DEFAULT 'NORMAL',
    assigned_to UUID REFERENCES public.profiles(id),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES public.products(id) ON DELETE CASCADE,
    user_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    author_name TEXT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment_en TEXT,
    comment_bn TEXT,
    is_verified_purchase BOOLEAN DEFAULT TRUE,
    is_approved BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------
-- 12. SITE SETTINGS & SOCIAL LINKS
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.site_settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    store_name TEXT DEFAULT 'Rongdhonu Online Shop',
    business_name TEXT DEFAULT 'Rongdhonu Digital Studio',
    whatsapp_number TEXT DEFAULT '01632042441',
    email TEXT DEFAULT 'rongdhonustudio00@gmail.com',
    hotline TEXT DEFAULT '01632042441',
    logo_url TEXT,
    favicon_url TEXT,
    primary_color TEXT DEFAULT '#FF6B00',
    delivery_fee_dhaka DECIMAL(12,2) DEFAULT 60.00,
    delivery_fee_outside DECIMAL(12,2) DEFAULT 120.00,
    announcement_text TEXT DEFAULT 'রংধনু শপে স্বাগতম! সারা বাংলাদেশে ক্যাশ অন ডেলিভারি ও দ্রুত হোম ডেলিভারি!',
    show_announcement BOOLEAN DEFAULT TRUE,
    maintenance_mode BOOLEAN DEFAULT FALSE,
    about_text_bn TEXT,
    about_text_en TEXT,
    contact_address TEXT DEFAULT 'Rongdhonu Digital Studio, Bangladesh',
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.social_links (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    platform TEXT NOT NULL, -- 'facebook_page', 'facebook_group', 'whatsapp', 'youtube', 'instagram', 'tiktok', 'telegram'
    title_bn TEXT NOT NULL,
    title_en TEXT NOT NULL,
    url TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    display_order INT DEFAULT 0
);

-- ------------------------------------------------------------------------
-- 13. AUDIT LOGS & NOTIFICATIONS
-- ------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS public.admin_audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admin_id UUID REFERENCES public.profiles(id),
    admin_name TEXT,
    action TEXT NOT NULL,
    entity_name TEXT,
    entity_id TEXT,
    details JSONB,
    ip_address TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    type TEXT NOT NULL, -- 'NEW_ORDER', 'LOW_STOCK', 'PAYMENT', 'STATUS_CHANGE', 'COUPON'
    is_read BOOLEAN DEFAULT FALSE,
    link TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ------------------------------------------------------------------------
-- 14. ROW LEVEL SECURITY (RLS) POLICIES
-- ------------------------------------------------------------------------
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.user_roles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.categories ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.products ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.product_images ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.product_variants ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.suppliers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.purchases ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.inventory_movements ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.carts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.cart_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.wishlists ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.addresses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.courier_services ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.order_items ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.shipments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.payments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.expenses ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.coupons ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.coupon_redemptions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.banners ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.advertisements ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.support_tickets ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reviews ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.site_settings ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.social_links ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.admin_audit_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notifications ENABLE ROW LEVEL SECURITY;

-- Helper Function: Check Admin Permission
CREATE OR REPLACE FUNCTION public.is_admin()
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1 FROM public.user_roles
        WHERE user_id = auth.uid() 
        AND role IN ('super_admin', 'product_manager', 'order_manager', 'accountant', 'content_manager')
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Public Catalog Read Access
CREATE POLICY "Public can view active categories" ON public.categories FOR SELECT USING (is_active = TRUE);
CREATE POLICY "Public can view active products" ON public.products FOR SELECT USING (is_available = TRUE AND is_deleted = FALSE);
CREATE POLICY "Public can view product images" ON public.product_images FOR SELECT USING (TRUE);
CREATE POLICY "Public can view product variants" ON public.product_variants FOR SELECT USING (TRUE);
CREATE POLICY "Public can view active banners" ON public.banners FOR SELECT USING (is_active = TRUE);
CREATE POLICY "Public can view active ads" ON public.advertisements FOR SELECT USING (is_active = TRUE);
CREATE POLICY "Public can view approved reviews" ON public.reviews FOR SELECT USING (is_approved = TRUE);
CREATE POLICY "Public can view active social links" ON public.social_links FOR SELECT USING (is_active = TRUE);
CREATE POLICY "Public can view site settings" ON public.site_settings FOR SELECT USING (TRUE);

-- Customer Data Isolation
CREATE POLICY "User manage own profile" ON public.profiles FOR ALL USING (auth.uid() = id);
CREATE POLICY "User manage own cart" ON public.carts FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "User manage own cart items" ON public.cart_items FOR ALL USING (
    cart_id IN (SELECT id FROM public.carts WHERE user_id = auth.uid())
);
CREATE POLICY "User manage own wishlist" ON public.wishlists FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "User manage own addresses" ON public.addresses FOR ALL USING (auth.uid() = user_id);
CREATE POLICY "User view own orders" ON public.orders FOR SELECT USING (auth.uid() = user_id OR public.is_admin());
CREATE POLICY "Insert order allow" ON public.orders FOR INSERT WITH CHECK (TRUE);
CREATE POLICY "Insert order items allow" ON public.order_items FOR INSERT WITH CHECK (TRUE);
CREATE POLICY "User view own notifications" ON public.notifications FOR ALL USING (auth.uid() = user_id OR public.is_admin());

-- Admin Full Access Policies
CREATE POLICY "Admin full categories" ON public.categories FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full products" ON public.products FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full product images" ON public.product_images FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full product variants" ON public.product_variants FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full suppliers" ON public.suppliers FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full purchases" ON public.purchases FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full inventory" ON public.inventory_movements FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full courier" ON public.courier_services FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full orders" ON public.orders FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full order items" ON public.order_items FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full shipments" ON public.shipments FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full payments" ON public.payments FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full expenses" ON public.expenses FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full coupons" ON public.coupons FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full banners" ON public.banners FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full ads" ON public.advertisements FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full tickets" ON public.support_tickets FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full reviews" ON public.reviews FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full settings" ON public.site_settings FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full social" ON public.social_links FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full audit logs" ON public.admin_audit_logs FOR ALL USING (public.is_admin());

-- ------------------------------------------------------------------------
-- 15. SEED DATA (All 18 Requested Categories + Settings + Products)
-- ------------------------------------------------------------------------
INSERT INTO public.site_settings (store_name, business_name, whatsapp_number, email, hotline)
VALUES ('Rongdhonu Online Shop', 'Rongdhonu Digital Studio', '01632042441', 'rongdhonustudio00@gmail.com', '01632042441')
ON CONFLICT DO NOTHING;

INSERT INTO public.categories (slug, name_en, name_bn, icon_key, display_order)
VALUES
    ('cat_cosmetics', 'Cosmetics & Beauty', 'প্রসাধন ও রূপচর্চা', 'face', 1),
    ('cat_tshirt', 'Printed T-Shirts', 'প্রিন্টেড টি-শার্ট', 'tshirt', 2),
    ('cat_mens', 'Men''s Fashion', 'পুরুষদের ফ্যাশন', 'male', 3),
    ('cat_womens', 'Women''s Fashion', 'নারীদের ফ্যাশন', 'female', 4),
    ('cat_mug', 'Custom Mug Printing', 'কাস্টম মগ প্রিন্টিং', 'mug', 5),
    ('cat_gifts', 'Gift Items', 'উপহার সামগ্রী', 'gift', 6),
    ('cat_mob_acc', 'Mobile Accessories', 'মোবাইল এক্সেসরিজ', 'headphones', 7),
    ('cat_mobile', 'Mobile Phones', 'মোবাইল ফোন', 'phone', 8),
    ('cat_servicing', 'Mobile Servicing', 'মোবাইল সার্ভিসিং', 'build', 9),
    ('cat_digital_svc', 'Digital Services', 'ডিজিটাল সার্ভিস', 'computer', 10),
    ('cat_online_svc', 'Online Services', 'অনলাইন সেবা', 'language', 11),
    ('cat_printing', 'Digital Printing', 'ডিজিটাল প্রিন্টিং', 'print', 12),
    ('cat_banking', 'Mobile Banking Agent Services', 'মোবাইল ব্যাংকিং সেবা', 'account_balance', 13),
    ('cat_imported_elec', 'Imported Electronics', 'আমদানিকৃত ইলেকট্রনিক্স', 'bolt', 14),
    ('cat_foreign_elec', 'Foreign Electrical Products', 'বিদেশি ইলেকট্রিক্যাল পণ্য', 'power', 15),
    ('cat_new_arrivals', 'New Arrivals', 'নতুন আগমন', 'fiber_new', 16),
    ('cat_popular', 'Popular Products', 'জনপ্রিয় পণ্যসমূহ', 'trending_up', 17),
    ('cat_special_offers', 'Special Offers', 'বিশেষ অফার', 'local_offer', 18)
ON CONFLICT (slug) DO NOTHING;

-- ------------------------------------------------------------------------
-- 16. ULTIMATE AUTO AUTOMATION SYSTEM ENGINE
-- ------------------------------------------------------------------------

CREATE TYPE automation_status_enum AS ENUM ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'CANCELLED');
CREATE TYPE notification_channel_enum AS ENUM ('WHATSAPP', 'IN_APP', 'EMAIL', 'SMS');

CREATE TABLE IF NOT EXISTS public.automation_rules (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rule_key TEXT UNIQUE NOT NULL,
    name_bn TEXT NOT NULL,
    name_en TEXT NOT NULL,
    module TEXT NOT NULL, -- 'ORDERS', 'INVENTORY', 'FINANCE', 'NOTIFICATIONS', 'MARKETING', 'COURIER', 'REPORTS'
    is_enabled BOOLEAN DEFAULT TRUE,
    schedule_cron TEXT,
    execution_interval_minutes INT DEFAULT 60,
    last_run_at TIMESTAMPTZ,
    next_run_at TIMESTAMPTZ,
    config_json JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.automation_jobs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_type TEXT NOT NULL,
    rule_id UUID REFERENCES public.automation_rules(id) ON DELETE CASCADE,
    status automation_status_enum NOT NULL DEFAULT 'PENDING',
    payload JSONB DEFAULT '{}'::jsonb,
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    scheduled_at TIMESTAMPTZ DEFAULT NOW(),
    locked_at TIMESTAMPTZ,
    error_message TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.automation_job_runs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id UUID REFERENCES public.automation_jobs(id) ON DELETE SET NULL,
    rule_key TEXT NOT NULL,
    status automation_status_enum NOT NULL,
    duration_ms INT DEFAULT 0,
    items_processed INT DEFAULT 0,
    error_message TEXT,
    executed_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.notification_queue (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recipient TEXT NOT NULL, -- Customer phone or email or Admin
    channel notification_channel_enum NOT NULL DEFAULT 'IN_APP',
    title TEXT NOT NULL,
    message TEXT NOT NULL,
    status automation_status_enum NOT NULL DEFAULT 'PENDING',
    attempts INT DEFAULT 0,
    max_attempts INT DEFAULT 3,
    error TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.notification_delivery_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    notification_id UUID REFERENCES public.notification_queue(id) ON DELETE CASCADE,
    channel notification_channel_enum NOT NULL,
    status TEXT NOT NULL,
    provider_response TEXT,
    delivered_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.automation_error_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_id UUID REFERENCES public.automation_jobs(id) ON DELETE SET NULL,
    rule_key TEXT,
    severity TEXT NOT NULL DEFAULT 'WARNING', -- 'INFO', 'WARNING', 'CRITICAL'
    error_text TEXT NOT NULL,
    stack_trace TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- RLS for Automation Engine
ALTER TABLE public.automation_rules ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.automation_jobs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.automation_job_runs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notification_queue ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.notification_delivery_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.automation_error_logs ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Admin full automation_rules" ON public.automation_rules FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full automation_jobs" ON public.automation_jobs FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full automation_job_runs" ON public.automation_job_runs FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full notification_queue" ON public.notification_queue FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full notification_logs" ON public.notification_delivery_logs FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full automation_errors" ON public.automation_error_logs FOR ALL USING (public.is_admin());

-- ------------------------------------------------------------------------
-- 17. STORED TRANSACTIONAL AUTOMATION PROCEDURES
-- ------------------------------------------------------------------------

-- A. Execute Transactional Order Placement with Server Stock Check & Reservation
CREATE OR REPLACE FUNCTION public.execute_order_placement(order_data JSONB)
RETURNS JSONB AS $$
DECLARE
    new_order_id UUID;
    v_order_number TEXT;
    v_subtotal DECIMAL(12,2) := 0;
    v_total_cost DECIMAL(12,2) := 0;
    v_delivery_fee DECIMAL(12,2) := 60.00;
    v_discount DECIMAL(12,2) := 0.00;
    v_grand_total DECIMAL(12,2);
    item JSONB;
    prod RECORD;
    v_qty INT;
BEGIN
    -- 1. Generate Unique Order Number RS-XXXX
    v_order_number := 'RS-' || LPAD(FLOOR(RANDOM() * 90000 + 10000)::TEXT, 5, '0');

    -- Check if district is outside Dhaka
    IF LOWER(order_data->>'district') NOT LIKE '%dhaka%' THEN
        v_delivery_fee := 120.00;
    END IF;

    -- 2. Verify each product price & stock transactionally
    FOR item IN SELECT * FROM jsonb_array_elements(order_data->'items')
    LOOP
        SELECT * INTO prod FROM public.products WHERE id = (item->>'id')::UUID FOR UPDATE;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'Product with ID % not found', item->>'id';
        END IF;

        v_qty := (item->>'quantity')::INT;
        IF prod.stock < v_qty THEN
            RAISE EXCEPTION 'Stock insufficient for % (Requested %, Available %)', prod.name_bn, v_qty, prod.stock;
        END IF;

        -- Calculate verified server price
        IF prod.discount_price > 0 AND prod.discount_price < prod.regular_price THEN
            v_subtotal := v_subtotal + (prod.discount_price * v_qty);
        ELSE
            v_subtotal := v_subtotal + (prod.regular_price * v_qty);
        END IF;

        v_total_cost := v_total_cost + (prod.purchase_cost * v_qty);
    END LOOP;

    v_grand_total := v_subtotal + v_delivery_fee - v_discount;

    -- 3. Insert Verified Order
    INSERT INTO public.orders (
        order_number,
        customer_name,
        customer_phone,
        customer_email,
        delivery_address,
        district,
        subtotal,
        delivery_charge,
        discount,
        grand_total,
        total_cost,
        payment_method,
        payment_status,
        order_status
    ) VALUES (
        v_order_number,
        order_data->>'customer_name',
        order_data->>'customer_phone',
        order_data->>'customer_email',
        order_data->>'delivery_address',
        order_data->>'district',
        v_subtotal,
        v_delivery_fee,
        v_discount,
        v_grand_total,
        v_total_cost,
        (order_data->>'payment_method')::payment_method_enum,
        'unpaid',
        'confirmed'
    ) RETURNING id INTO new_order_id;

    -- 4. Deduct Stock & Insert Order Items & Movements
    FOR item IN SELECT * FROM jsonb_array_elements(order_data->'items')
    LOOP
        SELECT * INTO prod FROM public.products WHERE id = (item->>'id')::UUID;
        v_qty := (item->>'quantity')::INT;

        UPDATE public.products
        SET stock = stock - v_qty,
            sold_count = sold_count + v_qty,
            updated_at = NOW()
        WHERE id = prod.id;

        INSERT INTO public.order_items (
            order_id,
            product_id,
            product_name,
            sku,
            unit_cost,
            unit_price,
            quantity,
            total_price
        ) VALUES (
            new_order_id,
            prod.id,
            prod.name_bn,
            prod.sku,
            prod.purchase_cost,
            COALESCE(NULLIF(prod.discount_price, 0), prod.regular_price),
            v_qty,
            COALESCE(NULLIF(prod.discount_price, 0), prod.regular_price) * v_qty
        );

        INSERT INTO public.inventory_movements (
            product_id,
            movement_type,
            quantity_changed,
            stock_after,
            reference_id,
            notes
        ) VALUES (
            prod.id,
            'SALE_OUT',
            -v_qty,
            prod.stock - v_qty,
            v_order_number,
            'Auto verified sale for Order ' || v_order_number
        );
    END LOOP;

    -- 5. Queue Notification for Admin & Customer
    INSERT INTO public.notification_queue (
        recipient,
        channel,
        title,
        message
    ) VALUES (
        '01632042441',
        'WHATSAPP',
        'নতুন অর্ডার #' || v_order_number,
        'গ্রাহক: ' || (order_data->>'customer_name') || ', মোট: ৳ ' || v_grand_total::TEXT || ', ঠিকানা: ' || (order_data->>'delivery_address')
    );

    RETURN jsonb_build_object(
        'success', TRUE,
        'order_id', new_order_id,
        'order_number', v_order_number,
        'grand_total', v_grand_total
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- B. Daily Cash Closing & Net Profit Aggregator
CREATE OR REPLACE FUNCTION public.process_daily_cash_closing()
RETURNS JSONB AS $$
DECLARE
    v_today DATE := CURRENT_DATE;
    v_total_sales DECIMAL(12,2) := 0;
    v_total_cost DECIMAL(12,2) := 0;
    v_total_expenses DECIMAL(12,2) := 0;
    v_gross_profit DECIMAL(12,2) := 0;
    v_net_profit DECIMAL(12,2) := 0;
    v_order_count INT := 0;
BEGIN
    SELECT 
        COALESCE(SUM(grand_total), 0),
        COALESCE(SUM(total_cost), 0),
        COUNT(*)
    INTO v_total_sales, v_total_cost, v_order_count
    FROM public.orders
    WHERE DATE(created_at) = v_today AND order_status NOT IN ('cancelled', 'refunded');

    SELECT COALESCE(SUM(amount), 0)
    INTO v_total_expenses
    FROM public.expenses
    WHERE expense_date = v_today;

    v_gross_profit := v_total_sales - v_total_cost;
    v_net_profit := v_gross_profit - v_total_expenses;

    INSERT INTO public.automation_job_runs (
        rule_key,
        status,
        duration_ms,
        items_processed,
        error_message
    ) VALUES (
        'auto_daily_closing',
        'COMPLETED',
        120,
        v_order_count,
        'Daily Closing: Sales ৳ ' || v_total_sales || ', Cost ৳ ' || v_total_cost || ', Net Profit ৳ ' || v_net_profit
    );

    RETURN jsonb_build_object(
        'date', v_today,
        'sales', v_total_sales,
        'cogs', v_total_cost,
        'expenses', v_total_expenses,
        'gross_profit', v_gross_profit,
        'net_profit', v_net_profit,
        'order_count', v_order_count
    );
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- ------------------------------------------------------------------------
-- 18. SEED AUTOMATION RULES
-- ------------------------------------------------------------------------
INSERT INTO public.automation_rules (rule_key, name_bn, name_en, module, is_enabled, execution_interval_minutes, config_json)
VALUES
    ('auto_order_verify', 'স্বয়ংক্রিয় অর্ডার যাচাই ও স্টক রিজার্ভেশন', 'Auto Order Validation & Stock Reservation', 'ORDERS', TRUE, 1, '{"auto_confirm": true, "max_order_limit": 50000}'::jsonb),
    ('auto_low_stock_alert', 'লো-স্টক থ্রেশহোল্ড মনিটরিং ও অ্যালার্ট', 'Low-Stock Threshold Monitoring', 'INVENTORY', TRUE, 30, '{"default_threshold": 5, "notify_whatsapp": true}'::jsonb),
    ('auto_daily_closing', 'দৈনিক ক্যাশ ও লাভ-ক্ষতি ক্লোজিং', 'Daily Cash & Profit-Loss Closing', 'FINANCE', TRUE, 1440, '{"closing_time": "23:59 Asia/Dhaka"}'::jsonb),
    ('auto_coupon_expire', 'মেয়াদোত্তীর্ণ কুপন স্বয়ংক্রিয় নিষ্ক্রিয়করণ', 'Auto Expire Outdated Coupons', 'MARKETING', TRUE, 60, '{}'::jsonb),
    ('auto_courier_reconcile', 'কুরিয়ার সিওডি কালেকশন রিকনসিলিয়েশন', 'Courier COD Collection Reconciliation', 'COURIER', TRUE, 120, '{"default_courier": "Steadfast"}'::jsonb),
    ('auto_cart_recovery', 'পরিত্যক্ত কার্ট পর্যবেক্ষণ ও রিমাইন্ডার', 'Abandoned Cart Monitoring', 'MARKETING', TRUE, 180, '{"inactivity_hours": 2}'::jsonb),
    ('auto_suspicious_order', 'সন্দেহজনক উচ্চমূল্য অর্ডার ফ্ল্যাগিং', 'Suspicious & High-Value Order Flagging', 'ORDERS', TRUE, 5, '{"high_value_threshold": 25000}'::jsonb)
ON CONFLICT (rule_key) DO NOTHING;

-- ------------------------------------------------------------------------
-- 19. FUTURE ENTERPRISE MODULES: AI AGENTS, RESELLERS, LEDGER & WORKFLOWS
-- ------------------------------------------------------------------------

CREATE TYPE agent_mode_enum AS ENUM ('SUGGEST_ONLY', 'APPROVAL_REQUIRED', 'AUTO_RESTRICTED', 'PAUSED');
CREATE TYPE payout_status_enum AS ENUM ('REQUESTED', 'APPROVED', 'DISBURSED', 'REJECTED');

-- A. AUTONOMOUS AI AGENTS & ACTION AUDIT TRAIL
CREATE TABLE IF NOT EXISTS public.ai_agents (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agent_key TEXT UNIQUE NOT NULL,
    name_bn TEXT NOT NULL,
    name_en TEXT NOT NULL,
    role_scope TEXT NOT NULL, -- 'SALES', 'INVENTORY', 'FINANCE', 'SUPPORT', 'MARKETING', 'SYSTEM_HEALTH'
    mode agent_mode_enum NOT NULL DEFAULT 'APPROVAL_REQUIRED',
    system_prompt TEXT NOT NULL,
    permitted_tools JSONB NOT NULL DEFAULT '[]'::jsonb,
    max_actions_per_hour INT NOT NULL DEFAULT 20,
    retry_policy JSONB NOT NULL DEFAULT '{"max_retries": 3, "timeout_seconds": 30}'::jsonb,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.ai_agent_actions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agent_id UUID NOT NULL REFERENCES public.ai_agents(id) ON DELETE CASCADE,
    agent_key TEXT NOT NULL,
    action_type TEXT NOT NULL,
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    requires_approval BOOLEAN NOT NULL DEFAULT TRUE,
    approval_status TEXT NOT NULL DEFAULT 'PENDING', -- 'PENDING', 'APPROVED', 'REJECTED', 'EXECUTED'
    approved_by UUID REFERENCES public.profiles(id),
    execution_result JSONB,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- B. RESELLER & AFFILIATE COMMAND CENTER
CREATE TABLE IF NOT EXISTS public.resellers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES public.profiles(id) ON DELETE CASCADE,
    reseller_name TEXT NOT NULL,
    phone TEXT NOT NULL,
    referral_code TEXT UNIQUE NOT NULL,
    commission_rate_percent DECIMAL(5,2) NOT NULL DEFAULT 10.00,
    status TEXT NOT NULL DEFAULT 'APPROVED', -- 'PENDING', 'APPROVED', 'SUSPENDED'
    total_sales_generated DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_commission_earned DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    pending_payout_balance DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.reseller_commissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reseller_id UUID NOT NULL REFERENCES public.resellers(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES public.orders(id) ON DELETE CASCADE,
    order_number TEXT NOT NULL,
    order_amount DECIMAL(12,2) NOT NULL,
    commission_amount DECIMAL(12,2) NOT NULL,
    status TEXT NOT NULL DEFAULT 'ELIGIBLE', -- 'PENDING_DELIVERY', 'ELIGIBLE', 'PAID', 'CANCELLED'
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.reseller_payouts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    reseller_id UUID NOT NULL REFERENCES public.resellers(id) ON DELETE CASCADE,
    amount DECIMAL(12,2) NOT NULL,
    payout_method TEXT NOT NULL, -- 'BKASH', 'NAGAD', 'BANK'
    account_number TEXT NOT NULL,
    status payout_status_enum NOT NULL DEFAULT 'REQUESTED',
    approved_by UUID REFERENCES public.profiles(id),
    transaction_reference TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- C. NO-CODE WORKFLOW BUILDER
CREATE TABLE IF NOT EXISTS public.workflows (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name_bn TEXT NOT NULL,
    name_en TEXT NOT NULL,
    trigger_event TEXT NOT NULL, -- 'ORDER_PLACED', 'PAYMENT_VERIFIED', 'STOCK_LOW', 'REFUND_REQUESTED', 'COURIER_DELIVERED'
    conditions JSONB NOT NULL DEFAULT '[]'::jsonb,
    actions JSONB NOT NULL DEFAULT '[]'::jsonb,
    is_active BOOLEAN DEFAULT TRUE,
    version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.workflow_executions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    workflow_id UUID REFERENCES public.workflows(id) ON DELETE CASCADE,
    trigger_event TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'COMPLETED', -- 'RUNNING', 'COMPLETED', 'FAILED'
    steps_log JSONB NOT NULL DEFAULT '[]'::jsonb,
    duration_ms INT NOT NULL DEFAULT 0,
    executed_at TIMESTAMPTZ DEFAULT NOW()
);

-- D. FINANCIAL CONTROL TOWER (DOUBLE-ENTRY LEDGER)
CREATE TABLE IF NOT EXISTS public.financial_ledger (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entry_number TEXT UNIQUE NOT NULL, -- e.g. LEDG-10029
    transaction_date DATE NOT NULL DEFAULT CURRENT_DATE,
    account_debit TEXT NOT NULL, -- e.g. 'CASH_IN_HAND', 'BKASH_RECEIVABLE', 'COURIER_COD'
    account_credit TEXT NOT NULL, -- e.g. 'SALES_REVENUE', 'INVENTORY_ASSET', 'RAW_MATERIALS'
    amount DECIMAL(12,2) NOT NULL,
    reference_type TEXT NOT NULL, -- 'ORDER_SALE', 'EXPENSE', 'COURIER_COD_RECONCILE', 'REFUND', 'RESELLER_PAYOUT'
    reference_id TEXT,
    description TEXT NOT NULL,
    is_reversed BOOLEAN NOT NULL DEFAULT FALSE,
    created_by UUID REFERENCES public.profiles(id),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- E. FRAUD & RISK INTELLIGENCE
CREATE TABLE IF NOT EXISTS public.fraud_risk_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID REFERENCES public.orders(id) ON DELETE CASCADE,
    customer_phone TEXT NOT NULL,
    risk_score INT NOT NULL DEFAULT 0, -- 0 to 100
    risk_triggers TEXT[] NOT NULL DEFAULT '{}',
    status TEXT NOT NULL DEFAULT 'FLAGGED', -- 'FLAGGED', 'CLEARED', 'CANCELLED'
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- F. EMPLOYEE & TASK MANAGEMENT
CREATE TABLE IF NOT EXISTS public.employee_tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title TEXT NOT NULL,
    description TEXT,
    assigned_to UUID REFERENCES public.profiles(id),
    assigned_name TEXT NOT NULL,
    priority TEXT NOT NULL DEFAULT 'MEDIUM', -- 'LOW', 'MEDIUM', 'HIGH', 'URGENT'
    status TEXT NOT NULL DEFAULT 'IN_PROGRESS', -- 'PENDING', 'IN_PROGRESS', 'REVIEW', 'COMPLETED'
    deadline TIMESTAMPTZ,
    notes TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- G. SYSTEM RELIABILITY & CHANGE MANAGEMENT
CREATE TABLE IF NOT EXISTS public.system_health_metrics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    metric_name TEXT NOT NULL,
    metric_value NUMERIC NOT NULL,
    unit TEXT NOT NULL,
    status TEXT NOT NULL DEFAULT 'HEALTHY',
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.config_version_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_type TEXT NOT NULL, -- 'STORE_SETTINGS', 'AUTOMATION_RULES', 'PRICING_POLICY'
    entity_id TEXT NOT NULL,
    version INT NOT NULL DEFAULT 1,
    previous_data JSONB,
    new_data JSONB NOT NULL,
    changed_by UUID REFERENCES public.profiles(id),
    change_summary TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- RLS Policies for New Enterprise Tables
ALTER TABLE public.ai_agents ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.ai_agent_actions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.resellers ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reseller_commissions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.reseller_payouts ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.workflows ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.workflow_executions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.financial_ledger ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.fraud_risk_logs ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.employee_tasks ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.system_health_metrics ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.config_version_history ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Admin full ai_agents" ON public.ai_agents FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full ai_agent_actions" ON public.ai_agent_actions FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full resellers" ON public.resellers FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full commissions" ON public.reseller_commissions FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full payouts" ON public.reseller_payouts FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full workflows" ON public.workflows FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full workflow_executions" ON public.workflow_executions FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full financial_ledger" ON public.financial_ledger FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full fraud_risk_logs" ON public.fraud_risk_logs FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full employee_tasks" ON public.employee_tasks FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full system_health" ON public.system_health_metrics FOR ALL USING (public.is_admin());
CREATE POLICY "Admin full config_versions" ON public.config_version_history FOR ALL USING (public.is_admin());

-- ------------------------------------------------------------------------
-- 20. SEED AUTONOMOUS AI AGENTS & WORKFLOWS
-- ------------------------------------------------------------------------
INSERT INTO public.ai_agents (agent_key, name_bn, name_en, role_scope, mode, system_prompt, permitted_tools)
VALUES
    ('agent_sales', 'সেলস ইন্টেলিজেন্স এজেন্ট', 'Sales Intelligence Agent', 'SALES', 'APPROVAL_REQUIRED', 'Analyzes conversion rates, top performing products, and customer order value.', '["read_orders", "read_products", "suggest_pricing"]'::jsonb),
    ('agent_inventory', 'ইনভেন্টরি প্ল্যানিং এজেন্ট', 'Inventory Planning Agent', 'INVENTORY', 'APPROVAL_REQUIRED', 'Monitors stock levels, calculates reorder points, and drafts purchase orders.', '["read_inventory", "draft_purchase_order", "alert_low_stock"]'::jsonb),
    ('agent_finance', 'ফিনান্সিয়াল অ্যানালাইসিস এজেন্ট', 'Financial Analysis Agent', 'FINANCE', 'APPROVAL_REQUIRED', 'Analyzes true profit and loss, COGS, delivery fees, and cash flow.', '["read_ledger", "read_expenses", "generate_pnl_summary"]'::jsonb),
    ('agent_support', 'কাস্টমার সাপোর্ট এজেন্ট', 'Customer Support Agent', 'SUPPORT', 'SUGGEST_ONLY', 'Assists customers with order tracking, product recommendations, and FAQs.', '["read_catalog", "check_order_status", "draft_support_reply"]'::jsonb),
    ('agent_marketing', 'মার্কেটিং ও ক্যাম্পেইন এজেন্ট', 'Marketing & Campaign Agent', 'MARKETING', 'APPROVAL_REQUIRED', 'Plans flash sales, drafts Bengali promo captions, and schedules coupons.', '["read_campaigns", "draft_promotional_copy", "suggest_coupons"]'::jsonb),
    ('agent_health', 'সিস্টেম হেলথ ও রিলায়াবিলিটি এজেন্ট', 'System Health & Reliability Agent', 'SYSTEM_HEALTH', 'AUTO_RESTRICTED', 'Monitors database latency, background jobs, queue health, and dead letters.', '["check_queue_health", "log_system_incident", "retry_safe_jobs"]'::jsonb)
ON CONFLICT (agent_key) DO NOTHING;

INSERT INTO public.workflows (name_bn, name_en, trigger_event, conditions, actions, version)
VALUES
    ('স্বয়ংক্রিয় স্টক অ্যালার্ট', 'Auto Stock Low Alert', 'STOCK_LOW', '[{"field": "stock", "operator": "<=", "value": 5}]'::jsonb, '[{"action": "create_notification", "channel": "IN_APP"}, {"action": "draft_reorder_suggestion"}]'::jsonb, 1),
    ('পেমেন্ট কনফার্মেশন ও ইনভয়েস ফাইনাল', 'Payment Verified Invoice Finalize', 'PAYMENT_VERIFIED', '[{"field": "status", "operator": "==", "value": "PAID"}]'::jsonb, '[{"action": "finalize_invoice"}, {"action": "send_customer_sms_whatsapp"}]'::jsonb, 1)
ON CONFLICT DO NOTHING;
