package com.alperenturker.englishcardgame.core.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
actual fun getTopSafeAreaPadding(): PaddingValues {
    // Desktop'ta safe area gerekmez
    return PaddingValues(top = 0.dp)
}

