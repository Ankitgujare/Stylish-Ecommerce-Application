package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.domain.model.CartItem
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    // Use checkoutItems instead of cartItems to support both "Buy Now" (single item) and "Place Order" (all items)
    val checkoutItems by cartViewModel.checkoutItems.collectAsState()
    val checkoutTotal by cartViewModel.checkoutTotal.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Order Summary",
                        style = MaterialTheme.typography.titleLarge,
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
            if (checkoutItems.isNotEmpty()) {
                CheckoutBottomBar(
                    totalAmount = checkoutTotal,
                    onProceedToPayment = {
                        navController.navigate("payment")
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF1F3F6))
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Address Section (Reused or similar to Cart)
                item {
                    AddressSection()
                }

                // Product Details
                items(checkoutItems) { cartItem ->
                    ProductDetailCard(cartItem = cartItem)
                }
                
                // Coupons Section
                item {
                    CouponsSection()
                }
                
                // Order Payment Details
                item {
                    OrderPaymentDetailsSection(cartTotal = checkoutTotal)
                }
            }
        }
    }
}

@Composable
private fun ProductDetailCard(cartItem: CartItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            AsyncImage(
                model = cartItem.product.thumbnail,
                contentDescription = cartItem.product.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Fit,
                error = painterResource(id = R.drawable.logo)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = cartItem.product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Description
                Text(
                    text = cartItem.product.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Size and Quantity Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Size: ${cartItem.selectedSize.ifEmpty { "M" }}",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Qty: ${cartItem.quantity}",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Price
                Text(
                    text = "₹${String.format("%.0f", cartItem.totalPrice)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Delivery Info
                Text(
                    text = "Delivery by Nov 25, Tue",
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun CouponsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.heart), // Replace with coupon icon if available
                    contentDescription = "Coupon",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Apply Coupons",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            
            TextButton(
                onClick = { /* Handle coupon selection */ }
            ) {
                Text(
                    text = "Select",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OrderPaymentDetailsSection(cartTotal: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Price Details",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp),
                color = Color.Gray
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Price",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "₹${String.format("%.0f", cartTotal)}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Delivery Fee",
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    text = "FREE",
                    fontSize = 14.sp,
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = Color.LightGray
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Amount Payable",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "₹${String.format("%.0f", cartTotal)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun CheckoutBottomBar(
    totalAmount: Double,
    onProceedToPayment: () -> Unit
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
                onClick = onProceedToPayment,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107) // Amber/Yellow
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .height(48.dp)
                    .width(200.dp)
            ) {
                Text(
                    text = "Proceed to Payment",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}