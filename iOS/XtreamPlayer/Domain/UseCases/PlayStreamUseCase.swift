// iOS/XtreamPlayer/Domain/UseCases/PlayStreamUseCase.swift
import Foundation

protocol PlayStreamUseCase {
    func execute(host: String, username: String, password: String, stream: Stream) throws -> URL
}

final class PlayStreamUseCaseImpl: PlayStreamUseCase {
    private let repository: PlaybackRepository

    init(repository: PlaybackRepository) {
        self.repository = repository
    }

    func execute(host: String, username: String, password: String, stream: Stream) throws -> URL {
        let url = try repository.getStreamURL(host: host, username: username, password: password, stream: stream)
        try repository.recordWatchHistory(stream)
        return url
    }
}
