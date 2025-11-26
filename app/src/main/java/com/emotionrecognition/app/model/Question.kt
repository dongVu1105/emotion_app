package com.emotionrecognition.app.model

/**
 * Data class đại diện cho một câu hỏi
 */
data class Question(
    val correctEmotion: EmotionType,
    val imageUrl: String,
    val options: List<EmotionType>
) {
    /**
     * Kiểm tra xem câu trả lời có đúng không
     */
    fun isCorrectAnswer(selectedEmotion: EmotionType): Boolean {
        return selectedEmotion == correctEmotion
    }
}
