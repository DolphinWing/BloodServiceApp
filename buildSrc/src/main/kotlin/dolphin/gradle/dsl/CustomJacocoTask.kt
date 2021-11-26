package dolphin.gradle.dsl

import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

abstract class CustomJacocoTask(
    taskDesc: String,
    taskGroup: String = "Reporting",
    // private val testModuleList: Array<String> = arrayOf("app"),
    private val sourceModuleList: Array<String> = arrayOf("app"),
) : JacocoReport() {

    init {
        group = taskGroup
        description = taskDesc
    }

    private val jacocoFileFilter = arrayOf(
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

    @Internal
    protected val defaultFlavor = ""

    private fun sourceDirectory(
        project: Project,
        flavor: String = defaultFlavor,
        modules: Array<String> = sourceModuleList,
    ): List<String> {
        val variant = flavor.toLowerCase(Locale.ROOT)
        val sources = ArrayList<String>()
        project.rootProject.subprojects.filter { p ->
            modules.contains(p.name)
        }.forEach { p ->
            // println("source>> ${p.name}: ${p.projectDir}")

            // source: main
            sources.add("${p.projectDir}/src/main/java")
            sources.add("${p.projectDir}/src/main/kotlin")
            // source: flavor
            sources.add("${p.projectDir}/src/${variant}/java")
            sources.add("${p.projectDir}/src/${variant}/kotlin")
        }
        return sources
    }

    @Suppress("SameParameterValue")
    protected fun jacocoExecFilter(flavor: String = defaultFlavor): Array<String> {
        val variant = flavor.toLowerCase(Locale.ROOT)
        return arrayOf(
            "jacoco/test${flavor}DebugUnitTest.exec",
            "outputs/unit_test_code_coverage/${variant}DebugUnitTest/test${flavor}DebugUnitTest.exec",
            "outputs/code_coverage/${variant}DebugAndroidTest/connected/*coverage.ec",
        )
    }

    protected fun executionDataList(project: Project, filter: List<String>): List<Any> {
        val execution = ArrayList<Any>()
        project.rootProject.subprojects.filter { p ->
            // p.plugins.hasPlugin("jacoco")
            sourceModuleList.contains(p.name)
        }.forEach { p ->
            // println("exec >> ${p.name}: ${p.buildDir}")
            execution.add(project.fileTree(p.buildDir) {
                includes.addAll(filter)
            })
        }
        return execution
    }

    @Suppress("SameParameterValue")
    private fun classDirectory(project: Project, flavor: String = defaultFlavor): List<Any> {
        val variant = flavor.toLowerCase(Locale.ROOT)
        val classes = ArrayList<Any>()
        project.rootProject.subprojects.filter { p ->
            // p.plugins.hasPlugin("jacoco")
            sourceModuleList.contains(p.name)
        }.forEach { p ->
            // java classes: main
            classes.add(project.fileTree("${p.buildDir}/intermediates/javac/debug") {
                excludes.addAll(jacocoFileFilter)
            })
            // java classes: flavor
            classes.add(project.fileTree("${p.buildDir}/intermediates/javac/${variant}Debug") {
                excludes.addAll(jacocoFileFilter)
            })
            // kotlin classes: main
            classes.add(project.fileTree("${p.buildDir}/tmp/kotlin-classes/debug") {
                excludes.addAll(jacocoFileFilter)
            })
            // kotlin classes: flavor
            classes.add(project.fileTree("${p.buildDir}/tmp/kotlin-classes/${variant}Debug") {
                excludes.addAll(jacocoFileFilter)
            })
        }
        return classes
    }

    protected fun extraDependsTest(project: Project, flavor: String = defaultFlavor) {
        // depends on module unit tests
        project.rootProject.subprojects.filter { p ->
            // p.plugins.hasPlugin("jacoco")
            sourceModuleList.contains(p.name)
        }.forEach { p ->
            p.tasks.findByName("test")?.let { // t ->
                // println("add ${p.name}:${t.name}")
                dependsOn(":${p.name}:test${flavor}DebugUnitTest")
            }
        }
    }

    protected fun extraDependsAndroidTest(
        project: Project,
        flavor: String = defaultFlavor,
    ) {
        // depends on module unit tests
        project.rootProject.subprojects.filter { p ->
            // p.plugins.hasPlugin("jacoco")
            sourceModuleList.contains(p.name)
        }.forEach { p ->
            p.tasks.findByName("test")?.let { // t ->
                // println("add ${p.name}:${t.name}")
                dependsOn(":${p.name}:connected${flavor}DebugAndroidTest")
            }
        }
    }

    protected fun setupReportConfigs(project: Project, flavor: String = defaultFlavor) {
        println("setup $name with $flavor")

        reports {
            xml.required.set(false)
            csv.required.set(false)
            html.destination = project.file("${project.rootProject.projectDir}/jacoco")
        }

        sourceDirectories.setFrom(project.files(sourceDirectory(project, flavor)))
        additionalSourceDirs.setFrom(project.files(sourceDirectory(project, flavor)))
        classDirectories.setFrom(project.files(classDirectory(project, flavor)))
        additionalClassDirs.setFrom(project.files(classDirectory(project, flavor)))

        doLast {
            val date = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US).format(Date())
            println("Report generated @ $date")
            println("file://${reports.html.outputLocation.get()}/index.html")
        }
    }
}
