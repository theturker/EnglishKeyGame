package com.alperenturker.englishcardgame.core.data.config

actual class GroqApiKeyProvider {
    actual fun getApiKey(): String {
        // Web'de localStorage veya config'ten alınabilir
        // Şimdilik sabit değer kullanıyoruz
        return "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"
    }
}

