pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        mavenLocal {
            content {
                includeGroup("io.github.libxposed")
            }
        }
    }
}

rootProject.name = "AppLock"
include(":app")
include(":api-stub")
