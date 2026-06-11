// iOS/XtreamPlayer/Common/DI/DIContainer.swift
import Foundation

class DIContainer {
    static let shared = DIContainer()
    
    private init() {}
    
    // MARK: - API Clients
    lazy var xtreamApiClient: XtreamApiClient = {
        XtreamApiClient()
    }()
    
    lazy var m3uParser: M3UParser = {
        M3UParser()
    }()
    
    // MARK: - Persistence
    lazy var coreDataManager: CoreDataManager = {
        CoreDataManager.shared
    }()
    
    lazy var keychainManager: KeychainManager = {
        KeychainManager.shared
    }()
    
    lazy var userDefaultsManager: UserDefaultsManager = {
        UserDefaultsManager.shared
    }()
    
    // MARK: - Repositories
    lazy var authRepository: AuthRepository = {
        AuthRepositoryImpl(
            apiClient: xtreamApiClient,
            keychainManager: keychainManager,
            userDefaultsManager: userDefaultsManager
        )
    }()
    
    lazy var streamRepository: StreamRepository = {
        StreamRepositoryImpl(
            apiClient: xtreamApiClient,
            m3uParser: m3uParser,
            coreDataManager: coreDataManager
        )
    }()
    
    lazy var profileRepository: ProfileRepository = {
        ProfileRepositoryImpl(
            coreDataManager: coreDataManager,
            keychainManager: keychainManager
        )
    }()
    
    lazy var playbackRepository: PlaybackRepository = {
        PlaybackRepositoryImpl(
            coreDataManager: coreDataManager,
            userDefaultsManager: userDefaultsManager
        )
    }()
    
    // MARK: - Use Cases
    lazy var loginUseCase: LoginUseCase = {
        LoginUseCaseImpl(repository: authRepository)
    }()
    
    lazy var getStreamsUseCase: GetStreamsUseCase = {
        GetStreamsUseCaseImpl(repository: streamRepository)
    }()
    
    lazy var playStreamUseCase: PlayStreamUseCase = {
        PlayStreamUseCaseImpl(repository: playbackRepository)
    }()
    
    lazy var searchStreamsUseCase: SearchStreamsUseCase = {
        SearchStreamsUseCaseImpl(repository: streamRepository)
    }()
    
    lazy var getFavoritesUseCase: GetFavoritesUseCase = {
        GetFavoritesUseCaseImpl(repository: streamRepository)
    }()
    
    lazy var getWatchHistoryUseCase: GetWatchHistoryUseCase = {
        GetWatchHistoryUseCaseImpl(repository: playbackRepository)
    }()
}
