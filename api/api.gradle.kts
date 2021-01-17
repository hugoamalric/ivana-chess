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
    /***********************
     * Implementation
     ***********************/

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
}

tasks {
    jar {
        enabled = true
    }
}
