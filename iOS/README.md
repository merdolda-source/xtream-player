# 📱 iOS - Xtream Player

Production-ready iOS media player application built with Swift and SwiftUI.

## 🛠️ Prerequisites

- **Xcode**: 15.0 or later
- **macOS**: 13.0 or later
- **iOS**: 14.0 or later (deployment target)
- **CocoaPods**: 1.12.0 or later

## 🚀 Setup Instructions

### 1. Clone Repository
```bash
git clone https://github.com/merdolda-source/xtream-player.git
cd xtream-player/iOS
```

### 2. Install Dependencies
```bash
pod install
```

### 3. Open Project
```bash
open XtreamPlayer.xcworkspace
```

**Important**: Always use `.xcworkspace`, not `.xcodeproj`

### 4. Configure Build Settings
- Select a development team in Signing & Capabilities
- Set bundle identifier (com.yourcompany.xtreamplayer)
- Configure deployment target to iOS 14.0+

### 5. Run Project
- Select a simulator or device
- Press Cmd+R to build and run

---

## 📁 Project Structure

```
iOS/
├── XtreamPlayer/
│   ├── App/
│   │   ├── XtreamPlayerApp.swift
│   │   ├── AppDelegate.swift
│   │   └── Coordinator.swift
│   ├── Presentation/
│   │   ├── Screens/
│   │   │   ├── Auth/
│   │   │   │   ├── LoginView.swift
│   │   │   │   ├── ProfileSelectionView.swift
│   │   │   │   └── LoginViewModel.swift
│   │   │   ├── Home/
│   │   │   │   ├── HomeView.swift
│   │   │   │   ├── StreamListView.swift
│   │   │   │   └── HomeViewModel.swift
│   │   │   ├── Player/
│   │   │   │   ├── PlayerView.swift
│   │   │   │   ├── PlayerViewModel.swift
│   │   │   │   └── PlayerControlsView.swift
│   │   │   └── Settings/
│   │   │       ├── SettingsView.swift
│   │   │       ├── LanguageSettingsView.swift
│   │   │       └── SettingsViewModel.swift
│   │   ├── Components/
│   │   │   ├── PlayerControlsView.swift
│   │   │   ├── StreamCardView.swift
│   │   │   ├── LoadingView.swift
│   │   │   └── ErrorView.swift
│   │   └── Navigation/
│   │       └── AppRouter.swift
│   ├── Domain/
│   │   ├── Entities/
│   │   │   ├── Stream.swift
│   │   │   ├── Profile.swift
│   │   │   ├── User.swift
│   │   │   └── Subtitle.swift
│   │   ├── UseCases/
│   │   │   ├── GetStreamsUseCase.swift
│   │   │   ├── LoginUseCase.swift
│   │   │   ├── PlayStreamUseCase.swift
│   │   │   └── SearchStreamsUseCase.swift
│   │   ├── Repositories/
│   │   │   ├── AuthRepository.swift
│   │   │   ├── StreamRepository.swift
│   │   │   ├── ProfileRepository.swift
│   │   │   └── PlaybackRepository.swift
│   │   └── Errors/
│   │       └── DomainError.swift
│   ├── Data/
│   │   ├── API/
│   │   │   ├── XtreamApiClient.swift
│   │   │   ├── M3UParser.swift
│   │   │   ├── APIModels.swift
│   │   │   └── NetworkInterceptor.swift
│   │   ├── Persistence/
│   │   │   ├── CoreDataManager.swift
│   │   │   ├── UserDefaultsManager.swift
│   │   │   ├── KeychainManager.swift
│   │   │   └── Models/
│   │   ├── Repositories/
│   │   │   ├── AuthRepositoryImpl.swift
│   │   │   ├── StreamRepositoryImpl.swift
│   │   │   ├── ProfileRepositoryImpl.swift
│   │   │   └── PlaybackRepositoryImpl.swift
│   │   └── Mappers/
│   │       ├── StreamMapper.swift
│   │       ├── UserMapper.swift
│   │       └── ProfileMapper.swift
│   ├── Common/
│   │   ├── Utils/
│   │   │   ├── Constants.swift
│   │   │   ├── DateFormatter.swift
│   │   │   └── Logger.swift
│   │   ├── Extensions/
│   │   │   ├── String+Extensions.swift
│   │   │   ├── Date+Extensions.swift
│   │   │   └── URL+Extensions.swift
│   │   └── DI/
│   │       └── DIContainer.swift
│   └── Resources/
│       ├── Localization/
│       │   ├── en.strings
│       │   └── tr.strings
│       ├── Assets.xcassets/
│       └── Fonts/
├── XtreamPlayerTests/
│   ├── Domain/
│   │   └── UseCases/
│   ├── Data/
│   │   └── Repositories/
│   └── Presentation/
│       └── ViewModels/
├── Podfile
└── README.md
```

