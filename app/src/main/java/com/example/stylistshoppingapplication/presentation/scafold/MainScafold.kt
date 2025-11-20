package com.example.stylistshoppingapplication.presentation.scafold

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.navigation.Routes
// Removed import as we'll use the consistent Bottom Navigation implementation
import com.google.firebase.auth.FirebaseAuth
import coil.compose.AsyncImage
import com.google.rpc.Help
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import com.example.stylistshoppingapplication.data.local.repository.ProfileRepository
import com.example.stylistshoppingapplication.data.local.model.ProfileEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavController,
    cartViewModel: com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel? = null,
    wishlistViewModel: com.example.stylistshoppingapplication.presentation.ViewModel.WishlistViewModel? = null,
    searchViewModel: com.example.stylistshoppingapplication.presentation.ViewModel.SearchViewModel? = null,
    content: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val profileRepository = remember { ProfileRepository(context) }
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                // Header with profile
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (localProfileImageUrl != null && localProfileImageUrl!!.isNotEmpty()) {
                            AsyncImage(
                                model = localProfileImageUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = profileImageUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.profile),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = currentUser?.displayName ?: "User",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = currentUser?.email ?: "user@example.com",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Navigation items
                val drawerItems = listOf(
                    DrawerItem(Icons.Default.Home, "Home", Routes.homescreen.route),
                    DrawerItem(Icons.Default.Person, "Profile", Routes.profileScreen.route),
                    DrawerItem(Icons.Default.Favorite, "Wishlist", Routes.wishlistScreen.route),
                    DrawerItem(Icons.Default.ShoppingCart, "Cart", Routes.cartScreen.route),
                    DrawerItem(Icons.Default.Search, "Search", Routes.searchScreen.route),
                    DrawerItem(Icons.Default.Settings, "Settings", Routes.settingsScreen.route),
//                    DrawerItem(Icons.Default.Help, "Help & Support", ""),
                    DrawerItem(Icons.Default.Info, "About", ""),
                    DrawerItem(Icons.Default.ExitToApp, "Logout", "")
                )

                LazyColumn {
                    items(drawerItems) { item ->
                        DrawerItemRow(
                            item = item,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                }
                                when (item.route) {
                                    Routes.homescreen.route -> navController.navigate(Routes.homescreen.route)
                                    Routes.profileScreen.route -> navController.navigate(Routes.profileScreen.route)
                                    Routes.wishlistScreen.route -> navController.navigate(Routes.wishlistScreen.route)
                                    Routes.cartScreen.route -> navController.navigate(Routes.cartScreen.route)
                                    Routes.searchScreen.route -> navController.navigate(Routes.searchScreen.route)
                                    Routes.settingsScreen.route -> navController.navigate(Routes.settingsScreen.route)
                                    "" -> {
                                        // Handle other items like Help, About, Logout
                                        if (item.title == "Logout") {
                                            FirebaseAuth.getInstance().signOut()
                                            navController.navigate(Routes.loginScreen.route) {
                                                popUpTo(Routes.homescreen.route) { inclusive = true }
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
               CenterAlignedTopAppBar(navController,drawerState,scope, localProfileImageUrl ?: profileImageUrl)
            },
            bottomBar = {
                BottomNavigationBar(navController = navController, cartViewModel = cartViewModel)
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                content()
            }
        }
    }
}

@Composable
fun DrawerItemRow(
    item: DrawerItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = item.title,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBar(navController: NavController, drawerState: DrawerState, scope: CoroutineScope, profileImageUrl: String?) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Stylish",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE),
                    fontSize = 24.sp
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
              scope.launch {
                  drawerState.open()
              }
            }) {
                Image(
                    painter = painterResource(id = R.drawable.menu),
                    contentDescription = "Menu",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        actions = {
            if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .clickable { navController.navigate(Routes.profileScreen.route) },
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)
                        .clickable { navController.navigate(Routes.profileScreen.route) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    )
}

data class DrawerItem(
    val icon: ImageVector,
    val title: String,
    val route: String
)

// Bottom Navigation Implementation (Consistent with CheckoutScreen)
@Composable
private fun BottomNavigationBar(navController: NavController, cartViewModel: com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel? = null) {
    val items = listOf(
        BottomNavItem("Home", R.drawable.home, Routes.homescreen.route),
        BottomNavItem("Wishlist", R.drawable.heart, Routes.wishlistScreen.route),
        BottomNavItem("Cart", R.drawable.cart, Routes.cartScreen.route),
        BottomNavItem("Search", R.drawable.search, Routes.searchScreen.route),
        BottomNavItem("Setting", R.drawable.settings, Routes.settingsScreen.route)
    )
    
    // Get cart item count
    val cartItemCount = cartViewModel?.cartItemCount?.collectAsState()?.value ?: 0

    BottomAppBar(
        containerColor = Color.White,
        contentColor = Color.Black,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                BottomNavigationItem(
                    item = item,
                    navController = navController,
                    isSelected = isItemSelected(item, navController),
                    cartItemCount = if (item.label == "Cart") cartItemCount else 0
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationItem(
    item: BottomNavItem,
    navController: NavController,
    isSelected: Boolean = false,
    cartItemCount: Int = 0
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { 
                navController.navigate(item.route)
            }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isSelected) Color.Red else Color.Transparent,
                    shape = RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = androidx.compose.ui.res.painterResource(id = item.icon),
                contentDescription = item.label,
                modifier = Modifier.size(24.dp),
                tint = if (isSelected) Color.White else Color.Black
            )
            
            // Display cart item count
            if (cartItemCount > 0 && item.label == "Cart") {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Red, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cartItemCount > 9) "9+" else cartItemCount.toString(),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Red else Color.Black
        )
    }
}

data class BottomNavItem(
    val label: String,
    val icon: Int,
    val route: String
)

// Helper function to determine if a bottom navigation item is selected
@Composable
private fun isItemSelected(item: BottomNavItem, navController: NavController): Boolean {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    return when (item.label) {
        "Home" -> currentDestination?.contains("home", ignoreCase = true) == true
        "Wishlist" -> currentDestination?.contains("wishlist", ignoreCase = true) == true
        "Cart" -> currentDestination?.contains("cart", ignoreCase = true) == true
        "Search" -> currentDestination?.contains("search", ignoreCase = true) == true
        "Setting" -> currentDestination?.contains("setting", ignoreCase = true) == true
        else -> false
    }
}