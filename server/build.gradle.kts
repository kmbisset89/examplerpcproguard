import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.rpc.plugin)
    alias(libs.plugins.ksp)
    alias(libs.plugins.spotless)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.kover)
    `maven-publish`
}

version = rootProject.version
group = "com.example.rpc.server"

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
        val jvmMain by getting

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.logback.android)
            implementation(libs.slf4j.api)
        }
        commonMain.dependencies {
            implementation(projects.api)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.netty)
            implementation(libs.rpc.ktor.server)
            implementation(libs.rpc.server)
            implementation(libs.rpc.serialization)
            implementation(libs.koin.core)
            implementation(libs.koin.annotation)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.server.cors.jvm)
            implementation(libs.ktor.server.websockets.jvm)
            implementation(libs.ktor.server.host.common.jvm)
            implementation(libs.ktor.server.cio)

            implementation(libs.kotlinx.atomicfu)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.logback.classic)
        }
    }
}

android {
    namespace = "com.example.rpc"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.whenTaskAdded {
    if (name == "kspCommonMainKotlinMetadata") {
        tasks.named("kspKotlinJvm") {
            dependsOn(this@whenTaskAdded)
        }
        tasks.named("kspDebugKotlinAndroid") {
            dependsOn(this@whenTaskAdded)
        }
        tasks.named("kspReleaseKotlinAndroid") {
            dependsOn(this@whenTaskAdded)
        }
    }
}

tasks.whenTaskAdded {
    if (name.contains("ReleaseUnitTest")) {
        enabled = false
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}
dependencies {
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
    add("kspAndroid", libs.koin.ksp.compiler)
    add("kspJvm", libs.koin.ksp.compiler)
}

ksp {
    arg("KOIN_CONFIG_CHECK", "true")
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

kover {

    reports {
        // filters for all report types of all build variants
        filters {
            excludes {
                androidGeneratedClasses()
            }
        }

        variant("release") {
            // filters for all report types only of 'release' build type
            filters {
                excludes {
                    androidGeneratedClasses()
                }
            }
        }
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
