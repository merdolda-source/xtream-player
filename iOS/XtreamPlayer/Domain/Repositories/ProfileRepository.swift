// iOS/XtreamPlayer/Domain/Repositories/ProfileRepository.swift
import Foundation

protocol ProfileRepository {
    func getProfiles() throws -> [Profile]
    func saveProfile(_ profile: Profile) throws
    func deleteProfile(_ profileId: String) throws
    func getActiveProfile() throws -> Profile?
    func setActiveProfile(_ profileId: String) throws
}
