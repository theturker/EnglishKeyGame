package com.alperenturker.englishcardgame.feature.quiz.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.alperenturker.englishcardgame.core.domain.model.AnswerOption
import com.alperenturker.englishcardgame.core.domain.model.LevelTestQuestion

@Composable
fun LevelTestScreen(
    questions: List<LevelTestQuestion>,
    currentQuestionIndex: Int,
    selectedAnswerId: String?,
    showResult: Boolean,
    score: Int,
    onAnswerSelected: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onComplete: () -> Unit,
    onResultComplete: () -> Unit
) {
    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    
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
        if (showResult) {
            // Result Screen
            LevelTestResultScreen(
                score = score,
                totalQuestions = questions.size,
                onComplete = onResultComplete
            )
        } else if (currentQuestion != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Progress Indicator
                Text(
                    text = "Question ${currentQuestionIndex + 1} / ${questions.size}",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF0F3460))
                ) {
                    val progress = (currentQuestionIndex + 1).toFloat() / questions.size
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4ECDC4),
                                        Color(0xFF44A08D)
                                    )
                                )
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Question Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(20.dp, RoundedCornerShape(28.dp)),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFFFE66D).copy(alpha = 0.1f),
                                        Color(0xFF4ECDC4).copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentQuestion.text,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                lineHeight = 34.sp
                            ),
                            textAlign = TextAlign.Center,
                            color = Color(0xFF1A1A2E)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Answer Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    currentQuestion.options.forEachIndexed { index, option ->
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
                            LevelTestAnswerOption(
                                option = option,
                                isSelected = selectedAnswerId == option.id,
                                onClick = { if (selectedAnswerId == null) onAnswerSelected(option.id) }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Next Button
                if (selectedAnswerId != null) {
                    Button(
                        onClick = {
                            if (currentQuestionIndex < questions.size - 1) {
                                onNextQuestion()
                            } else {
                                // Last question - complete test
                                onComplete()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4ECDC4)
                        )
                    ) {
                        Text(
                            text = if (currentQuestionIndex < questions.size - 1) "Next Question" else "Complete Test",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun LevelTestAnswerOption(
    option: AnswerOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "option_scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(onClick = onClick)
            .shadow(
                elevation = if (isSelected) 12.dp else 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = if (isSelected) Color(0xFF4ECDC4).copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) Color(0xFF4ECDC4) else Color.Gray.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isSelected) {
                            listOf(
                                Color(0xFF4ECDC4).copy(alpha = 0.2f),
                                Color(0xFF44A08D).copy(alpha = 0.2f)
                            )
                        } else {
                            listOf(
                                Color.White,
                                Color(0xFFF5F5F5)
                            )
                        }
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Option ID Badge
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.radialGradient(
                                colors = if (isSelected) {
                                    listOf(
                                        Color(0xFF4ECDC4),
                                        Color(0xFF44A08D)
                                    )
                                } else {
                                    listOf(
                                        Color(0xFF1E1E3F),
                                        Color(0xFF2A2A5A)
                                    )
                                }
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.id.uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    )
                }
                
                Text(
                    text = option.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 17.sp,
                        color = if (isSelected) Color(0xFF1A1A2E) else Color(0xFF1A1A2E)
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun LevelTestResultScreen(
    score: Int,
    totalQuestions: Int,
    onComplete: () -> Unit
) {
    val percentage = (score.toFloat() / totalQuestions) * 100f
    val difficulty = when {
        percentage <= 30f -> "EASY"
        percentage <= 60f -> "MEDIUM"
        else -> "HARD"
    }
    
    val difficultyColor = when (difficulty) {
        "EASY" -> Color(0xFF4ECDC4)
        "MEDIUM" -> Color(0xFFFFE66D)
        else -> Color(0xFFFF6B6B)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Result Icon
        Text(
            text = "🎉",
            fontSize = 100.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Score
        Text(
            text = "Your Score",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 20.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "$score / $totalQuestions",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 56.sp
            ),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Difficulty Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = difficultyColor.copy(alpha = 0.2f)
            ),
            border = BorderStroke(2.dp, difficultyColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Level",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = difficulty,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = difficultyColor,
                        fontSize = 32.sp
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Continue Button
        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4ECDC4)
            )
        ) {
            Text(
                text = "Continue",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

