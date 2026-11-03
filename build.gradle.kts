// AGP 9 ships its own Kotlin. This classpath entry moves the whole build to a newer compiler; the Compose compiler
// plugin uses the same version (see gradle/libs.versions.toml).
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
}
