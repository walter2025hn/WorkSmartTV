pluginManagement {
 pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    // Cambiamos a PREFER_SETTINGS para que use los repositorios definidos aquí
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "WorkSmartTV"
include(":app")
