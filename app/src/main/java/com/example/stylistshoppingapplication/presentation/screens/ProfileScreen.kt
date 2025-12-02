package com.example.stylistshoppingapplication.presentation.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.presentation.ViewModel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadImage(it) }
    }

    // Handle save success navigation or toast
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            // Optional: Navigate back or just show success (handled in VM)
            // navController.popBackStack() 
            viewModel.resetSavedState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Profile Picture Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    if (uiState.photoUrl != null) {
                        AsyncImage(
                            model = uiState.photoUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                                contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Edit Icon
                    IconButton(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.Blue, CircleShape)
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            if (uiState.isUploading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text(
                    text = "Uploading image...",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            // Personal Details Section
            Text(
                text = "Personal Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Name Field
            OutlinedTextField(
                value = uiState.displayName,
                onValueChange = { viewModel.updateField("displayName", it) },
                label = { Text("Full Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )
            
            // Email Field
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateField("email", it) },
                label = { Text("Email Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Password Field (Visual only for now)
            OutlinedTextField(
                value = "**********",
                onValueChange = { /* No-op for password direct edit */ },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                enabled = false, // Disable direct editing
                trailingIcon = {
                    TextButton(onClick = { Toast.makeText(context, "Change Password feature coming soon", Toast.LENGTH_SHORT).show() }) {
                        Text("Change", color = Color.Red, fontSize = 12.sp)
                    }
                }
            )

            // Business Address Details Section
            Text(
                text = "Business Address Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )

            // Pincode Field
            OutlinedTextField(
                value = uiState.pincode,
                onValueChange = { viewModel.updateField("pincode", it) },
                label = { Text("Pincode") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Address Field
            OutlinedTextField(
                value = uiState.address,
                onValueChange = { viewModel.updateField("address", it) },
                label = { Text("Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            // City Field
            OutlinedTextField(
                value = uiState.city,
                onValueChange = { viewModel.updateField("city", it) },
                label = { Text("City") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            // State Field
            OutlinedTextField(
                value = uiState.state,
                onValueChange = { viewModel.updateField("state", it) },
                label = { Text("State") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            // Country Field
            OutlinedTextField(
                value = uiState.country,
                onValueChange = { viewModel.updateField("country", it) },
                label = { Text("Country") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            // Bank Account Details Section
            Text(
                text = "Bank Account Details",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )

            // Bank Account Number Field
            OutlinedTextField(
                value = uiState.bankAccount,
                onValueChange = { viewModel.updateField("bankAccount", it) },
                label = { Text("Bank Account Number") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Account Holder Name Field
            OutlinedTextField(
                value = uiState.accountHolder,
                onValueChange = { viewModel.updateField("accountHolder", it) },
                label = { Text("Account Holder's Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            // IFSC Code Field
            OutlinedTextField(
                value = uiState.ifscCode,
                onValueChange = { viewModel.updateField("ifscCode", it) },
                label = { Text("IFSC Code") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            // Save Button
            Button(
                onClick = { viewModel.saveProfile() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63)
                ),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading && !uiState.isUploading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Save",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}