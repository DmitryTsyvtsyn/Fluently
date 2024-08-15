import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask

plugins {
    id("io.gitlab.arturbosch.detekt")
}

detekt {
    debug = false
    ignoreFailures = false
    allRules = false
    autoCorrect = true
    buildUponDefaultConfig = false
    disableDefaultRuleSets = false
    baseline = project.file("detekt-baseline.xml")
    config.setFrom(rootProject.file("detekt/detekt-config.yml"))
    source.setFrom("src/main/kotlin")
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = "17"

    reports {
        html.outputLocation.set(layout.buildDirectory.file("reports/lint/lint-report.html"))
        html.required.set(true)

        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

tasks.withType<DetektCreateBaselineTask> {
    jvmTarget = "17"
}