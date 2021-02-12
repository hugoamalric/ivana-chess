plugins {
    // Gradle
    application
    distribution

    // Kotlin
    kotlin("jvm")
    kotlin("plugin.spring")

    // Spring
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

application {
    mainClass.set("dev.gleroy.ivanachess.api.IvanaChessApiKt")
}

dependencies {
    val kotlintestVersion = "3.4.2"
    val mockitoKotlinVersion = "2.2.0"
    val mockkVersion = "1.10.5"

    /***********************
     * Implementation
     ***********************/

    // Ivana Chess
    implementation(project(":${rootProject.name}-core"))
    implementation(project(":${rootProject.name}-dto"))

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    /***********************
     * Runtime only
     ***********************/

    // Liquibase
    runtimeOnly("org.liquibase:liquibase-core")

    // Postgresql
    runtimeOnly("org.postgresql:postgresql")

    /***********************
     * Test implementation
     ***********************/

    // Kotlintest
    testImplementation("io.kotlintest:kotlintest-core:$kotlintestVersion")

    // Mockito Kotlin
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:$mockitoKotlinVersion")

    // Mockk
    testImplementation("io.mockk:mockk:$mockkVersion")

    // Spring
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

val fatjarClassifier = "fatjar"

val dockerGroup = "docker"
val dockerDir = projectDir.resolve("docker")
val imageName = "gleroy/${project.name}"

tasks {
    bootJar {
        archiveClassifier.set(fatjarClassifier)
    }

    bootRun {
        jvmArgs = listOf("-Dspring.profiles.active=dev")
    }

    create<Exec>("buildDockerImage") {
        group = dockerGroup
        dependsOn("copyFatjarToDockerDir")

        workingDir(dockerDir)
        executable("docker")
        args(
            "build",
            "-t", "$imageName:$version",
            "-t", "$imageName:latest",
            "--build-arg", "version=$version",
            "."
        )
    }

    create<Copy>("copyFatjarToDockerDir") {
        group = dockerGroup
        dependsOn("bootJar")

        from("$buildDir/libs/${project.name}-$version-$fatjarClassifier.jar")
        into(dockerDir)
    }

    jar {
        enabled = true
    }

    create("pushDockerImage") {
        group = dockerGroup
        dependsOn("buildDockerImage")

        doLast {
            arrayOf("$imageName:$version", "$imageName:latest").forEach { name ->
                exec {
                    executable("docker")
                    args("push", name)
                }
            }
        }
    }
}
