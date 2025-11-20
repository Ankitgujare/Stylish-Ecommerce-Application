package com.example.stylistshoppingapplication.presentation.screens

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
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.example.stylistshoppingapplication.presentation.ViewModel.WishlistViewModel
import com.example.stylistshoppingapplication.presentation.ViewModel.SearchViewModel
import com.example.stylistshoppingapplication.presentation.scafold.MainScaffold
import com.google.firebase.auth.FirebaseAuth
import com.example.stylistshoppingapplication.data.local.repository.ProfileRepository
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    productViewModel: ProductViewModel,
    navController: NavController,
    cartViewModel: CartViewModel? = null,
    wishlistViewModel: WishlistViewModel? = null,
    searchViewModel: SearchViewModel? = null
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
        cartViewModel = cartViewModel,
        wishlistViewModel = wishlistViewModel,
        searchViewModel = searchViewModel
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
                    cartViewModel = cartViewModel,
                    wishlistViewModel = wishlistViewModel
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
    cartViewModel: CartViewModel? = null,
    wishlistViewModel: WishlistViewModel? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Search bar
        SearchBar(navController)

        // Categories section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "All Featured",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row {
                TextButton(
                    onClick = { /* Handle sort */ },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Sort ↑↓", color = Color.Black)
                }
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(
                    onClick = { /* Handle filter */ },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Filter ↗", color = Color.Black)
                }
            }
        }

        CategoriesRow()

        // Banner section
        BannerSection()

        // Products list - Horizontal scrolling
        FeaturedProductsRow(
            products = featuredProducts,
            navController = navController,
            onProductClick = { product ->
                navController.navigate("product_detail/${product.id}")
            },
            onAddToCart = { product ->
                cartViewModel?.addToCart(product)
            },
            onAddToWishlist = { product ->
                wishlistViewModel?.addToWishlist(product)
            }
        )

        // Special Offers Section
        SpecialOffersSection()

        // Trending Products Section
        TrendingProductsSection(
            trendingProducts = trendingProducts,
            navController = navController,
            onProductClick = { product ->
                navController.navigate("product_detail/${product.id}")
            },
            onAddToCart = { product ->
                cartViewModel?.addToCart(product)
            },
            onAddToWishlist = { product ->
                wishlistViewModel?.addToWishlist(product)
            }
        )
    }
}

@Composable
fun SearchBar(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(40.dp)
            .background(
                color = Color(0xFFEEEEEE),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { navController.navigate(Routes.searchScreen.route) },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search any Product..",
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CategoriesRow() {
    val categories = listOf(
        "Beuty" to R.drawable.beuty,
        "Fashion" to R.drawable.fashion,
        "Kids" to R.drawable.kids,
        "Mens" to R.drawable.mens,
        "Womens" to R.drawable.women
    )

    LazyRow(
        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) { (name, icon) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { /* Handle category click */ }
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
                Text(
                    text = name,
                    fontSize = 12.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun BannerSection() {
    // List of banner images
    val bannerImages = listOf(
        R.drawable.product_banner_1,
        R.drawable.product_banner_2,
        R.drawable.product_banner_3
    )
    
    // Pager state for auto-scrolling
    val pagerState = rememberPagerState(pageCount = { bannerImages.size })
    
    // Auto-scrolling effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Change banner every 3 seconds
            val nextPage = (pagerState.currentPage + 1) % bannerImages.size
            pagerState.animateScrollToPage(nextPage)
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                // Display actual banner images
                Image(
                    painter = painterResource(id = bannerImages[page]),
                    contentDescription = "Banner ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            // Animated page indicator
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(bannerImages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == pagerState.currentPage) Color.White
                                else Color.White.copy(alpha = 0.5f)
                            )
                            .padding(2.dp)
                    )
                    if (index < bannerImages.size - 1) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedProductsRow(
    products: List<ProductModel>,
    navController: NavController,
    onProductClick: (ProductModel) -> Unit,
    onAddToCart: (ProductModel) -> Unit,
    onAddToWishlist: (ProductModel) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(0.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products.take(5)) { product ->
            ProductCard(
                product = product,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                onAddToWishlist = onAddToWishlist
            )
        }
    }
}

@Composable
fun ProductCard(
    product: ProductModel,
    onProductClick: (ProductModel) -> Unit,
    onAddToCart: (ProductModel) -> Unit,
    onAddToWishlist: (ProductModel) -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onProductClick(product) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(product.thumbnail)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                IconButton(
                    onClick = { onAddToWishlist(product) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Add to wishlist",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Red
                    )
                }
            }
            
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = product.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = product.category,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", product.price)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    IconButton(
                        onClick = { onAddToCart(product) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add to cart",
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF6200EE)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SpecialOffersSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F5FF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Special Offers ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "💺",
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We make sure you get the offer you need at best prices",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.specialoffer),
                contentDescription = "Special Offers",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
fun TrendingProductsSection(
    trendingProducts: List<ProductModel>,
    navController: NavController,
    onProductClick: (ProductModel) -> Unit,
    onAddToCart: (ProductModel) -> Unit,
    onAddToWishlist: (ProductModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending Products",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            TextButton(onClick = { 
                navController.navigate(Routes.viewAllProduct.route)
            }) {
                Text("View all →", color = Color(0xFF6200EE))
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 0.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(trendingProducts.take(5)) { product ->
                ProductCard(
                    product = product,
                    onProductClick = onProductClick,
                    onAddToCart = onAddToCart,
                    onAddToWishlist = onAddToWishlist
                )
            }
        }
    }
}

