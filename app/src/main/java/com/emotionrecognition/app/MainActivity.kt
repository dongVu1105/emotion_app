package com.emotionrecognition.app

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.emotionrecognition.app.api.ImageGenerationRepository
import com.emotionrecognition.app.data.AppDatabase
import com.emotionrecognition.app.data.QuestionHistory
import com.emotionrecognition.app.databinding.ActivityMainBinding
import com.emotionrecognition.app.model.EmotionType
import com.emotionrecognition.app.model.Question
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private lateinit var imageRepository: ImageGenerationRepository
    
    private var currentQuestion: Question? = null
    private var questionStartTime: Long = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Khởi tạo database và repository
        database = (application as EmotionApp).database
        imageRepository = ImageGenerationRepository(EmotionApp.API_KEY)
        
        setupListeners()
        loadNewQuestion()
    }
    
    private fun setupListeners() {
        // Xử lý khi người dùng chọn đáp án
        binding.btnOption1.setOnClickListener { onOptionSelected(it as MaterialButton, 0) }
        binding.btnOption2.setOnClickListener { onOptionSelected(it as MaterialButton, 1) }
        binding.btnOption3.setOnClickListener { onOptionSelected(it as MaterialButton, 2) }
        
        // Button tiếp theo
        binding.btnNext.setOnClickListener {
            resetUI()
            loadNewQuestion()
        }
        
        // Button thống kê
        binding.btnStatistics.setOnClickListener {
            showStatistics()
        }
    }
    
    /**
     * Tải câu hỏi mới
     */
    private fun loadNewQuestion() {
        showLoading(true)
        questionStartTime = System.currentTimeMillis()
        
        lifecycleScope.launch {
            try {
                // Bước 1: Chọn ngẫu nhiên 3 cảm xúc
                val randomEmotions = EmotionType.getRandomEmotions(3)
                
                // Bước 2: Chọn 1 cảm xúc trong 3 cảm xúc vừa chọn làm câu hỏi
                val correctEmotion = randomEmotions.random()
                
                // Bước 3: Gọi API để sinh ảnh cho cảm xúc đó
                val imageResult = imageRepository.generateImageForEmotion(correctEmotion)
                
                imageResult.onSuccess { imageUrl ->
                    // Tạo câu hỏi
                    currentQuestion = Question(
                        correctEmotion = correctEmotion,
                        imageUrl = imageUrl,
                        options = randomEmotions
                    )
                    
                    // Hiển thị câu hỏi
                    displayQuestion(currentQuestion!!)
                }.onFailure { error ->
                    showError(error.message ?: "Không thể tải hình ảnh")
                }
                
            } catch (e: Exception) {
                showError(e.message ?: "Đã xảy ra lỗi")
            } finally {
                showLoading(false)
            }
        }
    }
    
    /**
     * Hiển thị câu hỏi lên UI
     */
    private fun displayQuestion(question: Question) {
        // Load ảnh bằng Glide
        binding.progressBarImage.visibility = View.VISIBLE
        Glide.with(this)
            .load(question.imageUrl)
            .centerCrop()
            .into(binding.ivEmotion)
        
        binding.progressBarImage.visibility = View.GONE
        
        // Hiển thị 3 đáp án
        val buttons = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3)
        question.options.forEachIndexed { index, emotion ->
            buttons[index].text = emotion.vietnameseName
            buttons[index].isEnabled = true
        }
    }
    
    /**
     * Xử lý khi người dùng chọn đáp án
     */
    private fun onOptionSelected(button: MaterialButton, optionIndex: Int) {
        val question = currentQuestion ?: return
        val selectedEmotion = question.options[optionIndex]
        val isCorrect = question.isCorrectAnswer(selectedEmotion)
        
        // Tính thời gian làm bài
        val timeTaken = System.currentTimeMillis() - questionStartTime
        
        // Vô hiệu hóa tất cả các nút
        disableAllOptions()
        
        // Hiển thị kết quả
        showFeedback(isCorrect, question.correctEmotion, button)
        
        // Lưu vào database
        saveQuestionHistory(question.correctEmotion, isCorrect, timeTaken)
    }
    
    /**
     * Hiển thị phản hồi sau khi chọn đáp án
     */
    private fun showFeedback(isCorrect: Boolean, correctEmotion: EmotionType, selectedButton: MaterialButton) {
        // Đổi màu button được chọn
        if (isCorrect) {
            selectedButton.setBackgroundColor(getColor(R.color.correct_answer))
            binding.tvFeedback.text = getString(R.string.correct_answer)
            binding.tvFeedback.setTextColor(getColor(R.color.correct_answer))
        } else {
            selectedButton.setBackgroundColor(getColor(R.color.wrong_answer))
            binding.tvFeedback.text = getString(R.string.wrong_answer) + "\n" + 
                    getString(R.string.correct_emotion, correctEmotion.vietnameseName)
            binding.tvFeedback.setTextColor(getColor(R.color.wrong_answer))
        }
        
        binding.tvFeedback.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE
    }
    
    /**
     * Lưu lịch sử câu hỏi vào database
     */
    private fun saveQuestionHistory(emotion: EmotionType, isCorrect: Boolean, timeTaken: Long) {
        lifecycleScope.launch {
            try {
                val history = QuestionHistory.create(emotion, isCorrect, timeTaken)
                database.questionHistoryDao().insert(history)
            } catch (e: Exception) {
                // Log error
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Hiển thị thống kê
     */
    private fun showStatistics() {
        lifecycleScope.launch {
            try {
                val totalCount = database.questionHistoryDao().getTotalCount()
                val correctCount = database.questionHistoryDao().getCorrectCount()
                val accuracy = if (totalCount > 0) {
                    (correctCount.toFloat() / totalCount) * 100
                } else {
                    0f
                }
                
                val stats = database.questionHistoryDao().getStatsByEmotion()
                
                val message = buildString {
                    append(getString(R.string.total_questions, totalCount))
                    append("\n")
                    append(getString(R.string.correct_count, correctCount))
                    append("\n")
                    append(getString(R.string.accuracy, accuracy))
                    append("\n\n")
                    append("Chi tiết theo cảm xúc:\n")
                    stats.forEach { stat ->
                        val emotionAccuracy = if (stat.total > 0) {
                            (stat.correct.toFloat() / stat.total) * 100
                        } else {
                            0f
                        }
                        append("${stat.emotionAsked}: ${stat.correct}/${stat.total} (${String.format("%.1f", emotionAccuracy)}%)\n")
                    }
                }
                
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Thống kê")
                    .setMessage(message)
                    .setPositiveButton("Đóng", null)
                    .show()
                
            } catch (e: Exception) {
                showError("Không thể tải thống kê")
            }
        }
    }
    
    /**
     * Reset UI về trạng thái ban đầu
     */
    private fun resetUI() {
        binding.tvFeedback.visibility = View.GONE
        binding.btnNext.visibility = View.GONE
        
        val buttons = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3)
        buttons.forEach { button ->
            button.setBackgroundColor(Color.TRANSPARENT)
            button.isEnabled = true
        }
    }
    
    /**
     * Vô hiệu hóa tất cả các nút đáp án
     */
    private fun disableAllOptions() {
        binding.btnOption1.isEnabled = false
        binding.btnOption2.isEnabled = false
        binding.btnOption3.isEnabled = false
    }
    
    /**
     * Hiển thị/ẩn loading indicator
     */
    private fun showLoading(show: Boolean) {
        binding.progressIndicator.visibility = if (show) View.VISIBLE else View.GONE
        binding.layoutOptions.visibility = if (show) View.GONE else View.VISIBLE
    }
    
    /**
     * Hiển thị lỗi
     */
    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Lỗi")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                // Thử tải lại câu hỏi
                loadNewQuestion()
            }
            .show()
    }
}
