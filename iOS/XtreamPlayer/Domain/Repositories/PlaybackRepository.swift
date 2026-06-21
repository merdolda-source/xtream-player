// iOS/XtreamPlayer/Domain/Repositories/PlaybackRepository.swift
import Foundation

protocol PlaybackRepository {
    func getStreamURL(host: String, username: String, password: String, stream: Stream) throws -> URL
    func recordWatchHistory(_ stream: Stream) throws
    func getWatchHistory() throws -> [Stream]
    func clearWatchHistory() throws
    func savePlaybackPosition(streamId: String, position: Double) throws
    func getPlaybackPosition(streamId: String) -> Double?
}
