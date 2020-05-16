// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath (Libs.Google.gradleBuildTool)
        classpath(kotlin("gradle-plugin", version = Versions.org_jetbrains_kotlin))
        classpath (Libs.Google.services)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        //mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}