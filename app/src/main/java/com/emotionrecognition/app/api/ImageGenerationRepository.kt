package com.emotionrecognition.app.api

import android.content.Context
import android.util.Base64
import com.emotionrecognition.app.model.EmotionType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

/**
 * Repository để quản lý việc sinh ảnh từ API
 */
class ImageGenerationRepository(private val apiKey: String, private val context: Context) {
    
    companion object {
        private const val TAG = "ImageGenRepository"
    }
    
    private val geminiService: GeminiService by lazy {
        createRetrofit("https://generativelanguage.googleapis.com/")
            .create(GeminiService::class.java)
    }
    
    /**
     * Tạo Retrofit instance
     */
    private fun createRetrofit(baseUrl: String): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Sinh ảnh cho một cảm xúc cụ thể
     */
    suspend fun generateImageForEmotion(emotion: EmotionType): Result<String> {
        return try {
            if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY_HERE") {
                android.util.Log.i(TAG, "API key not configured, using placeholder image for ${emotion.vietnameseName}")
                Result.success(getPlaceholderImageUrl(emotion))
            } else {
                android.util.Log.d(TAG, "Generating image for emotion: ${emotion.vietnameseName}")
                
                val request = GeminiImageRequest(
                    prompt = emotion.getImagePrompt(),
                    aspectRatio = "1:1"
                )
                
                android.util.Log.d(TAG, "Calling Gemini API with prompt: ${emotion.getImagePrompt()}")
                val response = geminiService.generateImage(apiKey, request)
                
                val images = response.images
                if (!images.isNullOrEmpty()) {
                    android.util.Log.i(TAG, "Successfully generated image for ${emotion.vietnameseName}")
                    val base64Image = images[0].imageBytes
                    val imagePath = saveBase64ToCache(base64Image, "emotion_${emotion.name}_${System.currentTimeMillis()}.png")
                    Result.success(imagePath)
                } else {
                    android.util.Log.e(TAG, "Gemini API returned empty data for ${emotion.vietnameseName}")
                    Result.failure(Exception("No image generated"))
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Error calling Gemini API for ${emotion.vietnameseName}: ${e.javaClass.simpleName} - ${e.message}", e)
            android.util.Log.w(TAG, "Falling back to placeholder image for ${emotion.vietnameseName}")
            Result.success(getPlaceholderImageUrl(emotion))
        }
    }

    private fun saveBase64ToCache(base64String: String, fileName: String): String {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { outputStream ->
            outputStream.write(decodedBytes)
        }
        return file.absolutePath
    }
    
    /**
     * Lấy URL ảnh placeholder từ service miễn phí
     * Sử dụng UI Avatars hoặc DiceBear để tạo ảnh mặc định
     */
    private fun getPlaceholderImageUrl(emotion: EmotionType): String {
        // Sử dụng DiceBear API để tạo avatar với seed khác nhau cho mỗi cảm xúc
        val seed = emotion.name.lowercase()
        // Có thể thay đổi style: adventurer, avataaars, bottts, etc.
        return "https://api.dicebear.com/7.x/adventurer/png?seed=$seed&size=512"
    }
}
