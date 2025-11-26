# Emotion Recognition App

Ứng dụng Android giúp trẻ tự kỷ nhận diện các loại cảm xúc cơ bản và phức tạp.

## Tính năng chính

### 1. Hệ thống cảm xúc
Ứng dụng bao gồm 10 loại cảm xúc:
- Hạnh phúc (Happiness)
- Buồn (Sadness)
- Sợ hãi (Fear)
- Giận dữ (Anger)
- Ghê tởm (Disgust)
- Ngạc nhiên (Surprise)
- Khinh thường (Contempt)
- Tin tưởng (Trust)
- Yêu thương (Love)
- Mong đợi (Anticipation)

### 2. Cơ chế hoạt động
1. Hệ thống chọn ngẫu nhiên 3 loại cảm xúc
2. Từ 3 cảm xúc đó, chọn 1 cảm xúc để tạo câu hỏi
3. Gọi API OpenAI/Google Banana để sinh ảnh tương ứng với cảm xúc
4. Hiển thị ảnh và 3 đáp án cho người dùng
5. Người dùng chọn đáp án đúng
6. Hệ thống xác định đúng/sai và lưu vào database

### 3. Lưu trữ dữ liệu
Mỗi câu hỏi được lưu với thông tin:
- Thời gian làm câu hỏi
- Loại cảm xúc được hỏi
- Kết quả đúng/sai
- Thời gian hoàn thành câu hỏi

## Công nghệ sử dụng

- **Ngôn ngữ**: Kotlin
- **Database**: Room Database (SQLite)
- **API**: OpenAI DALL-E / Google Banana
- **Image Loading**: Glide
- **Network**: Retrofit + OkHttp
- **UI**: Material Design 3
- **Architecture**: MVVM pattern

## Cấu trúc dự án

```
app/
├── src/
│   └── main/
│       ├── java/com/emotionrecognition/app/
│       │   ├── model/
│       │   │   ├── EmotionType.kt          # Enum các loại cảm xúc
│       │   │   └── Question.kt             # Model câu hỏi
│       │   ├── data/
│       │   │   ├── QuestionHistory.kt      # Entity cho database
│       │   │   ├── QuestionHistoryDao.kt   # DAO
│       │   │   └── AppDatabase.kt          # Database
│       │   ├── api/
│       │   │   ├── OpenAIService.kt        # API interface
│       │   │   └── ImageGenerationRepository.kt # API repository
│       │   ├── EmotionApp.kt               # Application class
│       │   └── MainActivity.kt             # Main activity
│       ├── res/
│       │   ├── layout/
│       │   │   └── activity_main.xml       # Layout chính
│       │   ├── values/
│       │   │   ├── strings.xml             # Strings
│       │   │   ├── colors.xml              # Colors
│       │   │   └── themes.xml              # Themes
│       │   └── xml/
│       │       ├── backup_rules.xml
│       │       └── data_extraction_rules.xml
│       └── AndroidManifest.xml
└── build.gradle.kts
```

## Cài đặt và chạy

### Yêu cầu
- Android Studio Arctic Fox hoặc mới hơn
- Android SDK 24 trở lên
- JDK 8 trở lên

### Các bước cài đặt

1. **Mở dự án trong Android Studio**
   - File → Open → Chọn thư mục dự án

2. **Sync Gradle**
   - Android Studio sẽ tự động sync dependencies
   - Hoặc chọn: File → Sync Project with Gradle Files

3. **Cài đặt API Key**
   - Mở file `EmotionApp.kt`
   - Thay đổi giá trị `API_KEY`:
   ```kotlin
   companion object {
       var API_KEY = "your-openai-api-key-here"
   }
   ```
   
   **Lưu ý**: Nếu không có API key, ứng dụng sẽ sử dụng ảnh placeholder từ DiceBear API (miễn phí).

4. **Chạy ứng dụng**
   - Kết nối thiết bị Android hoặc khởi động emulator
   - Nhấn nút "Run" (▶️) trong Android Studio

