package com.example.stylistshoppingapplication.presentation.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.stylistshoppingapplication.domain.model.ProductModel
import com.example.stylistshoppingapplication.navigation.Routes
import com.example.stylistshoppingapplication.presentation.ViewModel.ProductViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.WishlistViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavHostController,
    productId: Int,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel? = null,
    wishlistViewModel: WishlistViewModel? = null
) {
    val uiState by productViewModel.uiState.collectAsState()
    var selectedImageIndex by remember { mutableStateOf(0) }
    var selectedSize by remember { mutableStateOf("7 UK") }
    val context = LocalContext.current
    
    // Get current product
    val currentProduct = (uiState as? ProductViewModel.ProductUiState.Success)
        ?.let { it.featuredProducts + it.trendingProducts }
        ?.find { it.id == productId }
    
    // Check if product is in wishlist
    var isInWishlist by remember { mutableStateOf(false) }
    
    // Update wishlist state when product changes
    LaunchedEffect(currentProduct?.id) {
        currentProduct?.let { product ->
            isInWishlist = wishlistViewModel?.isProductInWishlist(product.id) ?: false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { 
                        currentProduct?.let { product ->
                            cartViewModel?.addToCart(product, selectedSize = selectedSize)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Add to Cart", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { 
                        currentProduct?.let { product ->
                            // Add to cart and navigate to checkout
                            cartViewModel?.addToCart(product, selectedSize = selectedSize)
                            navController.navigate(Routes.checkoutScreen.route)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) {
                    Text("Buy Now", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        // Use the paddingValues parameter to avoid lint error
        Box(modifier = Modifier.padding(paddingValues)) {
            when (uiState) {
                is ProductViewModel.ProductUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ProductViewModel.ProductUiState.Error -> {
                    val error = uiState as ProductViewModel.ProductUiState.Error
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(error.message, color = Color.Red)
                    }
                }

                is ProductViewModel.ProductUiState.Success -> {
                    val state = uiState as ProductViewModel.ProductUiState.Success
                    val allProducts = (state.featuredProducts + state.trendingProducts).distinctBy { it.id }
                    val product = allProducts.find { it.id == productId }

                    if (product != null) {
                        ProductDetailContent(
                            product = product,
                            selectedImageIndex = selectedImageIndex,
                            onImageChange = { selectedImageIndex = it },
                            selectedSize = selectedSize,
                            onSizeChange = { selectedSize = it }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Product not found")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: ProductModel,
    selectedImageIndex: Int,
    onImageChange: (Int) -> Unit,
    selectedSize: String,
    onSizeChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        ProductImageSection(product, selectedImageIndex, onImageChange)
        SizeSelectionSection(selectedSize = selectedSize, onSizeChange = onSizeChange)
        ProductInfoSection(product)
    }
}

@Composable
private fun ProductImageSection(
    product: ProductModel,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val context = LocalContext.current
    val images = if (product.images.isNotEmpty()) product.images else listOf(product.thumbnail)

    Column {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(context)
                .data(images[selectedIndex])
                .crossfade(true)
                .build(),
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            items(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(if (index == selectedIndex) Color.Black else Color.LightGray)
                        .clickable { onSelect(index) }
                )
            }
        }
    }
}

@Composable
private fun SizeSelectionSection(
    selectedSize: String,
    onSizeChange: (String) -> Unit
) {
    val sizes = listOf("6 UK", "7 UK", "8 UK", "9 UK", "10 UK")

    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Size: $selectedSize", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sizes.size) { index ->
                val size = sizes[index]
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .border(
                            1.dp,
                            if (size == selectedSize) Color(0xFFFF4081) else Color.LightGray,
                            RoundedCornerShape(10.dp)
                        )
                        .background(if (size == selectedSize) Color(0xFFFFEBEE) else Color.White)
                        .clickable { onSizeChange(size) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = size,
                        color = if (size == selectedSize) Color(0xFFD81B60) else Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductInfoSection(product: ProductModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(product.title, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${product.brand ?: ""} • ${product.category}",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
            Spacer(modifier = Modifier.width(4.dp))
            Text("${product.rating}", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text("(${(product.rating * 1000).toInt()} ratings)", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(8.dp))

        val discountedPrice = product.price * (1 - product.discountPercentage / 100)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "₹${String.format("%.0f", discountedPrice * 83)}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "₹${String.format("%.0f", product.price * 83)}",
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("${product.discountPercentage.toInt()}% off", color = Color(0xFF388E3C))
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(product.description, color = Color.DarkGray, fontSize = 15.sp, lineHeight = 22.sp)

        Spacer(modifier = Modifier.height(12.dp))
        if (product.returnPolicy.isNotEmpty()) {
            Text("Return Policy: ${product.returnPolicy}", color = Color.Gray, fontSize = 14.sp)
        }
        if (product.warrantyInformation.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text("Warranty: ${product.warrantyInformation}", color = Color.Gray, fontSize = 14.sp)
        }

        // ✅ More Images Section
        if (product.images.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("More Images", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(product.images.size) { index ->
                    SubcomposeAsyncImage(
                        model = product.images[index],
                        contentDescription = "Product image ${index + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
            }
        }

        // 🌟 Dimensions Section
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Product Dimensions", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Height: ${product.dimensions.height} cm\n" +
                            "Width: ${product.dimensions.width} cm\n" +
                            "Depth: ${product.dimensions.depth} cm",
                    color = Color.DarkGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }

        // 🌟 Reviews Section
        if (product.reviews.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Text("Customer Reviews", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            product.reviews.take(5).forEach { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(review.reviewerName, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(review.rating) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(review.comment, color = Color.DarkGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(review.date, color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}