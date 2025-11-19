package com.alperenturker.englishcardgame.feature.quiz.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alperenturker.englishcardgame.core.common.getTopSafeAreaPadding
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.core.domain.model.UserProgress
import com.alperenturker.englishcardgame.feature.quiz.ui.theme.*
import com.alperenturker.englishcardgame.feature.quiz.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val safeAreaPadding = getTopSafeAreaPadding()
    
    // Refresh data when screen is shown
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        GlassBackground(modifier = Modifier.fillMaxSize(), variant = 1)
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFF4ECDC4),
                    modifier = Modifier.size(56.dp),
                    strokeWidth = 5.dp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(safeAreaPadding)
            ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "←",
                            fontSize = 24.sp,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "Profil",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 28.sp
                    )
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Spacer(modifier = Modifier.size(48.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Level Card - Glassmorphism
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                glowColor = getDifficultyColor(uiState.currentLevel),
                shape = RoundedCornerShape(28.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎯",
                            fontSize = 64.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Mevcut Seviye",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF1A1A2E).copy(alpha = 0.7f),
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = getDifficultyText(uiState.currentLevel),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = getDifficultyColor(uiState.currentLevel),
                                fontSize = 36.sp
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Overall Stats - Glassmorphism
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                glowColor = GlassColors.glowPurple,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp)
                ) {
                    Text(
                        text = "Genel İstatistikler",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    StatRow(
                        label = "Toplam Soru",
                        value = uiState.totalAnswered.toString(),
                        icon = "📝",
                        textColor = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    StatRow(
                        label = "Doğru Cevap",
                        value = uiState.totalCorrect.toString(),
                        icon = "✅",
                        textColor = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    StatRow(
                        label = "Başarı Oranı",
                        value = "${(uiState.accuracy * 100).toInt()}%",
                        icon = "📊",
                        textColor = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Category Progress
            if (uiState.categoryProgress.isNotEmpty()) {
                Text(
                    text = "Kategori İlerlemeleri",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                uiState.categoryProgress.forEach { progress ->
                    CategoryProgressCard(
                        progress = progress,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📚",
                            fontSize = 48.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = "Henüz kategori oynamadınız",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 16.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StatRow(
    label: String,
    value: String,
    icon: String,
    textColor: Color = Color.White
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = icon,
                fontSize = 28.sp
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = textColor.copy(alpha = 0.9f),
                    fontSize = 18.sp
                )
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 22.sp
            )
        )
    }
}

@Composable
fun CategoryProgressCard(
    progress: UserProgress,
    modifier: Modifier = Modifier
) {
    val categoryName = getCategoryName(progress.categoryId)
    val categoryIcon = getCategoryIcon(progress.categoryId)
    val accuracy = if (progress.totalAnswered > 0) {
        (progress.totalCorrect.toFloat() / progress.totalAnswered) * 100f
    } else 0f
    
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        glowColor = GlassColors.glowCyan,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = categoryIcon,
                        fontSize = 36.sp
                    )
                    Column {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassChip(
                            text = getDifficultyText(progress.currentDifficulty),
                            glowColor = getDifficultyColor(progress.currentDifficulty),
                            modifier = Modifier,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${accuracy.toInt()}%",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 22.sp
                        )
                    )
                    Text(
                        text = "${progress.totalCorrect}/${progress.totalAnswered}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar - Glassmorphism
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(5.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(accuracy / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    GlassColors.glowCyan.copy(alpha = 0.6f),
                                    GlassColors.glowCyan.copy(alpha = 0.4f)
                                )
                            )
                        )
                )
            }
        }
    }
}

fun getDifficultyText(difficulty: Difficulty): String {
    return when (difficulty) {
        Difficulty.EASY -> "Başlangıç"
        Difficulty.MEDIUM -> "Orta"
        Difficulty.HARD -> "İleri"
    }
}

fun getDifficultyColor(difficulty: Difficulty): Color {
    return when (difficulty) {
        Difficulty.EASY -> Color(0xFF4ECDC4)
        Difficulty.MEDIUM -> Color(0xFFFFE66D)
        Difficulty.HARD -> Color(0xFFFF6B6B)
    }
}

fun getCategoryName(categoryId: String): String {
    return when (categoryId) {
        "games" -> "Oyunlar"
        "movies" -> "Filmler"
        "culture" -> "Kültür"
        "science" -> "Bilim"
        "sports" -> "Spor"
        "travel" -> "Seyahat"
        "food" -> "Yemek"
        "technology" -> "Teknoloji"
        else -> categoryId
    }
}

fun getCategoryIcon(categoryId: String): String {
    return when (categoryId) {
        "games" -> "🎮"
        "movies" -> "🎬"
        "culture" -> "🎨"
        "science" -> "🔬"
        "sports" -> "⚽"
        "travel" -> "✈️"
        "food" -> "🍕"
        "technology" -> "💻"
        else -> "📚"
    }
}

