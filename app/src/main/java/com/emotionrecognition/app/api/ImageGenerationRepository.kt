package com.emotionrecognition.app.api

import com.emotionrecognition.app.model.EmotionType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repository để quản lý việc sinh ảnh từ API
 */
class ImageGenerationRepository(private val apiKey: String) {
    
    companion object {
        private const val TAG = "ImageGenRepository"
    }
    
    private val openAIService: OpenAIService by lazy {
        createRetrofit("https://api.openai.com/")
            .create(OpenAIService::class.java)
    }
    
    /**
     * Tạo Retrofit instance với authentication
     */
    private fun createRetrofit(baseUrl: String): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(request)
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
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
                // Trả về URL ảnh mặc định nếu chưa có API key
                Result.success(getPlaceholderImageUrl(emotion))
            } else {
                android.util.Log.d(TAG, "Generating image for emotion: ${emotion.vietnameseName}")
                
                val request = ImageGenerationRequest(
                    model = "dall-e-3",
                    prompt = emotion.getImagePrompt(),
                    n = 1,
                    size = "1024x1024",
                    quality = "standard"
                )
                
                android.util.Log.d(TAG, "Calling OpenAI API with prompt: ${emotion.getImagePrompt()}")
                val response = openAIService.generateImage(request)
                
                if (response.data.isNotEmpty()) {
                    android.util.Log.i(TAG, "Successfully generated image for ${emotion.vietnameseName}")
                    Result.success(response.data[0].url)
                } else {
                    android.util.Log.e(TAG, "OpenAI API returned empty data for ${emotion.vietnameseName}")
                    Result.failure(Exception("No image generated"))
                }
            }
        } catch (e: Exception) {
            // Nếu có lỗi, trả về ảnh placeholder
            android.util.Log.e(TAG, "Error calling OpenAI API for ${emotion.vietnameseName}: ${e.javaClass.simpleName} - ${e.message}", e)
            android.util.Log.e(TAG, "Stack trace:", e)
            android.util.Log.w(TAG, "Falling back to placeholder image for ${emotion.vietnameseName}")
            Result.success(getPlaceholderImageUrl(emotion))
        }
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
