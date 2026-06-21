// iOS/XtreamPlayer/Presentation/Screens/Auth/LoginViewModel.swift
import Foundation
import Combine

@MainActor
final class LoginViewModel: ObservableObject {
    @Published var host: String = ""
    @Published var username: String = ""
    @Published var password: String = ""
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?

    private let loginUseCase: LoginUseCase

    init(loginUseCase: LoginUseCase = DIContainer.shared.loginUseCase) {
        self.loginUseCase = loginUseCase
    }

    var canSubmit: Bool {
        !host.trimmingCharacters(in: .whitespaces).isEmpty &&
        !username.trimmingCharacters(in: .whitespaces).isEmpty &&
        !password.isEmpty &&
        !isLoading
    }

    func login() async -> User? {
        guard canSubmit else { return nil }
        isLoading = true
        errorMessage = nil

        defer { isLoading = false }

        do {
            let normalizedHost = normalizeHost(host)
            let user = try await loginUseCase.execute(host: normalizedHost, username: username, password: password)
            return user
        } catch {
            errorMessage = (error as? LocalizedError)?.errorDescription ?? "Unable to log in. Please check your details and try again."
            return nil
        }
    }

    private func normalizeHost(_ rawHost: String) -> String {
        var result = rawHost.trimmingCharacters(in: .whitespacesAndNewlines)
        result = result.replacingOccurrences(of: "http://", with: "")
        result = result.replacingOccurrences(of: "https://", with: "")
        if result.hasSuffix("/") {
            result.removeLast()
        }
        return result
    }
}
