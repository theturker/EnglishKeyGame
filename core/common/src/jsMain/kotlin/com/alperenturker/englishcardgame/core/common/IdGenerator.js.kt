package com.alperenturker.englishcardgame.core.common

import kotlin.random.Random

actual object IdGenerator {
    actual fun generate(): String {
        val timestamp = getCurrentTimeMillis()
        val random = Random.nextInt(1000, 9999)
        return "${timestamp}_${random}"
    }
    
    actual fun generateShort(): String {
        val timestamp = getCurrentTimeMillis()
        return timestamp.toString(36)
    }
}

actual fun getCurrentTimeMillis(): Long {
    return kotlin.js.Date.now().toLong()
}

