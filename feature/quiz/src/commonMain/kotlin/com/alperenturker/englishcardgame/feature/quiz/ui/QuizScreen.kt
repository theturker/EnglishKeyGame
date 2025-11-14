package com.alperenturker.englishcardgame.feature.quiz.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.graphicsLayer
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
            // Header - Only show if not completed
            if (!uiState.isQuizCompleted) {
                QuizHeader(
                    categoryName = categoryName,
                    categoryIcon = categoryIcon,
                    difficulty = uiState.difficulty,
                    score = uiState.score,
                    totalAnswered = uiState.totalAnswered,
                    currentQuestionNumber = uiState.currentQuestionNumber,
                    totalQuestions = uiState.totalQuestions,
                    onBackClick = onBackClick
                )
            }
            
            // Content
            when {
                uiState.isQuizCompleted -> {
                    ResultScreen(
                        correctAnswers = uiState.correctAnswers,
                        wrongAnswers = uiState.wrongAnswers,
                        previousDifficulty = uiState.previousDifficulty,
                        currentDifficulty = uiState.difficulty,
                        categoryName = categoryName,
                        categoryIcon = categoryIcon,
                        onBackToCategories = onBackClick,
                        onRestartQuiz = { viewModel.restartQuiz() }
                    )
                }
                
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(48.dp),
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Soru hazırlanıyor...",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
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
                            text = "⚠️",
                            fontSize = 64.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            text = uiState.errorMessage ?: "Bir hata oluştu",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        Button(
                            onClick = { viewModel.retry() },
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Tekrar Dene",
                                color = Color(0xFF667eea),
                                fontWeight = FontWeight.Bold
                            )
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
    currentQuestionNumber: Int,
    totalQuestions: Int,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.98f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            fontSize = 28.sp,
                            color = Color(0xFF667eea),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = categoryIcon ?: "📚",
                        fontSize = 28.sp,
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
            
            // Progress Bar
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { currentQuestionNumber.toFloat() / totalQuestions.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFF667eea),
                trackColor = Color(0xFF667eea).copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Soru $currentQuestionNumber / $totalQuestions",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
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
        Spacer(modifier = Modifier.height(24.dp))
        
        // Question Card with better styling
        AnimatedVisibility(
            visible = question.text.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(300)) + 
                    slideInVertically(
                        initialOffsetY = { -it / 2 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
            exit = fadeOut(animationSpec = tween(200)) + 
                   slideOutVertically(animationSpec = tween(200))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF667eea).copy(alpha = 0.05f),
                                    Color(0xFF764ba2).copy(alpha = 0.05f)
                                )
                            )
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            lineHeight = 32.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = Color(0xFF2D3748)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Answer Options with staggered animation
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            question.options.forEachIndexed { index, option ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(300, delayMillis = index * 100)
                    ) + slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    AnswerOptionCard(
                        option = option,
                        isSelected = uiState.selectedAnswerId == option.id,
                        showFeedback = uiState.showFeedback,
                        isCorrect = uiState.isAnswerCorrect == true && option.isCorrect,
                        onClick = { if (uiState.selectedAnswerId == null) onAnswerSelected(option) }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
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
        showFeedback && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.15f)
        showFeedback && isSelected && !option.isCorrect -> Color(0xFFF44336).copy(alpha = 0.15f)
        isSelected -> Color(0xFF667eea).copy(alpha = 0.15f)
        else -> Color.White
    }
    
    val borderColor = when {
        showFeedback && isCorrect -> Color(0xFF4CAF50)
        showFeedback && isSelected && !option.isCorrect -> Color(0xFFF44336)
        isSelected -> Color(0xFF667eea)
        else -> Color.Gray.copy(alpha = 0.2f)
    }
    
    val borderWidth = when {
        showFeedback && isCorrect -> 3.dp
        showFeedback && isSelected && !option.isCorrect -> 3.dp
        isSelected -> 2.dp
        else -> 1.dp
    }
    
    val scale = if (isSelected) 1.02f else 1f
    
    androidx.compose.animation.AnimatedContent(
        targetState = Triple(isSelected, showFeedback, isCorrect),
        transitionSpec = {
            (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
        },
        label = "answer_option"
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable(enabled = !showFeedback, onClick = onClick)
                .shadow(
                    elevation = if (isSelected || showFeedback) 8.dp else 4.dp,
                    shape = RoundedCornerShape(18.dp),
                    spotColor = borderColor.copy(alpha = 0.5f)
                )
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = borderWidth,
                color = borderColor
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Option ID Badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    showFeedback && isCorrect -> Color(0xFF4CAF50)
                                    showFeedback && isSelected && !option.isCorrect -> Color(0xFFF44336)
                                    isSelected -> Color(0xFF667eea)
                                    else -> Color(0xFF667eea).copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.id.uppercase(),
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    showFeedback && isCorrect -> Color.White
                                    showFeedback && isSelected && !option.isCorrect -> Color.White
                                    isSelected -> Color.White
                                    else -> Color(0xFF667eea)
                                }
                            )
                        )
                    }
                    
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected || showFeedback) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 16.sp,
                            color = Color(0xFF2D3748)
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Feedback Icon
                AnimatedVisibility(
                    visible = showFeedback,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                when {
                                    isCorrect -> Color(0xFF4CAF50)
                                    isSelected && !option.isCorrect -> Color(0xFFF44336)
                                    else -> Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isCorrect) "✓" else if (isSelected && !option.isCorrect) "✗" else "",
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

