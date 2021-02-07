import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    // Kotlin
    kotlin("jvm")

    // Spring
    id("io.spring.dependency-management")
}

dependencyManagement {
    imports {
        mavenBom(SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    val junitVersion = "5.7.0"
    val kotlintestVersion = "3.4.2"

    /***********************
     * Implementation
     ***********************/

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))

    /***********************
     * Test implementation
     ***********************/

    // Ivana Chess
    testImplementation(project(":${rootProject.name}-dto"))

    // Jackson
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    // Kotlin
    testImplementation(kotlin("reflect"))

    // Kotlintest
    testImplementation("io.kotlintest:kotlintest-core:$kotlintestVersion")
}

tasks {
    test {
        testLogging.showStandardStreams = true
    }
}
