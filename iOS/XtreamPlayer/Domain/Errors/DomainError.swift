// iOS/XtreamPlayer/Domain/Errors/DomainError.swift
import Foundation

enum DomainError: Error {
    case invalidStreamURL
    case invalidResponse
    case invalidCredentials
    case streamAccessDenied
    case streamNotFound
    case requestTimeout
}

extension DomainError: LocalizedError {
    var errorDescription: String? {
        switch self {
        case .invalidStreamURL:
            return "The stream URL is invalid."
        case .invalidResponse:
            return "The server returned an invalid response."
        case .invalidCredentials:
            return "The username or password is incorrect."
        case .streamAccessDenied:
            return "Access to this stream is denied."
        case .streamNotFound:
            return "The requested stream could not be found."
        case .requestTimeout:
            return "The request timed out. Please try again."
        }
    }
}
