package com.alperenturker.englishcardgame.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual class DispatchersProvider {
    actual val main: CoroutineDispatcher = Dispatchers.Main
    actual val io: CoroutineDispatcher = Dispatchers.Default
    actual val default: CoroutineDispatcher = Dispatchers.Default
}

