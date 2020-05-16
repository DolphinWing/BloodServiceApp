import kotlin.String

/**
 * https://github.com/ben-manes/gradle-versions-plugin
 * ./gradlew dependencyUpdates
 */
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

        const val preference: String = "androidx.preference:preference:${Versions.AndroidX.preference}"

        const val swipeRefreshLayout =
                "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.AndroidX.swipeRefreshLayout}"

        const val browser: String = "androidx.browser:browser:${Versions.AndroidX.browser}"
    }

    object Firebase {
        private const val firebase = "com.google.firebase"

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

    /**
     * https://github.com/markushi/android-ui
     */
    const val android_ui: String = "com.github.markushi:android-ui:" + Versions.android_ui

    const val superslim: String = "com.tonicartos:superslim:" + Versions.superslim
}
