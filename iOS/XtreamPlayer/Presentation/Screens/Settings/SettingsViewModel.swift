// iOS/XtreamPlayer/Presentation/Screens/Settings/SettingsViewModel.swift
import Foundation
import Combine

@MainActor
final class SettingsViewModel: ObservableObject {
    @Published var host: String
    @Published var username: String
    @Published var isLoggedIn: Bool

    private let authRepository: AuthRepository
    private let getWatchHistoryUseCase: GetWatchHistoryUseCase

    init(
        authRepository: AuthRepository = DIContainer.shared.authRepository,
        getWatchHistoryUseCase: GetWatchHistoryUseCase = DIContainer.shared.getWatchHistoryUseCase
    ) {
        self.authRepository = authRepository
        self.getWatchHistoryUseCase = getWatchHistoryUseCase
        self.isLoggedIn = authRepository.isLoggedIn()
        let user = authRepository.currentUser()
        self.host = ""
        self.username = user?.username ?? ""
    }

    func clearWatchHistory() async {
        try? await getWatchHistoryUseCase.clear()
    }

    func logout() {
        try? authRepository.logout()
        isLoggedIn = false
    }
}
