package com.alperenturker.englishcardgame.core.data.di

import com.alperenturker.englishcardgame.core.data.datasource.SimpleSettings
import com.russhwolf.settings.Settings

actual fun createAppSettings(): Settings {
    // Android için SimpleSettings kullan (basit in-memory storage)
    // Production'da SharedPreferencesSettings kullanılabilir
    return SimpleSettings()
}

