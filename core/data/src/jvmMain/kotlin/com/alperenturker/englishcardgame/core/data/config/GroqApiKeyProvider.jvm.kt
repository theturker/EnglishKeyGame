package com.alperenturker.englishcardgame.core.data.config

actual class GroqApiKeyProvider {
    actual fun getApiKey(): String {
        // JVM/Desktop'ta environment variable veya config file'dan alınabilir
        // Şimdilik sabit değer kullanıyoruz
        return System.getenv("GROQ_API_KEY") 
            ?: "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"
    }
}

