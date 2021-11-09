// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath (Libs.Google.gradleBuildTool)
        classpath(kotlin("gradle-plugin", version = Versions.org_jetbrains_kotlin))
        classpath (Libs.Google.services)
        classpath(Libs.jacoco)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter() {
            content {
                // https://github.com/davideas/FlexibleAdapter/issues/768
                includeModule("eu.davidea", "flexible-adapter")
                includeModule("eu.davidea", "flexible-adapter-ui")
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}