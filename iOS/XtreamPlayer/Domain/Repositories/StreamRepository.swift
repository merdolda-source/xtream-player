// iOS/XtreamPlayer/Domain/Repositories/StreamRepository.swift
import Foundation

protocol StreamRepository {
    func getLiveStreams(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream]
    func getVODStreams(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream]
    func getSeries(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream]
    func searchStreams(query: String) async throws -> [Stream]
    func getFavorites() async throws -> [Stream]
    func addFavorite(_ stream: Stream) async throws
    func removeFavorite(_ streamId: String) async throws
    func getWatchHistory() async throws -> [Stream]
}
