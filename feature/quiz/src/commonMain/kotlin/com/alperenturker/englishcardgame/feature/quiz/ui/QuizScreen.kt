package com.alperenturker.englishcardgame.feature.quiz.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.alperenturker.englishcardgame.feature.quiz.viewmodel.QuizUiState
import com.alperenturker.englishcardgame.feature.quiz.viewmodel.QuizViewModel

@Composable
fun QuizScreen(
    categoryId: String,
    categoryName: String,
    categoryIcon: String?,
    onBackClick: () -> Unit,
    viewModel: QuizViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    
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
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            QuizHeader(
                categoryName = categoryName,
                categoryIcon = categoryIcon,
                difficulty = uiState.difficulty,
                score = uiState.score,
                totalAnswered = uiState.totalAnswered,
                onBackClick = onBackClick
            )
            
            // Content
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                
                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "Bir hata oluştu",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.retry() }) {
                            Text("Tekrar Dene")
                        }
                    }
                }
                
                uiState.currentQuestion != null -> {
                    QuestionContent(
                        uiState = uiState,
                        onAnswerSelected = { answer ->
                            viewModel.onAnswerSelected(answer)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun QuizHeader(
    categoryName: String,
    categoryIcon: String?,
    difficulty: Difficulty,
    score: Int,
    totalAnswered: Int,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBackClick) {
                    Text("←", style = MaterialTheme.typography.titleLarge, color = Color(0xFF667eea))
                }
                Text(
                    text = categoryIcon ?: "📚",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Column {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    DifficultyChip(difficulty = difficulty)
                }
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Puan: $score",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF667eea)
                        )
                    )
                    Text(
                        text = "Toplam: $totalAnswered",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun DifficultyChip(difficulty: Difficulty) {
    val (text, color) = when (difficulty) {
        Difficulty.EASY -> "Kolay" to Color(0xFF4CAF50)
        Difficulty.MEDIUM -> "Orta" to Color(0xFFFF9800)
        Difficulty.HARD -> "Zor" to Color(0xFFF44336)
    }
    
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(top = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
fun QuestionContent(
    uiState: QuizUiState,
    onAnswerSelected: (com.alperenturker.englishcardgame.core.domain.model.AnswerOption) -> Unit
) {
    val question = uiState.currentQuestion ?: return
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Question Card
        AnimatedVisibility(
            visible = question.text.isNotEmpty(),
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Answer Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            question.options.forEach { option ->
                AnswerOptionCard(
                    option = option,
                    isSelected = uiState.selectedAnswerId == option.id,
                    showFeedback = uiState.showFeedback,
                    isCorrect = uiState.isAnswerCorrect == true && option.isCorrect,
                    onClick = { if (uiState.selectedAnswerId == null) onAnswerSelected(option) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AnswerOptionCard(
    option: com.alperenturker.englishcardgame.core.domain.model.AnswerOption,
    isSelected: Boolean,
    showFeedback: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        showFeedback && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.3f)
        showFeedback && isSelected && !option.isCorrect -> Color(0xFFF44336).copy(alpha = 0.3f)
        isSelected -> Color(0xFF667eea).copy(alpha = 0.3f)
        else -> Color.White
    }
    
    val borderColor = when {
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !option.isCorrect -> Color(0xFFF44336)
        isSelected -> Color(0xFF667eea)
        else -> Color.Gray.copy(alpha = 0.3f)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable(onClick = onClick)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${option.id.uppercase()}. ${option.text}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                modifier = Modifier.weight(1f)
            )
            
            if (showFeedback && isCorrect) {
                Text(
                    text = "✓",
                    fontSize = 24.sp,
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold
                )
            } else if (showFeedback && isSelected && !option.isCorrect) {
                Text(
                    text = "✗",
                    fontSize = 24.sp,
                    color = Color(0xFFF44336),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

