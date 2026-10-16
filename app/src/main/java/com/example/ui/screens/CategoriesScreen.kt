package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Category
import com.example.model.Language
import com.example.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.localization.Strings
import com.example.ui.theme.RongdhonuPrimary

enum class SortOption(val labelEn: String, val labelBn: String) {
    DEFAULT("Featured", "প্রস্তাবিত"),
    PRICE_LOW("Price: Low to High", "মূল্য: কম থেকে বেশি"),
    PRICE_HIGH("Price: High to Low", "মূল্য: বেশি থেকে কম"),
    RATING("Highest Rated", "সর্বোচ্চ রেটিং")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    language: Language,
    categories: List<Category>,
    products: List<Product>,
    selectedCategory: Category?,
    onSelectCategory: (Category) -> Unit,
    wishlist: Set<String>,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeCategory = selectedCategory ?: categories.firstOrNull()
    var sortOption by remember { mutableStateOf(SortOption.DEFAULT) }
    var inStockOnly by remember { mutableStateOf(false) }

    val categoryProducts = remember(activeCategory, products, sortOption, inStockOnly) {
        if (activeCategory == null) emptyList() else {
            var list = products.filter { it.categoryId == activeCategory.id }
            if (inStockOnly) {
                list = list.filter { it.stock > 0 }
            }
            when (sortOption) {
                SortOption.DEFAULT -> list
                SortOption.PRICE_LOW -> list.sortedBy { it.effectivePrice }
                SortOption.PRICE_HIGH -> list.sortedByDescending { it.effectivePrice }
                SortOption.RATING -> list.sortedByDescending { it.rating }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Horizontal Categories Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = activeCategory?.id == cat.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectCategory(cat) },
                        label = {
                            Text(
                                text = if (language == Language.BN) cat.nameBn else cat.nameEn,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // Sort & Filter options header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${if (language == Language.BN) activeCategory?.nameBn ?: "" else activeCategory?.nameEn ?: ""} (${categoryProducts.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // In Stock Toggle
            FilterChip(
                selected = inStockOnly,
                onClick = { inStockOnly = !inStockOnly },
                label = {
                    Text(
                        text = if (language == Language.BN) "স্টকে আছে" else "In Stock",
                        fontSize = 11.sp
                    )
                },
                modifier = Modifier.height(30.dp)
            )
        }

        // Products Grid
        if (categoryProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = "Empty",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (language == Language.BN) "এই ক্যাটাগরিতে পণ্য যুক্ত করা হচ্ছে..." else "No products found in this category",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(categoryProducts.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
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
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}
