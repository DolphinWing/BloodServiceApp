import com.google.firebase.perf.plugin.FirebasePerfExtension
import java.net.URL

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services") // Google Services Gradle plugin
    id("com.github.ben-manes.versions") // version Versions.gradleVersionsPlugin
    id("org.jlleitschuh.gradle.ktlint") version Versions.ktlintGradle
    id("org.jetbrains.dokka")
    id("com.google.firebase.firebase-perf")
    // id("version.gradle")
}

apply<dolphin.gradle.dsl.CustomJacocoReport>()

android {
    compileSdk = 33
    // buildToolsVersion("31.0.0")
    namespace = "dolphin.android.apps.BloodServiceApp"

    defaultConfig {
        applicationId = "dolphin.android.apps.BloodServiceApp"
        targetSdk = 33
        resourceConfigurations.addAll(arrayOf("zh_TW"))
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            isTestCoverageEnabled = false
//            enableUnitTestCoverage(false)
//            enableAndroidTestCoverage(false)
        }
        getByName("debug") {
            isMinifyEnabled = false
            isTestCoverageEnabled = false /* FIXME: jacoco cause runtime crash */
//            enableUnitTestCoverage(false)
//            enableAndroidTestCoverage(false)

            // Set this flag to 'false' to disable @AddTrace annotation processing and automatic
            // HTTP/S network request monitoring for a specific build variant at compile time
            // See https://stackoverflow.com/a/57451108
            (this as ExtensionAware).configure<FirebasePerfExtension> {
                setInstrumentationEnabled(false)
            }
        }
    }

    flavorDimensions.add("mode")
    productFlavors {
        create("compose") {
            versionCode = 226
            versionName = "3.3.0"
            dimension = "mode"
            minSdk = 21
        }
//        create("flexible") {
//            versionCode = 112
//            versionName = "2.5.3"
//            dimension = "mode"
//            minSdk = 21
//        }
    }

    lint {
        disable.add("PackageName") // disable("PackageName")
    }

//    // maybe https://github.com/evant/gradle-retrolambda
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    buildFeatures.compose = true

    composeOptions {
        // kotlinCompilerVersion = Versions.AndroidX.composeCompiler
        kotlinCompilerExtensionVersion = Versions.AndroidX.compose
    }

    testOptions {
        // animationsDisabled = true
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    // Gradle automatically adds 'android.test.runner' as a dependency.
    useLibrary("android.test.runner")
    useLibrary("android.test.base")
    useLibrary("android.test.mock")

    // https://stackoverflow.com/a/47509465
    packagingOptions {
        resources.excludes.add("/META-INF/AL2.0")
        resources.excludes.add("/META-INF/LGPL2.1")
        resources.excludes.add("/LICENSE.txt")
    }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

tasks {
    /**
     * https://github.com/ben-manes/gradle-versions-plugin
     * ./gradlew dependencyUpdates
     */
    withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
        // Example 2: disallow release candidates as upgradable versions from stable versions
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }

        // optional parameters
        checkForGradleUpdate = true
        outputFormatter = "json"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }

    // https://kotlin.github.io/dokka/1.4.0/user_guide/gradle/usage/
    dokkaHtml {
        outputDirectory.set(rootProject.projectDir.resolve("dokka"))

        // Set module name displayed in the final output
        moduleName.set("DolphinWing-BloodServiceApp")

        dokkaSourceSets.configureEach {
            // This name will be shown in the final output
            if (name == "compose" || name == "main") { // show only production docs
                suppress.set(false)
                displayName.set(name)
            } else { // ignore others
                suppress.set(true)
            }

            // Do not output deprecated members. Applies globally, can be overridden by packageOptions
            skipDeprecated.set(false)

            // Platform used for code analysis. See the "Platforms" section of this readme
            platform.set(org.jetbrains.dokka.Platform.jvm)

            // List of files with module and package documentation
            // https://kotlinlang.org/docs/reference/kotlin-doc.html#module-and-package-documentation
            includes.from("packages.md")

            // Disable linking to online Android documentation (only applicable for Android projects)
            noAndroidSdkLink.set(false)

            // Disable linking to online JDK documentation
            noJdkLink.set(true)

            // Allows linking to documentation of the project"s dependencies (generated with Javadoc or Dokka)
            // Repeat for multiple links
            externalDocumentationLink {
                // Root URL of the generated documentation to link with.
                // The trailing slash is required!
                val refsFromAndroid = "https://developer.android.com/reference"
                url.set(URL("$refsFromAndroid/kotlin/"))
                packageListUrl.set(URL("$refsFromAndroid/androidx/package-list"))
            }

            // Include generated files in documentation
            // By default Dokka will omit all files in folder named generated that is a child of buildDir
            suppressGeneratedFiles.set(true)
        }
    }
}

