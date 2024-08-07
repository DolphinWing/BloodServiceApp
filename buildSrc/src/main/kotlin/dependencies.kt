/**
 * https://github.com/ben-manes/gradle-versions-plugin
 * ./gradlew dependencyUpdates
 */
object Versions {
    /**
     * https://www.jacoco.org/jacoco/trunk/doc/changes.html
     */
    const val jacoco = "0.8.10"

    object Google {
        const val gradleBuildTool = "8.1.1"

        /**
         * https://developers.google.com/android/guides/google-services-plugin
         */
        const val services: String = "4.3.15"

        /**
         * https://developers.google.com/android/guides/setup
         */
        const val playServices = "18.5.0"
        const val analytics = "18.0.2"
        const val ads = "23.2.0"

        const val material: String = "1.9.0"
    }

    const val okhttp: String = "4.12.0"

    object AndroidX {
        /**
         * https://developer.android.com/jetpack/androidx/releases/activity
         */
        const val activity = "1.7.1"

        const val appcompat: String = "1.6.1"

        /**
         * https://developer.android.com/jetpack/androidx/releases/compose
         */
        const val compose = "1.4.7"
        const val conposeBom = "2023.05.01"

        const val coreKtx: String = "1.10.1"

        const val constraintLayout: String = "2.1.4"

        const val lifecycle: String = "2.6.1"

        /**
         * https://developer.android.com/jetpack/androidx/releases/fragment
         */
        const val fragment = "1.3.6"

        const val recyclerView: String = "1.3.0"

        const val preference: String = "1.2.0"

        const val swipeRefreshLayout = "1.1.0"

        const val browser: String = "1.2.0"

        const val work: String = "2.8.1"

        /**
         * https://developer.android.com/topic/libraries/architecture/datastore
         */
        const val datastore = "1.0.0"
    }

    /**
     * https://firebase.google.com/docs/android/learn-more
     */
    object Firebase {
        const val bom: String = "33.1.2"

//        const val core: String = "19.0.0"
//        const val analytics: String = "19.0.0"
//        const val config: String = "21.0.0"

        /**
         * https://firebase.google.com/docs/perf-mon/get-started-android
         */
        const val performance: String = "1.4.2"
    }

    object FlexibleAdapter {
        const val ui: String = "1.0.0"
        const val core: String = "5.1.0"
    }

    object JetBrains {
        const val kotlinLib = "1.8.21"
        const val coroutines = "1.7.0"

        /**
         * https://github.com/Kotlin/dokka
         */
        const val dokka = "1.8.10"
    }

    /**
     * https://github.com/ben-manes/gradle-versions-plugin
     * ./gradlew dependencyUpdates
     */
    const val gradleVersionsPlugin = "0.46.0"

    /**
     * https://github.com/jlleitschuh/ktlint-gradle
     * ./gradlew --continue ktlintCheck
     * ./gradlew :app:ktlintComposeSourceSetCheck
     * ./gradlew runKtlintFormatOverFlexibleSourceSet
     */
    const val ktlintGradle = "11.3.2"

    object Test {
        const val junit = "4.13.2"
        const val robolectric = "4.10.2"
        const val mockito = "5.3.1"
        const val mockitoInline = "5.2.0"
    }

    /**
     * https://developer.android.com/jetpack/androidx/releases/test
     * https://maven.google.com/web/index.html
     */
    object AndroidXTest {
        const val core = "1.5.0"
        const val runner = "1.5.2"
        const val junit = "1.1.5"
        const val espresso = "3.4.0"

        // https://developer.android.com/jetpack/androidx/releases/arch-core
        const val archCore = "2.2.0"
    }
}

object Libs {
    const val jacoco = "org.jacoco:org.jacoco.core:${Versions.jacoco}"

    const val gradleVersionsPlugin =
        "com.github.ben-manes:gradle-versions-plugin:${Versions.gradleVersionsPlugin}"

