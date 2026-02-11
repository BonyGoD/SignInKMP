// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "SignInKMPSwift",
    platforms: [
        .iOS(.v15)
    ],
    products: [
        .library(
            name: "SignInKMPSwift",
            targets: ["SignInKMPSwift"]
        ),
    ],
    dependencies: [
        .package(url: "https://github.com/firebase/firebase-ios-sdk.git", from: "12.0.0"),
        .package(url: "https://github.com/google/GoogleSignIn-iOS.git", from: "9.1.0")
    ],
    targets: [
        .target(
            name: "SignInKMPSwift",
            dependencies: [
                .product(name: "FirebaseCore", package: "firebase-ios-sdk"),
                .product(name: "FirebaseAuth", package: "firebase-ios-sdk"),
                .product(name: "GoogleSignIn", package: "GoogleSignIn-iOS")
            ],
            path: "SignInKMPSwift/Sources/SignInKMPSwift"
        )
    ]
)

