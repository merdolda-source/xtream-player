// iOS/XtreamPlayer/App/XtreamPlayerApp.swift
import SwiftUI

@main
struct XtreamPlayerApp: App {
    @StateObject private var router = AppRouter(authRepository: DIContainer.shared.authRepository)

    init() {
        Logger.setup()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
                .environmentObject(router)
        }
    }
}

/// Decides which top-level flow to show based on auth state, and hosts the
/// player as a full-screen cover above whichever flow is active.
struct RootView: View {
    @EnvironmentObject private var router: AppRouter

    var body: some View {
        Group {
            switch router.authState {
            case .loggedOut:
                LoginView()
            case .loggedIn(let user):
                MainTabView(user: user)
            }
        }
        .fullScreenCover(item: $router.presentedStream) { session in
            PlayerView(session: session)
                .environmentObject(router)
        }
    }
}

/// Root tab container shown after login: Home (Live/VOD/Series/Favorites/History)
/// plus Settings.
struct MainTabView: View {
    let user: User
    @State private var credentials: StoredSessionCredentials?

    var body: some View {
        TabView {
            Group {
                if let credentials {
                    HomeView(host: credentials.host, username: credentials.username, password: credentials.password)
                } else {
                    ProgressView()
                }
            }
            .tabItem {
                Label("Home", systemImage: "house")
            }

            SettingsView()
                .tabItem {
                    Label("Settings", systemImage: "gear")
                }
        }
        .onAppear {
            credentials = StoredSessionCredentials.load(for: user)
        }
    }
}

/// Helper that resolves the host/username/password needed to build stream
/// URLs after login. Username comes from the logged-in `User`; host/password
/// are read back from UserDefaults/Keychain via the auth flow's last-used
/// values since `User` itself does not carry the host or password.
struct StoredSessionCredentials {
    let host: String
    let username: String
    let password: String

    static func load(for user: User) -> StoredSessionCredentials? {
        guard let host = UserDefaultsManager.shared.lastHost else { return nil }
        return StoredSessionCredentials(host: host, username: user.username, password: user.password)
    }
}
