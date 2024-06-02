plugins {
    id("android-library-convention")
}

android {
    namespace = "io.github.dmitrytsyvtsyn.fluently.happening_detail"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.extension.get()
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.jetbrains.immutable.collections)
    implementation(libs.androidx.lifecycle.viewmodel.compose.ktx)
    implementation(project(":happening_data"))
    implementation(project(":happening_pickers"))
    implementation(project(":core"))
}