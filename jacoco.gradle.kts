/**
 * Reference: Unified Code Coverage for Android: Revisited by Rafael Toledo
 * https://proandroiddev.com/unified-code-coverage-for-android-revisited-44789c9b722f
 * https://github.com/rafaeltoledo/unified-code-coverage-android
 */
import java.text.SimpleDateFormat
import java.util.Date

//plugins {
//    id("jacoco")
//}
apply(plugin = "jacoco")

//jacoco {
//    toolVersion = Versions.jacoco
//}

val testModuleList = arrayOf("app")
val sourceModuleList = arrayOf("app")

val jacocoFileFilter = arrayOf(
    "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "android/**/*.*",
    "android/**/*.*",
    "**/*\$Lambda$*.*", // Jacoco can not handle several "$" in class name.
    "**/*\$inlined$*.*", // Kotlin specific, Jacoco can not handle several "$" in class name.
    // dagger
    "**/*Dagger*.*", "**/*Module*.*", "**/*MembersInjector*.*",
    "**/*_Provide*Factory*.*", "**/*_Factory*.*",
    // navigation safe-args
    "**/*FragmentDirections*.*", "**/*FragmentArgs*.*", "**/*DialogArgs*.*",
    // others
    "**/*$$*" //, "**/*\$[0-9]+.*"
)

fun sourceDirectory(flavor: String): List<String> {
    val sources = ArrayList<String>()
    rootProject.subprojects.filter { p ->
        // p.plugins.hasPlugin("jacoco")
        sourceModuleList.contains(p.name)
    }.forEach { p ->
        // println("source>> ${p.name}: ${p.projectDir}")

        // source: main
        sources.add("${p.projectDir}/src/main/java")
        sources.add("${p.projectDir}/src/main/kotlin")
        // source: production
        sources.add("${p.projectDir}/src/${flavor.toLowerCase()}/java")
        sources.add("${p.projectDir}/src/${flavor.toLowerCase()}/kotlin")
    }
    return sources
}

fun jacocoExecFilter(flavor: String) = arrayOf(
    "jacoco/test${flavor}DebugUnitTest.exec",
    "outputs/unit_test_code_coverage/${flavor.toLowerCase()}DebugUnitTest/test${flavor}DebugUnitTest.exec",
    "outputs/code_coverage/${flavor.toLowerCase()}DebugAndroidTest/connected/*coverage.ec",
)

fun executionDataList(filter: List<String>): List<Any> {
    val execution = ArrayList<Any>()
    rootProject.subprojects.filter { p ->
        // p.plugins.hasPlugin("jacoco")
        sourceModuleList.contains(p.name)
    }.forEach { p ->
        // println("exec >> ${p.name}: ${p.buildDir}")
        execution.add(fileTree(p.buildDir) {
            includes.addAll(filter)
        })
    }
    return execution
}

fun classDirectory(flavor: String): List<Any> {
    val classes = ArrayList<Any>()
    rootProject.subprojects.filter { p ->
        // p.plugins.hasPlugin("jacoco")
        sourceModuleList.contains(p.name)
    }.forEach { p ->
        // java classes: main
        classes.add(fileTree("${p.buildDir}/intermediates/javac/debug") {
            excludes.addAll(jacocoFileFilter)
        })
        // java classes: production
        classes.add(fileTree("${p.buildDir}/intermediates/javac/${flavor.toLowerCase()}Debug") {
            excludes.addAll(jacocoFileFilter)
        })
        // kotlin classes: main
        classes.add(fileTree("${p.buildDir}/tmp/kotlin-classes/debug") {
            excludes.addAll(jacocoFileFilter)
        })
        // kotlin classes: production
        classes.add(fileTree("${p.buildDir}/tmp/kotlin-classes/${flavor.toLowerCase()}Debug") {
            excludes.addAll(jacocoFileFilter)
        })
    }
    return classes
}

