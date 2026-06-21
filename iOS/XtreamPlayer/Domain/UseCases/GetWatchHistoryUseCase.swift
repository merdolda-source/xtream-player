// iOS/XtreamPlayer/Domain/UseCases/GetWatchHistoryUseCase.swift
import Foundation

protocol GetWatchHistoryUseCase {
    func execute() async throws -> [Stream]
    func clear() async throws
}

final class GetWatchHistoryUseCaseImpl: GetWatchHistoryUseCase {
    private let repository: PlaybackRepository

    init(repository: PlaybackRepository) {
        self.repository = repository
    }

    func execute() async throws -> [Stream] {
        try repository.getWatchHistory()
    }

    func clear() async throws {
        try repository.clearWatchHistory()
    }
}
