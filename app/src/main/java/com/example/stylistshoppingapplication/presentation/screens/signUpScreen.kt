package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.presentation.ViewModel.AuthViewModel
import com.example.stylistshoppingapplication.domain.util.Results

@Composable
fun SignUpScreen(
    navController: NavController,
    onLoginClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val authState by authViewModel.authState.collectAsState()
    
    // Observe auth state and navigate on success
    LaunchedEffect(authState) {
        when (val state = authState) {
            is Results.Success -> {
                // Navigate to home - AppNavigation will show onboarding for first-time users
                navController.navigate("home") {
                    popUpTo("signup") { inclusive = true }
                }
            }
            is Results.Failure -> {
                showError = true
                errorMessage = state.message
            }
            else -> {
                showError = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Create an account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Username or Email") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.eye else R.drawable.eye_crossed
                            ),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (confirmPasswordVisible) R.drawable.eye else R.drawable.eye_crossed
                            ),
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            // Error message
            if (showError) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "By clicking the Register button, you agree to the public offer",
                color = Color.Gray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Create Account Button
            Button(
                onClick = {
                    when {
                        email.isBlank() -> {
                            showError = true
                            errorMessage = "Please enter your email"
                        }
                        password.isBlank() -> {
                            showError = true
                            errorMessage = "Please enter a password"
                        }
                        password != confirmPassword -> {
                            showError = true
                            errorMessage = "Passwords do not match"
                        }
                        password.length < 6 -> {
                            showError = true
                            errorMessage = "Password must be at least 6 characters"
                        }
                        else -> {
                            showError = false
                            authViewModel.signup(email, password)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC62828)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = authState !is Results.Loading
            ) {
                if (authState is Results.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Create Account",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "- OR Continue with -",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Social Icons Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialLoginIcon(R.drawable.googleicon) { /* TODO: Google Sign-In */ }
                SocialLoginIcon(R.drawable.appleicon) { /* TODO: Apple Sign-In */ }
                SocialLoginIcon(R.drawable.facebookicon) { /* TODO: Facebook Sign-In */ }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "I Already Have an Account ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Login",
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}