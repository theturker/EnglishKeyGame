package com.alperenturker.englishcardgame.core.common

import kotlin.random.Random

/**
 * Generates unique IDs for entities
 * Uses platform-specific time sources for cross-platform compatibility
 */
expect object IdGenerator {
    fun generate(): String
    fun generateShort(): String
}

/**
 * Gets current time in milliseconds (platform-specific implementation)
 */
expect fun getCurrentTimeMillis(): Long

