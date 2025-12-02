package com.example.stylistshoppingapplication.presentation.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.stylistshoppingapplication.domain.util.Results
import com.example.stylistshoppingapplication.navigation.Routes
import com.example.stylistshoppingapplication.presentation.ViewModel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(
    navController: NavController,
    onRegister: () -> Unit,
    onForgot: () -> Unit,
    authViewModel: AuthViewModel,
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            authViewModel.signInWithGoogle(credential)
        } catch (e: ApiException) {
            errorMessage = "Google Sign-In failed: ${e.message}"
            showError = true
        }
    }

    LaunchedEffect(authState) {
        when(val currentstate=authState){
            is Results.Success->{
                // Navigate to home - AppNavigation will handle onboarding for first-time users
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
                
            }

            is Results.Failure->{
                showError=true
                errorMessage=currentstate.message

            }

            Results.Ideal,Results.Loading->{
                showError=false
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
            // Title
            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
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

            // Forgot Password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onForgot) {
                    Text(
                        text = "Forgot Password?",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            //Login Button
            Button(
                onClick ={
                    if(username.isNotBlank() && password.isNotBlank()){
                        authViewModel.login(username,password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                SocialLoginIcon(
                    iconRes = R.drawable.googleicon,
                    onClick = {
                        // Sign out any existing Google account first
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(context.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        
                        // Sign out first to ensure clean state
                        googleSignInClient.signOut().addOnCompleteListener {
                            // Now launch sign-in
                            googleSignInLauncher.launch(googleSignInClient.signInIntent)
                        }
                    }
                )
                SocialLoginIcon(R.drawable.appleicon) { /* Apple Sign-In not implemented yet */ }
                SocialLoginIcon(R.drawable.facebookicon) { /* Facebook Sign-In not implemented yet */ }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sign Up link
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Create An Account ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                TextButton(onClick = onRegister) {
                    Text(
                        text = "Sign Up",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SocialLoginIcon(iconRes: Int, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .size(52.dp)
            .border(1.dp, Color.Red, CircleShape),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
