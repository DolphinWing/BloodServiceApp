plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("com.google.gms.google-services") // Google Services Gradle plugin
    id("com.github.ben-manes.versions") version "0.28.0"
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    defaultConfig {
        applicationId = "dolphin.android.apps.BloodServiceApp"
        targetSdkVersion(29)
        resConfigs("zh_TW")
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(true)
            //useProguard true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            minifyEnabled(false)
            //useProguard false
        }
    }

    flavorDimensions("mode")

    productFlavors {
        create("flexible") {
            versionCode = 105
            versionName = "2.5.0"
            dimension("mode")
            minSdkVersion(21)
        }
        //legacy flavor
        create("design") {
            versionCode = 76
            versionName = "2.1.2"
            dimension("mode")
            minSdkVersion(14)
        }
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

tasks {
    /**
     * https://github.com/ben-manes/gradle-versions-plugin
     * ./gradlew dependencyUpdates
     */
    withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
        // optional parameters
        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk7", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION))
    implementation(Libs.okhttp)

    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.swipeRefreshLayout)
    implementation(Libs.AndroidX.browser)
    implementation(Libs.AndroidX.recyclerView)
    implementation(Libs.Google.material)
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.preference)

    "designImplementation"(Libs.android_ui)
    //play services
    implementation(Libs.Google.PlayServices.core)
    implementation(Libs.Google.PlayServices.ads)
    "designImplementation"(Libs.Google.PlayServices.analytics)
    "designImplementation"(Libs.superslim)


    //Firebase
    implementation(Libs.Firebase.core)
    implementation(Libs.Firebase.analytics)
    implementation(Libs.Firebase.remoteConfig)

    //flexible
    //https://github.com/davideas/FlexibleAdapter
    "flexibleImplementation"(Libs.FlexibleAdapter.core)
    "flexibleImplementation"(Libs.FlexibleAdapter.ui)

    "flexibleImplementation"(Libs.AndroidX.lifecycleExtensions)
    "flexibleImplementation"(Libs.AndroidX.lifecycleViewModel)
    "flexibleImplementation"(Libs.AndroidX.coreKtx)
    "flexibleImplementation"(Libs.AndroidX.fragment)

}