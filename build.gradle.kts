plugins {
    java
    id("io.qameta.allure") version "2.12.0"
}

group = "com.vod.onboarding"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val junitVersion = "5.11.4"
val playwrightVersion = "1.48.0"
val gsonVersion = "2.11.0"
val assertjVersion = "3.27.3"
// 2.29.1 exists for allure-java-* but not for allure-commandline on Maven Central
val allureVersion = "2.29.0"

repositories {
    mavenCentral()
}

dependencies {
    // Test-only repo (no production sources): implementation keeps IDE + Gradle
    // test compile classpath in sync (testCompileClasspath extends compileClasspath).
    implementation("com.microsoft.playwright:playwright:$playwrightVersion")
    implementation(platform("org.junit:junit-bom:$junitVersion"))
    implementation("org.junit.jupiter:junit-jupiter")
    implementation("org.assertj:assertj-core:$assertjVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    // allure-java-commons: @Epic, @Feature, @Step, etc. (explicit for IDE classpath)
    implementation("io.qameta.allure:allure-java-commons:$allureVersion")
    implementation("io.qameta.allure:allure-junit5:$allureVersion")
}

allure {
    version.set(allureVersion)
    adapter {
        autoconfigure.set(true)
        aspectjWeaver.set(true)
        frameworks {
            junit5 {
                adapterVersion.set(allureVersion)
            }
        }
    }
    report {
        version.set(allureVersion)
    }
}

tasks.withType<JavaExec>().configureEach {
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(17))
        },
    )
}

fun testClassFqns(tag: String?): List<String> {
    val root = file("src/test/java")
    return fileTree(root) { include("**/*Test.java") }.files
        .map { f ->
            f.relativeTo(root).invariantSeparatorsPath.removeSuffix(".java").replace("/", ".")
        }
        .filter { fqn ->
            when (tag) {
                "api" -> fqn.contains(".api.")
                "ui" -> fqn.contains(".ui.")
                else -> true
            }
        }
        .sorted()
}

tasks.test {
    useJUnitPlatform {
        if (project.hasProperty("groups")) {
            includeTags(project.property("groups") as String)
        }
    }

    if (project.hasProperty("shardIndex") && project.hasProperty("shardTotal")) {
        val shardIndex = project.property("shardIndex").toString().toInt()
        val shardTotal = project.property("shardTotal").toString().toInt()
        val tag = project.findProperty("groups")?.toString()
        val shardClasses =
            testClassFqns(tag).filter { Math.floorMod(it.hashCode(), shardTotal) == shardIndex }
        filter {
            isFailOnNoMatchingTests = shardClasses.isNotEmpty()
            shardClasses.forEach { includeTestsMatching(it) }
        }
    }

    // One JVM; parallelism is JUnit 5 concurrent methods (not Gradle forks).
    maxParallelForks = 1

    val junitParallelism =
        (Runtime.getRuntime().availableProcessors()).coerceIn(2, 8).toString()
    if (project.hasProperty("singleThread")) {
        systemProperty("junit.jupiter.execution.parallel.enabled", "false")
    } else {
        systemProperty("junit.jupiter.execution.parallel.enabled", "true")
        systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
        systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
        systemProperty("junit.jupiter.execution.parallel.config.strategy", "fixed")
        systemProperty("junit.jupiter.execution.parallel.config.fixed.parallelism", junitParallelism)
    }

    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
    // Optional Gradle -P flags → JVM system properties (see TestEnvironment)
    mapOf(
        "vodTestTarget" to "vod.test.target",
        "vodBaseUrl" to "vod.base.url",
        "vodBrowser" to "vod.browser",
        "vodHeadless" to "vod.headless",
        "vodTraceOnFailure" to "vod.trace.on.failure",
        "vodTraceDir" to "vod.trace.dir",
        "shardIndex" to "ci.shard.index",
        "shardTotal" to "ci.shard.total",
    ).forEach { (gradleProperty, systemProperty) ->
        if (project.hasProperty(gradleProperty)) {
            systemProperty(systemProperty, project.property(gradleProperty) as String)
        }
    }
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.register<JavaExec>("installPlaywright") {
    group = "playwright"
    description = "Install Chromium (default). Use: playwright install firefox|webkit for -Dvod.browser"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.microsoft.playwright.CLI")
    args("install", "chromium")
}

tasks.register<JavaExec>("validateCatalog") {
    group = "verification"
    description = "Validate test case catalog JSON (English, unique ids, automated_in)"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.vod.onboarding.common.catalog.CatalogValidator")
}

tasks.register<JavaExec>("syncCatalogDocs") {
    group = "verification"
    description = "Copy src/test/resources/test-cases/*.json to docs/test-cases/"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.vod.onboarding.common.catalog.CatalogValidator")
    args("vod-preferences.json", "--sync-docs")
}

tasks.register<JavaExec>("exportTestRail") {
    group = "verification"
    description = "Export test catalog to TestRail CSV under build/testrail/"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.vod.onboarding.common.catalog.TestRailExporter")
    finalizedBy("syncCatalogDocs")
}
