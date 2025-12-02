package com.example.stylistshoppingapplication.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth

data class DrawerMenuItem(
    val icon: ImageVector,
    val title: String,
    val route: String? = null,
    val action: (() -> Unit)? = null
)

@Composable
fun NavigationDrawer(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userEmail = currentUser?.email ?: "user@example.com"
    val userPhotoUrl = currentUser?.photoUrl?.toString()

    val menuItems = listOf(
        DrawerMenuItem(Icons.Default.Home, "Home", "home"),
        DrawerMenuItem(Icons.Default.Person, "Profile", "profile"),
        DrawerMenuItem(Icons.Default.FavoriteBorder, "Wishlist", "wishlist"),
        DrawerMenuItem(Icons.Default.ShoppingCart, "Cart", "cart"),
        DrawerMenuItem(Icons.Default.Search, "Search", "search"),
        DrawerMenuItem(Icons.Default.Settings, "Settings", "settings"),
        DrawerMenuItem(Icons.Default.Info, "About", "about"),
        DrawerMenuItem(Icons.Default.ExitToApp, "Logout", action = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        })
    )

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = Color.White
    ) {
        // Header with user profile
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(0xFF3F51B5)) // Blue header
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
                if (userPhotoUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(userPhotoUrl),
                        contentDescription = "Profile Photo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Default avatar
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE91E63)), // Pink background
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // User Email
                Text(
                    text = userEmail,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu Items
        menuItems.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                },
                selected = false,
                onClick = {
                    if (item.action != null) {
                        item.action.invoke()
                    } else if (item.route != null) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    selectedContainerColor = Color(0xFFFFEBEE)
                )
            )
        }
    }
}
