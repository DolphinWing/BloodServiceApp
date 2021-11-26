package dolphin.gradle.dsl

// import com.android.build.api.dsl.ApplicationExtension
// import com.android.build.api.variant.AndroidComponentsExtension
import Versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

/**
 * See https://www.youtube.com/watch?v=LPzBVtwGxlo
 * Sample: https://github.com/android/gradle-recipes
 */
class CustomJacocoReport : Plugin<Project> {
    override fun apply(project: Project) {
        // println("apply ${project.projectDir}")

//        val android = project.extensions.getByType(ApplicationExtension::class.java)
//        android.buildTypes.forEach { type ->
//            println("found buildTypes ${type.name}")
//        }

//        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
//        androidComponents.onVariants { variant ->
//            println("found variant ${variant.name}")
//        }

        project.tasks.register<GithubPagesTask>("updateGithubPages")

        // show dokka html main page
        project.tasks.findByName("dokkaHtml")?.doLast {
            println("file://${project.rootProject.projectDir.resolve("dokka")}/index.html")
        }

        registerJacocoReports(project, "Compose")
    }

    @Suppress("SameParameterValue")
    private fun registerJacocoReports(project: Project, flavor: String) {
        project.plugins.apply("jacoco")
        (project.extensions.getByName("jacoco") as? JacocoPluginExtension)?.let { jacoco ->
            jacoco.toolVersion = Versions.jacoco
            // println("apply jacoco ${jacoco.toolVersion}")
        }

        project.tasks.apply {
            register<JacocoUnitTestReport>("jacocoUnitTestReport") {
                // println("apply jacocoUnitTestReport")
                this.flavor = flavor
            }
            register<JacocoAndroidTestReport>("jacocoAndroidTestReport") {
                // println("apply dolphin.gradle.dsl.JacocoAndroidTestReport")
                this.flavor = flavor
            }
            register<JacocoCombinedTestReport>("jacocoCombinedTestReport") {
                // println("apply dolphin.gradle.dsl.JacocoCombinedTestReport")
                this.flavor = flavor
            }
            register<JacocoTestReport>("jacocoTestReport") {
                // println("apply jacocoTestReport")
                this.flavor = flavor
            }
        }

        project.afterEvaluate {
            // Java 9+
            // https://medium.com/@jack_martynov/adopt-android-build-on-the-jdk11-macos-cc8f05995341
            // https://github.com/annypatel/Tickmarks/commit/7a91641229abc7a34d5b17222adf89ca40d58480
            tasks.withType(org.gradle.api.tasks.testing.Test::class.java) {
                extensions.configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                    // https://github.com/gradle/gradle/issues/5184
                    excludes = listOf("jdk.internal.*")
                }
            }
        }
    }
}
