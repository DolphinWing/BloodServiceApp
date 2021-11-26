package dolphin.gradle.dsl

import org.gradle.api.tasks.Input

abstract class JacocoAndroidTestReport :
    CustomJacocoTask("Generate Jacoco coverage reports with Android Tests.") {

    @Input
    var flavor: String = defaultFlavor
        set(value) {
            setupFlavor(value)
            field = value
        }

    private fun setupFlavor(value: String) {
        dependsOn("connected${value}DebugAndroidTest")
        extraDependsAndroidTest(project, value) // we only run AndroidTest in app.
        setupReportConfigs(project, value)
        val filter = jacocoExecFilter(value).filter { it.endsWith("coverage.ec") }
        executionData.setFrom(project.files(executionDataList(project, filter)))
    }
}