fun extraDependsTest(task: Task, flavor: String) {
    // depends on module unit tests
    rootProject.subprojects.filter { p ->
        // p.plugins.hasPlugin("jacoco")
        sourceModuleList.contains(p.name)
    }.forEach { p ->
        p.tasks.findByName("test")?.let { // t ->
            // println("add ${p.name}:${t.name}")
            task.dependsOn(":${p.name}:test${flavor}DebugUnitTest")
        }
    }
}

fun extraDependsAndroidTest(task: Task, flavor: String) {
    // depends on module unit tests
    rootProject.subprojects.filter { p ->
        // p.plugins.hasPlugin("jacoco")
        sourceModuleList.contains(p.name)
    }.forEach { p ->
        p.tasks.findByName("test")?.let { t ->
            // println("add ${p.name}:${t.name}")
            task.dependsOn(":${p.name}:connected${flavor}DebugAndroidTest")
        }
    }
}

fun setupReports(task: JacocoReport, flavor: String) {
    task.reports {
        xml.required.set(false)
        csv.required.set(false)
        html.destination = file("${rootProject.projectDir}/jacoco")
    }

    task.doLast {
        println("Report generated @ ${SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Date())}")
        println("file://${task.reports.html.outputLocation.get()}/index.html")
    }

    task.sourceDirectories.setFrom(files(sourceDirectory(flavor)))
    task.additionalSourceDirs.setFrom(files(sourceDirectory(flavor)))
    task.classDirectories.setFrom(files(classDirectory(flavor)))
    task.additionalClassDirs.setFrom(files(classDirectory(flavor)))
}

tasks {
    val flavor = "Compose"

    register("jacocoUnitTestReport", JacocoReport::class.java) {
        group = "Reporting"
        description = "Generate Jacoco coverage reports with Unit Tests."

        dependsOn("test${flavor}DebugUnitTest")
        extraDependsTest(this, flavor)
        setupReports(this, flavor)
        executionData.setFrom(
            files(executionDataList(jacocoExecFilter(flavor).filter { it.endsWith(".exec") }))
        )
    }

    register("jacocoAndroidTestReport", JacocoReport::class.java) {
        group = "Reporting"
        description = "Generate Jacoco coverage reports with Android Tests."

        dependsOn("connected${flavor}DebugAndroidTest")
        extraDependsAndroidTest(this, flavor) // we only run AndroidTest in app.
        setupReports(this, flavor)
        executionData.setFrom(
            files(executionDataList(jacocoExecFilter(flavor).filter { it.endsWith("coverage.ec") }))
        )
    }

    register("jacocoCombinedTestReport", JacocoReport::class.java) {
        group = "Reporting"
        description = "Generate Jacoco coverage reports combined Android and Unit Tests."

        dependsOn("test${flavor}DebugUnitTest", "connected${flavor}DebugAndroidTest")
        extraDependsTest(this, flavor)
        extraDependsAndroidTest(this, flavor)
        setupReports(this, flavor)
        executionData.setFrom(files(executionDataList(jacocoExecFilter(flavor).toList())))
    }

    register("jacocoTestReport", JacocoReport::class.java) {
        // val flavor = project.ext["build_flavor"].toString().capitalize() // product flavor
        group = "Reporting"
        description = "Generate Jacoco coverage reports."
        // only merge with existing test result
        setupReports(this, flavor)
        executionData.setFrom(files(executionDataList(jacocoExecFilter(flavor).toList())))
    }
}

afterEvaluate {
    // Java 9+
    // https://medium.com/@jack_martynov/adopt-android-build-on-the-jdk11-macos-cc8f05995341
    // https://github.com/annypatel/Tickmarks/commit/7a91641229abc7a34d5b17222adf89ca40d58480
    tasks.withType(org.gradle.api.tasks.testing.Test::class) {
        extensions.configure<JacocoTaskExtension> {
            isIncludeNoLocationClasses = true
            // https://github.com/gradle/gradle/issues/5184
            excludes = listOf("jdk.internal.*")
        }
    }
}
