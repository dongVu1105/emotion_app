package com.emotionrecognition.app.model

/**
 * Enum định nghĩa các loại cảm xúc cơ bản và phức tạp
 */
enum class EmotionType(val vietnameseName: String) {
    HAPPINESS("Hạnh phúc"),
    SADNESS("Buồn"),
    FEAR("Sợ hãi"),
    ANGER("Giận dữ"),
    DISGUST("Ghê tởm"),
    SURPRISE("Ngạc nhiên"),
    CONTEMPT("Khinh thường"),
    TRUST("Tin tưởng"),
    LOVE("Yêu thương"),
    ANTICIPATION("Mong đợi");

    companion object {
        /**
         * Lấy ngẫu nhiên 3 cảm xúc khác nhau
         */
        fun getRandomEmotions(count: Int = 3): List<EmotionType> {
            return values().toList().shuffled().take(count)
        }

        /**
         * Lấy tên tiếng Việt từ EmotionType
         */
        fun fromVietnameseName(name: String): EmotionType? {
            return values().find { it.vietnameseName == name }
        }
    }

    /**
     * Tạo prompt cho việc sinh ảnh
     */
    fun getImagePrompt(): String {
        return when (this) {
            HAPPINESS -> "Photorealistic shot of a diverse person in a real-world setting showing clear happiness, throwing head back in laughter, hands clapping or raised in joy, relaxed and open body posture, natural lighting, genuine atmosphere."

            SADNESS -> "Photorealistic shot of a diverse person in a real-world setting showing clear sadness, sitting with slumped shoulders, face buried in hands or hugging their knees, body curled inwards, looking down, gloomy and soft lighting."

            FEAR -> "Photorealistic shot of a diverse person in a real-world setting showing clear fear, backing away defensively, hands raised to protect the face, body trembling and shrinking back, wide eyes, dramatic shadows."

            ANGER -> "Photorealistic shot of a diverse person in a real-world setting showing clear anger, standing aggressively, clenching fists tight at sides, leaning forward with tension in neck and shoulders, intense shouting expression."

            DISGUST -> "Photorealistic shot of a diverse person in a real-world setting showing clear disgust, turning their body away from the camera, holding a hand up to block the view or covering their nose/mouth, recoiling posture."

            SURPRISE -> "Photorealistic shot of a diverse person in a real-world setting showing clear surprise, hands instinctively flying to cover the mouth or clutching the chest, jaw dropped, body jolted back in shock."

            CONTEMPT -> "Photorealistic shot of a diverse person in a real-world setting showing clear contempt, crossing arms tightly across the chest, looking down their nose with a sneer, leaning back with a superior and dismissive attitude."

            TRUST -> "Photorealistic shot of a diverse person in a real-world setting showing clear trust, standing with open palms, offering a handshake or hand placed sincerely on their heart, relaxed shoulders, warm and welcoming body language."

            LOVE -> "Photorealistic shot of a diverse person in a real-world setting showing clear love, leaning forward gently, hands pressed affectionately to their chest or reaching out softly, head tilted to the side, warm and soft focus."

            ANTICIPATION -> "Photorealistic shot of a diverse person in a real-world setting showing clear anticipation, leaning forward on a surface or rubbing hands together eagerly, looking towards the side with intent focus, fidgeting with excitement."
        }
    }
}
