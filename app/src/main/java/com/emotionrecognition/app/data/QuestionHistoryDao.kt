package com.emotionrecognition.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) cho QuestionHistory
 */
@Dao
interface QuestionHistoryDao {
    
    /**
     * Thêm một bản ghi lịch sử câu hỏi mới
     */
    @Insert
    suspend fun insert(history: QuestionHistory)
    
    /**
     * Lấy tất cả lịch sử câu hỏi, sắp xếp theo thời gian giảm dần
     */
    @Query("SELECT * FROM question_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<QuestionHistory>>
    
    /**
     * Lấy số lượng câu hỏi đã trả lời đúng
     */
    @Query("SELECT COUNT(*) FROM question_history WHERE isCorrect = 1")
    suspend fun getCorrectCount(): Int
    
    /**
     * Lấy tổng số câu hỏi đã trả lời
     */
    @Query("SELECT COUNT(*) FROM question_history")
    suspend fun getTotalCount(): Int
    
    /**
     * Lấy lịch sử câu hỏi theo loại cảm xúc
     */
    @Query("SELECT * FROM question_history WHERE emotionAsked = :emotion ORDER BY timestamp DESC")
    fun getHistoryByEmotion(emotion: String): Flow<List<QuestionHistory>>
    
    /**
     * Xóa tất cả lịch sử
     */
    @Query("DELETE FROM question_history")
    suspend fun deleteAll()
    
    /**
     * Lấy thống kê theo từng cảm xúc
     */
    @Query("""
        SELECT emotionAsked, 
               COUNT(*) as total,
               SUM(CASE WHEN isCorrect = 1 THEN 1 ELSE 0 END) as correct
        FROM question_history 
        GROUP BY emotionAsked
    """)
    suspend fun getStatsByEmotion(): List<EmotionStats>
}

/**
 * Data class cho thống kê
 */
data class EmotionStats(
    val emotionAsked: String,
    val total: Int,
    val correct: Int
)
