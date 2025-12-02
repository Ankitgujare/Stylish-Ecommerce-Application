package com.example.stylistshoppingapplication.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.stylistshoppingapplication.R
import com.example.stylistshoppingapplication.domain.model.ProductModel
import com.example.stylistshoppingapplication.navigation.Routes
import com.example.stylistshoppingapplication.presentation.ViewModel.ProductViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.CartViewModel

import com.example.stylistshoppingapplication.presentation.scafold.MainScaffold
import com.google.firebase.auth.FirebaseAuth
import com.example.stylistshoppingapplication.data.local.repository.ProfileRepository

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    productViewModel: ProductViewModel,
    navController: NavController,
    cartViewModel: CartViewModel? = null
) {
    val uiState by productViewModel.uiState.collectAsState()
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

    MainScaffold(
        navController = navController,
        cartViewModel = cartViewModel
    ) {
        when (val state = uiState) {
            is ProductViewModel.ProductUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ProductViewModel.ProductUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = Color.Red)
                }
            }
            is ProductViewModel.ProductUiState.Success -> {
                HomeContent(
                    featuredProducts = state.featuredProducts,
                    trendingProducts = state.trendingProducts,
                    navController = navController,
                    cartViewModel = cartViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    featuredProducts: List<ProductModel>,
    trendingProducts: List<ProductModel>,
    navController: NavController,
    cartViewModel: CartViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SearchBar(navController)
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Featured",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                SortFilterButton(text = "Sort", icon = "↕")
                Spacer(modifier = Modifier.width(8.dp))
                SortFilterButton(text = "Filter", icon = "⚙")
            }
        }

        CategoriesRow(navController)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Auto-scrolling Banner Carousel
        BannerCarousel()

        Spacer(modifier = Modifier.height(16.dp))
        
        DealOfDaySection(navController)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Featured Products Horizontal List
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(featuredProducts) { product ->
                Box(modifier = Modifier.width(200.dp)) { // Resize ProductCard
                    ProductCard(
                        product = product,
                        onProductClick = { navController.navigate("product_detail/${product.id}") },
                        onAddToCart = { cartViewModel?.addToCart(product) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SpecialOffersSection()

        Spacer(modifier = Modifier.height(16.dp))


        FlatAndHeelsSection()

        Spacer(modifier = Modifier.height(16.dp))

        TrendingProductsSection(navController)
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(trendingProducts) { product ->
                Box(modifier = Modifier.width(200.dp)) { // Resize ProductCard
                    ProductCard(
                        product = product,
                        onProductClick = { navController.navigate("product_detail/${product.id}") },
                        onAddToCart = { cartViewModel?.addToCart(product) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp)) // Bottom padding for navigation bar

        SummerSaleCard(
            imageRes = R.drawable.summersale,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        SponsoredCard(
            imageRes = R.drawable.ofbanner
        )

        Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for navigation bar
    }
}

@Composable
fun SponsoredCard(
    modifier: Modifier = Modifier,
    imageRes: Int
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Sponsored",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                // Image Banner
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = "Sponsored Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp) // Adjust height as needed
                )
                
                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "up to 50% Off",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "View Details",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun SummerSaleCard(
    modifier: Modifier = Modifier,
    imageRes: Int, // drawable resource
    title: String = "New Arrivals",
    subtitle: String = "Summer' 25 Collections",
    buttonText: String = "View all \u2192"
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Top: Image banner
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Summer sale banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Adjusted height for the banner
            )

            // Bottom: Text and Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { /* navigate to collection */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)), // Dark Red/Burgundy
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = buttonText, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // For Pager
@Composable
fun BannerCarousel() {
    val banners = listOf(
        R.drawable.product_banner_1,
        R.drawable.product_banner_2,
        R.drawable.product_banner_3
    )
    val pagerState = rememberPagerState(pageCount = { banners.size })
    
    // Auto-scroll logic
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Scroll every 3 seconds
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp))
        ) { page ->
            Image(
                painter = painterResource(id = banners[page]),
                contentDescription = "Banner $page",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Indicators
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFFFD6E87) else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
fun SearchBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(50.dp)
            .background(Color(0xFFF0F1F2), RoundedCornerShape(8.dp)) // Light gray background
            .clickable { navController.navigate(Routes.searchScreen.route) }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Search any Product..", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.microphone),
                contentDescription = "Voice Search",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SortFilterButton(text: String, icon: String) {
    Box(
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(6.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
            .clickable { /* TODO */ }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
             Text(text = text, fontSize = 12.sp, color = Color.Black)
             Spacer(modifier = Modifier.width(4.dp))
             Text(text = icon, fontSize = 12.sp, color = Color.Black)
        }
    }
}

@Composable
fun CategoriesRow(navController: NavController) {
    val context = LocalContext.current
    val categories = listOf(
        Pair("Beauty", R.drawable.beuty),
        Pair("Fashion", R.drawable.fashion),
        Pair("Kids", R.drawable.kids),
        Pair("Mens", R.drawable.mens),
        Pair("Womens", R.drawable.women),
        Pair("Jewelry", R.drawable.jwelery),
        Pair("Electronics", R.drawable.electronic)
    )
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { (name, icon) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { 
                    when (name) {
                        "Mens" -> navController.navigate("category_products/men's clothing")
                        "Womens" -> navController.navigate("category_products/women's clothing")
                        "Jewelry" -> navController.navigate("category_products/jewelery")
                        "Electronics" -> navController.navigate("category_products/electronics")
                        else -> Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = name, fontSize = 12.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun DealOfDaySection(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4392F9)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Deal of the Day", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("22h 55m 20s remaining", color = Color.White, fontSize = 12.sp)
                }
            }
            OutlinedButton(
                onClick = { navController.navigate("featured_products") },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("View all →", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SpecialOffersSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFD)),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             Image(
                painter = painterResource(id = R.drawable.specialoffer),
                contentDescription = "Special Offer",
                modifier = Modifier.size(100.dp), // Increased size
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Special Offers", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("😱", fontSize = 16.sp)
                }
                Text(
                    "We make sure you get the\noffer you need at best prices", 
                    fontSize = 12.sp, 
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun FlatAndHeelsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Dark overlay for better text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            0.0f to Color.Transparent,
                            0.4f to Color.Black.copy(alpha = 0.4f),
                            startX = 0f,
                            endX = 1000f
                        )
                    )
            )

            // Yellow sidebar on the left
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(21.dp)
                    .background(Color(0xFFFFD700).copy(alpha = 0.7f))
            )

            // Background image (sandal)
            Image(
                painter = painterResource(id = R.drawable.flatheal),
                contentDescription = "Flat and Heels",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterStart)
            )

            // Stars overlay concentrated near the yellow bar
            Canvas(modifier = Modifier.fillMaxSize()) {
                val starColor = Color(0xFFFFD700)
                val starAreaWidth = size.width * 0.3f // Limit stars to left 30% of the card
                val starAreaHeight = size.height * 0.9f // Limit stars to middle 90% of height

                // Increase number of stars to 80
                repeat(480) {
                    drawCircle(
                        color = starColor,
                        radius = 2.dp.toPx(),
                        center = Offset(
                            // Constrain x to left area (0 to starAreaWidth)
                            x = Random.nextFloat() * starAreaWidth,
                            // Constrain y to middle area with padding
                            y = (size.height - starAreaHeight) / 2 +
                                    Random.nextFloat() * starAreaHeight
                        ),
                        alpha = 0.8f
                    )
                }

                // Add a cluster of larger stars near the bar
                repeat(50) {
                    drawCircle(
                        color = starColor,
                        radius = 3.dp.toPx(), // Larger stars
                        center = Offset(
                            x = Random.nextFloat() * (size.width * 0.15f),
                            y = size.height * 0.3f +
                                    Random.nextFloat() * (size.height * 0.4f)
                        ),
                        alpha = 0.9f
                    )
                }
            }

            // Content aligned to the right
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Flat and Heels",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Stand a chance to get rewarded",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Red button with white text
                Button(
                    onClick = { /* Handle visit now */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE53935)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Visit now →",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun TrendingProductsSection(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFD6E87)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Trending Products", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Last Date 29/02/22", color = Color.White, fontSize = 12.sp)
                }
            }
            OutlinedButton(
                onClick = { navController.navigate("trending_products") },
                border = BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("View all →", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
