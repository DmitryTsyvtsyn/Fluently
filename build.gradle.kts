plugins {
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

allprojects.forEach { project ->
    project.plugins.apply("detekt-convention")
}