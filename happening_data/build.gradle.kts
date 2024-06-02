plugins {
    id("android-library-convention")
}

android {
    namespace = "io.github.dmitrytsyvtsyn.fluently.data"
}

dependencies {
    implementation(libs.room.runtime)
    implementation(project(":core"))
}