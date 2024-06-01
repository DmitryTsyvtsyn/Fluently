plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

kotlin {
    jvmToolchain(17)
}

android {
    namespace = "io.github.dmitrytsyvtsyn.fluently.happening_list"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        vectorDrawables {
            useSupportLibrary = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.jetbrains.immutable.collections)
    implementation(libs.androidx.lifecycle.viewmodel.compose.ktx)
    implementation(project(":core"))
    implementation(project(":happening_data"))
    implementation(project(":happening_pickers"))
    implementation(project(":happening_detail"))
}