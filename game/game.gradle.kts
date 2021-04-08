plugins {
    // Kotlin
    kotlin("jvm")
}

dependencies {
    val jacksonVersion = "2.12.2"
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

    // Jackson
    testImplementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    // Kotlin
    testImplementation(kotlin("reflect"))

    // Kotlintest
    testImplementation("io.kotlintest:kotlintest-core:$kotlintestVersion")
}
