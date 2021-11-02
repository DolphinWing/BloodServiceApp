/**
 * https://github.com/ben-manes/gradle-versions-plugin
 * ./gradlew dependencyUpdates
 */
object Versions {
    object Google {
        const val gradleBuildTool = "7.0.3"
        const val services: String = "4.3.10"

        const val playServices = "17.6.0"
        const val analytics = "17.0.0"
        const val ads = "20.4.0"

        const val material: String = "1.5.0-alpha05"
    }

    const val okhttp: String = "4.9.2"

    object AndroidX {
        const val appcompat: String = "1.3.1"

        const val coreKtx: String = "1.7.0"

        const val lifecycle: String = "2.4.0"

        const val constraintLayout: String = "2.1.1"

        /**
         * https://developer.android.com/jetpack/androidx/releases/activity
         */
        const val activity = "1.4.0"

        /**
         * https://developer.android.com/jetpack/androidx/releases/fragment
         */
        const val fragment = "1.3.6"

        const val recyclerView: String = "1.2.1"

        const val preference: String = "1.1.1"

        const val swipeRefreshLayout = "1.1.0"

        const val browser: String = "1.2.0"

        const val work: String = "2.7.0"

        /**
         * https://developer.android.com/jetpack/androidx/releases/compose
         */
        const val compose = "1.0.4"
        const val composeMaterial3 = "1.0.0-alpha01"
    }

    object Firebase {
        const val bom: String = "29.0.0"

        const val core: String = "19.0.0"
        const val analytics: String = "19.0.0"
        const val config: String = "21.0.0"
    }

    object FlexibleAdapter {
        const val ui: String = "1.0.0"

        const val core: String = "5.1.0"
    }

    const val org_jetbrains_kotlin: String = "1.5.31"

    /**
     * https://github.com/ben-manes/gradle-versions-plugin
     * ./gradlew dependencyUpdates
     */
    const val gradleVersionsPlugin = "0.39.0"

    /**
     * https://github.com/jlleitschuh/ktlint-gradle
     * ./gradlew --continue ktlintCheck
     * ./gradlew :app:ktlintComposeSourceSetCheck
     * ./gradlew runKtlintFormatOverFlexibleSourceSet
     */
    const val ktlintGradle = "10.2.0"
}

object Libs {

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
            const val runtime = "androidx.compose.runtime:runtime:${Versions.AndroidX.compose}"
            const val livedata =
                "androidx.compose.runtime:runtime-livedata:${Versions.AndroidX.compose}"

            const val activity =
                "androidx.activity:activity-compose:${Versions.AndroidX.activity}"
            const val lifecycle =
                "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.AndroidX.lifecycle}"

            const val core = "androidx.compose.ui:ui:${Versions.AndroidX.compose}"
            const val compiler = "androidx.compose.compiler:compiler:${Versions.AndroidX.compose}"

            const val foundation =
                "androidx.compose.foundation:foundation:${Versions.AndroidX.compose}"
            const val layout =
                "androidx.compose.foundation:foundation-layout:${Versions.AndroidX.compose}"

            const val material = "androidx.compose.material:material:${Versions.AndroidX.compose}"
            const val material3 =
                "androidx.compose.material3:material3:${Versions.AndroidX.composeMaterial3}"
            const val materialIcons =
                "androidx.compose.material:material-icons-extended:${Versions.AndroidX.compose}"
            const val materialRipple =
                "androidx.compose.material:material-ripple:${Versions.AndroidX.compose}"

            const val uiTooling = "androidx.compose.ui:ui-tooling:${Versions.AndroidX.compose}"
        }
    }

    object Firebase {
        private const val firebase = "com.google.firebase"

        const val bom: String = "$firebase:firebase-bom:${Versions.Firebase.bom}"

        const val core: String = "$firebase:firebase-core:${Versions.Firebase.core}"

        const val analytics: String = "$firebase:firebase-analytics:${Versions.Firebase.analytics}"

        const val remoteConfig: String = "$firebase:firebase-config:${Versions.Firebase.config}"
    }

    /**
     * https://github.com/davideas/FlexibleAdapter
     */
    object FlexibleAdapter {
        private const val ownerProject = "eu.davidea:flexible-adapter"
        const val core: String = "$ownerProject:${Versions.FlexibleAdapter.core}"
        const val ui: String = "$ownerProject-ui:${Versions.FlexibleAdapter.ui}"
    }
}