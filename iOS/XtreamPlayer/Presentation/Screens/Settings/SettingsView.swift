// iOS/XtreamPlayer/Presentation/Screens/Settings/SettingsView.swift
import SwiftUI

struct SettingsView: View {
    @EnvironmentObject private var router: AppRouter
    @StateObject private var viewModel = SettingsViewModel()
    @State private var showClearHistoryConfirmation = false

    var body: some View {
        NavigationView {
            Form {
                Section("Account") {
                    HStack {
                        Text("Username")
                        Spacer()
                        Text(viewModel.username)
                            .foregroundColor(.secondary)
                    }
                }

                Section("Data") {
                    Button("Clear Watch History", role: .destructive) {
                        showClearHistoryConfirmation = true
                    }
                }

                Section {
                    Button("Log Out", role: .destructive) {
                        router.logout()
                    }
                }

                Section("About") {
                    HStack {
                        Text("Version")
                        Spacer()
                        Text("1.0.0 (MVP)")
                            .foregroundColor(.secondary)
                    }
                }
            }
            .navigationTitle("Settings")
            .confirmationDialog(
                "Clear all watch history?",
                isPresented: $showClearHistoryConfirmation,
                titleVisibility: .visible
            ) {
                Button("Clear History", role: .destructive) {
                    Task { await viewModel.clearWatchHistory() }
                }
                Button("Cancel", role: .cancel) {}
            }
        }
    }
}

struct SettingsView_Previews: PreviewProvider {
    static var previews: some View {
        SettingsView()
            .environmentObject(AppRouter(authRepository: DIContainer.shared.authRepository))
    }
}
