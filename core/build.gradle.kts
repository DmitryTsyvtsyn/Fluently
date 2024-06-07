plugins {
    id("compose-library-convention")
}

android {
    namespace = "io.github.dmitrytsyvtsyn.fluently.core"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.collection.jvm)
    implementation(libs.jetbrains.immutable.collections)

    api(libs.androidx.compose.navigation)
    api(libs.androidx.core.ktx)
    api(libs.koin)
}