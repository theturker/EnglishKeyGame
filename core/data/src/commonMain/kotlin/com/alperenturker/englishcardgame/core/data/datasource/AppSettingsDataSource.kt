package com.alperenturker.englishcardgame.core.data.datasource

import com.russhwolf.settings.Settings

interface AppSettingsDataSource {
    suspend fun hasCompletedOnboarding(): Boolean
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun hasCompletedLevelTest(): Boolean
    suspend fun setLevelTestCompleted(completed: Boolean)
    suspend fun getInitialDifficulty(): String?
    suspend fun setInitialDifficulty(difficulty: String)
}

class AppSettingsDataSourceImpl(
    private val settings: Settings
) : AppSettingsDataSource {
    
    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_LEVEL_TEST_COMPLETED = "level_test_completed"
        private const val KEY_INITIAL_DIFFICULTY = "initial_difficulty"
    }
    
    override suspend fun hasCompletedOnboarding(): Boolean {
        return settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
    
    override suspend fun setOnboardingCompleted(completed: Boolean) {
        settings.putBoolean(KEY_ONBOARDING_COMPLETED, completed)
    }
    
    override suspend fun hasCompletedLevelTest(): Boolean {
        return settings.getBoolean(KEY_LEVEL_TEST_COMPLETED, false)
    }
    
    override suspend fun setLevelTestCompleted(completed: Boolean) {
        settings.putBoolean(KEY_LEVEL_TEST_COMPLETED, completed)
    }
    
    override suspend fun getInitialDifficulty(): String? {
        return settings.getStringOrNull(KEY_INITIAL_DIFFICULTY)
    }
    
    override suspend fun setInitialDifficulty(difficulty: String) {
        settings.putString(KEY_INITIAL_DIFFICULTY, difficulty)
    }
    
    private fun Settings.getStringOrNull(key: String): String? {
        return if (hasKey(key)) {
            getString(key, "")
        } else {
            null
        }
    }
}

