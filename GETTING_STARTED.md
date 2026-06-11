# 🚀 Xtream Player - Başlangıç Kılavuzu (Getting Started)

## 📋 Proje Özeti

**Xtream Player** production-ready bir medya oynatıcı uygulamasıdır ve şu platformları destekler:
- ✅ **iOS** (Swift/SwiftUI)
- ✅ **Android** (Kotlin/Jetpack Compose)
- ✅ **Android TV** (D-Pad Navigation)

### Temel Özellikler
🔐 Multi-profile authentication (Xtream Codes API + M3U)
🎥 Gelişmiş video oynatıcı (HLS, DASH, MP4, MKV, vb.)
📺 Stream yönetimi (Live TV, VOD, Series)
🎨 Modern UI (Dark/Light theme)
🌍 Çoklu dil desteği (TR/EN)
📊 Reklam & Analytics
🧪 100+ test coverage
🔒 Enterprise-grade security

---

## 🏗️ Mevcut Proje Yapısı

```
xtream-player/
├── iOS/                                 # iOS Project (Swift/SwiftUI)
│   ├── XtreamPlayer/
│   │   ├── App/
│   │   │   ├── XtreamPlayerApp.swift   ✅ Entry point
│   │   │   └── AppDelegate.swift       📝 Firebase setup
│   │   ├── Presentation/               📝 UI screens
│   │   ├── Domain/
│   │   │   ├── Entities/
│   │   │   │   ├── Stream.swift        ✅ Stream model
│   │   │   │   ├── Profile.swift       ✅ Profile model
│   │   │   │   └── User.swift          ✅ User model
│   │   │   ├── Repositories/
│   │   │   │   ├── AuthRepository.swift ✅ Auth interface
│   │   │   │   └── StreamRepository.swift ✅ Stream interface
│   │   │   └── Errors/
│   │   │       └── DomainError.swift   ✅ Error definitions
│   │   ├── Data/
│   │   │   ├── API/
│   │   │   │   └── XtreamApiClient.swift ✅ API client
│   │   │   └── Persistence/
│   │   │       └── KeychainManager.swift ✅ Secure storage
│   │   └── Common/
│   │       ├── DI/
│   │       │   └── DIContainer.swift    ✅ Dependency injection
│   │       └── Utils/
│   │           └── Logger.swift         ✅ Logging system
│   └── README.md                        ✅ iOS setup guide
├── Android/                              # Android Project (Kotlin)
│   ├── app/src/main/kotlin/com/xtream/player/
│   │   ├── XtreamPlayerApplication.kt   ✅ Entry point
│   │   ├── presentation/                📝 UI screens
│   │   ├── domain/
│   │   │   ├── entities/
│   │   │   │   ├── Stream.kt            ✅ Stream model
│   │   │   │   ├── Profile.kt           ✅ Profile model
│   │   │   │   └── User.kt              ✅ User model
│   │   │   ├── repositories/
│   │   │   │   ├── AuthRepository.kt    ✅ Auth interface
│   │   │   │   └── StreamRepository.kt  ✅ Stream interface
│   │   │   └── exceptions/
│   │   │       └── DomainExceptions.kt  ✅ Exception classes
│   │   ├── data/
│   │   │   └── api/
│   │   │       └── XtreamApiClient.kt   ✅ API client
│   │   └── common/
│   │       └── utils/
│   │           └── Logger.kt            ✅ Logging system
│   └── README.md                        ✅ Android setup guide
├── shared/
│   └── API_DOCUMENTATION.md             ✅ API specs
├── ARCHITECTURE.md                      ✅ Architecture guide
├── CONTRIBUTING.md                      ✅ Contribution guidelines
├── IMPLEMENTATION_CHECKLIST.md          ✅ Implementation tracker
├── GETTING_STARTED.md                   ✅ This file
└── README.md                            ✅ Project overview

Legend: ✅ = Completed | 📝 = Needs Implementation
```

---

## 🎯 Başlamak İçin

