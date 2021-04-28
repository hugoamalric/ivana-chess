import org.gradle.api.tasks.testing.logging.TestExceptionFormat
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

data class DatabaseProperties(
    val host: String,
    val port: Int,
    val name: String,
    val schema: String,
    val username: String,
    val password: String
)

fun databaseProperties() = DatabaseProperties(
    host = project.property("db.host").toString(),
    port = project.property("db.port").toString().toInt(),
    name = project.property("db.name").toString(),
    schema = project.property("db.schema").toString(),
    username = project.property("db.username").toString(),
    password = project.property("db.password").toString()
)

fun dropDatabase(props: DatabaseProperties) {
    exec {
        executable = "psql"
        args(
            "-h",
            props.host,
            "-p",
            props.port,
            "-U",
            props.username,
            props.name,
            "-c",
            "DROP SCHEMA IF EXISTS \"${props.schema}\" CASCADE; CREATE SCHEMA \"${props.schema}\";"
        )
        environment("PGPASSWORD", props.password)
    }
}

val isCi = project.property("ci").toString().toBoolean()
val testSchema = project.property("db.test-schema").toString()

allprojects {
    val rootGroup = "dev.gleroy.ivanachess"
    group = if (projectDir.parentFile == rootProject.projectDir) {
        rootGroup
    } else {
        "$rootGroup.${projectDir.parentFile.name}"
    }
    version = project.property("ivana-chess.version")!!.toString()

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
        dependsOn(":dropTestDatabase")

        useJUnitPlatform()
        testLogging {
            showExceptions = true
            exceptionFormat = TestExceptionFormat.FULL
        }

        val dbProps = databaseProperties().copy(schema = testSchema)
        systemProperty(
            "ivana-chess.db.url",
            "jdbc:postgresql://${dbProps.host}:${dbProps.port}/${dbProps.name}?currentSchema=${dbProps.schema}"
        )
    }
}

tasks {
    create<Exec>("dockerComposeUp") {
        group = "docker"
        enabled = !isCi

        executable("bash")
        args(
            "-c",
            "docker-compose -f ${rootProject.projectDir.resolve("docker-compose-dev.yml")} up -d"
        )
    }

    create("dropDatabase") {
        val dbProps = databaseProperties()
        doLast {
            dropDatabase(dbProps)
        }
    }

    create("dropTestDatabase") {
        enabled = !isCi

        val dbProps = databaseProperties()
        doLast {
            if (!isCi) {
                dropDatabase(dbProps.copy(schema = testSchema))
            }
        }
    }

    wrapper {
        gradleVersion = "6.8"
    }
}
