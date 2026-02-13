//
//  SignInCallbackHelper.swift
//  SignInKMPSwift
//
//  Helper generico que coordina la autenticacion social (Google, Apple, etc.) entre Kotlin y Swift usando NSNotificationCenter
//
//  Created by Ivan Boniquet Rodriguez on 4/1/26.
//  Copyright © 2026 BonyGoD. All rights reserved.

import Foundation
import UIKit

/// Coordinador central para manejar las solicitudes de autenticacion desde Kotlin
/// Soporta multiples proveedores: Google, Apple, etc.
public class SignInCallbackHelper {

    public static let shared = SignInCallbackHelper()

    private init() {
        setupNotificationObserver()
    }

    private func setupNotificationObserver() {
        // Observador para Google Sign-In
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleGoogleSignInRequest),
            name: NSNotification.Name("GoogleSignInRequested"),
            object: nil
        )

        // Observador para Apple Sign-In
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleAppleSignInRequest),
            name: NSNotification.Name("AppleSignInRequested"),
            object: nil
        )
    }

    @objc private func handleGoogleSignInRequest() {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = windowScene.windows.first?.rootViewController else {
            notifyKotlinError(provider: "Google", error: "No se pudo obtener el view controller")
            return
        }

        GoogleSignInBridge.signIn(presentingViewController: rootVC) { [weak self] userData, error in
            if let error = error {
                self?.notifyKotlinError(provider: "Google", error: error.localizedDescription)
            } else if let userData = userData {
                self?.notifyKotlinGoogleSuccess(userData: userData)
            } else {
                self?.notifyKotlinError(provider: "Google", error: "Unknown error")
            }
        }
    }

    @objc private func handleAppleSignInRequest() {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = windowScene.windows.first?.rootViewController else {
            notifyKotlinError(provider: "Apple", error: "No se pudo obtener el view controller")
            return
        }

        AppleSignInBridge.signIn(presentingViewController: rootVC) { [weak self] userData, error in
            if let error = error {
                self?.notifyKotlinError(provider: "Apple", error: error.localizedDescription)
            } else if let userData = userData {
                self?.notifyKotlinAppleSuccess(userData: userData)
            } else {
                self?.notifyKotlinError(provider: "Apple", error: "Unknown error")
            }
        }
    }

    private func notifyKotlinError(provider: String, error: String) {
        NotificationCenter.default.post(
            name: NSNotification.Name("\(provider)SignInError"),
            object: nil,
            userInfo: ["error": error]
        )
    }

    private func notifyKotlinGoogleSuccess(userData: GoogleUserData) {
        let userInfo: [String: Any] = [
            "displayName": userData.displayName,
            "uid": userData.uid,
            "email": userData.email,
            "photoURL": userData.photoURL
        ]

        NotificationCenter.default.post(
            name: NSNotification.Name("GoogleSignInSuccess"),
            object: nil,
            userInfo: userInfo
        )
    }

    private func notifyKotlinAppleSuccess(userData: AppleUserData) {
        let userInfo: [String: Any] = [
            "displayName": userData.displayName,
            "uid": userData.uid,
            "email": userData.email,
            "photoURL": userData.photoURL
        ]

        NotificationCenter.default.post(
            name: NSNotification.Name("AppleSignInSuccess"),
            object: nil,
            userInfo: userInfo
        )
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}





