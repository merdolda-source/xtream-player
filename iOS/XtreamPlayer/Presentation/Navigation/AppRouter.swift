// iOS/XtreamPlayer/Presentation/Navigation/AppRouter.swift
import Foundation
import SwiftUI
import Combine

/// Top-level navigation state for the app. Drives whether the user sees the
/// login flow or the authenticated Home flow, and hosts the currently
/// presented player session (if any).
final class AppRouter: ObservableObject {
    enum AuthState {
        case loggedOut
        case loggedIn(User)
    }

    @Published var authState: AuthState = .loggedOut
    @Published var presentedStream: PlayerSession?

    private let authRepository: AuthRepository

    init(authRepository: AuthRepository) {
        self.authRepository = authRepository
        if authRepository.isLoggedIn(), let user = authRepository.currentUser() {
            authState = .loggedIn(user)
        }
    }

    func handleLoginSuccess(_ user: User) {
        authState = .loggedIn(user)
    }

    func logout() {
        try? authRepository.logout()
        authState = .loggedOut
    }

    func play(_ session: PlayerSession) {
        presentedStream = session
    }

    func dismissPlayer() {
        presentedStream = nil
    }
}

/// Lightweight value describing what should be played, passed to the player sheet.
struct PlayerSession: Identifiable, Equatable {
    let id = UUID()
    let stream: Stream
    let host: String
    let username: String
    let password: String
}
