package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylistshoppingapplication.R

@Composable
fun SettingsScreen(
    navController: NavController
) {
    var isDarkMode by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(true) }
    var pushNotifications by remember { mutableStateOf(false) }

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
                text = "Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Appearance Section
        SettingsSection(
            title = "Appearance",
            items = listOf(
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Dark Mode",
                    subtitle = "Switch between light and dark themes",
                    trailing = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it }
                        )
                    }
                )
            )
        )

        // Notifications Section
        SettingsSection(
            title = "Notifications",
            items = listOf(
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Push Notifications",
                    subtitle = "Receive notifications on your device",
                    trailing = {
                        Switch(
                            checked = pushNotifications,
                            onCheckedChange = { pushNotifications = it }
                        )
                    }
                ),
                SettingsItem(
                    icon = Icons.Default.Email,
                    title = "Email Notifications",
                    subtitle = "Receive updates via email",
                    trailing = {
                        Switch(
                            checked = emailNotifications,
                            onCheckedChange = { emailNotifications = it }
                        )
                    }
                )
            )
        )

        // Account Section
        SettingsSection(
            title = "Account",
            items = listOf(
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Edit Profile",
                    subtitle = "Update your personal information",
                    onClick = { /* Navigate to profile edit */ }
                ),
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = "Change Password",
                    subtitle = "Update your account password",
                    onClick = { /* Navigate to change password */ }
                ),
                SettingsItem(
                    icon = Icons.Default.Check,
                    title = "Privacy Settings",
                    subtitle = "Manage your privacy preferences",
                    onClick = { /* Navigate to privacy settings */ }
                )
            )
        )

        // Support Section
        SettingsSection(
            title = "Support",
            items = listOf(
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Help Center",
                    subtitle = "Get help and support",
                    onClick = { /* Navigate to help center */ }
                ),
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Contact Us",
                    subtitle = "Get in touch with our team",
                    onClick = { /* Navigate to contact */ }
                ),
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = "App version and information",
                    onClick = { /* Navigate to about */ }
                )
            )
        )

        // App Settings Section
        SettingsSection(
            title = "App Settings",
            items = listOf(
                SettingsItem(
                    icon = Icons.Default.Settings,
                    title = "Language",
                    subtitle = "English",
                    onClick = { /* Navigate to language selection */ }
                ),
                SettingsItem(
                    icon = Icons.Default.LocationOn,
                    title = "Location Services",
                    subtitle = "Allow location access for better experience",
                    onClick = { /* Navigate to location settings */ }
                ),
                SettingsItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Storage",
                    subtitle = "Manage app storage and cache",
                    onClick = { /* Navigate to storage settings */ }
                )
            )
        )

        // Logout Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { /* Handle logout */ },
            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Logout",
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingsSection(
    title: String,
    items: List<SettingsItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        items.forEach { item ->
            SettingsItemRow(item = item)
        }
    }
}

@Composable
fun SettingsItemRow(
    item: SettingsItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .clickable { item.onClick?.invoke() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                if (item.subtitle.isNotEmpty()) {
                    Text(
                        text = item.subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            item.trailing?.invoke()
        }
    }
}

data class SettingsItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String = "",
    val onClick: (() -> Unit)? = null,
    val trailing: (@Composable () -> Unit)? = null
)
