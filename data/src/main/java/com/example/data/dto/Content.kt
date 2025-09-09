package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
){
    @Serializable
    data class ImageUrl(
        val url: String
    )
}