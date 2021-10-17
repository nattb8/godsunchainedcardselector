package com.nattb8.godsunchainedcardselector.domain.model

enum class CosmeticQuality(val id: Int) {
    PLAIN(5), SHADOW(3)
}

data class Card(
    val id: Long,
    val name: String,
    val effect: String,
    val plainImageUrl: String,
    val shadowImageUrl: String
)