// iOS/XtreamPlayer/Data/Repositories/ProfileRepositoryImpl.swift
import Foundation

final class ProfileRepositoryImpl: ProfileRepository {
    private let coreDataManager: CoreDataManager
    private let keychainManager: KeychainManager

    private let profilesKey = "com.xtream.player.profiles"
    private let activeProfileKey = "com.xtream.player.activeProfile"

    init(coreDataManager: CoreDataManager, keychainManager: KeychainManager) {
        self.coreDataManager = coreDataManager
        self.keychainManager = keychainManager
    }

    func getProfiles() throws -> [Profile] {
        (try? keychainManager.retrieveCodable(forKey: profilesKey, as: [Profile].self)) ?? []
    }

    func saveProfile(_ profile: Profile) throws {
        var profiles = try getProfiles()
        if let index = profiles.firstIndex(where: { $0.id == profile.id }) {
            profiles[index] = profile
        } else {
            profiles.append(profile)
        }
        try keychainManager.saveCodable(profiles, forKey: profilesKey)
    }

    func deleteProfile(_ profileId: String) throws {
        var profiles = try getProfiles()
        profiles.removeAll { $0.id == profileId }
        try keychainManager.saveCodable(profiles, forKey: profilesKey)
    }

    func getActiveProfile() throws -> Profile? {
        guard let activeId = try? keychainManager.retrieve(forKey: activeProfileKey) else { return nil }
        return try getProfiles().first { $0.id == activeId }
    }

    func setActiveProfile(_ profileId: String) throws {
        try keychainManager.save(profileId, forKey: activeProfileKey)
    }
}
