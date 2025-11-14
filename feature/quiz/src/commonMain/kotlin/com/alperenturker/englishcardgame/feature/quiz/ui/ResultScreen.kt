package com.alperenturker.englishcardgame.feature.quiz.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Category Icon
            AnimatedContent(
                targetState = categoryIcon,
                transitionSpec = {
                    fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
                }
            ) { icon ->
                Text(
                    text = icon ?: "🎉",
                    fontSize = 80.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Title
            Text(
                text = "Quiz Tamamlandı!",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = categoryName,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White.copy(alpha = 0.9f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // Results Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Percentage Circle
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(60.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF4CAF50).copy(alpha = 0.3f),
                                        Color(0xFF4CAF50)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Başarı",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Correct and Wrong Counts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ResultStatCard(
                            label = "Doğru",
                            count = correctAnswers,
                            color = Color(0xFF4CAF50),
                            icon = "✓"
                        )
                        ResultStatCard(
                            label = "Yanlış",
                            count = wrongAnswers,
                            color = Color(0xFFF44336),
                            icon = "✗"
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onRestartQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        text = "Tekrar Oyna",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667eea)
                        )
                    )
                }
                
                OutlinedButton(
                    onClick = onBackToCategories,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.White, Color.White.copy(alpha = 0.8f))
                        )
                    )
                ) {
                    Text(
                        text = "Kategorilere Dön",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
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
    icon: String
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
fun DifficultyChangeCard(
    increased: Boolean,
    previousDifficulty: Difficulty,
    currentDifficulty: Difficulty
) {
    val (message, color) = if (increased) {
        "Seviyeniz Yükseltildi! 🎉" to Color(0xFF4CAF50)
    } else {
        "Seviye Düşürüldü" to Color(0xFFFF9800)
    }
    
    val previousText = when (previousDifficulty) {
        Difficulty.EASY -> "Kolay"
        Difficulty.MEDIUM -> "Orta"
        Difficulty.HARD -> "Zor"
    }
    
    val currentText = when (currentDifficulty) {
        Difficulty.EASY -> "Kolay"
        Difficulty.MEDIUM -> "Orta"
        Difficulty.HARD -> "Zor"
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$previousText → $currentText",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

