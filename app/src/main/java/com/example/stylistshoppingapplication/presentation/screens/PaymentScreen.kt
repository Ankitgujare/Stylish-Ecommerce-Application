package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel

@Composable
fun PaymentScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val checkoutTotal by cartViewModel.checkoutTotal.collectAsState()
    var selectedPayment by remember { mutableStateOf(0) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    val paymentMethods = listOf(
        "VISA" to "***********2109",
        "PayPal" to "***********2109", 
        "Mastercard" to "***********2109",
        "Apple Pay" to "***********2109"
    )
    
    // Payment Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color.White,
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color(0xFFD32F2F), shape = CircleShape)
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Payment done successfully",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = false }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Continue", color = Color.White)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Payment",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Order Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Order Summary",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Order Amount")
                        Text("₹${String.format("%.0f", checkoutTotal)}")
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Shipping")
                        Text("Free", color = Color(0xFF388E3C), fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${String.format("%.0f", checkoutTotal)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Payment Methods
            Text(
                text = "Payment Method",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            paymentMethods.forEachIndexed { index, (method, number) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPayment == index) 
                            Color(0xFFE3F2FD) 
                        else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    onClick = { selectedPayment = index }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Payment method icon placeholder
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    when (method) {
                                        "VISA" -> Color(0xFF1A237E)
                                        "PayPal" -> Color(0xFF003087)
                                        "Mastercard" -> Color(0xFFB71C1C)
                                        "Apple Pay" -> Color.Black
                                        else -> Color.Gray
                                    },
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = method.first().toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = method,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.weight(1f))
                        
                        Text(
                            text = number,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        
                        if (selectedPayment == index) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF1565C0)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Pay Button - RED COLOR
            Button(
                onClick = { 
                    showSuccessDialog = true
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)  // Red color
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Pay ₹${String.format("%.0f", checkoutTotal)}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}