// iOS/XtreamPlayer/Data/Persistence/KeychainManager.swift
import Foundation
import Security

class KeychainManager {
    static let shared = KeychainManager()
    
    private init() {}
    
    enum KeychainError: Error {
        case saveError
        case retrieveError
        case deleteError
        case decodingError
    }
    
    // MARK: - Public Methods
    
    func save(_ value: String, forKey key: String) throws {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecValueData as String: value.data(using: .utf8) ?? Data()
        ]
        
        SecItemDelete(query as CFDictionary)
        
        let status = SecItemAdd(query as CFDictionary, nil)
        guard status == errSecSuccess else {
            throw KeychainError.saveError
        }
    }
    
    func retrieve(forKey key: String) throws -> String {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
            kSecReturnData as String: kCFBooleanTrue as Any,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]
        
        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        
        guard status == errSecSuccess else {
            throw KeychainError.retrieveError
        }
        
        guard let data = result as? Data,
              let value = String(data: data, encoding: .utf8) else {
            throw KeychainError.decodingError
        }
        
        return value
    }
    
    func delete(forKey key: String) throws {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key
        ]
        
        let status = SecItemDelete(query as CFDictionary)
        guard status == errSecSuccess else {
            throw KeychainError.deleteError
        }
    }
    
    func saveCodable<T: Codable>(_ value: T, forKey key: String) throws {
        let encoder = JSONEncoder()
        let data = try encoder.encode(value)
        let jsonString = String(data: data, encoding: .utf8) ?? ""
        try save(jsonString, forKey: key)
    }
    
    func retrieveCodable<T: Codable>(forKey key: String, as type: T.Type) throws -> T {
        let jsonString = try retrieve(forKey: key)
        let decoder = JSONDecoder()
        let data = jsonString.data(using: .utf8) ?? Data()
        return try decoder.decode(type, from: data)
    }
}
