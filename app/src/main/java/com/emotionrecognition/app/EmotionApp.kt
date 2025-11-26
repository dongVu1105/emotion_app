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
        // API Key OpenAI - Thay bằng key của bạn có dạng: sk-proj-... hoặc sk-...
        // Lấy tại: https://platform.openai.com/api-keys
        var API_KEY = "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"
    }
}
