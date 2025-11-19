package com.alperenturker.englishcardgame.feature.quiz.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.alperenturker.englishcardgame.core.domain.model.Difficulty
import com.alperenturker.englishcardgame.feature.quiz.ui.theme.*
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
    val safeAreaPadding = getTopSafeAreaPadding()
    
    Box(modifier = Modifier.fillMaxSize()) {
        GlassBackground(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(safeAreaPadding)
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
                                color = Color(0xFF4ECDC4),
                                modifier = Modifier.size(56.dp),
                                strokeWidth = 5.dp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Soru hazırlanıyor...",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 18.sp
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
                        Card(
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
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
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
    val progress = currentQuestionNumber.toFloat() / totalQuestions.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "progress"
    )
    
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        glowColor = GlassColors.glowCyan,
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        GlassColors.glowCyan.copy(alpha = 0.3f),
                                        GlassColors.glowCyan.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                color = GlassColors.glowCyan.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "←",
                            fontSize = 28.sp,
                            color = GlassColors.glowCyan,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = categoryIcon ?: "📚",
                        fontSize = 36.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column {
                        Text(
                            text = categoryName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        )
                        DifficultyChip(difficulty = difficulty)
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⭐",
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$score",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFE66D),
                                fontSize = 22.sp
                            )
                        )
                    }
                    Text(
                        text = "Toplam: $totalAnswered",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    )
                }
            }
            
            // Progress Bar
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F3460))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF4ECDC4),
                                    Color(0xFF44A08D),
                                    Color(0xFF4ECDC4)
                                )
                            )
                        )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Soru $currentQuestionNumber / $totalQuestions",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DifficultyChip(difficulty: Difficulty) {
    val (text, glowColor) = when (difficulty) {
        Difficulty.EASY -> "🟢 Kolay" to GlassColors.glowCyan
        Difficulty.MEDIUM -> "🟡 Orta" to GlassColors.glowYellow
        Difficulty.HARD -> "🔴 Zor" to GlassColors.glowPink
    }
    
    GlassChip(
        text = text,
        glowColor = glowColor,
        modifier = Modifier.padding(top = 4.dp),
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
fun QuestionContent(
    uiState: QuizUiState,
    onAnswerSelected: (com.alperenturker.englishcardgame.core.domain.model.AnswerOption) -> Unit
) {
    val question = uiState.currentQuestion ?: return
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Question Card
        AnimatedVisibility(
            visible = question.text.isNotEmpty(),
            enter = fadeIn(animationSpec = tween(400)) + 
                    scaleIn(
                        initialScale = 0.9f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
            exit = fadeOut() + scaleOut()
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowColor = GlassColors.glowYellow,
                shape = RoundedCornerShape(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            lineHeight = 40.sp
                        ),
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Answer Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            question.options.forEachIndexed { index, option ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(400, delayMillis = index * 100)
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
    val glowColor = when {
        showFeedback && isCorrect -> GlassColors.glowCyan
        showFeedback && isSelected && !option.isCorrect -> GlassColors.glowPink
        isSelected -> GlassColors.glowYellow
        else -> GlassColors.glowCyan.copy(alpha = 0.3f)
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "option_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (showFeedback || isSelected) 0.6f else 0.3f,
        targetValue = if (showFeedback || isSelected) 0.9f else 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "option_glow_alpha"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected || showFeedback) 1.03f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "option_scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(enabled = !showFeedback, onClick = onClick)
            .shadow(
                elevation = if (showFeedback || isSelected) 20.dp else 12.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = glowColor.copy(alpha = glowAlpha)
            )
            .border(
                width = if (showFeedback || isSelected) 2.5.dp else 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        glowColor.copy(alpha = glowAlpha),
                        glowColor.copy(alpha = glowAlpha * 0.6f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = when {
                            showFeedback && isCorrect -> listOf(
                                GlassColors.glowCyan.copy(alpha = 0.3f),
                                GlassColors.glowCyan.copy(alpha = 0.2f),
                                Color(0xFFFFFFFF).copy(alpha = 0.1f)
                            )
                            showFeedback && isSelected && !option.isCorrect -> listOf(
                                GlassColors.glowPink.copy(alpha = 0.3f),
                                GlassColors.glowPink.copy(alpha = 0.2f),
                                Color(0xFFFFFFFF).copy(alpha = 0.1f)
                            )
                            isSelected -> listOf(
                                GlassColors.glowYellow.copy(alpha = 0.3f),
                                GlassColors.glowYellow.copy(alpha = 0.2f),
                                Color(0xFFFFFFFF).copy(alpha = 0.1f)
                            )
                            else -> listOf(
                                GlassColors.glassCardLight,
                                GlassColors.glassCardMedium,
                                GlassColors.glassCardDark
                            )
                        }
                    )
                )
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Option ID Badge - Glassmorphism style
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                brush = Brush.radialGradient(
                                    colors = when {
                                        showFeedback && isCorrect -> listOf(
                                            GlassColors.glowCyan.copy(alpha = 0.4f),
                                            GlassColors.glowCyan.copy(alpha = 0.2f)
                                        )
                                        showFeedback && isSelected && !option.isCorrect -> listOf(
                                            GlassColors.glowPink.copy(alpha = 0.4f),
                                            GlassColors.glowPink.copy(alpha = 0.2f)
                                        )
                                        isSelected -> listOf(
                                            GlassColors.glowYellow.copy(alpha = 0.4f),
                                            GlassColors.glowYellow.copy(alpha = 0.2f)
                                        )
                                        else -> listOf(
                                            GlassColors.glowCyan.copy(alpha = 0.2f),
                                            GlassColors.glowCyan.copy(alpha = 0.1f)
                                        )
                                    }
                                )
                            )
                            .border(
                                width = 1.5.dp,
                                color = glowColor.copy(alpha = glowAlpha * 0.8f),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.id.uppercase(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        )
                    }
                    
                    // Text - Glassmorphism style
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected || showFeedback) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 18.sp,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentHeight()
                    )
                }
                
                // Feedback Icon
                AnimatedVisibility(
                    visible = showFeedback,
                    enter = scaleIn(
                        initialScale = 0.5f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    if (isCorrect || (isSelected && !option.isCorrect)) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = if (isCorrect) {
                                            listOf(
                                                Color(0xFF4ECDC4),
                                                Color(0xFF44A08D)
                                            )
                                        } else {
                                            listOf(
                                                Color(0xFFFF6B6B),
                                                Color(0xFFFF5252)
                                            )
                                        }
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isCorrect) "✓" else "✗",
                                fontSize = 32.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
