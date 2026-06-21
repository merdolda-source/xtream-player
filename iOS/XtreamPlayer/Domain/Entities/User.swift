// iOS/XtreamPlayer/Domain/Entities/User.swift
import Foundation

enum UserStatus: String, Codable {
    case active = "Active"
    case banned = "Banned"
    case expired = "Expired"
    case disabled = "Disabled"
}

struct User: Codable, Equatable {
    let username: String
    let password: String
    let email: String?
    let status: UserStatus
    let createdAt: Date
    let expiresAt: Date?
    let isTrial: Bool
    let activeConnections: Int
    let maxConnections: Int
}
