import io.gitlab.arturbosch.detekt.extensions.DetektExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
    id("org.owasp.dependencycheck") version "12.2.2"
}

dependencyCheck {
    failOnError = false
    skipConfigurations = listOf("detekt", "detektPlugins", "ksp", "kspAndroid", "kspTest")
    nvd {
        apiKey = project.findProperty("nvdApiKey") as String? ?: System.getenv("NVD_API_KEY")
    }
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<DetektExtension> {
        autoCorrect = true
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    }

    dependencies {
        "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
        "detekt"("io.gitlab.arturbosch.detekt:detekt-cli:1.23.8")
        "detekt"("ru.kode:detekt-rules-compose:1.4.0")
    }
}
