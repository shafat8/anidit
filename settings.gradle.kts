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
    }
}

rootProject.name = "AniDit"

include(
    ":app",
    ":core",
    ":audio-analysis",
    ":video-analysis",
    ":style-interpreter",
    ":decision-engine"
)
