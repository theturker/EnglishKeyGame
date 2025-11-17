package com.alperenturker.englishcardgame.core.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
actual fun getTopSafeAreaPadding(): PaddingValues {
    // iOS için safe area genellikle 44-50dp civarı
    return PaddingValues(top = 0.dp) // iOS'ta genellikle ComposeView zaten safe area'yı handle ediyor
}

