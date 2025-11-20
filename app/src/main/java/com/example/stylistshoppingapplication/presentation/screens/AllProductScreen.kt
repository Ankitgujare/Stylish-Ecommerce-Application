package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.domain.model.ProductModel
import com.example.stylistshoppingapplication.presentation.ViewModel.ProductViewModel

@Composable
fun ViewAllProductScreen(
    productViewModel: ProductViewModel,
    navController: NavController
) {
    val uiState by productViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ViewAllTopBar(
                productCount = when (uiState) {
                    is ProductViewModel.ProductUiState.Success -> {
                        val state = uiState as ProductViewModel.ProductUiState.Success
                        (state.featuredProducts + state.trendingProducts).distinctBy { it.id }.size
                    }
                    else -> 0
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            when (uiState) {
                is ProductViewModel.ProductUiState.Loading -> {
                    LoadingState()
                }

                is ProductViewModel.ProductUiState.Error -> {
                    val error = uiState as ProductViewModel.ProductUiState.Error
                    ErrorState(
                        errorMessage = error.message,
                        onRetry = { productViewModel.fetchProducts() }
                    )
                }

                is ProductViewModel.ProductUiState.Success -> {
                    val state = uiState as ProductViewModel.ProductUiState.Success
                    val allProducts = (state.featuredProducts + state.trendingProducts).distinctBy { it.id }

                    if (allProducts.isEmpty()) {
                        EmptyProductsState()
                    } else {
                        ProductsGrid(
                            products = allProducts,
                            onProductClick = { product ->
                                // ✅ Navigate to ProductDetailScreen with ID
                                navController.navigate("product_detail/${product.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewAllTopBar(
    productCount: Int,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Suggested For You",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            Text(
                text = "$productCount Products",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(end = 16.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.Black
        )
    )
}

@Composable
private fun ProductsGrid(
    products: List<ProductModel>,
    onProductClick: (ProductModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                onProductClick = onProductClick
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF1976D2))
    }
}

@Composable
private fun ErrorState(errorMessage: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error", color = Color.Red, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Retry") }
        }
    }
}

@Composable
private fun EmptyProductsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("No products available", color = Color.Gray)
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    onProductClick: (ProductModel) -> Unit
) {
    val discountedPrice = calculateDiscountedPrice(product.price, product.discountPercentage)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = product.thumbnail,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.placeholder)
            )

            Spacer(modifier = Modifier.height(8.dp))

            product.brand?.let {
                Text(
                    text = it.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = TextUnit(18f, TextUnitType.Sp),
                modifier = Modifier.height(48.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))
            RatingSection(rating = product.rating)
            Spacer(modifier = Modifier.height(8.dp))
            PriceSection(
                originalPrice = product.price,
                discountedPrice = discountedPrice,
                discountPercentage = product.discountPercentage
            )
            ExchangeOption(discountedPrice = discountedPrice)
        }
    }
}

@Composable
private fun RatingSection(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val filledStars = rating.toInt()
        val halfStar = rating - filledStars >= 0.5
        Row {
            repeat(filledStars) {
                Icon(
                    painter = painterResource(id = R.drawable.starfilled),
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
            }
            if (halfStar) {
                Icon(
                    painter = painterResource(id = R.drawable.half_star),
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(16.dp)
                )
            }
            repeat(5 - filledStars - if (halfStar) 1 else 0) {
                Icon(
                    painter = painterResource(id = R.drawable.outlinestar),
                    contentDescription = null,
                    tint = Color(0xFFE0E0E0),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text("%.1f".format(rating), color = Color(0xFF666666))
    }
}

@Composable
private fun PriceSection(
    originalPrice: Double,
    discountedPrice: Double,
    discountPercentage: Double
) {
    Column {
        Text(
            text = "₹${formatPrice(originalPrice)}",
            color = Color(0xFF666666),
            textDecoration = TextDecoration.LineThrough
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "₹${formatPrice(discountedPrice)}",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFE8F5E8), shape = RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${discountPercentage.toInt()}% off",
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ExchangeOption(discountedPrice: Double) {
    val exchangePrice = discountedPrice - 100
    Text(
        text = "Or Pay ₹${formatPrice(exchangePrice)} + 💷100",
        color = Color(0xFF1976D2),
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 6.dp)
    )
}

private fun calculateDiscountedPrice(originalPrice: Double, discountPercentage: Double): Double {
    return originalPrice - (originalPrice * discountPercentage / 100)
}

private fun formatPrice(price: Double): String {
    return if (price >= 1000) "%,d".format(price.toInt())
    else "%.2f".format(price)
}
