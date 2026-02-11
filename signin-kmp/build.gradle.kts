import org.gradle.kotlin.dsl.buildConfigField
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.gradleBuildConfig)
    id("maven-publish")
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val iosTargets = listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64()
    )

    iosTargets.forEach { target ->
        target.binaries.framework {
            baseName = "GoogleSignInKMP"
            isStatic = true
            if (System.getProperty("os.name").contains("Mac", ignoreCase = true)) {
                binaryOption("bundleId", "dev.bonygod.googlesignin.kmp")
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                // Dependency Injection
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)

                // Firebase
                api(project.dependencies.platform(libs.firebase.bom))
                api(libs.firebase.auth)

                // Sign In with Google
                api(libs.androidx.credentials)
                api(libs.androidx.credentials.play.services.auth)
                api(libs.googleid)
                api(libs.play.services.auth)
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
        }

        val iosTest by creating {
            dependsOn(commonTest)
        }

        iosTargets.forEach { target ->
            target.compilations["main"].defaultSourceSet.dependsOn(iosMain)
            target.compilations["test"].defaultSourceSet.dependsOn(iosTest)
        }
    }
}

android {
    namespace = "dev.bonygod.googlesignin.kmp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

buildConfig {
    packageName("dev.bonygod.signin.kmp")

    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.reader())
    }

    val apiKey = properties.getProperty("FIREBASE_API_KEY") ?: "CONFIGURE_IN_LOCAL_PROPERTIES"
    val clientId = properties.getProperty("CLIENT_ID") ?: "CONFIGURE_IN_LOCAL_PROPERTIES"

    buildConfigField("FIREBASE_API_KEY", apiKey)
    buildConfigField("CLIENT_ID", clientId)
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// ---------------------------------------------------
// ✅ Incluir commonMain en el AAR de Android
// ---------------------------------------------------
afterEvaluate {
    val commonJar by tasks.registering(Jar::class) {
        archiveBaseName.set("signin-kmp-common")
        archiveClassifier.set("common")
        from(kotlin.sourceSets["commonMain"].kotlin.srcDirs)
        from(kotlin.sourceSets["commonMain"].resources.srcDirs)
    }

    tasks.register<Copy>("includeCommonInAndroidAar") {
        dependsOn(commonJar)
        from(commonJar.flatMap { it.archiveFile })
        into("$buildDir/outputs/aar")
    }
}

// Información para publicación en JitPack
group = "com.github.BonyGoD"
version = "1.0.0"
