// iOS/XtreamPlayer/Presentation/Screens/Player/PlayerViewModel.swift
import Foundation
import AVFoundation
import Combine

@MainActor
final class PlayerViewModel: ObservableObject {
    @Published var player: AVPlayer?
    @Published var errorMessage: String?
    @Published var isLoading: Bool = true

    private let playStreamUseCase: PlayStreamUseCase
    private let playbackRepository: PlaybackRepository
    private var timeObserverToken: Any?

    let session: PlayerSession

    init(
        session: PlayerSession,
        playStreamUseCase: PlayStreamUseCase = DIContainer.shared.playStreamUseCase,
        playbackRepository: PlaybackRepository = DIContainer.shared.playbackRepository
    ) {
        self.session = session
        self.playStreamUseCase = playStreamUseCase
        self.playbackRepository = playbackRepository
    }

    func loadAndPlay() {
        isLoading = true
        errorMessage = nil

        do {
            let url = try playStreamUseCase.execute(
                host: session.host,
                username: session.username,
                password: session.password,
                stream: session.stream
            )

            let avPlayer = AVPlayer(url: url)
            self.player = avPlayer

            if let savedPosition = playbackRepository.getPlaybackPosition(streamId: session.stream.streamId), savedPosition > 0 {
                let time = CMTime(seconds: savedPosition, preferredTimescale: 1)
                avPlayer.seek(to: time)
            }

            observePlayback(avPlayer)
            avPlayer.play()
            isLoading = false
        } catch {
            errorMessage = (error as? LocalizedError)?.errorDescription ?? "Unable to play this stream."
            isLoading = false
        }
    }

    func stop() {
        if let token = timeObserverToken {
            player?.removeTimeObserver(token)
            timeObserverToken = nil
        }
        savePosition()
        player?.pause()
        player = nil
    }

    private func observePlayback(_ player: AVPlayer) {
        let interval = CMTime(seconds: 5, preferredTimescale: 1)
        timeObserverToken = player.addPeriodicTimeObserver(forTime: interval, queue: .main) { [weak self] time in
            self?.persistPosition(time.seconds)
        }
    }

    private func persistPosition(_ seconds: Double) {
        guard seconds.isFinite, seconds > 0 else { return }
        try? playbackRepository.savePlaybackPosition(streamId: session.stream.streamId, position: seconds)
    }

    private func savePosition() {
        guard let currentTime = player?.currentTime().seconds, currentTime.isFinite, currentTime > 0 else { return }
        try? playbackRepository.savePlaybackPosition(streamId: session.stream.streamId, position: currentTime)
    }
}
