plugins {
    id("compose-library-convention")
}

android {
    namespace = "io.github.dmitrytsyvtsyn.fluently.happening_list"
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