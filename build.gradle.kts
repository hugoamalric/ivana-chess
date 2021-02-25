import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.21"

    // Kotlin
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false

    // Spring
    id("org.springframework.boot") version "2.4.2" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
}

allprojects {
    val rootGroup = "dev.gleroy.ivanachess"
    group = if (projectDir.parentFile == rootProject.projectDir) {
        rootGroup
    } else {
        "$rootGroup.${projectDir.parentFile.name}"
    }
    version = "0.2.1"

    repositories {
        mavenCentral()
    }

    tasks.withType<Jar> {
        manifest {
            attributes(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
        }
    }

    tasks.withType<Tar> {
        compression = Compression.GZIP
        archiveExtension.set("tar.gz")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks {
    wrapper {
        gradleVersion = "6.8"
    }
}
