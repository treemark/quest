plugins {
    id("com.android.application")
}

android {
    namespace = "com.quest.helloworld"
    compileSdk = 34
    ndkVersion = "25.2.9519653"

    defaultConfig {
        applicationId = "com.quest.helloworld"
        minSdk = 29  // Quest 3 requires Android 10+
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        ndk {
            abiFilters += listOf("arm64-v8a")  // Quest 3 uses ARM64
        }
        
        // Enable native build with CMake
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                arguments += "-DANDROID_STL=c++_shared"
            }
        }
    }
    
    // Configure CMake build
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
        }
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

configurations.all {
    // Exclude desktop module - we only want Android
    exclude(group = "org.jmonkeyengine", module = "jme3-desktop")
    exclude(group = "org.jmonkeyengine", module = "jme3-lwjgl")
    exclude(group = "org.jmonkeyengine", module = "jme3-lwjgl3")
    
    // Fix Kotlin stdlib conflicts
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk7")
    exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-jdk8")
}

dependencies {
    // jMonkeyEngine Core for Android
    implementation("org.jmonkeyengine:jme3-core:3.6.1-stable")
    implementation("org.jmonkeyengine:jme3-android:3.6.1-stable")
    implementation("org.jmonkeyengine:jme3-android-native:3.6.1-stable")
    implementation("org.jmonkeyengine:jme3-effects:3.6.1-stable")

    // Dagger Dependency Injection (plain Dagger, not Hilt)
    implementation("com.google.dagger:dagger:2.48")
    annotationProcessor("com.google.dagger:dagger-compiler:2.48")
    
    // Dagger Android Support
    implementation("com.google.dagger:dagger-android:2.48")
    implementation("com.google.dagger:dagger-android-support:2.48")
    annotationProcessor("com.google.dagger:dagger-android-processor:2.48")

    // AndroidX
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core:1.12.0")

    // Logging
    implementation("org.slf4j:slf4j-android:1.7.36")
    
    // Kotlin stdlib (unified version)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
}
