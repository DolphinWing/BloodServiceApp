package dolphin.gradle.dsl

import org.gradle.api.tasks.Input

abstract class JacocoTestReport : CustomJacocoTask("Generate Jacoco coverage reports.") {

    @Input
    var flavor: String = defaultFlavor
        set(value) {
            setupFlavor(value)
            field = value
        }

    private fun setupFlavor(value: String) {
        setupReportConfigs(project)
        val execs = executionDataList(project, jacocoExecFilter(value).toList())
        executionData.setFrom(project.files(execs))
    }
}
