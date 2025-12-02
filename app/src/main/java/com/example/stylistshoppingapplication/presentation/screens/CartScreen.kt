package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.domain.model.CartItem
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val cartTotal by cartViewModel.cartTotal.collectAsState()
    val cartItemCount by cartViewModel.cartItemCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cart",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                CartBottomBar(
                    totalAmount = cartTotal,
                    onPlaceOrder = {
                        cartViewModel.prepareCheckout(cartItems)
                        navController.navigate("checkout")
                    }
                )
            }
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyCartState()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF1F3F6)) // Light gray background like Flipkart
            ) {
                // Address Section
                AddressSection()

                Spacer(modifier = Modifier.height(8.dp))

                // Cart Items
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(cartItems) { cartItem ->
                        CartItemCard(
                            cartItem = cartItem,
                            onQuantityChange = { newQuantity ->
                                cartViewModel.updateQuantity(cartItem.product.id, newQuantity)
                            },
                            onRemoveItem = {
                                cartViewModel.removeFromCart(cartItem.product.id)
                            },
                            onBuyNow = {
                                cartViewModel.prepareCheckout(listOf(cartItem))
                                navController.navigate("checkout")
                            }
                        )
                    }
                    
                    item {
                        PriceDetailsSection(cartTotal = cartTotal, itemCount = cartItemCount)
                    }
                    
                    item {
                        SafePaymentFooter()
                    }
                }
            }
        }
    }
}

@Composable
fun AddressSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp) // Rectangular
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Deliver to: ",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Ankit Gupta, 444606",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "HOME",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "444606, The greater kailash nager near...",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            OutlinedButton(
                onClick = { /* TODO: Change Address */ },
                shape = RoundedCornerShape(4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Change", color = Color(0xFF2874F0), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemoveItem: () -> Unit,
    onBuyNow: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Product Image
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = cartItem.product.thumbnail,
                        contentDescription = cartItem.product.title,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Fit
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Quantity Selector
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                            .clickable { /* TODO: Open Quantity Dialog */ }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Qty: ${cartItem.quantity}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Select Quantity",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    // For simplicity, using simple +/- buttons below if dropdown is complex
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         IconButton(onClick = { if (cartItem.quantity > 1) onQuantityChange(cartItem.quantity - 1) }, modifier = Modifier.size(24.dp)) {
                             Icon(painterResource(R.drawable.minus), "Decrease", modifier = Modifier.size(12.dp))
                         }
                         IconButton(onClick = { onQuantityChange(cartItem.quantity + 1) }, modifier = Modifier.size(24.dp)) {
                             Icon(Icons.Default.Add, "Increase", modifier = Modifier.size(12.dp))
                         }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Product Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cartItem.product.title,
                        fontSize = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = cartItem.selectedSize.ifEmpty { "Size: M" }, // Placeholder if empty
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Discounted Price
                        Text(
                            text = "₹${String.format("%.0f", cartItem.product.price)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Original Price
                        val originalPrice = cartItem.product.price / (1 - cartItem.product.discountPercentage / 100)
                        Text(
                            text = "₹${String.format("%.0f", originalPrice)}",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Discount Percentage
                        Text(
                            text = "${cartItem.product.discountPercentage.toInt()}% Off",
                            fontSize = 14.sp,
                            color = Color(0xFF388E3C),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Delivery by Nov 25, Tue",
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
            
            Divider(color = Color(0xFFF0F0F0))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(
                    onClick = onRemoveItem,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Remove", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
                
                Divider(
                    color = Color(0xFFF0F0F0),
                    modifier = Modifier
                        .height(48.dp)
                        .width(1.dp)
                )
                
                TextButton(
                    onClick = onBuyNow,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Buy this now",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buy this now", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PriceDetailsSection(cartTotal: Double, itemCount: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Price Details",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Price ($itemCount items)", fontSize = 14.sp, color = Color.Black)
                Text("₹${String.format("%.0f", cartTotal)}", fontSize = 14.sp, color = Color.Black)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Discount", fontSize = 14.sp, color = Color.Black)
                Text("- ₹0", fontSize = 14.sp, color = Color(0xFF388E3C)) // Placeholder discount logic
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Delivery Charges", fontSize = 14.sp, color = Color.Black)
                Text("FREE", fontSize = 14.sp, color = Color(0xFF388E3C))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(color = Color.LightGray)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Amount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("₹${String.format("%.0f", cartTotal)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "You will save ₹0 on this order",
                fontSize = 14.sp,
                color = Color(0xFF388E3C),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SafePaymentFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Safe",
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Safe and Secure Payments. 100% Authentic Products.",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun CartBottomBar(
    totalAmount: Double,
    onPlaceOrder: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "₹${String.format("%.0f", totalAmount)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "View Price Details",
                    fontSize = 12.sp,
                    color = Color(0xFF2874F0),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Button(
                onClick = onPlaceOrder,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)), // Amber/Yellow color
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .width(160.dp)
                    .height(48.dp)
            ) {
                Text(
                    text = "Place Order",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun EmptyCartState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your cart is empty",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add some products to get started",
                color = Color.Gray
            )
        }
    }
}