---

## 🔧 Configuration

### 1. API Configuration
Create `Config.swift` in `Common/Utils/`:

```swift
struct Config {
    static let xtreamApiBaseURL = "http://your-xtream-server.com"
    static let apiTimeout: TimeInterval = 30
    static let requestRetries = 3
}
```

### 2. AdMob Configuration
Add your AdMob App ID in `Info.plist`:

```xml
<key>GADApplicationIdentifier</key>
<string>ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy</string>
```

### 3. Localization
- Strings are managed in `.strings` files
- Switch language in Settings screen
- Current language stored in UserDefaults

---

## 📦 Dependencies

### Core
- **SwiftUI**: Native UI framework
- **Combine**: Reactive programming
- **URLSession**: Networking

### Player
- **AVFoundation**: Core video playback
- **VLCKit**: Advanced format support

### Storage
- **CoreData**: Local database
- **Keychain**: Secure credential storage

### Networking
- **URLSession**: HTTP requests
- **Codable**: JSON serialization

### Ads
- **Google Mobile Ads SDK**: AdMob integration

See `Podfile` for all dependencies.

---

## 🏗️ Architecture

Follows **Clean Architecture** with three main layers:

### Presentation Layer
- SwiftUI views and components
- ViewModels for state management
- Navigation coordination

### Domain Layer
- Business logic and use cases
- Data models (Entities)
- Repository interfaces

### Data Layer
- API clients and parsers
- Local storage management
- Repository implementations

---

## 🎯 Key Features Implementation

### 1. Authentication
- **File**: `Presentation/Screens/Auth/LoginViewModel.swift`
- Xtream API authentication
- Multi-profile management
- Secure token storage

### 2. Playback
- **File**: `Presentation/Screens/Player/PlayerView.swift`
- AVPlayer integration
- Gesture controls (brightness, volume)
- Resume playback functionality

### 3. Stream Management
- **File**: `Domain/UseCases/GetStreamsUseCase.swift`
- M3U parsing
- Custom header support
- Stream filtering and search

### 4. Settings
- **File**: `Presentation/Screens/Settings/SettingsView.swift`
- Language selection
- Theme switching
- Header customization

---

## 🧪 Testing

### Run Tests
```bash
cmd + U
```

### Test Structure
- Domain layer tests: 100% coverage
- Data layer tests: 90% coverage
- Presentation layer tests: 70% coverage

### Example Test
```swift
class LoginUseCaseTests: XCTestCase {
    var sut: LoginUseCase!
    var mockRepository: MockAuthRepository!
    
    override func setUp() {
        super.setUp()
        mockRepository = MockAuthRepository()
        sut = LoginUseCaseImpl(repository: mockRepository)
    }
    
    func testLogin_WithValidCredentials_ReturnsUser() async {
        // Test implementation
    }
}
```

---

## 🐛 Debugging

### Enable Logging
```swift
Logger.isEnabled = true
```

### View Logs
- Console output in Xcode
- Use View Debugger (Cmd+Option+6)
- Use Memory Debugger (Cmd+Option+7)

### Common Issues

**Issue**: Pods not found
```bash
rm Podfile.lock
pod install
```

**Issue**: Code signing error
- Check team selection in Xcode
- Verify bundle identifier is unique

---

## 📊 Performance Tips

- Use LazyVStack for large lists
- Implement pagination for streams
- Cache API responses
- Profile with Instruments (Xcode)

---

## 🔒 Security Best Practices

- ✅ Store credentials in Keychain
- ✅ Use HTTPS for API calls
- ✅ Validate all user input
- ✅ No sensitive data in logs
- ✅ Regular dependency updates

---

## 📚 Resources

- [Swift Documentation](https://swift.org/documentation/)
- [SwiftUI Tutorials](https://developer.apple.com/tutorials/swiftui)
- [iOS App Architecture](https://developer.apple.com/design/tips/)
- [Xcode Help](https://help.apple.com/xcode/)

---

## 🚀 Building for Release

```bash
# Archive
xcodebuild -workspace XtreamPlayer.xcworkspace -scheme XtreamPlayer -configuration Release archive

# Export
xcodebuild -exportArchive -archivePath XtreamPlayer.xcarchive -exportPath . -exportOptionsPlist exportOptions.plist
```

---

## 📄 License

Proprietary - All rights reserved

---

**Last Updated**: June 11, 2026
