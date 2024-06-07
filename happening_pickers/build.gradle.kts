plugins {
    id("compose-library-convention")
}

android {
    namespace = "io.github.dmitrytsyvtsyn.fluently.happening_pickers"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.navigation)
    implementation(project(":core"))
}