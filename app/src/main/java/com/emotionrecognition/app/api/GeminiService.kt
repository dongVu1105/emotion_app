package com.emotionrecognition.app.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GeminiService {
    @POST("v1beta/models/imagen-3.0-fast-generate-001:generateImage")
    suspend fun generateImage(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiImageRequest
    ): GeminiImageResponse
}

data class GeminiImageRequest(
    val prompt: String,
    val aspectRatio: String = "1:1"
)

data class GeminiImageResponse(
    val images: List<GeminiImageResult>?
)

data class GeminiImageResult(
    val imageBytes: String,
    val mimeType: String
)
