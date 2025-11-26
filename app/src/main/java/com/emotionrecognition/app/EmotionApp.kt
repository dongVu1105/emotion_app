package com.emotionrecognition.app

import android.app.Application
import com.emotionrecognition.app.data.AppDatabase

/**
 * Application class để khởi tạo các thành phần global
 */
class EmotionApp : Application() {
    
    val database: AppDatabase by lazy { 
        AppDatabase.getDatabase(this) 
    }
    
    companion object {
        // API Key sẽ được người dùng nhập vào
        // Mặc định sử dụng placeholder để test với ảnh dummy
        // Thay đổi thành Gemini API Key của bạn
        var API_KEY = "AIzaSyBJ4shDErKZiw1fb8pJ0ghfAoGNLHmxF68"
    }
}
