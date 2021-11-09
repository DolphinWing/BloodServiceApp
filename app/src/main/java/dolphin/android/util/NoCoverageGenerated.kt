package dolphin.android.util

/**
 * Filter out classes from JaCoCo report using annotations
 * https://afanasev.net/kotlin/jacoco/2020/04/10/jacoco-ignore-annotation.html
 *
 * Note this method is not recommended by JaCoCo. It's a workaround.
 */
@Retention(AnnotationRetention.BINARY)
annotation class NoCoverageGenerated

/**
 * Use this to annotate some test-free codes.
 */
typealias NoCoverageRequired = NoCoverageGenerated

/**
 * Use this to annotate experimental classes. DO NOT include them to coverage report.
 */
typealias ExperimentalNoCoverage = NoCoverageGenerated

/**
 * Use this to annotate debug only classes.
 */
typealias DebugOnlyNoCoverage = NoCoverageGenerated

/**
 * Use this to annotate deprecated classes.
 */
typealias DeprecatedNoCoverage = NoCoverageGenerated