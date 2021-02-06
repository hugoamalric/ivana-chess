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
}
