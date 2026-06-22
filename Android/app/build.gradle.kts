plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.xtream.player"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xtream.player"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // Signing Configuration for Release APK
    signingConfigs {
        create("release") {
            storeFile = file("keystore/xtream-player-release.jks")
            storePassword = "XtreamPlayer2026!"
            keyAlias = "xtream-key"
            keyPassword = "XtreamPlayer2026!"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")

            buildFeatures {
                buildConfig = true
            }

            buildConfigField("String", "API_BASE_URL", "\"https://xtream-player.com/api/\"")
            buildConfigField("Boolean", "DEBUG_LOGS", "false")
        }

        debug {
            isDebuggable = true
            buildConfigField("String", "API_BASE_URL", "\"http://localhost:8000/api/\"")
            buildConfigField("Boolean", "DEBUG_LOGS", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
        // Room 2.5.2's bundled kotlinx-metadata-jvm reader can choke on the
        // newest Kotlin 1.9 metadata encoding when run through kapt, causing
        // it to lose track of which DAO functions are `suspend` (it then
        // treats the Continuation param as a regular query param). Capping
        // the emitted metadata/language level at 1.8 keeps it in a format
        // every consumer here can parse, without changing any source code.
        languageVersion = "1.8"
        apiVersion = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // AndroidX & Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.0")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.ui:ui-graphics:1.5.0")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.0")
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation("androidx.compose.foundation:foundation:1.5.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.0")

    // Material Icons (extended set: PlayCircle, Subtitles, Favorite, History, etc.)
    implementation("androidx.compose.material:material-icons-extended:1.5.0")

    // Image loading for stream/series posters and episode thumbnails
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.10.0")
    implementation("com.squareup.retrofit2:converter-gson:2.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Hilt DI
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Media3/ExoPlayer - hls/dash extras widen format support beyond plain
    // progressive mp4/mkv (Xtream live channels are commonly HLS or DASH).
    implementation("androidx.media3:media3-exoplayer:1.1.0")
    implementation("androidx.media3:media3-exoplayer-hls:1.1.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.1.0")
    implementation("androidx.media3:media3-ui:1.1.0")
    implementation("androidx.media3:media3-session:1.1.0")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // JSON Serialization
    implementation("com.google.code.gson:gson:2.10.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("io.mockk:mockk:1.13.5")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")

    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.0")
}

// room-compiler 2.5.2 pulls in kotlinx-metadata-jvm 0.5.0, which cannot parse
// the Kotlin metadata format emitted by the Kotlin 1.9.0 compiler (mv=1.9.0).
// Without this, Room's kapt processor silently fails to recognize suspend
// DAO functions (it sees a raw Continuation parameter instead of awaiting a
// suspend fun) and emits "Not sure how to handle ... return type" errors.
// Forcing a newer kotlinx-metadata-jvm that understands 1.9 metadata fixes
// suspend-fun recognition for Room without changing any DAO/entity code.
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")
    }
}
