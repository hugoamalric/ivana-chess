plugins {
    // Kotlin
    kotlin("jvm")
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

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")

    // Kotlin
    testImplementation(kotlin("reflect"))

    // Kotlintest
    testImplementation("io.kotlintest:kotlintest-core:$kotlintestVersion")
}
