import java.util.*

rootProject.name = "RPCCopy"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    fun <T> checkLocalPropsAndProjectProperties(localProps: java.util.Properties, extras: ExtraPropertiesExtension, key: String): T? {
        val localValue = try {
            localProps.get(key) as? T
        } catch (e: Exception) {
            null
        }
        val projectValue = try {
            extras.get(key) as? T
        } catch (e: Exception) {
            null
        }
        return localValue ?: projectValue
    }
    fun PluginManagementSpec.loadProperties(fileName: String, path: String = rootDir.absolutePath) = java.util.Properties().also { properties ->
        val file = File("$path/$fileName")
        if (file.exists()) {
            file.inputStream().use { properties.load(it) }
        }
    }
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        val localProperties = this@pluginManagement.loadProperties("local.properties")
    }
}

dependencyResolutionManagement {
    fun <T> checkLocalPropsAndProjectProperties(localProps: java.util.Properties, extras: ExtraPropertiesExtension, key: String): T? {
        val localValue = try {
            localProps.get(key) as? T
        } catch (e: Exception) {
            null
        }
        val projectValue = try {
            extras.get(key) as? T
        } catch (e: Exception) {
            null
        }
        return localValue ?: projectValue
    }
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

val properties = Properties().apply {
    if (file("local.properties").exists()) {
        load(file("local.properties").inputStream())
    }
}

include(
    listOf(
        ":client",
        ":server",
        ":api",
        ":exampleapp"
    )
)

if (properties["takrepo.user"] != null) {
    include(":standalone-tak")
}