    object JetBrains {
        const val coroutinesCore =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.JetBrains.coroutines}"
        const val coroutinesAndroid =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.JetBrains.coroutines}"

        /**
         * Dispatchers.Main can't run in unit test
         * See https://is.gd/5b8vlR or
         * https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/
         */
        const val coroutinesTest =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.JetBrains.coroutines}"

        const val dokka = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.JetBrains.dokka}"
    }

    object Google {
        const val gradleBuildTool =
            "com.android.tools.build:gradle:${Versions.Google.gradleBuildTool}"

        const val services: String = "com.google.gms:google-services:${Versions.Google.services}"

        object PlayServices {
            private const val gms = "com.google.android.gms"

            const val core = "$gms:play-services-base:${Versions.Google.playServices}"
            const val analytics = "$gms:play-services-analytics:${Versions.Google.analytics}"
            const val ads = "$gms:play-services-ads-lite:${Versions.Google.ads}"
        }

        /**
         * http://developer.android.com/tools/extras/support-library.html
         */
        const val material: String =
            "com.google.android.material:material:${Versions.Google.material}"
        const val mdcAdapter =
            "com.google.android.material:compose-theme-adapter:${Versions.AndroidX.compose}"
    }

    /**
     * https://square.github.io/okhttp/
     */
    const val okhttp: String = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val mockServer = "com.squareup.okhttp3:mockwebserver:${Versions.okhttp}"

    /**
     * https://developer.android.com/jetpack/androidx
     */
    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appcompat}"

        const val coreKtx: String = "androidx.core:core-ktx:${Versions.AndroidX.coreKtx}"

        const val constraintLayout: String =
            "androidx.constraintlayout:constraintlayout:${Versions.AndroidX.constraintLayout}"

        const val activity = "androidx.activity:activity-ktx:${Versions.AndroidX.activity}"
        const val fragment: String = "androidx.fragment:fragment-ktx:${Versions.AndroidX.fragment}"

        const val recyclerView: String =
            "androidx.recyclerview:recyclerview:${Versions.AndroidX.recyclerView}"

        /**
         * https://developer.android.com/topic/libraries/architecture/index.html
         */
        const val lifecycleExtensions: String =
            "androidx.lifecycle:lifecycle-extensions:${Versions.AndroidX.lifecycle}"
        const val lifecycleViewModel: String =
            "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.AndroidX.lifecycle}"
        const val liveData: String =
            "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.AndroidX.lifecycle}"

        const val preference: String =
            "androidx.preference:preference-ktx:${Versions.AndroidX.preference}"

        const val swipeRefreshLayout =
            "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.swipeRefreshLayout}"

        const val browser: String = "androidx.browser:browser:${Versions.AndroidX.browser}"

        const val work: String = "androidx.work:work-runtime-ktx:${Versions.AndroidX.work}"

        // Compose
        object Compose {
//            const val runtime = "androidx.compose.runtime:runtime:${Versions.AndroidX.compose}"
//            const val livedata =
//                "androidx.compose.runtime:runtime-livedata:${Versions.AndroidX.compose}"

            const val activity =
                "androidx.activity:activity-compose:${Versions.AndroidX.activity}"
            const val lifecycle =
                "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.AndroidX.lifecycle}"

//            const val compiler = "androidx.compose.compiler:compiler:${Versions.AndroidX.compose}"
//
//            const val foundation =
//                "androidx.compose.foundation:foundation:${Versions.AndroidX.compose}"
//            const val layout =
//                "androidx.compose.foundation:foundation-layout:${Versions.AndroidX.compose}"

//            const val materialRipple = "androidx.compose.material:material-ripple"
        }

        const val datastore =
            "androidx.datastore:datastore-preferences:${Versions.AndroidX.datastore}"
    }

    object Firebase {
        private const val firebase = "com.google.firebase"

        const val bom: String = "$firebase:firebase-bom:${Versions.Firebase.bom}"

//        const val core: String = "$firebase:firebase-core:${Versions.Firebase.core}"
//
//        const val analytics: String = "$firebase:firebase-analytics-ktx"
//
//        const val remoteConfig: String = "$firebase:firebase-config:${Versions.Firebase.config}"

        // Performance Monitoring plugin
        const val performance: String = "$firebase:perf-plugin:${Versions.Firebase.performance}"
    }

    /**
     * https://github.com/davideas/FlexibleAdapter
     */
    object FlexibleAdapter {
        const val group = "eu.davidea"
        const val moduleCore = "flexible-adapter"
        const val moduleUi = "flexible-adapter-ui"
        const val LibCore: String = "$group:$moduleCore:${Versions.FlexibleAdapter.core}"
        const val LibUi: String = "$group:$moduleUi:${Versions.FlexibleAdapter.ui}"
    }

    object Test {
        const val junit = "junit:junit:${Versions.Test.junit}"

        const val robolectric = "org.robolectric:robolectric:${Versions.Test.robolectric}"

        const val mockito = "org.mockito:mockito-core:${Versions.Test.mockito}"
        const val mockitoInline = "org.mockito:mockito-inline:${Versions.Test.mockitoInline}"
    }

    object AndroidXTest {
        const val core = "androidx.test:core-ktx:${Versions.AndroidXTest.core}"
        const val rules = "androidx.test:rules:${Versions.AndroidXTest.core}"
        const val runner = "androidx.test:runner:${Versions.AndroidXTest.runner}"

        const val junit = "androidx.test.ext:junit-ktx:${Versions.AndroidXTest.junit}"

        // https://developer.android.com/jetpack/androidx/releases/arch-core
        const val archCore = "androidx.arch.core:core-testing:${Versions.AndroidXTest.archCore}"

        object Compose {
            const val test = "androidx.compose.ui:ui-test:${Versions.AndroidX.compose}"
            const val junit = "androidx.compose.ui:ui-test-junit4:${Versions.AndroidX.compose}"
            const val manifest = "androidx.compose.ui:ui-test-manifest:${Versions.AndroidX.compose}"
        }
    }
}
