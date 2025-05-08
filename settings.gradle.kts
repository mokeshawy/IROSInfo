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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(uri("https://jitpack.io"))
        maven(uri("https://nexus.mobile.fawry.io/repository/maven-public/"))
    }
}

rootProject.name = "IROSInfo"
include(":app")
include(":common")
include(":common:bases")
include(":common:dialog")
include(":common:storage")
include(":common:camera")
include(":common:connectivity")
include(":common:crash_reporting")
include(":common:localization")
include(":common:maps")
include(":common:extensions")
include(":common:utils")
include(":common:network_checker")
include(":common:exo_player")
 