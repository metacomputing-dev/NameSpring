// settings.gradle.kts
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NameSpring"
include(":app")

// Get the Naming Engine from https://github.com/metacomputing-dev/NamingEngine
// TODO add it as a submodule
include(":namingengine")
project(":namingengine").projectDir = File("../../Private.NamingEngine/NamingEngine/namingengine")