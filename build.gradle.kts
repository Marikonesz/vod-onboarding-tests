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

tasks.test {
    useJUnitPlatform {
        if (project.hasProperty("groups")) {
            includeTags(project.property("groups") as String)
        }
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

tasks.register<JavaExec>("exportTestRail") {
    group = "verification"
    description = "Export test catalog to TestRail CSV under build/testrail/"
    classpath = sourceSets.test.get().runtimeClasspath
    mainClass.set("com.vod.onboarding.common.catalog.TestRailExporter")
}
