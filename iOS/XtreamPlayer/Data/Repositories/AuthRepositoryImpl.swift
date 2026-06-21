// iOS/XtreamPlayer/Data/Repositories/AuthRepositoryImpl.swift
import Foundation

final class AuthRepositoryImpl: AuthRepository {
    private let apiClient: XtreamApiClient
    private let keychainManager: KeychainManager
    private let userDefaultsManager: UserDefaultsManager

    private let credentialsKey = "com.xtream.player.credentials"
    private let cachedUserKey = "com.xtream.player.cachedUser"

    init(apiClient: XtreamApiClient, keychainManager: KeychainManager, userDefaultsManager: UserDefaultsManager) {
        self.apiClient = apiClient
        self.keychainManager = keychainManager
        self.userDefaultsManager = userDefaultsManager
    }

    func login(host: String, username: String, password: String) async throws -> User {
        let user = try await apiClient.login(host: host, username: username, password: password)

        let credentials = StoredCredentials(host: host, username: username, password: password)
        try keychainManager.saveCodable(credentials, forKey: credentialsKey)
        try keychainManager.saveCodable(user, forKey: cachedUserKey)

        userDefaultsManager.isLoggedIn = true
        userDefaultsManager.lastHost = host
        userDefaultsManager.lastUsername = username

        return user
    }

    func logout() throws {
        try keychainManager.delete(forKey: credentialsKey)
        try keychainManager.delete(forKey: cachedUserKey)
        userDefaultsManager.isLoggedIn = false
    }

    func currentUser() -> User? {
        try? keychainManager.retrieveCodable(forKey: cachedUserKey, as: User.self)
    }

    func isLoggedIn() -> Bool {
        userDefaultsManager.isLoggedIn
    }
}

private struct StoredCredentials: Codable {
    let host: String
    let username: String
    let password: String
}
