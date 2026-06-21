// iOS/XtreamPlayer/Domain/Repositories/AuthRepository.swift
import Foundation

protocol AuthRepository {
    func login(host: String, username: String, password: String) async throws -> User
    func logout() throws
    func currentUser() -> User?
    func isLoggedIn() -> Bool
}
