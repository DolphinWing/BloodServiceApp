import kotlin.String
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

/**
 * https://github.com/ben-manes/gradle-versions-plugin
 * ./gradlew dependencyUpdates
 */
object Versions {
    object Google {
        const val gradleBuildTool = "4.1.0-alpha09"
        const val services: String = "4.3.3"

        const val playServices = "17.2.1"
        const val analytics = "17.0.0"
        const val ads = "19.1.0"

        const val material: String = "1.1.0"
    }

    const val okhttp: String = "4.6.0"

    object AndroidX {
        const val appcompat: String = "1.1.0"

        const val coreKtx: String = "1.2.0"

        const val lifecycle: String = "2.2.0"

        const val constraintLayout: String = "1.1.3"

        const val fragment: String = "1.2.4"

        const val recyclerView: String = "1.1.0"

        const val preference: String = "1.1.1"

        const val swipeRefreshLayout = "1.0.0"

        const val browser: String = "1.2.0"
    }

    object Firebase {
        const val core: String = "17.4.1"
        const val analytics: String = "17.4.1"
        const val config: String = "19.1.4"
    }

    object FlexibleAdapter {
        const val ui: String = "1.0.0"

        const val core: String = "5.1.0"
    }

    const val org_jetbrains_kotlin: String = "1.3.72"

    const val lint_gradle: String = "27.1.0-alpha09"

    const val android_ui: String = "1.2"

    const val superslim: String = "0.4.13"
}
