package com.alperenturker.englishcardgame.core.data.di

import com.alperenturker.englishcardgame.core.data.datasource.SimpleSettings
import com.russhwolf.settings.Settings

actual fun createAppSettings(): Settings {
    // iOS için SimpleSettings kullan (basit in-memory storage)
    // Production'da NSUserDefaultsSettings kullanılabilir
    return SimpleSettings()
}

