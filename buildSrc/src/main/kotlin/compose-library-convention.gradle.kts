import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("android-library-convention")
}

android {
    val libs = the<LibrariesForLibs>()

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.extension.get()
    }
}