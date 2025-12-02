package com.example.stylistshoppingapplication.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stylistshoppingapplication.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    navController: NavController,
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Choose Products",
            description = "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit.",
            imageRes = R.drawable.onboardingone
        ),
        OnboardingPage(
            title = "Make Payment",
            description = "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit.",
            imageRes = R.drawable.onboardingtwo
        ),
        OnboardingPage(
            title = "Get Your Order",
            description = "Amet minim mollit non deserunt ullamco est sit aliqua dolor do amet sint. Velit officia consequat duis enim velit mollit.",
            imageRes = R.drawable.onboardinglast
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${pagerState.currentPage + 1}/${pages.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
                
                if (pagerState.currentPage < pages.size - 1) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.clickable {
                            onFinish()
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                OnboardingPageContent(page = pages[pageIndex])
            }

            // Indicators
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.Black else Color.LightGray
                    val width = if (pagerState.currentPage == iteration) 32.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                            .width(width)
                            .height(8.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ) {
                        Text(text = "Prev", color = Color.Gray, fontSize = 16.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp)) // Placeholder to keep alignment
                }

                if (pagerState.currentPage < pages.size - 1) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    ) {
                        Text(text = "Next", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    TextButton(
                        onClick = {
                            onFinish()
                        }
                    ) {
                        Text(text = "Get Started", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = page.imageRes),
            contentDescription = page.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }
}

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)
