plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

object Versions {
    const val versionName = "1.0.0"
}

group = "io.github.Rosemoe.sora-editor"

version = Versions.versionName

android {
    namespace = "io.github.rosemoe.sora"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin { compilerOptions { jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17 } }
}

dependencies {

    // Material
    api(libs.material)

    // AndroidX
    api(libs.androidx.annotation)

    api(libs.collection)

    // Kotlin
    api(libs.kotlin.stdlib)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.robolectric)

    // Android Testing
    androidTestImplementation(libs.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}