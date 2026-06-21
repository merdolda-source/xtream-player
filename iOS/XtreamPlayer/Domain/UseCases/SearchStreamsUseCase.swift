// iOS/XtreamPlayer/Domain/UseCases/SearchStreamsUseCase.swift
import Foundation

protocol SearchStreamsUseCase {
    func execute(query: String) async throws -> [Stream]
}

final class SearchStreamsUseCaseImpl: SearchStreamsUseCase {
    private let repository: StreamRepository

    init(repository: StreamRepository) {
        self.repository = repository
    }

    func execute(query: String) async throws -> [Stream] {
        try await repository.searchStreams(query: query)
    }
}
