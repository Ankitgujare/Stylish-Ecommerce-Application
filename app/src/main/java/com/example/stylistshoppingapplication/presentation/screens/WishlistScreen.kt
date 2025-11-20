package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stylistshoppingapplication.domain.model.WishlistItem
import com.example.stylistshoppingapplication.presentation.ViewModel.WishlistViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    navController: NavController,
    wishlistViewModel: WishlistViewModel,
    cartViewModel: CartViewModel? = null
) {
    val wishlistItems by wishlistViewModel.wishlistItems.collectAsState()
    val wishlistCount by wishlistViewModel.wishlistCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Wishlist ($wishlistCount)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (wishlistItems.isNotEmpty()) {
                        TextButton(onClick = { wishlistViewModel.clearWishlist() }) {
                            Text("Clear All", color = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        if (wishlistItems.isEmpty()) {
            EmptyWishlistState()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF5F5F5))
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(wishlistItems) { wishlistItem ->
                        WishlistItemCard(
                            wishlistItem = wishlistItem,
                            onProductClick = { product ->
                                navController.navigate("product_detail/${product.id}")
                            },
                            onRemoveFromWishlist = { productId ->
                                wishlistViewModel.removeFromWishlist(productId)
                            },
                            onAddToCart = { product ->
                                cartViewModel?.addToCart(product)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WishlistItemCard(
    wishlistItem: WishlistItem,
    onProductClick: (com.example.stylistshoppingapplication.domain.model.ProductModel) -> Unit,
    onRemoveFromWishlist: (Int) -> Unit,
    onAddToCart: (com.example.stylistshoppingapplication.domain.model.ProductModel) -> Unit
) {
    val product = wishlistItem.product
    val discountedPrice = calculateDiscountedPrice(product.price, product.discountPercentage)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            Column(modifier = Modifier.padding(12.dp)) {
                AsyncImage(
                    model = product.thumbnail,
                    contentDescription = product.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
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
                    lineHeight = 18.sp,
                    modifier = Modifier.height(48.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))
                
                // Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val filledStars = product.rating.toInt()
                    val halfStar = product.rating - filledStars >= 0.5
                    Row {
                        repeat(filledStars) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.stylistshoppingapplication.R.drawable.starfilled),
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        if (halfStar) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.stylistshoppingapplication.R.drawable.half_star),
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        repeat(5 - filledStars - if (halfStar) 1 else 0) {
                            Icon(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.stylistshoppingapplication.R.drawable.outlinestar),
                                contentDescription = null,
                                tint = Color(0xFFE0E0E0),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("%.1f".format(product.rating), color = Color(0xFF666666), fontSize = 12.sp)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price
                Column {
                    Text(
                        text = "₹${formatPrice(product.price)}",
                        color = Color(0xFF666666),
                        textDecoration = TextDecoration.LineThrough,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${formatPrice(discountedPrice)}",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E8), shape = RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${product.discountPercentage.toInt()}% off",
                                color = Color(0xFF388E3C),
                                fontWeight = FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onAddToCart(product) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Add to Cart",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to Cart", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
            
            // Remove from wishlist button
            IconButton(
                onClick = { onRemoveFromWishlist(product.id) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Remove from Wishlist",
                    tint = Color.Red,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyWishlistState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = "Empty Wishlist",
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your wishlist is empty",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add products you love to your wishlist",
                color = Color.Gray
            )
        }
    }
}

private fun calculateDiscountedPrice(originalPrice: Double, discountPercentage: Double): Double {
    return originalPrice - (originalPrice * discountPercentage / 100)
}

private fun formatPrice(price: Double): String {
    return if (price >= 1000) "%,d".format(price.toInt())
    else "%.2f".format(price)
}
