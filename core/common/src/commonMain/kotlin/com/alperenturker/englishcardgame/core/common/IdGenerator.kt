package com.alperenturker.englishcardgame.core.common

import kotlinx.datetime.Clock
import kotlin.random.Random

/**
 * Generates unique IDs for entities
 */
object IdGenerator {
    fun generate(): String {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val random = Random.nextInt(1000, 9999)
        return "${timestamp}_${random}"
    }
    
    fun generateShort(): String {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        return timestamp.toString(36)
    }
}

