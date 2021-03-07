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
    val javaJwtVersion = "3.10.1"
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

    // Java JWT
    implementation("com.auth0:java-jwt:$javaJwtVersion")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-security")

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

val isCi = project.property("ci").toString().toBoolean()
val testSchema = project.property("ivana-chess-api.db.test-schema").toString()

data class DatabaseProperties(
    val host: String,
    val port: Int,
    val name: String,
    val schema: String,
    val username: String,
    val password: String
)

fun databaseProperties() = DatabaseProperties(
    host = project.property("ivana-chess-api.db.host").toString(),
    port = project.property("ivana-chess-api.db.port").toString().toInt(),
    name = project.property("ivana-chess-api.db.name").toString(),
    schema = project.property("ivana-chess-api.db.schema").toString(),
    username = project.property("ivana-chess-api.db.username").toString(),
    password = project.property("ivana-chess-api.db.password").toString()
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
            "DROP SCHEMA \"${props.schema}\" CASCADE; CREATE SCHEMA \"${props.schema}\";"
        )
        environment("PGPASSWORD", props.password)
    }
}

tasks {
    bootJar {
        archiveClassifier.set(fatjarClassifier)
    }

    bootRun {
        dependsOn("dockerComposeUp")

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

    create("dockerComposeUp") {
        group = dockerGroup

        doLast {
            if (!isCi) {
                exec {
                    executable("bash")
                    args(
                        "-c",
                        "docker-compose -f ${projectDir.resolve("docker-compose-dev.yml")} up -d && sleep 2"
                    )
                }
            }
        }
    }

    create("dropDatabase") {
        dependsOn("dockerComposeUp")

        val dbProps = databaseProperties()
        doLast {
            dropDatabase(dbProps)
        }
    }

    create("dropTestDatabase") {
        dependsOn("dockerComposeUp")

        val dbProps = databaseProperties()
        doLast {
            if (!isCi) {
                dropDatabase(dbProps.copy(schema = testSchema))
            }
        }
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

    test {
        dependsOn("dropTestDatabase")

        val dbProps = databaseProperties().copy(schema = testSchema)
        systemProperty(
            "ivana-chess.db.url",
            "jdbc:postgresql://${dbProps.host}:${dbProps.port}/${dbProps.name}?currentSchema=${dbProps.schema}"
        )
    }
}
