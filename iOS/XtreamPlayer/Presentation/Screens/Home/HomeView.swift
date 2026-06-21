// iOS/XtreamPlayer/Presentation/Screens/Home/HomeView.swift
import SwiftUI

/// Home screen. Hosts Live/VOD/Series tabs plus Favorites and Watch History,
/// reachable from the same tab bar so there's a single navigable surface
/// after login (no separate top-level screens for favorites/history).
struct HomeView: View {
    @EnvironmentObject private var router: AppRouter
    @StateObject private var viewModel: HomeViewModel

    private let host: String
    private let username: String
    private let password: String

    init(host: String, username: String, password: String) {
        self.host = host
        self.username = username
        self.password = password
        _viewModel = StateObject(wrappedValue: HomeViewModel(host: host, username: username, password: password))
    }

    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                if viewModel.isLoading && currentStreams.isEmpty {
                    ProgressView()
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    StreamListView(
                        streams: currentStreams,
                        isFavorite: viewModel.isFavorite,
                        onSelect: play,
                        onToggleFavorite: { stream in
                            Task { await viewModel.toggleFavorite(stream) }
                        },
                        emptyMessage: emptyMessage(for: viewModel.selectedTab)
                    )
                }

                if let errorMessage = viewModel.errorMessage {
                    Text(errorMessage)
                        .font(.footnote)
                        .foregroundColor(.red)
                        .padding()
                }
            }
            .navigationTitle(viewModel.selectedTab.rawValue)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button {
                        router.logout()
                    } label: {
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                    }
                }
            }
            .searchable(text: $viewModel.searchQuery, prompt: "Search streams")
            .onSubmit(of: .search) {
                Task { await viewModel.search() }
            }
        }
        .safeAreaInset(edge: .bottom) {
            tabBar
        }
        .task {
            await viewModel.loadInitialData()
        }
    }

    private var currentStreams: [Stream] {
        if !viewModel.searchQuery.trimmingCharacters(in: .whitespaces).isEmpty {
            return viewModel.searchResults
        }
        switch viewModel.selectedTab {
        case .live: return viewModel.liveStreams
        case .vod: return viewModel.vodStreams
        case .series: return viewModel.seriesStreams
        case .favorites: return viewModel.favorites
        case .history: return viewModel.watchHistory
        }
    }

    private var tabBar: some View {
        HStack {
            ForEach(StreamTab.allCases) { tab in
                Button {
                    viewModel.tabSelected(tab)
                } label: {
                    VStack(spacing: 4) {
                        Image(systemName: icon(for: tab))
                        Text(tab.rawValue)
                            .font(.caption2)
                    }
                    .frame(maxWidth: .infinity)
                }
                .foregroundColor(viewModel.selectedTab == tab ? .accentColor : .secondary)
            }
        }
        .padding(.vertical, 8)
        .background(.bar)
    }

    private func icon(for tab: StreamTab) -> String {
        switch tab {
        case .live: return "tv"
        case .vod: return "film"
        case .series: return "play.tv"
        case .favorites: return "star"
        case .history: return "clock"
        }
    }

    private func emptyMessage(for tab: StreamTab) -> String {
        switch tab {
        case .live: return "No live channels found."
        case .vod: return "No movies found."
        case .series: return "No series found."
        case .favorites: return "You haven't added any favorites yet."
        case .history: return "You haven't watched anything yet."
        }
    }

    private func play(_ stream: Stream) {
        router.play(PlayerSession(stream: stream, host: host, username: username, password: password))
    }
}
