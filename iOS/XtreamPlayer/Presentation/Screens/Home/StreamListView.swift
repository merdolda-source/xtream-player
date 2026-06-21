// iOS/XtreamPlayer/Presentation/Screens/Home/StreamListView.swift
import SwiftUI

/// Reusable list of streams used by every tab on the Home screen
/// (Live, VOD, Series, Favorites, History).
struct StreamListView: View {
    let streams: [Stream]
    let isFavorite: (Stream) -> Bool
    let onSelect: (Stream) -> Void
    let onToggleFavorite: (Stream) -> Void
    var emptyMessage: String = "Nothing here yet."

    var body: some View {
        if streams.isEmpty {
            VStack(spacing: 12) {
                Image(systemName: "tray")
                    .font(.system(size: 40))
                    .foregroundColor(.secondary)
                Text(emptyMessage)
                    .foregroundColor(.secondary)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        } else {
            List(streams) { stream in
                StreamRow(
                    stream: stream,
                    isFavorite: isFavorite(stream),
                    onToggleFavorite: { onToggleFavorite(stream) }
                )
                .contentShape(Rectangle())
                .onTapGesture {
                    onSelect(stream)
                }
            }
            .listStyle(.plain)
        }
    }
}

private struct StreamRow: View {
    let stream: Stream
    let isFavorite: Bool
    let onToggleFavorite: () -> Void

    var body: some View {
        HStack(spacing: 12) {
            AsyncImage(url: URL(string: stream.streamIcon)) { phase in
                if let image = phase.image {
                    image.resizable().aspectRatio(contentMode: .fit)
                } else {
                    Image(systemName: "tv")
                        .foregroundColor(.secondary)
                }
            }
            .frame(width: 44, height: 44)
            .background(Color.secondary.opacity(0.1))
            .clipShape(RoundedRectangle(cornerRadius: 6))

            VStack(alignment: .leading, spacing: 2) {
                Text(stream.name)
                    .font(.body)
                    .lineLimit(1)
                Text(stream.categoryName)
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .lineLimit(1)
            }

            Spacer()

            Button(action: onToggleFavorite) {
                Image(systemName: isFavorite ? "star.fill" : "star")
                    .foregroundColor(isFavorite ? .yellow : .secondary)
            }
            .buttonStyle(.plain)
        }
        .padding(.vertical, 4)
    }
}