dependencies {
    implementation(kotlin("stdlib", Versions.JetBrains.kotlinLib))
    implementation(Libs.okhttp)
    testImplementation(Libs.mockServer)

    implementation(Libs.AndroidX.appcompat)
    implementation(Libs.AndroidX.swipeRefreshLayout)
    // implementation(Libs.AndroidX.browser)
    implementation(Libs.AndroidX.recyclerView)
    implementation(Libs.AndroidX.constraintLayout)
    implementation(Libs.AndroidX.preference)
    implementation(Libs.AndroidX.activity)
    implementation(Libs.Google.material)
    // implementation(Libs.Google.mdcAdapter)
    implementation(Libs.AndroidX.datastore)

    // play services
    implementation(Libs.Google.PlayServices.core)
    // check https://developers.google.com/admob/android/quick-start
    implementation(Libs.Google.PlayServices.ads)
    // For apps targeting Android 12, add WorkManager dependency.
    constraints {
        implementation(Libs.AndroidX.work) {
            because(
                "androidx.work:work-runtime:2.1.0 pulled from play-services-ads " +
                    "has a bug using PendingIntent without FLAG_IMMUTABLE or FLAG_MUTABLE " +
                    "and will fail in apps targeting S+."
            )
        }
    }
    implementation(Libs.AndroidX.work) // ads-lite depends on WorkManager

    // Firebase
    implementation(platform(Libs.Firebase.bom))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-config-ktx")
    implementation("com.google.firebase:firebase-perf-ktx")

//    // flexible
//    // https://github.com/davideas/FlexibleAdapter
//    "flexibleImplementation"(Libs.FlexibleAdapter.LibCore)
//    "flexibleImplementation"(Libs.FlexibleAdapter.LibUi)
//    "flexibleImplementation"(Libs.AndroidX.fragment)

    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.lifecycleViewModel)
    implementation(Libs.AndroidX.liveData)
    // implementation(Libs.AndroidX.lifecycleExtensions)
    implementation(Libs.JetBrains.coroutinesCore)
    implementation(Libs.JetBrains.coroutinesAndroid)
    testImplementation(Libs.JetBrains.coroutinesTest)

    /* Jetpack Compose */
    implementation(platform("androidx.compose:compose-bom:${Versions.AndroidX.conposeBom}"))
//    implementation(Libs.AndroidX.Compose.compiler)
//    implementation(Libs.AndroidX.Compose.runtime)
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation(Libs.AndroidX.Compose.activity)
    implementation(Libs.AndroidX.Compose.lifecycle)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
//    implementation(Libs.AndroidX.Compose.layout)
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.material:material-icons-extended")
    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // unit test
    testImplementation(Libs.Test.junit)
    testImplementation(Libs.Test.robolectric)
    testImplementation(Libs.Test.mockito)
    testImplementation(Libs.Test.mockitoInline)
    testImplementation(Libs.AndroidXTest.core)
    testImplementation(Libs.AndroidXTest.junit)
    testImplementation(Libs.AndroidXTest.rules)
    testImplementation(Libs.AndroidXTest.runner)
    testImplementation(Libs.AndroidXTest.archCore)
//    androidTestImplementation(Libs.AndroidXTest.Compose.test)
//    androidTestImplementation(Libs.AndroidXTest.Compose.junit)
//    androidTestImplementation(Libs.AndroidXTest.core)

    // implementation("org.jacoco:org.jacoco.agent:${Versions.jacoco}:runtime")
}

// https://github.com/jlleitschuh/ktlint-gradle
// https://github.com/JLLeitschuh/ktlint-gradle/blob/master/samples/android-app/build.gradle.kts
ktlint {
    android.set(true)
    // outputColorName.set("RED")
}
