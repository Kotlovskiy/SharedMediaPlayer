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
    //id("org.sonarqube") version "7.3.1.8318"
}

dependencyCheck {
    failOnError = false
    //suppressionFile = "config/owasp/suppressions.xml"
    skipConfigurations = listOf("detekt", "detektPlugins", "ksp", "kspAndroid", "kspTest")
    nvd {
        apiKey = project.findProperty("nvdApiKey") as String? ?: System.getenv("NVD_API_KEY")
    }
}
/*
sonarqube {
    properties {
        property("sonar.projectKey", "Kotlovskiy_SharedMediaPlayer")
        property("sonar.projectName", "Shared Media Player")
        property("sonar.projectVersion", "1.0")

        property("sonar.profile", "Security Only")

        property("sonar.modules", "app,auth,common-network-error,core-network,storage,hello,room,settings,core-ui")
        property("sonar.sources", "src/main/java,src/main/kotlin")
        property("sonar.sourceEncoding", "UTF-8")

        property("sonar.tests", "src/test/java,src/test/kotlin,src/androidTest/java,src/androidTest/kotlin")

        property("sonar.language", "kotlin")

        property("sonar.android.enable", "true")

        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "kotlovskiy")
        property("sonar.token", System.getenv("SONAR_TOKEN")
            ?: project.findProperty("sonarToken") as String? ?: "")
    }
}
*/
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
