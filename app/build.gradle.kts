plugins {
    alias(libs.plugins.android.application)
    // Compose is used by this sample only; the SDK itself is Views/XML and needs no Compose.
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "vn.kalapa.ads.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "vn.kalapa.ads.sample"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            // The SDK ships its own consumer rules: no extra keep rules are needed.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

// -PkalapaAdsVersion overrides the version in gradle/libs.versions.toml.
val kalapaAdsVersion = providers.gradleProperty("kalapaAdsVersion").orElse(libs.versions.kalapaAds)

dependencies {
    // The BOM sets the version of every vn.kalapa artifact, so the line below carries none.
    implementation(platform("vn.kalapa:ads-bom:${kalapaAdsVersion.get()}"))
    implementation(libs.kalapa.ads.ui.view) // Android Views for the banner and native ads; includes the headless `ads`

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.core)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
}
