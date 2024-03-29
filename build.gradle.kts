// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(Libs.Google.gradleBuildTool)
        classpath(kotlin("gradle-plugin", version = Versions.JetBrains.kotlinLib))
        classpath(Libs.Google.services) // Google Services plugin
        classpath(Libs.jacoco)
        classpath(Libs.JetBrains.dokka)
        classpath(Libs.Firebase.performance) // Performance Monitoring plugin
        classpath(Libs.gradleVersionsPlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()

        @Suppress("JcenterRepositoryObsolete")
        jcenter() {
            content {
                // https://github.com/davideas/FlexibleAdapter/issues/768
                includeModule(Libs.FlexibleAdapter.group, Libs.FlexibleAdapter.moduleCore)
                includeModule(Libs.FlexibleAdapter.group, Libs.FlexibleAdapter.moduleUi)
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
    delete(file("${rootProject.projectDir}/jacoco"))
    delete(file("${rootProject.projectDir}/dokka"))
}