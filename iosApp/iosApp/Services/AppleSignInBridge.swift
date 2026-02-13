//
//  AppleSignInBridge.swift
//  iosApp
//
//  Created by Ivan Boniquet Rodriguez on 12/2/26.
//  Copyright © 2026 BonyGoD. All rights reserved.
//

import Foundation
import AuthenticationServices
import UIKit
import FirebaseAuth

/// Servicio encargado de gestionar la autenticación con Apple Sign-In
@objc public class AppleSignInBridge: NSObject {

    private static var currentNonce: String?
    private static var completionHandler: ((AppleUserData?, NSError?) -> Void)?
    private static var currentDelegate: AppleSignInDelegate?
    private static var currentAuthController: ASAuthorizationController?

    /// Inicia el proceso de autenticación con Apple
    /// - Parameters:
    ///   - presentingViewController: ViewController desde donde se presenta el sign-in
    ///   - completion: Callback con el resultado (AppleUserData o Error)
    @objc public static func signIn(
        presentingViewController: UIViewController,
        completion: @escaping (AppleUserData?, NSError?) -> Void
    ) {
        self.completionHandler = completion

        let nonce = randomNonceString()
        currentNonce = nonce

        let appleIDProvider = ASAuthorizationAppleIDProvider()
        let request = appleIDProvider.createRequest()
        request.requestedScopes = [.fullName, .email]
        request.nonce = sha256(nonce)

        let authorizationController = ASAuthorizationController(authorizationRequests: [request])
        let delegate = AppleSignInDelegate()
        currentDelegate = delegate  // ⚠️ IMPORTANTE: Mantener referencia fuerte
        currentAuthController = authorizationController  // ⚠️ IMPORTANTE: Mantener referencia fuerte
        authorizationController.delegate = delegate
        authorizationController.presentationContextProvider = delegate
        authorizationController.performRequests()
    }

    fileprivate static func handleAppleSignIn(authorization: ASAuthorization) {
        guard let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential else {
            completionHandler?(nil, NSError(domain: "Invalid Apple Credential", code: -1))
            return
        }

        guard let nonce = currentNonce else {
            completionHandler?(nil, NSError(domain: "Invalid Apple Credential", code: -1))
            return
        }

        guard let appleIDToken = appleIDCredential.identityToken else {
            completionHandler?(nil, NSError(domain: "Invalid Apple Credential", code: -1))
            return
        }

        guard let idTokenString = String(data: appleIDToken, encoding: .utf8) else {
            completionHandler?(nil, NSError(domain: "Invalid Apple Credential", code: -1))
            return
        }

        let credential = OAuthProvider.appleCredential(
            withIDToken: idTokenString,
            rawNonce: nonce,
            fullName: appleIDCredential.fullName
        )

        Auth.auth().signIn(with: credential) { authResult, error in
            if let error = error {
                completionHandler?(nil, error as NSError)
                return
            }

            let firebaseUid = authResult?.user.uid ?? ""
            let email = authResult?.user.email ?? appleIDCredential.email ?? ""

            // Apple puede no proporcionar el nombre después del primer inicio de sesión
            var displayName = authResult?.user.displayName ?? ""
            if displayName.isEmpty {
                if let givenName = appleIDCredential.fullName?.givenName,
                   let familyName = appleIDCredential.fullName?.familyName {
                    displayName = "\(givenName) \(familyName)"
                }
            }

            let photoURL = authResult?.user.photoURL?.absoluteString ?? ""

            let userData = AppleUserData(
                displayName: displayName,
                uid: firebaseUid,
                email: email,
                photoURL: photoURL
            )

            completionHandler?(userData, nil)
            currentDelegate = nil  // Limpiar delegate
            currentAuthController = nil  // Limpiar controller
        }
    }

    fileprivate static func handleError(_ error: Error) {
        completionHandler?(nil, error as NSError)
        currentDelegate = nil  // Limpiar delegate
        currentAuthController = nil  // Limpiar controller
    }

    // MARK: - Helper Functions

    private static func randomNonceString(length: Int = 32) -> String {
        precondition(length > 0)
        let charset: [Character] =
        Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")
        var result = ""
        var remainingLength = length

        while remainingLength > 0 {
            let randoms: [UInt8] = (0 ..< 16).map { _ in
                var random: UInt8 = 0
                let errorCode = SecRandomCopyBytes(kSecRandomDefault, 1, &random)
                if errorCode != errSecSuccess {
                    fatalError("Unable to generate nonce. SecRandomCopyBytes failed with OSStatus \(errorCode)")
                }
                return random
            }

            randoms.forEach { random in
                if remainingLength == 0 {
                    return
                }

                if random < charset.count {
                    result.append(charset[Int(random)])
                    remainingLength -= 1
                }
            }
        }

        return result
    }

    private static func sha256(_ input: String) -> String {
        let inputData = Data(input.utf8)
        let hashedData = SHA256.hash(data: inputData)
        let hashString = hashedData.compactMap {
            String(format: "%02x", $0)
        }.joined()
        return hashString
    }
}

// MARK: - AppleSignInDelegate

private class AppleSignInDelegate: NSObject, ASAuthorizationControllerDelegate, ASAuthorizationControllerPresentationContextProviding {

    func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
        AppleSignInBridge.handleAppleSignIn(authorization: authorization)
    }

    func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
        AppleSignInBridge.handleError(error)
    }

    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let window = windowScene.windows.first else {
            fatalError("No window available")
        }
        return window
    }
}

import CryptoKit


