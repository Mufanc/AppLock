pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://api.xposed.info/")
    }
}

rootProject.name = "AppLock"
include(":app")
include(":hidden-api-stub")
include(
    ":easyhook:api",
    ":easyhook:ksp-xposed"
)