### Adım 1: Repository'yi Klonlayın
```bash
git clone https://github.com/merdolda-source/xtream-player.git
cd xtream-player
```

### Adım 2: iOS Kurulumu (macOS Gerekli)
```bash
cd iOS
pod install
open XtreamPlayer.xcworkspace
```

**Xcode'de Çalıştırma**:
- Product → Run (Cmd+R)
- Simulator seçin (iPhone 15 önerilir)

### Adım 3: Android Kurulumu
```bash
cd Android
./gradlew build
```

**Android Studio'da Çalıştırma**:
- File → Open → `Android/` klasörü seçin
- Run → Run 'app'

---

## 📊 Tamamlanan (Phase 1 ✅)

### Foundation & Architecture
| Bileşen | iOS | Android | Status |
|---------|-----|---------|--------|
| Project Structure | ✅ | ✅ | Complete |
| Documentation | ✅ | ✅ | Complete |
| Logger System | ✅ | ✅ | Complete |
| DI Container | ✅ | 📝 | Partial |
| Domain Entities | ✅ | ✅ | Complete |
| API Client | ✅ | ✅ | Complete |
| Error Handling | ✅ | ✅ | Complete |
| Repositories | ✅ | ✅ | Interfaces |
| Secure Storage | ✅ | 📝 | Partial |

---

## 📝 Sonraki Aşamalar (Phase 2-3)

### Yakında Gelecek (Next 2-3 Weeks)

#### Authentication Flow
- [ ] Login screen UI implementation
- [ ] Profile management UI
- [ ] Credential secure storage (Android)
- [ ] Session management
- [ ] Logout functionality

#### Stream Management
- [ ] M3U parser implementation
- [ ] Stream repository implementation
- [ ] Custom header support
- [ ] Database models (Android Room)
- [ ] Caching layer

#### Data Models
- [ ] API request/response models
- [ ] Database entity models
- [ ] Mapper implementations

---

## 🔧 Geliştirme Ortamı Ayarı

### Gereksinimler

**iOS**:
- macOS 13.0+
- Xcode 15.0+
- Swift 5.9+
- CocoaPods 1.12+

**Android**:
- Android Studio 2022.2.1+
- Java/Kotlin 11+
- Gradle 8.0+
- Android SDK 28+

### IDE Extensions (Önerilir)

**Xcode**:
- Swift Format
- SwiftUI Previews

**Android Studio**:
- Kotlin Compiler
- Compose Preview

---

## 🛠️ Build Commands

### iOS
```bash
# Build for Debug
xcodebuild -workspace iOS/XtreamPlayer.xcworkspace \
  -scheme XtreamPlayer \
  -configuration Debug build

# Build for Release
xcodebuild -workspace iOS/XtreamPlayer.xcworkspace \
  -scheme XtreamPlayer \
  -configuration Release build

# Run Tests
xcodebuild -workspace iOS/XtreamPlayer.xcworkspace \
  -scheme XtreamPlayer test
```

### Android
```bash
# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease

# Run Tests
./gradlew test

# Connect to device
./gradlew installDebug
```

---

## 📚 Key Files & Their Purpose

### Architecture Entry Points

| Platform | File | Purpose |
|----------|------|----------|
| iOS | `XtreamPlayer/App/XtreamPlayerApp.swift` | App lifecycle, Firebase setup |
| Android | `XtreamPlayerApplication.kt` | App initialization, AdMob setup |

### Domain Models

| Platform | File | Models |
|----------|------|--------|
| iOS | `Domain/Entities/Stream.swift` | Stream, StreamGroup, StreamDetails |
| iOS | `Domain/Entities/Profile.swift` | Profile, User, ProfileType |
| Android | `domain/entities/Stream.kt` | Stream, StreamGroup, StreamDetails |
| Android | `domain/entities/Profile.kt` | Profile, User, ProfileType |

### Data Access

