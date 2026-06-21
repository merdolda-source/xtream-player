// iOS/XtreamPlayer/Presentation/Screens/Home/HomeViewModel.swift
import Foundation
import Combine

enum StreamTab: String, CaseIterable, Identifiable {
    case live = "Live TV"
    case vod = "Movies"
    case series = "Series"
    case favorites = "Favorites"
    case history = "History"

    var id: String { rawValue }
}

@MainActor
final class HomeViewModel: ObservableObject {
    @Published var selectedTab: StreamTab = .live
    @Published var liveStreams: [Stream] = []
    @Published var vodStreams: [Stream] = []
    @Published var seriesStreams: [Stream] = []
    @Published var favorites: [Stream] = []
    @Published var watchHistory: [Stream] = []
    @Published var searchQuery: String = ""
    @Published var searchResults: [Stream] = []
    @Published var isLoading: Bool = false
    @Published var errorMessage: String?

    private let getStreamsUseCase: GetStreamsUseCase
    private let searchStreamsUseCase: SearchStreamsUseCase
    private let getFavoritesUseCase: GetFavoritesUseCase
    private let getWatchHistoryUseCase: GetWatchHistoryUseCase

    private let host: String
    private let username: String
    private let password: String

    init(
        host: String,
        username: String,
        password: String,
        getStreamsUseCase: GetStreamsUseCase = DIContainer.shared.getStreamsUseCase,
        searchStreamsUseCase: SearchStreamsUseCase = DIContainer.shared.searchStreamsUseCase,
        getFavoritesUseCase: GetFavoritesUseCase = DIContainer.shared.getFavoritesUseCase,
        getWatchHistoryUseCase: GetWatchHistoryUseCase = DIContainer.shared.getWatchHistoryUseCase
    ) {
        self.host = host
        self.username = username
        self.password = password
        self.getStreamsUseCase = getStreamsUseCase
        self.searchStreamsUseCase = searchStreamsUseCase
        self.getFavoritesUseCase = getFavoritesUseCase
        self.getWatchHistoryUseCase = getWatchHistoryUseCase
    }

    func loadInitialData() async {
        await loadLiveStreams()
        await loadFavorites()
        await loadWatchHistory()
    }

    func loadLiveStreams(categoryId: String? = nil) async {
        await load {
            self.liveStreams = try await self.getStreamsUseCase.executeLive(
                host: self.host, username: self.username, password: self.password, categoryId: categoryId
            )
        }
    }

    func loadVODStreams(categoryId: String? = nil) async {
        await load {
            self.vodStreams = try await self.getStreamsUseCase.executeVOD(
                host: self.host, username: self.username, password: self.password, categoryId: categoryId
            )
        }
    }

    func loadSeries(categoryId: String? = nil) async {
        await load {
            self.seriesStreams = try await self.getStreamsUseCase.executeSeries(
                host: self.host, username: self.username, password: self.password, categoryId: categoryId
            )
        }
    }

    func loadFavorites() async {
        await load {
            self.favorites = try await self.getFavoritesUseCase.execute()
        }
    }

    func loadWatchHistory() async {
        await load {
            self.watchHistory = try await self.getWatchHistoryUseCase.execute()
        }
    }

    func search() async {
        guard !searchQuery.trimmingCharacters(in: .whitespaces).isEmpty else {
            searchResults = []
            return
        }
        await load {
            self.searchResults = try await self.searchStreamsUseCase.execute(query: self.searchQuery)
        }
    }

    func toggleFavorite(_ stream: Stream) async {
        do {
            if favorites.contains(where: { $0.streamId == stream.streamId }) {
                try await getFavoritesUseCase.removeFavorite(stream.streamId)
            } else {
                try await getFavoritesUseCase.addFavorite(stream)
            }
            await loadFavorites()
        } catch {
            errorMessage = "Could not update favorites."
        }
    }

    func isFavorite(_ stream: Stream) -> Bool {
        favorites.contains { $0.streamId == stream.streamId }
    }

    func tabSelected(_ tab: StreamTab) {
        selectedTab = tab
        Task {
            switch tab {
            case .live:
                if liveStreams.isEmpty { await loadLiveStreams() }
            case .vod:
                if vodStreams.isEmpty { await loadVODStreams() }
            case .series:
                if seriesStreams.isEmpty { await loadSeries() }
            case .favorites:
                await loadFavorites()
            case .history:
                await loadWatchHistory()
            }
        }
    }

    private func load(_ operation: @escaping () async throws -> Void) async {
        isLoading = true
        errorMessage = nil
        defer { isLoading = false }
        do {
            try await operation()
        } catch {
            errorMessage = (error as? LocalizedError)?.errorDescription ?? "Something went wrong. Please try again."
        }
    }
}
