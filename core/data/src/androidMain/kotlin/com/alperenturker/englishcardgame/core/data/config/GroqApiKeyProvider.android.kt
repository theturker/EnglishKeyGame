package com.alperenturker.englishcardgame.core.data.config

actual class GroqApiKeyProvider {
    actual fun getApiKey(): String {
        // Android'de BuildConfig veya resources'tan alınabilir
        // Şimdilik sabit değer kullanıyoruz
        return "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"
    }
}

