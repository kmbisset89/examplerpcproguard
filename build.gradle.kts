import java.util.*

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.rpc.plugin) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.build.config.generator) apply false
    alias(libs.plugins.kover)
}

afterEvaluate {
    logger.lifecycle("Version: $version")
}

fun findProperty(name: String, localProperties: Properties): String? = if (rootProject.hasProperty(name)) {
    rootProject.property(name) as String
} else {
    localProperties[name] as String?
}

dependencies {
    kover(project(":client"))
    kover(project(":api"))
    kover(project(":server"))
}

subprojects {
    // Apply Groovy logic to all subprojects that are for TAK
    this.apply("../groovy-logic.gradle")
}
