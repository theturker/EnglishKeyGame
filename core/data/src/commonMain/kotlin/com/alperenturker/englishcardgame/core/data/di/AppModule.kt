package com.alperenturker.englishcardgame.core.data.di

import com.alperenturker.englishcardgame.core.data.config.GroqApiKeyProvider
import com.alperenturker.englishcardgame.core.data.datasource.GroqRemoteDataSource
import com.alperenturker.englishcardgame.core.data.datasource.LocalQuestionDataSource
import com.alperenturker.englishcardgame.core.data.datasource.LocalQuestionDataSourceImpl
import com.alperenturker.englishcardgame.core.data.datasource.LocalUserProgressDataSource
import com.alperenturker.englishcardgame.core.data.datasource.LocalUserProgressDataSourceImpl
import com.alperenturker.englishcardgame.core.data.repository.QuestionRepositoryImpl
import com.alperenturker.englishcardgame.core.data.repository.UserProgressRepositoryImpl
import com.alperenturker.englishcardgame.core.domain.repository.QuestionRepository
import com.alperenturker.englishcardgame.core.domain.repository.UserProgressRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.russhwolf.settings.Settings

object AppModule {
    // Ktor HttpClient
    private val httpClient: HttpClient by lazy {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    encodeDefaults = false
                })
            }
        }
    }
    
    // Settings - platform-specific, will be provided via expect/actual
    private val settings: Settings = createAppSettings()
    
    // JSON
    private val json: Json by lazy {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = false
        }
    }
    
    // API Key Provider
    private val groqApiKeyProvider: GroqApiKeyProvider by lazy {
        GroqApiKeyProvider()
    }
    
    // Data Sources
    private val groqRemoteDataSource: GroqRemoteDataSource by lazy {
        GroqRemoteDataSource(httpClient, groqApiKeyProvider)
    }
    
    private val localQuestionDataSource: LocalQuestionDataSource by lazy {
        LocalQuestionDataSourceImpl()
    }
    
    private val localUserProgressDataSource: LocalUserProgressDataSource by lazy {
        LocalUserProgressDataSourceImpl(settings, json)
    }
    
    // Repositories
    val questionRepository: QuestionRepository by lazy {
        QuestionRepositoryImpl(groqRemoteDataSource, localQuestionDataSource)
    }
    
    val userProgressRepository: UserProgressRepository by lazy {
        UserProgressRepositoryImpl(localUserProgressDataSource)
    }
}

