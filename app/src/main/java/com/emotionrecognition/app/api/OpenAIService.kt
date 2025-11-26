package com.emotionrecognition.app.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Interface cho OpenAI API (DALL-E)
 */
interface OpenAIService {
    
    @Headers("Content-Type: application/json")
    @POST("v1/images/generations")
    suspend fun generateImage(@Body request: ImageGenerationRequest): ImageGenerationResponse
}

/**
 * Request body cho OpenAI DALL-E
 */
data class ImageGenerationRequest(
    val model: String = "dall-e-3",
    val prompt: String,
    val n: Int = 1,
    val size: String = "1024x1024",
    val quality: String = "standard",
    val response_format: String = "url"
)

/**
 * Response từ OpenAI DALL-E
 */
data class ImageGenerationResponse(
    val created: Long,
    val data: List<ImageData>
)

data class ImageData(
    val url: String
)
