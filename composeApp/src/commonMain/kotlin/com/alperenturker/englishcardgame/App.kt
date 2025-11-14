package com.alperenturker.englishcardgame

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.alperenturker.englishcardgame.feature.quiz.navigation.AppNavigation

@Composable
fun App() {
    MaterialTheme {
        AppNavigation(modifier = Modifier.fillMaxSize())
    }
}