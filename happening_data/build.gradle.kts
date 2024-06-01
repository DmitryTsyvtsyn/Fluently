plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "io.github.dmitrytsyvtsyn.fluently.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        vectorDrawables {
            useSupportLibrary = true
        }
    }
}

dependencies {
    implementation(libs.room.runtime)
    implementation(project(":core"))
}