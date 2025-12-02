package com.example.stylistshoppingapplication.presentation.scafold

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.data.local.repository.ProfileRepository
import com.example.stylistshoppingapplication.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopAppBar(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    profileImageUrl: String?
) {
    TopAppBar(
        modifier = Modifier.padding(top = 16.dp),
        navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.Black, // Reverted to Black
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Stylish",
                    color = Color(0xFF4392F9),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate(Routes.profileScreen.route) },
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .clickable { navController.navigate(Routes.profileScreen.route) },
                    contentScale = ContentScale.Crop
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White) // Reverted to White
    )
}

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
               CenterAlignedTopAppBar(navController, drawerState, scope, localProfileImageUrl ?: profileImageUrl)
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
        )
    }
}

data class DrawerItem(
    val icon: ImageVector,
    val title: String,
    val route: String
)

// Bottom Navigation Implementation (Consistent with CheckoutScreen)
@Composable
fun BottomNavigationBar(
    navController: NavController,
    cartViewModel: com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel? = null
) {
    val items = listOf(
        BottomNavItem("Home", R.drawable.home, Routes.homescreen.route),
        BottomNavItem("Wishlist", R.drawable.heart, Routes.wishlistScreen.route),
        BottomNavItem("Cart", R.drawable.cart, Routes.cartScreen.route),
        BottomNavItem("Search", R.drawable.search, Routes.searchScreen.route),
        BottomNavItem("Setting", R.drawable.settings, Routes.settingsScreen.route)
    )

    // Get cart item count
    val cartItemCount = cartViewModel?.cartItemCount?.collectAsState()?.value ?: 0

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    if (item.label == "Cart" && cartItemCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge { Text(text = cartItemCount.toString()) }
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = { Text(text = item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.homescreen.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: Int,
    val route: String
)