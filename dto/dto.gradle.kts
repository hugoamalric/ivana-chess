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

    /***********************
     * Compile only
     ***********************/

    // Jackson
    compileOnly("com.fasterxml.jackson.core:jackson-databind")

    // Validation
    compileOnly("javax.validation:validation-api")

    /***********************
     * Implementation
     ***********************/

    // Ivana Chess
    implementation(project(":${rootProject.name}-core"))

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))

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
}
