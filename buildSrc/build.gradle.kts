plugins {
    `kotlin-dsl`
}

dependencies {
    api(libs.android)
    api(libs.kotlin.android)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

