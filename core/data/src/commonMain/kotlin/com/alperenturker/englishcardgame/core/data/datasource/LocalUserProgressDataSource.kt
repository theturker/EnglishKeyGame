package com.alperenturker.englishcardgame.core.data.datasource

import com.alperenturker.englishcardgame.core.data.mapper.toDomain
import com.alperenturker.englishcardgame.core.data.mapper.toSerializable
import com.alperenturker.englishcardgame.core.data.mapper.UserProgressSerializable
import com.alperenturker.englishcardgame.core.domain.model.UserProgress
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface LocalUserProgressDataSource {
    suspend fun getProgress(categoryId: String): UserProgress?
    suspend fun saveProgress(progress: UserProgress)
    suspend fun getAllProgress(): List<UserProgress>
}

class LocalUserProgressDataSourceImpl(
    private val settings: Settings,
    private val json: Json
) : LocalUserProgressDataSource {
    
    companion object {
        private const val PROGRESS_PREFIX = "user_progress_"
        private const val ALL_PROGRESS_KEYS = "all_progress_keys"
    }
    
    override suspend fun getProgress(categoryId: String): UserProgress? {
        val key = "$PROGRESS_PREFIX$categoryId"
        val jsonString = settings.getStringOrNull(key) ?: return null
        return try {
            val serializable = json.decodeFromString<UserProgressSerializable>(jsonString)
            serializable.toDomain()
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun saveProgress(progress: UserProgress) {
        val key = "$PROGRESS_PREFIX${progress.categoryId}"
        val serializable = progress.toSerializable()
        val jsonString = json.encodeToString(serializable)
        settings[key] = jsonString
        
        // Store the key in the list of all progress keys
        val allKeys = getAllProgressKeys()
        if (!allKeys.contains(progress.categoryId)) {
            allKeys.add(progress.categoryId)
            settings[ALL_PROGRESS_KEYS] = json.encodeToString(allKeys)
        }
    }
    
    override suspend fun getAllProgress(): List<UserProgress> {
        val allKeys = getAllProgressKeys()
        return allKeys.mapNotNull { categoryId ->
            getProgress(categoryId)
        }
    }
    
    private fun getAllProgressKeys(): MutableList<String> {
        val keysJson = settings.getStringOrNull(ALL_PROGRESS_KEYS) ?: return mutableListOf()
        return try {
            json.decodeFromString<MutableList<String>>(keysJson)
        } catch (e: Exception) {
            mutableListOf()
        }
    }
}

// Settings extension for nullable string
private fun Settings.getStringOrNull(key: String): String? {
    return if (hasKey(key)) {
        get<String>(key)
    } else {
        null
    }
}

