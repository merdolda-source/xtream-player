// iOS/XtreamPlayer/Domain/Entities/Stream.swift
import Foundation

enum StreamType: String, Codable {
    case live
    case vod
    case series
}

struct Stream: Codable, Identifiable, Equatable, Hashable {
    let id: Int
    let num: Int
    let name: String
    let streamIcon: String
    let streamId: String
    let categoryId: String
    let categoryName: String
    let directSource: String
    let rating: Double?
    let added: Date
    var type: StreamType

    init(
        id: Int,
        num: Int,
        name: String,
        streamIcon: String,
        streamId: String,
        categoryId: String,
        categoryName: String,
        directSource: String,
        rating: Double? = nil,
        added: Date = Date(),
        type: StreamType = .live
    ) {
        self.id = id
        self.num = num
        self.name = name
        self.streamIcon = streamIcon
        self.streamId = streamId
        self.categoryId = categoryId
        self.categoryName = categoryName
        self.directSource = directSource
        self.rating = rating
        self.added = added
        self.type = type
    }
}
