// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath (Libs.com_android_tools_build_gradle)
        classpath(kotlin("gradle-plugin", version = Versions.org_jetbrains_kotlin))
        classpath (Libs.google_services)
    }
}

plugins {
    id("de.fayard.buildSrcVersions") version "0.6.1"
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