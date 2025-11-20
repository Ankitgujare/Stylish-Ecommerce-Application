package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.navigation.Routes
import com.example.stylistshoppingapplication.ui.theme.Montserrat

@Composable
fun GetstartedScreen(Getstarted: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(R.drawable.imggetstarted), // replace with your drawable name
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Texts and Button
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You want",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 24.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal
                )
            )

            Text(
                text = "Authentic here",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal
                )
            )

            Text(
                text = "you go!",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Find it here, buy it now!",
                style = TextStyle(
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = Getstarted,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2E63)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = Montserrat,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
@Preview(showSystemUi = true)
fun GetstartedPreview() {

}