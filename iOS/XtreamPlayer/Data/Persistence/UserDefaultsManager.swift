// iOS/XtreamPlayer/Data/Persistence/UserDefaultsManager.swift
import Foundation

/// Thin wrapper around `UserDefaults` for simple app settings and small
/// pieces of state (active profile id, playback positions, preferences).
final class UserDefaultsManager {
    static let shared = UserDefaultsManager()

    private let defaults: UserDefaults

    private enum Keys {
        static let activeProfileId = "com.xtream.player.activeProfileId"
        static let isLoggedIn = "com.xtream.player.isLoggedIn"
        static let lastHost = "com.xtream.player.lastHost"
        static let lastUsername = "com.xtream.player.lastUsername"
        static let playbackPositionPrefix = "com.xtream.player.playbackPosition."
    }

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
    }

    // MARK: - Session

    var isLoggedIn: Bool {
        get { defaults.bool(forKey: Keys.isLoggedIn) }
        set { defaults.set(newValue, forKey: Keys.isLoggedIn) }
    }

    var lastHost: String? {
        get { defaults.string(forKey: Keys.lastHost) }
        set { defaults.set(newValue, forKey: Keys.lastHost) }
    }

    var lastUsername: String? {
        get { defaults.string(forKey: Keys.lastUsername) }
        set { defaults.set(newValue, forKey: Keys.lastUsername) }
    }

    // MARK: - Profiles

    var activeProfileId: String? {
        get { defaults.string(forKey: Keys.activeProfileId) }
        set { defaults.set(newValue, forKey: Keys.activeProfileId) }
    }

    // MARK: - Playback positions

    func setPlaybackPosition(_ position: Double, forStreamId streamId: String) {
        defaults.set(position, forKey: Keys.playbackPositionPrefix + streamId)
    }

    func getPlaybackPosition(forStreamId streamId: String) -> Double? {
        let key = Keys.playbackPositionPrefix + streamId
        guard defaults.object(forKey: key) != nil else { return nil }
        return defaults.double(forKey: key)
    }

    // MARK: - Generic

    func set(_ value: Any?, forKey key: String) {
        defaults.set(value, forKey: key)
    }

    func value(forKey key: String) -> Any? {
        defaults.object(forKey: key)
    }

    func removeAll() {
        defaults.removePersistentDomain(forName: "com.xtream.player")
    }
}
