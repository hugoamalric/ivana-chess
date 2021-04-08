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
    val kotlintestVersion = "3.4.2"
    val mockkVersion = "1.10.5"

    /***********************
     * Implementation
     ***********************/

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))

    /***********************
     * API
     ***********************/

    // Jackson
    api("com.fasterxml.jackson.core:jackson-databind")

    // Ivana Chess
    api(project(":${rootProject.name}-core"))

    // Validation
    api("javax.validation:validation-api")

    /***********************
     * Test implementation
     ***********************/

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // Kotlin
    testImplementation(kotlin("reflect"))

    // Kotlintest
    testImplementation("io.kotlintest:kotlintest-core:$kotlintestVersion")

    // Mockk
    testImplementation("io.mockk:mockk:$mockkVersion")
}
