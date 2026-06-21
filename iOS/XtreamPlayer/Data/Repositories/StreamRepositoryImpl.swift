// iOS/XtreamPlayer/Data/Repositories/StreamRepositoryImpl.swift
import Foundation

final class StreamRepositoryImpl: StreamRepository {
    private let apiClient: XtreamApiClient
    private let m3uParser: M3UParser
    private let coreDataManager: CoreDataManager

    /// In-memory cache of the most recently fetched streams across all
    /// categories/types, used as the search corpus for `searchStreams`.
    private var allKnownStreams: [Stream] = []

    init(apiClient: XtreamApiClient, m3uParser: M3UParser, coreDataManager: CoreDataManager) {
        self.apiClient = apiClient
        self.m3uParser = m3uParser
        self.coreDataManager = coreDataManager
    }

    func getLiveStreams(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream] {
        let streams = try await apiClient.getLiveStreams(host: host, username: username, password: password, categoryId: categoryId)
            .map { tagged($0, as: .live) }
        cache(streams)
        return streams
    }

    func getVODStreams(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream] {
        let streams = try await apiClient.getVODStreams(host: host, username: username, password: password, categoryId: categoryId)
            .map { tagged($0, as: .vod) }
        cache(streams)
        return streams
    }

    func getSeries(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream] {
        let streams = try await apiClient.getSeries(host: host, username: username, password: password, categoryId: categoryId)
            .map { tagged($0, as: .series) }
        cache(streams)
        return streams
    }

    func searchStreams(query: String) async throws -> [Stream] {
        guard !query.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return [] }
        let lowercasedQuery = query.lowercased()
        return allKnownStreams.filter { $0.name.lowercased().contains(lowercasedQuery) }
    }

    func getFavorites() async throws -> [Stream] {
        try coreDataManager.fetchFavorites()
    }

    func addFavorite(_ stream: Stream) async throws {
        try coreDataManager.addFavorite(stream)
    }

    func removeFavorite(_ streamId: String) async throws {
        try coreDataManager.removeFavorite(streamId: streamId)
    }

    func getWatchHistory() async throws -> [Stream] {
        try coreDataManager.fetchWatchHistory()
    }

    // MARK: - Private

    private func tagged(_ stream: Stream, as type: StreamType) -> Stream {
        var stream = stream
        stream.type = type
        return stream
    }

    private func cache(_ streams: [Stream]) {
        for stream in streams {
            if let index = allKnownStreams.firstIndex(where: { $0.streamId == stream.streamId }) {
                allKnownStreams[index] = stream
            } else {
                allKnownStreams.append(stream)
            }
        }
    }
}
