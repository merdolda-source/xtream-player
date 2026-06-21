// iOS/XtreamPlayer/Presentation/Screens/Player/PlayerView.swift
import SwiftUI
import AVKit

struct PlayerView: View {
    @EnvironmentObject private var router: AppRouter
    @StateObject private var viewModel: PlayerViewModel

    init(session: PlayerSession) {
        _viewModel = StateObject(wrappedValue: PlayerViewModel(session: session))
    }

    var body: some View {
        ZStack {
            Color.black.ignoresSafeArea()

            if let player = viewModel.player {
                VideoPlayer(player: player)
                    .ignoresSafeArea()
            } else if viewModel.isLoading {
                ProgressView()
                    .progressViewStyle(.circular)
                    .tint(.white)
            }

            if let errorMessage = viewModel.errorMessage {
                VStack(spacing: 16) {
                    Image(systemName: "exclamationmark.triangle")
                        .font(.system(size: 40))
                        .foregroundColor(.white)
                    Text(errorMessage)
                        .foregroundColor(.white)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 32)
                    Button("Close") {
                        closePlayer()
                    }
                    .buttonStyle(.borderedProminent)
                }
            }

            VStack {
                HStack {
                    Button {
                        closePlayer()
                    } label: {
                        Image(systemName: "xmark.circle.fill")
                            .font(.title)
                            .foregroundColor(.white.opacity(0.8))
                    }
                    .padding()
                    Spacer()
                }
                Spacer()
            }
        }
        .navigationBarHidden(true)
        .onAppear {
            viewModel.loadAndPlay()
        }
        .onDisappear {
            viewModel.stop()
        }
    }

    private func closePlayer() {
        viewModel.stop()
        router.dismissPlayer()
    }
}
