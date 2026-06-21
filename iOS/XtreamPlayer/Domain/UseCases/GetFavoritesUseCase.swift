// iOS/XtreamPlayer/Domain/UseCases/GetFavoritesUseCase.swift
import Foundation

protocol GetFavoritesUseCase {
    func execute() async throws -> [Stream]
    func addFavorite(_ stream: Stream) async throws
    func removeFavorite(_ streamId: String) async throws
}

final class GetFavoritesUseCaseImpl: GetFavoritesUseCase {
    private let repository: StreamRepository

    init(repository: StreamRepository) {
        self.repository = repository
    }

    func execute() async throws -> [Stream] {
        try await repository.getFavorites()
    }

    func addFavorite(_ stream: Stream) async throws {
        try await repository.addFavorite(stream)
    }

    func removeFavorite(_ streamId: String) async throws {
        try await repository.removeFavorite(streamId)
    }
}
