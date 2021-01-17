plugins {
    // Gradle
    application
    distribution

    // Kotlin
    kotlin("jvm")
    kotlin("plugin.spring")

    // Spring
    id("org.springframework.boot") version "2.4.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

dependencies {
    val rootProjectName = rootProject.name

    val kotlintestVersion = "3.4.2"

    /***********************
     * Implementation
     ***********************/

    // Ivana Chess
    implementation(project(":$rootProjectName-core"))

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")

    /***********************
     * Runtime
     ***********************/

    // Jackson
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")

    /***********************
     * Test implementation
     ***********************/

    // Kotlintest
    testImplementation("io.kotlintest:kotlintest-core:$kotlintestVersion")

    // Mockk
    testImplementation("io.mockk:mockk:1.10.5")

    // Spring
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    jar {
        enabled = true
    }
}