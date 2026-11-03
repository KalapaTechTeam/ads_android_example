pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Set -PkalapaLocalRepo=<dir> to resolve an SDK build that is not on Maven Central yet.
        providers.gradleProperty("kalapaLocalRepo").orNull?.let { maven { url = uri(it) } }
        google()
        mavenCentral()
    }
}

rootProject.name = "kalapa-ads-android-sample"
include(":app")
