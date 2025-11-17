package com.alperenturker.englishcardgame.feature.quiz.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.alperenturker.englishcardgame.core.domain.model.Difficulty

@Composable
fun ResultScreen(
    correctAnswers: Int,
    wrongAnswers: Int,
    previousDifficulty: Difficulty?,
    currentDifficulty: Difficulty,
    categoryName: String,
    categoryIcon: String?,
    onBackToCategories: () -> Unit,
    onRestartQuiz: () -> Unit
) {
    val totalQuestions = correctAnswers + wrongAnswers
    val percentage = if (totalQuestions > 0) {
        (correctAnswers * 100) / totalQuestions
    } else 0
    
    val difficultyChanged = previousDifficulty != null && previousDifficulty != currentDifficulty
    val difficultyIncreased = difficultyChanged && 
        (previousDifficulty == Difficulty.EASY && currentDifficulty == Difficulty.MEDIUM ||
         previousDifficulty == Difficulty.MEDIUM && currentDifficulty == Difficulty.HARD)
    
    // Animated percentage
    var animatedPercentage by remember { mutableStateOf(0) }
    LaunchedEffect(percentage) {
        for (i in 0..percentage) {
            animatedPercentage = i
            kotlinx.coroutines.delay(20)
        }
    }
    
    // Celebration emoji based on percentage
    val celebrationEmoji = when {
        percentage >= 90 -> "🏆"
        percentage >= 70 -> "🎉"
        percentage >= 50 -> "👍"
        else -> "💪"
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        // Confetti effect (simple stars)
        Box(modifier = Modifier.fillMaxSize()) {
            repeat(30) {
                val offsetX = remember { (0..100).random() }
                val offsetY = remember { (0..100).random() }
                val size = remember { (3..8).random().dp }
                val delay = remember { (0..1000).random() }
                
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(delay.toLong())
                }
                
                Box(
                    modifier = Modifier
                        .offset(
                            x = (offsetX * 4).dp,
                            y = (offsetY * 8).dp
                        )
                        .size(size)
                        .background(
                            when (it % 4) {
                                0 -> Color(0xFFFF6B6B)
                                1 -> Color(0xFF4ECDC4)
                                2 -> Color(0xFFFFE66D)
                                else -> Color(0xFFFF6B9D)
                            }.copy(alpha = 0.6f),
                            RoundedCornerShape(50)
                        )
                )
            }
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Celebration Icon
            Text(
                text = "$celebrationEmoji ${categoryIcon ?: "🎯"}",
                fontSize = 96.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Title
            Text(
                text = "Quiz Tamamlandı!",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 36.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = categoryName,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Results Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(24.dp, RoundedCornerShape(28.dp)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E3F).copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E1E3F),
                                    Color(0xFF2A2A5A)
                                )
                            )
                        )
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Percentage Circle with gradient
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(80.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    colors = when {
                                        percentage >= 90 -> listOf(
                                            Color(0xFFFFE66D),
                                            Color(0xFFFFD93D),
                                            Color(0xFFFFB84D)
                                        )
                                        percentage >= 70 -> listOf(
                                            Color(0xFF4ECDC4),
                                            Color(0xFF44A08D),
                                            Color(0xFF4ECDC4)
                                        )
                                        percentage >= 50 -> listOf(
                                            Color(0xFFA8E6CF),
                                            Color(0xFF88D8A3),
                                            Color(0xFF6BCB94)
                                        )
                                        else -> listOf(
                                            Color(0xFFFF6B6B),
                                            Color(0xFFFF8E8E),
                                            Color(0xFFFF6B9D)
                                        )
                                    }
                                )
                            )
                            .shadow(16.dp, RoundedCornerShape(80.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$animatedPercentage%",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 56.sp
                                )
                            )
                            Text(
                                text = "Başarı",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Correct and Wrong Counts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ResultStatCard(
                            label = "Doğru",
                            count = correctAnswers,
                            color = Color(0xFF4ECDC4),
                            icon = "✓",
                            gradient = listOf(
                                Color(0xFF4ECDC4),
                                Color(0xFF44A08D)
                            )
                        )
                        ResultStatCard(
                            label = "Yanlış",
                            count = wrongAnswers,
                            color = Color(0xFFFF6B6B),
                            icon = "✗",
                            gradient = listOf(
                                Color(0xFFFF6B6B),
                                Color(0xFFFF5252)
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Difficulty Change Info
                    if (difficultyChanged) {
                        DifficultyChangeCard(
                            increased = difficultyIncreased,
                            previousDifficulty = previousDifficulty!!,
                            currentDifficulty = currentDifficulty
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onRestartQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4ECDC4)
                    )
                ) {
                    Text(
                        text = "🔄 Tekrar Oyna",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    )
                }
                
                OutlinedButton(
                    onClick = onBackToCategories,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = BorderStroke(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFE66D),
                                Color(0xFFFFD93D)
                            )
                        )
                    )
                ) {
                    Text(
                        text = "🏠 Kategorilere Dön",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun ResultStatCard(
    label: String,
    count: Int,
    color: Color,
    icon: String,
    gradient: List<Color>
) {
    var animatedCount by remember { mutableStateOf(0) }
    LaunchedEffect(count) {
        for (i in 0..count) {
            animatedCount = i
            kotlinx.coroutines.delay(50)
        }
    }
    
    Card(
        modifier = Modifier
            .width(140.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(colors = gradient)
                )
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = icon,
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "$animatedCount",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 36.sp
                    )
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

@Composable
fun DifficultyChangeCard(
    increased: Boolean,
    previousDifficulty: Difficulty,
    currentDifficulty: Difficulty
) {
    val (message, gradient) = if (increased) {
        "Seviyeniz Yükseltildi! 🎉" to listOf(
            Color(0xFF4ECDC4),
            Color(0xFF44A08D)
        )
    } else {
        "Seviye Düşürüldü" to listOf(
            Color(0xFFFFE66D),
            Color(0xFFFFD93D)
        )
    }
    
    val previousText = when (previousDifficulty) {
        Difficulty.EASY -> "🟢 Kolay"
        Difficulty.MEDIUM -> "🟡 Orta"
        Difficulty.HARD -> "🔴 Zor"
    }
    
    val currentText = when (currentDifficulty) {
        Difficulty.EASY -> "🟢 Kolay"
        Difficulty.MEDIUM -> "🟡 Orta"
        Difficulty.HARD -> "🔴 Zor"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(colors = gradient)
                )
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$previousText → $currentText",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
