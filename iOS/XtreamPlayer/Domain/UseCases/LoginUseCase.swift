// iOS/XtreamPlayer/Domain/UseCases/LoginUseCase.swift
import Foundation

protocol LoginUseCase {
    func execute(host: String, username: String, password: String) async throws -> User
}

final class LoginUseCaseImpl: LoginUseCase {
    private let repository: AuthRepository

    init(repository: AuthRepository) {
        self.repository = repository
    }

    func execute(host: String, username: String, password: String) async throws -> User {
        try await repository.login(host: host, username: username, password: password)
    }
}
