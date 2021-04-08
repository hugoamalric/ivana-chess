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

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))

    // Validation
    implementation("javax.validation:validation-api")

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
