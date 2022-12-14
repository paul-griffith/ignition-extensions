enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://nexus.inductiveautomation.com/repository/public")
    }
}

rootProject.name = "ignition-extensions"

dependencyResolutionManagement {
    repositories {
        // enable resolving dependencies from the inductive automation artifact repository
        maven(url = "https://nexus.inductiveautomation.com/repository/public")
        mavenCentral()
    }
}

include(
    ":",
    ":common",
    ":gateway",
    ":designer",
    ":client",
)
