// iOS/XtreamPlayer/Domain/UseCases/GetStreamsUseCase.swift
import Foundation

protocol GetStreamsUseCase {
    func executeLive(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream]
    func executeVOD(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream]
    func executeSeries(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream]
}

final class GetStreamsUseCaseImpl: GetStreamsUseCase {
    private let repository: StreamRepository

    init(repository: StreamRepository) {
        self.repository = repository
    }

    func executeLive(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream] {
        try await repository.getLiveStreams(host: host, username: username, password: password, categoryId: categoryId)
    }

    func executeVOD(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream] {
        try await repository.getVODStreams(host: host, username: username, password: password, categoryId: categoryId)
    }

    func executeSeries(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream] {
        try await repository.getSeries(host: host, username: username, password: password, categoryId: categoryId)
    }
}
