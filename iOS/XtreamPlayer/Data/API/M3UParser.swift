// iOS/XtreamPlayer/Data/API/M3UParser.swift
import Foundation

/// Parses M3U / M3U8 playlist text into `Stream` entities.
/// This provides an alternative import path to the Xtream player_api.php
/// JSON endpoints (e.g. for users who only have a playlist URL).
final class M3UParser {

    /// Parses raw M3U playlist content.
    ///
    /// Expected format:
    /// ```
    /// #EXTM3U
    /// #EXTINF:-1 tvg-id="ch1" tvg-name="Channel 1" tvg-logo="http://..." group-title="News",Channel 1
    /// http://example.com/stream1.m3u8
    /// ```
    func parse(_ content: String, type: StreamType = .live) -> [Stream] {
        var streams: [Stream] = []

        let lines = content
            .components(separatedBy: .newlines)
            .map { $0.trimmingCharacters(in: .whitespaces) }
            .filter { !$0.isEmpty }

        guard !lines.isEmpty else { return streams }

        var pendingName: String?
        var pendingLogo: String = ""
        var pendingCategory: String = "Uncategorized"
        var pendingId: String?
        var index = 0

        for line in lines {
            if line.hasPrefix("#EXTM3U") {
                continue
            } else if line.hasPrefix("#EXTINF") {
                let info = parseExtInf(line)
                pendingName = info.name
                pendingLogo = info.logo
                pendingCategory = info.group
                pendingId = info.tvgId
            } else if line.hasPrefix("#") {
                // Other directives (#EXTGRP, #EXTVLCOPT, etc.) are ignored for MVP.
                continue
            } else {
                // This line is a URL.
                let url = line
                let name = pendingName ?? url
                let streamIdString = pendingId ?? String(index)
                let stream = Stream(
                    id: index,
                    num: index,
                    name: name,
                    streamIcon: pendingLogo,
                    streamId: streamIdString,
                    categoryId: pendingCategory,
                    categoryName: pendingCategory,
                    directSource: url,
                    rating: nil,
                    added: Date(),
                    type: type
                )
                streams.append(stream)
                index += 1

                pendingName = nil
                pendingLogo = ""
                pendingCategory = "Uncategorized"
                pendingId = nil
            }
        }

        return streams
    }

    // MARK: - Private

    private struct ExtInfData {
        let name: String
        let logo: String
        let group: String
        let tvgId: String?
    }

    private func parseExtInf(_ line: String) -> ExtInfData {
        // Format: #EXTINF:-1 attr="value" attr2="value2",Display Name
        let name: String
        if let commaIndex = line.lastIndex(of: ",") {
            name = String(line[line.index(after: commaIndex)...]).trimmingCharacters(in: .whitespaces)
        } else {
            name = ""
        }

        let logo = extractAttribute(named: "tvg-logo", from: line) ?? ""
        let group = extractAttribute(named: "group-title", from: line) ?? "Uncategorized"
        let tvgId = extractAttribute(named: "tvg-id", from: line)

        let displayName = name.isEmpty ? (extractAttribute(named: "tvg-name", from: line) ?? "Unknown") : name

        return ExtInfData(name: displayName, logo: logo, group: group, tvgId: tvgId)
    }

    private func extractAttribute(named attribute: String, from line: String) -> String? {
        let pattern = "\(attribute)=\"([^\"]*)\""
        guard let regex = try? NSRegularExpression(pattern: pattern, options: []) else {
            return nil
        }
        let range = NSRange(line.startIndex..<line.endIndex, in: line)
        guard let match = regex.firstMatch(in: line, options: [], range: range),
              let valueRange = Range(match.range(at: 1), in: line) else {
            return nil
        }
        return String(line[valueRange])
    }
}
