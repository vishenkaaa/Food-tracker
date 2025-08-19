package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val role: String,
    val content: List<Content>
)