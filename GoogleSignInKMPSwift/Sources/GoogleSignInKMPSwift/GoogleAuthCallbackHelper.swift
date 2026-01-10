//
//  GoogleAuthCallbackHelper.swift
//  GoogleSignInKMPSwift
//
//  Helper que coordina Google Sign-In entre Kotlin y Swift usando NSNotificationCenter
//
//  Created by Ivan Boniquet Rodriguez on 4/1/26.
//  Copyright © 2026 BonyGoD. All rights reserved.

import Foundation
import UIKit

public class GoogleAuthCallbackHelper {

    public static let shared = GoogleAuthCallbackHelper()

    private init() {
        setupNotificationObserver()
    }

    private func setupNotificationObserver() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(handleSignInRequest),
            name: NSNotification.Name("GoogleSignInRequested"),
            object: nil
        )
    }

    @objc private func handleSignInRequest() {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootVC = windowScene.windows.first?.rootViewController else {
            notifyKotlinError("No se pudo obtener el view controller")
            return
        }

        GoogleSignInBridge.signIn(presentingViewController: rootVC) { [weak self] userData, error in
            if let error = error {
                self?.notifyKotlinError(error.localizedDescription)
            } else if let userData = userData {
                self?.notifyKotlinSuccess(userData: userData)
            } else {
                self?.notifyKotlinError("Unknown error")
            }
        }
    }

    private func notifyKotlinError(_ error: String) {
        NotificationCenter.default.post(
            name: NSNotification.Name("GoogleSignInError"),
            object: nil,
            userInfo: ["error": error]
        )
    }

    private func notifyKotlinSuccess(userData: GoogleUserData) {
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

    deinit {
        NotificationCenter.default.removeObserver(self)
    }
}




