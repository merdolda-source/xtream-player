// iOS/XtreamPlayer/Data/Repositories/PlaybackRepositoryImpl.swift
import Foundation

final class PlaybackRepositoryImpl: PlaybackRepository {
    private let coreDataManager: CoreDataManager
    private let userDefaultsManager: UserDefaultsManager

    init(coreDataManager: CoreDataManager, userDefaultsManager: UserDefaultsManager) {
        self.coreDataManager = coreDataManager
        self.userDefaultsManager = userDefaultsManager
    }

    func getStreamURL(host: String, username: String, password: String, stream: Stream) throws -> URL {
        // If the API already gave us a fully-formed direct source URL, use it.
        if !stream.directSource.isEmpty, let url = URL(string: stream.directSource) {
            return url
        }

        // Otherwise, construct the conventional Xtream stream URL based on type.
        let extensionForType: String
        switch stream.type {
        case .live:
            extensionForType = "m3u8"
        case .vod:
            extensionForType = "mp4"
        case .series:
            extensionForType = "mp4"
        }

        let path = "\(stream.type.rawValue)/\(username)/\(password)/\(stream.streamId).\(extensionForType)"
        guard let url = URL(string: "http://\(host)/\(path)") else {
            throw DomainError.invalidStreamURL
        }
        return url
    }

    func recordWatchHistory(_ stream: Stream) throws {
        try coreDataManager.addWatchHistoryEntry(stream)
    }

    func getWatchHistory() throws -> [Stream] {
        try coreDataManager.fetchWatchHistory()
    }

    func clearWatchHistory() throws {
        try coreDataManager.clearWatchHistory()
    }

    func savePlaybackPosition(streamId: String, position: Double) throws {
        userDefaultsManager.setPlaybackPosition(position, forStreamId: streamId)
    }

    func getPlaybackPosition(streamId: String) -> Double? {
        userDefaultsManager.getPlaybackPosition(forStreamId: streamId)
    }
}
