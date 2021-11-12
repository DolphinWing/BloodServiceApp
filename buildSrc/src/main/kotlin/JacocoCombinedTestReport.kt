import org.gradle.api.tasks.Input

abstract class JacocoCombinedTestReport :
    CustomJacocoTask("Generate Jacoco coverage reports combined Android and Unit Tests.") {

    @Input
    var flavor: String = defaultFlavor
        set(value) {
            setupFlavor(value)
            field = value
        }

    private fun setupFlavor(value: String) {
        dependsOn("test${value}DebugUnitTest", "connected${value}DebugAndroidTest")
        extraDependsTest(project, value)
        extraDependsAndroidTest(project, value)
        setupReportConfigs(project, value)

        val execs = executionDataList(project, jacocoExecFilter(value).toList())
        executionData.setFrom(project.files(execs))

        println(description)
    }
}
