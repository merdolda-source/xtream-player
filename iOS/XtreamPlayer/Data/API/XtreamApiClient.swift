// iOS/XtreamPlayer/Data/API/XtreamApiClient.swift
import Foundation

class XtreamApiClient {
    private let session: URLSession
    private let baseURL: URL?
    private let decoder = JSONDecoder()
    
    init(baseURL: URL? = nil) {
        self.baseURL = baseURL
        
        let config = URLSessionConfiguration.default
        config.timeoutIntervalForRequest = 30
        config.timeoutIntervalForResource = 60
        config.waitsForConnectivity = true
        
        self.session = URLSession(configuration: config)
    }
    
    // MARK: - Public Methods
    
    /// Login to Xtream Codes server
    func login(host: String, username: String, password: String) async throws -> User {
        let url = URL(string: "http://\(host)/player_api.php")?.appendingQueryParameters([
            "username": username,
            "password": password,
            "action": "get_live_categories"
        ])
        
        guard let url = url else {
            throw DomainError.invalidStreamURL
        }
        
        let (data, response) = try await session.data(from: url)
        try validateResponse(response, statusCode: 200)
        
        let userResponse = try decoder.decode(UserResponse.self, from: data)
        return userResponse.toDomain()
    }
    
    /// Get live streams for category
    func getLiveStreams(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream] {
        var parameters: [String: String] = [
            "username": username,
            "password": password,
            "action": "get_live_streams"
        ]
        
        if let categoryId = categoryId {
            parameters["category_id"] = categoryId
        }
        
        let url = URL(string: "http://\(host)/player_api.php")?
            .appendingQueryParameters(parameters)
        
        guard let url = url else {
            throw DomainError.invalidStreamURL
        }
        
        let (data, response) = try await session.data(from: url)
        try validateResponse(response, statusCode: 200)
        
        let streams = try decoder.decode([StreamResponse].self, from: data)
        return streams.map { $0.toDomain() }
    }
    
    /// Get VOD (Movie) streams
    func getVODStreams(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream] {
        var parameters: [String: String] = [
            "username": username,
            "password": password,
            "action": "get_vod_streams"
        ]
        
        if let categoryId = categoryId {
            parameters["category_id"] = categoryId
        }
        
        let url = URL(string: "http://\(host)/player_api.php")?
            .appendingQueryParameters(parameters)
        
        guard let url = url else {
            throw DomainError.invalidStreamURL
        }
        
        let (data, response) = try await session.data(from: url)
        try validateResponse(response, statusCode: 200)
        
        let streams = try decoder.decode([StreamResponse].self, from: data)
        return streams.map { $0.toDomain() }
    }
    
    /// Get series
    func getSeries(host: String, username: String, password: String, categoryId: String?) async throws -> [Stream] {
        var parameters: [String: String] = [
            "username": username,
            "password": password,
            "action": "get_series"
        ]
        
        if let categoryId = categoryId {
            parameters["category_id"] = categoryId
        }
        
        let url = URL(string: "http://\(host)/player_api.php")?
            .appendingQueryParameters(parameters)
        
        guard let url = url else {
            throw DomainError.invalidStreamURL
        }
        
        let (data, response) = try await session.data(from: url)
        try validateResponse(response, statusCode: 200)
        
        let streams = try decoder.decode([StreamResponse].self, from: data)
        return streams.map { $0.toDomain() }
    }
    
    // MARK: - Private Methods
    
    private func validateResponse(_ response: URLResponse, statusCode: Int) throws {
        guard let httpResponse = response as? HTTPURLResponse else {
            throw DomainError.invalidResponse
        }
        
        switch httpResponse.statusCode {
        case 200...299:
            break
        case 401:
            throw DomainError.invalidCredentials
        case 403:
            throw DomainError.streamAccessDenied
        case 404:
            throw DomainError.streamNotFound
        case 408, 504:
            throw DomainError.requestTimeout
        default:
            throw DomainError.invalidResponse
        }
    }
}

// MARK: - API Models

struct UserResponse: Codable {
    let username: String
    let password: String
    let email: String?
    let status: String
    let createdAt: String
    let expiresAt: String?
    let isTrial: Bool
    let activeConnections: Int
    let maxConnections: Int
    
    func toDomain() -> User {
        User(
            username: username,
            password: password,
            email: email,
            status: UserStatus(rawValue: status) ?? .active,
            createdAt: ISO8601DateFormatter().date(from: createdAt) ?? Date(),
            expiresAt: expiresAt.flatMap { ISO8601DateFormatter().date(from: $0) },
            isTrial: isTrial,
            activeConnections: activeConnections,
            maxConnections: maxConnections
        )
    }
}

struct StreamResponse: Codable {
    let num: Int
    let name: String
    let streamIcon: String
    let streamId: Int
    let categoryId: String
    let categoryName: String
    let directSource: String
    let rating: Double?
    let added: String
    
    enum CodingKeys: String, CodingKey {
        case num, name, rating, added
        case streamIcon = "stream_icon"
        case streamId = "stream_id"
        case categoryId = "category_id"
        case categoryName = "category_name"
        case directSource = "direct_source"
    }
    
    func toDomain() -> Stream {
        Stream(
            id: streamId,
            num: num,
            name: name,
            streamIcon: streamIcon,
            streamId: String(streamId),
            categoryId: categoryId,
            categoryName: categoryName,
            directSource: directSource,
            rating: rating,
            added: ISO8601DateFormatter().date(from: added) ?? Date()
        )
    }
}

// MARK: - URL Extension

extension URL {
    func appendingQueryParameters(_ parameters: [String: String]) -> URL {
        var components = URLComponents(url: self, resolvingAgainstBaseURL: false)
        components?.queryItems = parameters.map { URLQueryItem(name: $0.key, value: $0.value) }
        return components?.url ?? self
    }
}
