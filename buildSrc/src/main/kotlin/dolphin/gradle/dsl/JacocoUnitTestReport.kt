package dolphin.gradle.dsl

import org.gradle.api.tasks.Input

abstract class JacocoUnitTestReport :
    CustomJacocoTask("Generate Jacoco coverage reports with Unit Tests.") {

    @Input
    var flavor: String = defaultFlavor
        set(value) {
            setupFlavor(value)
            field = value
        }

    private fun setupFlavor(value: String) {
        // println("setup $value")
        dependsOn("test${value}DebugUnitTest")
        extraDependsTest(project, value)
        setupReportConfigs(project, value)
        val filter = jacocoExecFilter(value).filter { it.endsWith(".exec") }
        executionData.setFrom(project.files(executionDataList(project, filter)))
    }
}
