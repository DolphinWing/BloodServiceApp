package dolphin.gradle.dsl

import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Task

abstract class GithubPagesTask : DefaultTask() {
    init {
        group = "Reporting"
        description = "Generate Jacoco coverage reports and dokka documents"

        // generate combined test report
        project.tasks.findByName("jacocoCombinedTestReport")?.let {
            dependsOn("jacocoCombinedTestReport")
        }

        // generate dokka report
        project.tasks.findByName("dokkaHtml")?.let {
            dependsOn("dokkaHtml")
        }
    }

    override fun doFirst(action: Action<in Task>): Task {
        val jacoco = project.file("${project.rootProject.projectDir}/jacoco")
        jacoco.deleteRecursively()

        val dokka = project.file("${project.rootProject.projectDir}/dokka")
        dokka.deleteRecursively()
        return super.doFirst(action)
    }
}
