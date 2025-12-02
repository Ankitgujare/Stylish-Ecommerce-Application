package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.ProductViewModel
import androidx.compose.ui.platform.LocalContext
import com.example.stylistshoppingapplication.data.local.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturedProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel? = null
) {
    val uiState by productViewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val profileRepository = remember { ProfileRepository(context) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    var localProfileImageUrl by remember { mutableStateOf<String?>(null) }

    // Load profile image from Room Database
    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { userId ->
            profileRepository.getProfileById(userId).collect { profile ->
                localProfileImageUrl = profile?.photoUrl
            }
        }
    }

    val profileImageUrl = currentUser?.photoUrl?.toString() ?: localProfileImageUrl

    LaunchedEffect(Unit) {
        productViewModel.fetchProducts()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                // Top Bar with Logo and Profile
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.Black
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = com.example.stylistshoppingapplication.R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Stylish",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6200EE)
                        )
                    }
                    
                    if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.LightGray, CircleShape)
                                .clickable { navController.navigate("profile_screen") },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = com.example.stylistshoppingapplication.R.drawable.profile),
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, Color.LightGray, CircleShape)
                                .clickable { navController.navigate("profile_screen") },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search any Product..") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                    },
                    trailingIcon = {
                        IconButton(onClick = { /* Voice search */ }) {
                            Icon(
                                painter = painterResource(id = com.example.stylistshoppingapplication.R.drawable.search),
                                contentDescription = "Voice Search",
                                modifier = Modifier.size(20.dp),
                                tint = Color.Gray
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF6200EE),
                        unfocusedBorderColor = Color(0xFF6200EE),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    singleLine = true
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = uiState) {
                is ProductViewModel.ProductUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ProductViewModel.ProductUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = Color.Red)
                    }
                }
                is ProductViewModel.ProductUiState.Success -> {
                    val filteredProducts = if (searchQuery.isBlank()) {
                        state.featuredProducts
                    } else {
                        state.featuredProducts.filter {
                            it.title.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                        }
                    }
                    
                    Column {
                        // Filter and Sort Row with Product Count
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${filteredProducts.size} Items",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Row {
                                Button(
                                    onClick = { /* TODO: Sort */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    elevation = ButtonDefaults.buttonElevation(2.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                ) {
                                    Text("Sort ↕", color = Color.Black, fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { /* TODO: Filter */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    elevation = ButtonDefaults.buttonElevation(2.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                    modifier = Modifier.border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                ) {
                                    Text("Filter ⚙", color = Color.Black, fontSize = 14.sp)
                                }
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredProducts) { product ->
                                ProductCard(
                                    product = product,
                                    onProductClick = { navController.navigate("product_detail/${product.id}") },
                                    onAddToCart = { cartViewModel?.addToCart(product) },
                                    onAddToWishlist = { /* TODO */ }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
