plugins {
    id("com.android.application")
    kotlin("android")
    // kotlin("android.extensions")
    id("com.google.gms.google-services") // Google Services Gradle plugin
    id("com.github.ben-manes.versions") version "0.39.0"
}

android {
    compileSdkVersion(31)
    // buildToolsVersion("31.0.0")

    defaultConfig {
        applicationId = "dolphin.android.apps.BloodServiceApp"
        targetSdkVersion(31)
        resConfigs("zh_TW")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    flavorDimensions("mode")

    productFlavors {
        create("flexible") {
            versionCode = 112
            versionName = "2.5.3"
            dimension("mode")
            minSdkVersion(21)
        }
//        //legacy flavor
//        create("design") {
//            versionCode = 76
//            versionName = "2.1.2"
//            dimension("mode")
//            minSdkVersion(14)
//        }
    }

    // maybe https://github.com/evant/gradle-retrolambda
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks {
    /**
     * https://github.com/ben-manes/gradle-versions-plugin
     * ./gradlew dependencyUpdates
     */
    withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
        // Example 2: disallow release candidates as upgradable versions from stable versions
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }

        // optional parameters
        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }
}

dependencies {
    implementation(kotlin("stdlib", Versions.org_jetbrains_kotlin))
    implementation(Libs.okhttp)

    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.swipeRefreshLayout)
    // implementation(Libs.AndroidX.browser)
    implementation(Libs.AndroidX.recyclerView)
    implementation(Libs.Google.material)
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.preference)

    // play services
    implementation(Libs.Google.PlayServices.core)
    implementation(Libs.Google.PlayServices.ads)
    implementation(Libs.AndroidX.work) // ads-lite depends on WorkManager
//    "designImplementation"(Libs.Google.PlayServices.analytics)
//    "designImplementation"(Libs.android_ui)
//    "designImplementation"(Libs.superslim)

    // Firebase
    implementation(Libs.Firebase.core)
    implementation(Libs.Firebase.analytics)
    implementation(Libs.Firebase.remoteConfig)

    // flexible
    // https://github.com/davideas/FlexibleAdapter
    "flexibleImplementation"(Libs.FlexibleAdapter.core)
    "flexibleImplementation"(Libs.FlexibleAdapter.ui)

    // "flexibleImplementation"(Libs.AndroidX.lifecycleExtensions)
    "flexibleImplementation"(Libs.AndroidX.lifecycleViewModel)
    "flexibleImplementation"(Libs.AndroidX.coreKtx)
    "flexibleImplementation"(Libs.AndroidX.fragment)
}