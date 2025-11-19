package com.alperenturker.englishcardgame.feature.quiz.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Glassmorphism / Liquid Glass Design System
 * 2025 tarzı buzlu cam efektleri için tema ve yardımcı fonksiyonlar
 */

// Glassmorphism Renk Paleti
object GlassColors {
    // Arka plan gradientleri (liquid glass için)
    val backgroundGradient = listOf(
        Color(0xFF0A0E27), // Derin mavi-siyah
        Color(0xFF1A1A3E), // Koyu mor-mavi
        Color(0xFF16213E), // Orta mavi
        Color(0xFF0F3460)  // Açık mavi
    )
    
    val backgroundGradientVariant = listOf(
        Color(0xFF1A0B2E), // Mor tonları
        Color(0xFF16213E),
        Color(0xFF0F3460)
    )
    
    // Glass kart renkleri (yarı saydam)
    val glassCardLight = Color(0xFFFFFFFF).copy(alpha = 0.15f)
    val glassCardMedium = Color(0xFFFFFFFF).copy(alpha = 0.1f)
    val glassCardDark = Color(0xFF000000).copy(alpha = 0.2f)
    
    // Parlayan kenarlık renkleri
    val glowCyan = Color(0xFF00D9FF)
    val glowPurple = Color(0xFFB794F6)
    val glowPink = Color(0xFFFF6B9D)
    val glowYellow = Color(0xFFFFE66D)
    
    // Accent renkler
    val accentCyan = Color(0xFF4ECDC4)
    val accentPurple = Color(0xFFAA96DA)
    val accentPink = Color(0xFFFF6B9D)
    val accentYellow = Color(0xFFFFE66D)
}

/**
 * Glassmorphism kart komponenti
 * Buzlu cam efekti ile yarı saydam kart
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    glowColor: Color = GlassColors.glowCyan,
    glowIntensity: Float = 0.3f,
    content: @Composable ColumnScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = glowIntensity,
        targetValue = glowIntensity * 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    
    Card(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = shape,
                spotColor = glowColor.copy(alpha = glowAlpha)
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        glowColor.copy(alpha = glowAlpha),
                        glowColor.copy(alpha = glowAlpha * 0.5f),
                        Color.Transparent
                    )
                ),
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GlassColors.glassCardLight,
                            GlassColors.glassCardMedium,
                            GlassColors.glassCardDark
                        )
                    ),
                    shape = shape
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = content
            )
        }
    }
}

/**
 * Glassmorphism buton komponenti
 * Akışkan animasyonlu buton
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    icon: String? = null,
    enabled: Boolean = true,
    glowColor: Color = GlassColors.glowCyan,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp)
) {
    val scale = remember { mutableStateOf(1f) }
    val coroutineScope = rememberCoroutineScope()
    
    val animatedScale by animateFloatAsState(
        targetValue = scale.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "button_scale"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "button_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "button_glow_alpha"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clickable(enabled = enabled) {
                scale.value = 0.95f
                onClick()
                coroutineScope.launch {
                    delay(100)
                    scale.value = 1f
                }
            }
            .shadow(
                elevation = 16.dp,
                shape = shape,
                spotColor = glowColor.copy(alpha = glowAlpha)
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        glowColor.copy(alpha = glowAlpha),
                        glowColor.copy(alpha = glowAlpha * 0.6f)
                    )
                ),
                shape = shape
            )
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.3f),
                        glowColor.copy(alpha = 0.2f),
                        glowColor.copy(alpha = 0.15f)
                    )
                ),
                shape = shape
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Text(
                    text = icon,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
    }
}

/**
 * Glassmorphism arka plan
 * Blur efekti simülasyonu ile gradient arka plan
 */
@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    variant: Int = 0
) {
    val gradient = when (variant) {
        1 -> GlassColors.backgroundGradientVariant
        else -> GlassColors.backgroundGradient
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradient
                )
            )
    )
}

/**
 * Glassmorphism chip/badge
 */
@Composable
fun GlassChip(
    text: String,
    modifier: Modifier = Modifier,
    glowColor: Color = GlassColors.glowCyan,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chip_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "chip_glow_alpha"
    )
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.2f),
                        glowColor.copy(alpha = 0.1f)
                    )
                ),
                shape = shape
            )
            .border(
                width = 1.dp,
                color = glowColor.copy(alpha = glowAlpha),
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
    }
}

