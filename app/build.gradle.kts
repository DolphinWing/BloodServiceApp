plugins {
    id("com.android.application")
    kotlin("android")
    // kotlin("android.extensions")
    id("com.google.gms.google-services") // Google Services Gradle plugin
    id("com.github.ben-manes.versions") version Versions.gradleVersionsPlugin
    id("org.jlleitschuh.gradle.ktlint") version Versions.ktlintGradle
}

android {
    compileSdk = 31
    // buildToolsVersion("31.0.0")

    defaultConfig {
        applicationId = "dolphin.android.apps.BloodServiceApp"
        targetSdk = 31
        resourceConfigurations.addAll(arrayOf("zh_TW"))
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }

    flavorDimensions.add("mode")
    productFlavors {
        create("compose") {
            versionCode = 214
            versionName = "3.1.0"
            dimension = "mode"
            minSdk = 21
        }
        create("flexible") {
            versionCode = 112
            versionName = "2.5.3"
            dimension = "mode"
            minSdk = 21
        }
    }

    lint {
        disable("PackageName")
    }

    // maybe https://github.com/evant/gradle-retrolambda
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures.compose = true

    composeOptions {
        // kotlinCompilerVersion = Versions.AndroidX.composeCompiler
        kotlinCompilerExtensionVersion = Versions.AndroidX.compose
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
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.preference)
    implementation(Libs.AndroidX.activity)
    implementation(Libs.Google.material)
    // implementation(Libs.Google.mdcAdapter)

    // play services
    implementation(Libs.Google.PlayServices.core)
    implementation(Libs.Google.PlayServices.ads)
    implementation(Libs.AndroidX.work) // ads-lite depends on WorkManager

    // Firebase
    implementation(platform(Libs.Firebase.bom))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")

    // flexible
    // https://github.com/davideas/FlexibleAdapter
    "flexibleImplementation"(Libs.FlexibleAdapter.core)
    "flexibleImplementation"(Libs.FlexibleAdapter.ui)
    "flexibleImplementation"(Libs.AndroidX.fragment)

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.lifecycleViewModel)
    implementation(Libs.AndroidX.liveData)
    // implementation(Libs.AndroidX.lifecycleExtensions)

    /* Jetpack Compose */
    implementation(Libs.AndroidX.Compose.compiler)
    implementation(Libs.AndroidX.Compose.runtime)
    implementation(Libs.AndroidX.Compose.livedata)
    implementation(Libs.AndroidX.Compose.activity)
    implementation(Libs.AndroidX.Compose.lifecycle)
    implementation(Libs.AndroidX.Compose.core)
    implementation(Libs.AndroidX.Compose.foundation)
    implementation(Libs.AndroidX.Compose.layout)
    implementation(Libs.AndroidX.Compose.material)
    implementation(Libs.AndroidX.Compose.material3)
    implementation(Libs.AndroidX.Compose.materialIcons)
    implementation(Libs.AndroidX.Compose.uiTooling)
}

// https://github.com/jlleitschuh/ktlint-gradle
// https://github.com/JLLeitschuh/ktlint-gradle/blob/master/samples/android-app/build.gradle.kts
ktlint {
    android.set(true)
    // outputColorName.set("RED")
}
