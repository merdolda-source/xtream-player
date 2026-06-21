// iOS/XtreamPlayer/Domain/Entities/Profile.swift
import Foundation

/// Minimal profile model. Profiles are not core to the MVP scope but
/// DIContainer wires up a ProfileRepository, so a lightweight entity
/// is provided to support a single-profile (or simple multi-profile) setup.
struct Profile: Codable, Identifiable, Equatable {
    let id: String
    var name: String
    var host: String
    var username: String
    var isActive: Bool

    init(
        id: String = UUID().uuidString,
        name: String,
        host: String,
        username: String,
        isActive: Bool = true
    ) {
        self.id = id
        self.name = name
        self.host = host
        self.username = username
        self.isActive = isActive
    }
}
