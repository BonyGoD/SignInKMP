//
//  AppleUserData.swift
//  SignInKMPSwift
//
//  Created by Ivan Boniquet Rodriguez on 12/2/26.
//  Copyright © 2026 BonyGoD. All rights reserved.
//

import Foundation

@objc public class AppleUserData: NSObject {
    @objc public let displayName: String
    @objc public let uid: String
    @objc public let email: String
    @objc public let photoURL: String

    public init(
        displayName: String,
        uid: String,
        email: String,
        photoURL: String
    ) {
        self.displayName = displayName
        self.uid = uid
        self.email = email
        self.photoURL = photoURL
        super.init()
    }
}

