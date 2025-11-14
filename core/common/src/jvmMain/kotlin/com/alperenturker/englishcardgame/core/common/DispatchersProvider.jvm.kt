package com.alperenturker.englishcardgame.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.swing.Swing

actual class DispatchersProvider {
    actual val main: CoroutineDispatcher = Dispatchers.Swing
    actual val io: CoroutineDispatcher = Dispatchers.IO
    actual val default: CoroutineDispatcher = Dispatchers.Default
}

