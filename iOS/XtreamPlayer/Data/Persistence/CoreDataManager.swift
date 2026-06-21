// iOS/XtreamPlayer/Data/Persistence/CoreDataManager.swift
import Foundation

// NOTE: A real Core Data stack requires authoring a `.xcdatamodeld` file, which is a
// binary/Xcode-editor-only artifact that cannot be created or validated outside Xcode
// (no Xcode/macOS is available in this build environment). To keep persistence working
// and testable today, this class instead implements a lightweight Codable + FileManager
// JSON-file-backed store that exposes the same kind of save/fetch/delete API a repository
// would need. It can be swapped for a genuine NSPersistentContainer-based implementation
// later without changing the call sites in the Data/Repositories layer.
final class CoreDataManager {
    static let shared = CoreDataManager()

    private let fileManager = FileManager.default
    private let encoder = JSONEncoder()
    private let decoder = JSONDecoder()
    private let queue = DispatchQueue(label: "com.xtream.player.coredatamanager", attributes: .concurrent)

    private let favoritesFileName = "favorites.json"
    private let watchHistoryFileName = "watch_history.json"

    private init() {
        encoder.dateEncodingStrategy = .iso8601
        decoder.dateDecodingStrategy = .iso8601
        createStorageDirectoryIfNeeded()
    }

    // MARK: - Favorites

    func saveFavorites(_ streams: [Stream]) throws {
        try write(streams, to: favoritesFileName)
    }

    func fetchFavorites() throws -> [Stream] {
        try read([Stream].self, from: favoritesFileName) ?? []
    }

    func addFavorite(_ stream: Stream) throws {
        var current = try fetchFavorites()
        if !current.contains(where: { $0.streamId == stream.streamId }) {
            current.append(stream)
            try saveFavorites(current)
        }
    }

    func removeFavorite(streamId: String) throws {
        var current = try fetchFavorites()
        current.removeAll { $0.streamId == streamId }
        try saveFavorites(current)
    }

    // MARK: - Watch History

    func saveWatchHistory(_ streams: [Stream]) throws {
        try write(streams, to: watchHistoryFileName)
    }

    func fetchWatchHistory() throws -> [Stream] {
        try read([Stream].self, from: watchHistoryFileName) ?? []
    }

    func addWatchHistoryEntry(_ stream: Stream) throws {
        var current = try fetchWatchHistory()
        current.removeAll { $0.streamId == stream.streamId }
        current.insert(stream, at: 0)
        try saveWatchHistory(current)
    }

    func clearWatchHistory() throws {
        try saveWatchHistory([])
    }

    // MARK: - Generic helpers

    private func write<T: Encodable>(_ value: T, to fileName: String) throws {
        let url = fileURL(for: fileName)
        let data = try encoder.encode(value)
        try queue.sync(flags: .barrier) {
            try data.write(to: url, options: .atomic)
        }
    }

    private func read<T: Decodable>(_ type: T.Type, from fileName: String) throws -> T? {
        let url = fileURL(for: fileName)
        return try queue.sync {
            guard fileManager.fileExists(atPath: url.path) else { return nil }
            let data = try Data(contentsOf: url)
            return try decoder.decode(type, from: data)
        }
    }

    private func fileURL(for fileName: String) -> URL {
        storageDirectory().appendingPathComponent(fileName)
    }

    private func storageDirectory() -> URL {
        let baseDirectory: URL
        if let appSupport = fileManager.urls(for: .applicationSupportDirectory, in: .userDomainMask).first {
            baseDirectory = appSupport
        } else {
            baseDirectory = fileManager.temporaryDirectory
        }
        return baseDirectory.appendingPathComponent("XtreamPlayer", isDirectory: true)
    }

    private func createStorageDirectoryIfNeeded() {
        let directory = storageDirectory()
        if !fileManager.fileExists(atPath: directory.path) {
            try? fileManager.createDirectory(at: directory, withIntermediateDirectories: true)
        }
    }
}
