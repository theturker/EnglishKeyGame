package com.alperenturker.englishcardgame.core.data.config

/**
 * Platform-specific API key provider interface
 * Her platform kendi implementasyonunu sağlamalı
 */
expect class GroqApiKeyProvider() {
    fun getApiKey(): String
}

