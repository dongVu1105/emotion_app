package com.emotionrecognition.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.emotionrecognition.app.model.EmotionType

/**
 * Entity đại diện cho lịch sử câu hỏi trong database
 */
@Entity(tableName = "question_history")
data class QuestionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val timestamp: Long,                    // Thời gian làm câu hỏi
    val emotionAsked: String,               // Loại cảm xúc được hỏi (lưu tên tiếng Việt)
    val isCorrect: Boolean,                 // Người dùng chọn đúng hay sai
    val timeTaken: Long = 0                 // Thời gian (ms) để trả lời câu hỏi
) {
    companion object {
        /**
         * Tạo QuestionHistory từ EmotionType và kết quả
         */
        fun create(emotion: EmotionType, isCorrect: Boolean, timeTaken: Long = 0): QuestionHistory {
            return QuestionHistory(
                timestamp = System.currentTimeMillis(),
                emotionAsked = emotion.vietnameseName,
                isCorrect = isCorrect,
                timeTaken = timeTaken
            )
        }
    }
}
