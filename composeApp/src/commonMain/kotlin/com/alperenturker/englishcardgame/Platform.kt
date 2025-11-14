package com.alperenturker.englishcardgame

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform