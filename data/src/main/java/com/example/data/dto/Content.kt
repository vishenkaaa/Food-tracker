package com.example.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class Content(
    val type: String,
    val text: String? = null,
    val image_url: String? = null
){
    @Serializable
    data class ImageUrl(
        val url: String
    )
}