## Hướng dẫn sử dụng

### Cho người dùng

1. **Bắt đầu**
   - Mở ứng dụng, câu hỏi đầu tiên sẽ tự động hiển thị
   - Xem hình ảnh cảm xúc được hiển thị

2. **Trả lời câu hỏi**
   - Nhấn vào một trong 3 đáp án
   - Hệ thống sẽ hiển thị kết quả đúng/sai
   - Nhấn "Câu tiếp theo" để tiếp tục

3. **Xem thống kê**
   - Nhấn nút "Xem thống kê" ở cuối màn hình
   - Xem tổng số câu hỏi, độ chính xác và thống kê theo từng cảm xúc

### Cho nhà phát triển

#### Thay đổi API
Để sử dụng Google Banana thay vì OpenAI:

1. Mở `OpenAIService.kt`
2. Thay đổi base URL và request/response models
3. Cập nhật `ImageGenerationRepository.kt` với logic mới

#### Thêm cảm xúc mới
1. Mở `EmotionType.kt`
2. Thêm cảm xúc mới vào enum:
```kotlin
enum class EmotionType(val vietnameseName: String) {
    // ... các cảm xúc hiện có
    NEW_EMOTION("Tên tiếng Việt")
}
```
3. Thêm prompt cho cảm xúc mới trong hàm `getImagePrompt()`

#### Tùy chỉnh số lượng đáp án
Trong `MainActivity.kt`, thay đổi tham số trong hàm `getRandomEmotions()`:
```kotlin
val randomEmotions = EmotionType.getRandomEmotions(4) // Thay 3 thành 4
```

## Database Schema

### QuestionHistory Table
| Column | Type | Description |
|--------|------|-------------|
| id | Long | Primary key (auto-increment) |
| timestamp | Long | Thời gian làm câu hỏi (milliseconds) |
| emotionAsked | String | Tên cảm xúc (tiếng Việt) |
| isCorrect | Boolean | Đúng hay sai |
| timeTaken | Long | Thời gian hoàn thành (milliseconds) |

## API Reference

### OpenAI DALL-E
- **Endpoint**: `https://api.openai.com/v1/images/generations`
- **Method**: POST
- **Headers**: 
  - `Authorization: Bearer YOUR_API_KEY`
  - `Content-Type: application/json`
- **Body**:
```json
{
  "prompt": "A face showing clear happiness emotion",
  "n": 1,
  "size": "512x512"
}
```

### Placeholder API (DiceBear)
- **URL**: `https://api.dicebear.com/7.x/adventurer/png?seed={emotion}&size=512`
- **Miễn phí**, không cần API key
- Sử dụng khi API chính không khả dụng

## Tính năng nâng cao (TODO)

- [ ] Thêm chế độ luyện tập theo từng cảm xúc
- [ ] Biểu đồ thống kê chi tiết
- [ ] Export dữ liệu thống kê
- [ ] Chế độ nhiều người chơi
- [ ] Âm thanh và hiệu ứng động
- [ ] Dark mode
- [ ] Đa ngôn ngữ

## Xử lý lỗi

### Không có kết nối Internet
- Ứng dụng sẽ hiển thị thông báo lỗi
- Người dùng có thể thử lại

### API Key không hợp lệ
- Ứng dụng tự động sử dụng ảnh placeholder
- Vẫn có thể chơi bình thường

### Lỗi database
- Các lỗi được ghi log
- Không ảnh hưởng đến luồng chơi chính

## Licenses

- Material Design Components: Apache 2.0
- Retrofit: Apache 2.0
- Glide: BSD, MIT and Apache 2.0
- Room: Apache 2.0

## Tác giả

Dự án được phát triển cho mục đích giáo dục, giúp trẻ tự kỷ học cách nhận diện cảm xúc.

## Liên hệ & Đóng góp

Nếu bạn muốn đóng góp hoặc báo lỗi, vui lòng tạo issue hoặc pull request trên repository.
# emotion_app