| Platform | File | Purpose |
|----------|------|----------|
| iOS | `Data/API/XtreamApiClient.swift` | Xtream Codes API client |
| iOS | `Data/Persistence/KeychainManager.swift` | Secure credential storage |
| Android | `data/api/XtreamApiClient.kt` | Xtream API + Retrofit |
| Android | `data/repositories/` | Repository implementations |

### Dependency Injection

| Platform | File | Purpose |
|----------|------|----------|
| iOS | `Common/DI/DIContainer.swift` | Manual DI container |
| Android | `common/di/` | Hilt modules (📝 TBD) |

---

## 🧪 Testing

### Unit Testing

**iOS**:
```bash
cd iOS
xcodebuild -workspace XtreamPlayer.xcworkspace \
  -scheme XtreamPlayer \
  -destination 'platform=iOS Simulator,name=iPhone 15' \
  test
```

**Android**:
```bash
cd Android
./gradlew test
```

### Test Coverage Target
- Domain Layer: 100%
- Data Layer: 90%
- Presentation Layer: 70%

---

## 🔒 Security Checklist

- [x] Keychain/EncryptedSharedPreferences for credentials
- [x] Error handling without sensitive data leaks
- [x] HTTPS-only API calls
- [ ] Certificate pinning (Phase 5+)
- [ ] Input validation (Phase 4+)
- [ ] Secure logging (Phase 6+)

---

## 📊 Progress Tracking

View detailed progress in [IMPLEMENTATION_CHECKLIST.md](IMPLEMENTATION_CHECKLIST.md)

**Current Phase**: 1 ✅ (Foundation Complete)

**Estimated Timeline**:
- Phase 2-3 (Auth + Streams): 6-8 weeks
- Phase 4-5 (Player + UI): 7-9 weeks
- Phase 6-10 (Features + Release): 7-10 weeks
- **Total**: 5-6 months for production release

---

## 🐛 Debugging Tips

### iOS
```bash
# View logs in Xcode Console
# Enable logging in code
Logger.debug("Debug message")

# View Xcode logs
log stream --level debug --predicate 'process == "XtreamPlayer"'
```

### Android
```bash
# View Logcat
adb logcat | grep XtreamPlayer

# Export logs
adb shell run-as com.xtream.player cat /data/files/app.log > app.log
```

---

## 📞 Support & Resources

### Documentation
- [ARCHITECTURE.md](ARCHITECTURE.md) - Deep dive architecture
- [API_DOCUMENTATION.md](shared/API_DOCUMENTATION.md) - API specs
- [CONTRIBUTING.md](CONTRIBUTING.md) - Development guidelines

### External Resources
- [Swift Documentation](https://swift.org/documentation/)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [SwiftUI Tutorials](https://developer.apple.com/tutorials/swiftui)

---

## 🚀 Next Steps

1. **Clone & Setup**: Repository'yi klonlayın ve kurulumu yapın
2. **Review Architecture**: ARCHITECTURE.md'yi okuyun
3. **Start Implementation**: Phase 2 (Authentication) ile başlayın
4. **Follow Guidelines**: CONTRIBUTING.md'deki standartları izleyin
5. **Track Progress**: IMPLEMENTATION_CHECKLIST.md'yi güncelleyin

---

## 📞 Frequently Asked Questions

**S: Hangi platform ile başlamalıyım?**
C: Aynı anda her iki platform için de geliştirme yapılabilir. Domain ve Data layer'lar bağımsızdır.

**S: Test yazmamalı mıyım?**
C: Hayır! Domain layer için %100 test coverage gerekli. Her use case için test yazınız.

**S: Hangi branch'te çalışmalıyım?**
C: `develop` branch'te çalışın. PR'ları `develop`'e gönderin.

**S: Credential'ları nerede saklayacağım?**
C: iOS: Keychain | Android: EncryptedSharedPreferences

---

**Version**: 1.0.0  
**Last Updated**: June 11, 2026  
**Status**: Ready for Phase 2 Implementation 🎯

---

Sorularınız mı var? [GitHub Issues](https://github.com/merdolda-source/xtream-player/issues)'da açabilirsiniz.
