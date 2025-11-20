package com.example.stylistshoppingapplication.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.domain.util.Results
import com.example.stylistshoppingapplication.navigation.Routes
import com.example.stylistshoppingapplication.presentation.ViewModel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import java.util.concurrent.TimeUnit

@Composable
fun SignUpScreen(navController: NavController,
                 authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var showOtpField by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
    var forceResendingToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }
    var isVerifying by remember { mutableStateOf(false) }
    var resendToken by remember { mutableStateOf("") }
    
    // Google Sign-In setup
    val context = LocalContext.current
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }
    
    // Phone Auth setup
    val auth = FirebaseAuth.getInstance()

    val authState by authViewModel.authState.collectAsState()
    var errorMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    
    // Format phone number helper
    fun formatPhoneNumber(number: String): String {
        // Remove all non-digit characters
        val digits = number.filter { it.isDigit() }
        
        // If it starts with 0, replace with country code (assuming India +91)
        if (digits.startsWith("0")) {
            return "+91${digits.drop(1)}"
        }
        
        // If it doesn't start with +, add +91 (assuming India)
        if (!digits.startsWith("+")) {
            return "+91$digits"
        }
        
        return digits
    }
    
    LaunchedEffect(authState) {
        when(val currentstate=authState){
            is Results.Success->{
                // Let AppNavigation handle the navigation based on user preferences
                // This will show onboarding for first-time users and home for returning users
            }

            is Results.Failure->{
                showError=true
                errorMessage=currentstate.message

            }

            Results.Ideal, Results.Loading->{
                showError=false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Create an\naccount",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username or Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Username or Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
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
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.eye else R.drawable.eye_crossed
                        ),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(16.dp))
        
        // Phone Number Field
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Phone Number") },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        // OTP Field (shown when verification code is sent)
        if (showOtpField) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = verificationCode,
                onValueChange = { verificationCode = it },
                label = { Text("Enter OTP") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Send OTP Button
        Button(
            onClick = {
                if (phoneNumber.isNotBlank() && !showOtpField) {
                    // Format phone number
                    val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
                    
                    // Validate phone number
                    if (formattedPhoneNumber.length < 12) {
                        Toast.makeText(context, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    // Send OTP
                    isVerifying = true
                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(formattedPhoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(context as android.app.Activity)
                        .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                // Auto-verification
                                isVerifying = false
                                authViewModel.signInWithCredential(credential)
                            }
                            
                            override fun onVerificationFailed(e: FirebaseException) {
                                // Handle verification failure
                                isVerifying = false
                                Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                            
                            override fun onCodeSent(
                                newVerificationId: String,
                                token: PhoneAuthProvider.ForceResendingToken
                            ) {
                                // Save verification ID and show OTP field
                                verificationId = newVerificationId
                                forceResendingToken = token
                                showOtpField = true
                                isVerifying = false
                            }
                        })
                        .build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else if (showOtpField && verificationCode.isNotBlank()) {
                    // Verify OTP
                    isVerifying = true
                    val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
                    authViewModel.signInWithCredential(credential)
                }
            },
            enabled = !isVerifying,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            if (isVerifying) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (showOtpField) "Verify OTP" else "Send OTP",
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Bold, 
                    color = Color.White
                )
            }
        }
        
        if (showOtpField) {
            TextButton(
                onClick = {
                    // Format phone number
                    val formattedPhoneNumber = formatPhoneNumber(phoneNumber)
                    
                    if (forceResendingToken != null) {
                        // Resend OTP
                        isVerifying = true
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(formattedPhoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(context as android.app.Activity)
                            .setForceResendingToken(forceResendingToken!!)
                            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                                    // Auto-verification
                                    isVerifying = false
                                    authViewModel.signInWithCredential(credential)
                                }
                                
                                override fun onVerificationFailed(e: FirebaseException) {
                                    // Handle verification failure
                                    isVerifying = false
                                    Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                                
                                override fun onCodeSent(
                                    newVerificationId: String,
                                    token: PhoneAuthProvider.ForceResendingToken
                                ) {
                                    // Save verification ID
                                    verificationId = newVerificationId
                                    forceResendingToken = token
                                    isVerifying = false
                                    Toast.makeText(context, "OTP resent successfully", Toast.LENGTH_SHORT).show()
                                }
                            })
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
                    }
                },
                enabled = !isVerifying
            ) {
                Text("Resend OTP", color = Color(0xFF2196F3))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "By clicking the Register button, you agree to the public offer",
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if(email.isNotBlank() && password.isNotBlank()){
                    if (password == confirmPassword){
                        authViewModel.signup(email,password)
                    }else{
                        Toast.makeText(context, "Password Mismatch", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2E55)),
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("Create Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "- OR Continue with -",
            color = Color.Gray,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Google Sign-In launcher
        val launcher = rememberLauncherForActivityResult(
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
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialLoginButton(iconRes = R.drawable.googleicon) {
                // Sign out first to ensure clean state
                googleSignInClient.signOut().addOnCompleteListener {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                }
            }
            SocialLoginButton(iconRes = R.drawable.appleicon) {
                // Handle Apple Sign-In
            }
            SocialLoginButton(iconRes = R.drawable.facebookicon) {
                // Handle Facebook Sign-In
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row {
            Text(text = "I Already Have an Account ", color = Color.Gray, fontSize = 14.sp)
            Text(
                text = "Login",
                color = Color(0xFFFF2E55),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(Routes.loginScreen.route)
                }
            )
        }
    }
}

@Composable
fun SocialLoginButton(iconRes: Int, onClick: () -> Unit = {}) {
    Surface(
        shape = RoundedCornerShape(50),
        border = ButtonDefaults.outlinedButtonBorder,
        tonalElevation = 1.dp,
        modifier = Modifier
            .size(50.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}