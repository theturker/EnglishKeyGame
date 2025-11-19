package com.alperenturker.englishcardgame.feature.quiz.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperenturker.englishcardgame.core.common.getTopSafeAreaPadding
import com.alperenturker.englishcardgame.core.domain.model.Category
import com.alperenturker.englishcardgame.feature.quiz.ui.theme.*
import com.alperenturker.englishcardgame.feature.quiz.viewmodel.CategoryListViewModel

@Composable
fun CategoryListScreen(
    onCategorySelected: (String, String, String?) -> Unit,
    onProfileClick: () -> Unit,
    viewModel: CategoryListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val safeAreaPadding = getTopSafeAreaPadding()
    
    Box(modifier = Modifier.fillMaxSize()) {
        GlassBackground(modifier = Modifier.fillMaxSize())
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(safeAreaPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Button - Glassmorphism style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    GlassColors.glowPurple.copy(alpha = 0.3f),
                                    GlassColors.glowPurple.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    GlassColors.glowPurple.copy(alpha = 0.6f),
                                    GlassColors.glowPurple.copy(alpha = 0.3f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = 28.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Title - Glassmorphism style
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎮",
                    fontSize = 80.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "English Quiz",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 56.sp,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(4.dp),
                        spotColor = GlassColors.glowCyan.copy(alpha = 0.5f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Kategorilerden birini seç ve başla!",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFF4ECDC4),
                                modifier = Modifier.size(56.dp),
                                strokeWidth = 5.dp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Kategoriler yükleniyor...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                
                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFFF6B6B).copy(alpha = 0.9f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚠️",
                                    fontSize = 64.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = uiState.errorMessage ?: "Bir hata oluştu",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = { viewModel.retry() },
                                    modifier = Modifier.height(50.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = "Tekrar Dene",
                                        color = Color(0xFFFF6B6B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
                
                else -> {
                    CategoryGrid(
                        categories = uiState.categories,
                        onCategoryClick = { category ->
                            onCategorySelected(category.id, category.name, category.icon)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryGrid(
    categories: List<Category>,
    onCategoryClick: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        modifier = modifier
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    // Glassmorphism glow renkleri
    val glowColors = listOf(
        GlassColors.glowCyan,
        GlassColors.glowPurple,
        GlassColors.glowPink,
        GlassColors.glowYellow,
        GlassColors.accentCyan,
        GlassColors.accentPurple
    )
    
    val colorIndex = (category.id.hashCode() % glowColors.size).let { 
        if (it < 0) -it else it 
    }
    val glowColor = glowColors[colorIndex]
    
    val infiniteTransition = rememberInfiniteTransition(label = "category_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "category_glow_alpha"
    )
    
    var isHovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.03f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onClick)
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = glowColor.copy(alpha = glowAlpha)
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        glowColor.copy(alpha = glowAlpha),
                        glowColor.copy(alpha = glowAlpha * 0.5f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.25f),
                            glowColor.copy(alpha = 0.15f),
                            Color(0xFFFFFFFF).copy(alpha = 0.1f),
                            Color(0xFF000000).copy(alpha = 0.15f)
                        )
                    )
                )
                .padding(28.dp),
            contentAlignment = Alignment.Center
        ) {
            // Decorative glass circle
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(70.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = category.icon ?: "📚",
                    fontSize = 80.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
