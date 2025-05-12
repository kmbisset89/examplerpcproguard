import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.rpc.plugin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kover)
    `maven-publish`
}

version = rootProject.version
group = "com.example.rpc.api"

kotlin {

    compilerOptions {
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
        optIn.add("kotlinx.coroutines.ExperimentalCoroutinesApi")
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release")
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.rpc.core)
            implementation(libs.rpc.serialization)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "com.example.rpc.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

spotless {
    java {
        target("**/*.java")
        googleJavaFormat().aosp()
        removeUnusedImports()
        trimTrailingWhitespace()
    }
    kotlin {
        target("**/*.kt")
        targetExclude("${layout.buildDirectory}/**/*.kt")
        ktlint("1.2.1").setEditorConfigPath(rootProject.file(".editorconfig").path)
        toggleOffOn() // use comments spotless:off / spotless:on to bypass applying spotless to a section
        trimTrailingWhitespace()
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.2.1")
    }
}

tasks.publish.dependsOn(tasks.build)

publishing {
    publications {
        repositories {
            val localProps =
                Properties().also {
                    if (file(rootProject.file("local.properties").path).exists()) {
                        it.load(file(rootProject.file("local.properties").path).inputStream())
                    }
                }
            maven {
                url = uri(
                    // Use the Release repository for versioned releases, and the Snapshots repository for non-versioned releases
                    if ("\\d+\\.\\d+\\.\\d+".toRegex().matches(version.toString())) {
                        "https://devops.jtaps.us/JTAPS/_packaging/Releases/maven/v1"
                    } else {
                        "https://devops.jtaps.us/JTAPS/_packaging/Snapshots/maven/v1"
                    }
                )

                credentials {
                    username = findProperty("artifactoryUsername", localProps)
                    password = findProperty("artifactoryPassword", localProps)
                }
            }
        }
    }
}

fun findProperty(name: String, localProperties: Properties): String? = if (rootProject.hasProperty(name)) {
    rootProject.property(name) as String
} else {
    localProperties[name] as String?
}
