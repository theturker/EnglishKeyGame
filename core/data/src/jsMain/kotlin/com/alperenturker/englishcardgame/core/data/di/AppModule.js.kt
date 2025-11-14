package com.alperenturker.englishcardgame.core.data.di

import com.alperenturker.englishcardgame.core.data.datasource.SimpleSettings
import com.russhwolf.settings.Settings

actual fun createAppSettings(): Settings {
    // Web/JS için SimpleSettings kullan (basit in-memory storage)
    // Production'da LocalStorageSettings kullanılabilir
    return SimpleSettings()
}

