plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("com.google.gms.google-services") // Google Services Gradle plugin
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")

    defaultConfig {
        applicationId ="dolphin.android.apps.BloodServiceApp"
        targetSdkVersion (29)
        resConfigs ("zh_TW")
    }

    buildTypes {
        getByName("release") {
            minifyEnabled (true)
            //useProguard true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            minifyEnabled (false)
            //useProguard false
        }
    }

    flavorDimensions ("mode")

    productFlavors {
        create("design")  {
            versionCode =76
            versionName ="2.1.2"
            dimension ("mode")
            minSdkVersion (14)
        }
        create("flexible") {
            versionCode =105
            versionName ="2.5.0"
            dimension ("mode")
            minSdkVersion (21)
        }
    }

    // maybe https://github.com/evant/gradle-retrolambda
    compileOptions {
        sourceCompatibility =JavaVersion.VERSION_1_8
        targetCompatibility =JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation (Libs.kotlin_stdlib_jdk7)
    implementation (Libs.okhttp)

    implementation (Libs.appcompat)
    "designImplementation" (Libs.android_ui)
    //play services
    implementation (Libs.play_services_base)
    implementation (Libs.play_services_ads_lite)
    "designImplementation" (Libs.play_services_analytics)

    implementation (Libs.recyclerview)
    implementation (Libs.material)
    implementation (Libs.constraintlayout)
    implementation (Libs.preference)
    //Firebase
    implementation (Libs.firebase_core)
    implementation (Libs.firebase_analytics)
    implementation (Libs.firebase_config)

    //design
    "designImplementation" (Libs.superslim)

    //flexible
    //https://github.com/davideas/FlexibleAdapter
    "flexibleImplementation" (Libs.flexible_adapter)
    "flexibleImplementation" (Libs.flexible_adapter_ui)

    "flexibleImplementation" (Libs.lifecycle_extensions)
    "flexibleImplementation" (Libs.core_ktx)
    "flexibleImplementation" (Libs.fragment_ktx)
    "flexibleImplementation" (Libs.lifecycle_viewmodel_ktx)
    implementation (Libs.swipeRefreshLayout)
    implementation (Libs.browser)
}