plugins {
    `kotlin-dsl`
}

dependencies {
    api(libs.android)
    api(libs.kotlin.android)
    api(libs.arturbosch.detekt.plugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

