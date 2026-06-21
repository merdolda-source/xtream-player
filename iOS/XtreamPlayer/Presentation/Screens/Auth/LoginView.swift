// iOS/XtreamPlayer/Presentation/Screens/Auth/LoginView.swift
import SwiftUI

struct LoginView: View {
    @EnvironmentObject private var router: AppRouter
    @StateObject private var viewModel = LoginViewModel()
    @FocusState private var focusedField: Field?

    private enum Field {
        case host, username, password
    }

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    header

                    VStack(spacing: 16) {
                        TextField("Server URL (e.g. example.com:8080)", text: $viewModel.host)
                            .textFieldStyle(.roundedBorder)
                            .keyboardType(.URL)
                            .autocorrectionDisabled(true)
                            .textInputAutocapitalization(.never)
                            .focused($focusedField, equals: .host)
                            .submitLabel(.next)

                        TextField("Username", text: $viewModel.username)
                            .textFieldStyle(.roundedBorder)
                            .autocorrectionDisabled(true)
                            .textInputAutocapitalization(.never)
                            .focused($focusedField, equals: .username)
                            .submitLabel(.next)

                        SecureField("Password", text: $viewModel.password)
                            .textFieldStyle(.roundedBorder)
                            .focused($focusedField, equals: .password)
                            .submitLabel(.go)
                            .onSubmit { Task { await submit() } }
                    }
                    .padding(.horizontal)

                    if let errorMessage = viewModel.errorMessage {
                        Text(errorMessage)
                            .font(.footnote)
                            .foregroundColor(.red)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal)
                    }

                    Button {
                        Task { await submit() }
                    } label: {
                        if viewModel.isLoading {
                            ProgressView()
                                .frame(maxWidth: .infinity)
                        } else {
                            Text("Log In")
                                .frame(maxWidth: .infinity)
                        }
                    }
                    .buttonStyle(.borderedProminent)
                    .disabled(!viewModel.canSubmit)
                    .padding(.horizontal)
                }
                .padding(.top, 60)
            }
            .navigationBarHidden(true)
        }
    }

    private var header: some View {
        VStack(spacing: 8) {
            Image(systemName: "tv.fill")
                .font(.system(size: 56))
                .foregroundColor(.accentColor)
            Text("Xtream Player")
                .font(.largeTitle)
                .fontWeight(.bold)
            Text("Sign in with your Xtream Codes account")
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
    }

    private func submit() async {
        focusedField = nil
        if let user = await viewModel.login() {
            router.handleLoginSuccess(user)
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
            .environmentObject(AppRouter(authRepository: DIContainer.shared.authRepository))
    }
}
