package com.alperenturker.englishcardgame.core.common

import kotlin.random.Random
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

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
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}

