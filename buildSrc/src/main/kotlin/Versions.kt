/**
 * https://github.com/ben-manes/gradle-versions-plugin
 * ./gradlew dependencyUpdates
 */
object Versions {
    object Google {
        const val gradleBuildTool = "7.0.2"
        const val services: String = "4.3.10"

        const val playServices = "17.6.0"
        const val analytics = "17.0.0"
        const val ads = "20.4.0"

        const val material: String = "1.4.0"
    }

    const val okhttp: String = "4.9.2"

    object AndroidX {
        const val appcompat: String = "1.3.1"

        const val coreKtx: String = "1.6.0"

        const val lifecycle: String = "2.3.1"
        const val viewModelCompose = "1.0.0-alpha07"

        const val constraintLayout: String = "2.1.1"

        /**
         * https://developer.android.com/jetpack/androidx/releases/activity
         */
        const val activity = "1.3.1"

        /**
         * https://developer.android.com/jetpack/androidx/releases/fragment
         */
        const val fragment = "1.3.6"

        const val recyclerView: String = "1.2.1"

        const val preference: String = "1.1.1"

        const val swipeRefreshLayout = "1.1.0"

        const val browser: String = "1.2.0"

        const val work: String = "2.7.0-rc01"

        /**
         * https://developer.android.com/jetpack/androidx/releases/compose
         */
        const val compose = "1.0.3"
    }

    object Firebase {
        const val bom: String = "28.4.1"
        const val core: String = "19.0.0"
        const val analytics: String = "19.0.0"
        const val config: String = "21.0.0"
    }

    object FlexibleAdapter {
        const val ui: String = "1.0.0"

        const val core: String = "5.1.0"
    }

    const val org_jetbrains_kotlin: String = "1.5.30"

//    const val lint_gradle: String = "27.1.0-alpha09"
//
//    const val android_ui: String = "1.2"
//
//    const val superslim: String = "0.4.13"
}
