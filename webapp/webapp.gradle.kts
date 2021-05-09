import com.moowork.gradle.node.npm.NpmTask
import java.nio.file.Files

plugins {
  id("com.github.node-gradle.node") version "2.2.0"
}

node {
  version = "14.15.4"
  npmVersion = "6.14.10"

  download = true
}

val distDirname = "dist"

val dockerGroup = "docker"
val dockerDir = projectDir.resolve("docker")
val imageName = "gleroy/${project.name}"

tasks {
  create("assemble") {
    dependsOn("distTar")
  }

  create<NpmTask>("build") {
    group = "build"
    dependsOn("npm_install", "createVersionFile")

    setArgs(listOf("run", "build", "--", "--prod"))

    inputs.files("angular.json", "tsconfig.json", "src")
    outputs.dir(buildDir.resolve(distDirname))
  }

  create<Exec>("buildDockerImage") {
    group = dockerGroup
    dependsOn("copyDistDirToDockerDir")

    workingDir(dockerDir)
    executable("docker")
    args(
      "build",
      "-t", "$imageName:$version",
      "-t", "$imageName:latest",
      "."
    )
  }

  create("check") {
    group = "verification"
  }

  create("clean") {
    group = "build"

    doLast {
      buildDir.deleteRecursively()
      projectDir.resolve("node_modules").deleteRecursively()
    }
  }

  create<Sync>("copyDistDirToDockerDir") {
    group = dockerGroup
    dependsOn("assemble")

    from(buildDir.resolve(distDirname))
    into(dockerDir.resolve(distDirname))
  }

  create("createVersionFile") {
    val file = projectDir.resolve("src/app/version.ts")

    doLast {
      Files.newBufferedWriter(file.toPath()).use { writer ->
        writer.append("export const Version = '${project.version}'\n")
      }
    }

    outputs.upToDateWhen { false }
  }

  create<Tar>("distTar") {
    dependsOn("build")

    destinationDirectory.set(buildDir)
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())

    from(buildDir.resolve(distDirname))
    into("${project.name}-${project.version}")
  }

  create<NpmTask>("lint") {
    group = "verification"
    dependsOn("npm_install")

    setArgs(listOf("run", "lint"))
  }

  getByName("npm_install") {
    group = "build"

    inputs.files("package.json", "package-lock.json")
    outputs.dir("node_modules")
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

  create<NpmTask>("serve") {
    group = "application"
    dependsOn("npm_install")

    val locale = project.property("ivana-chess-webapp.locale")

    setArgs(listOf("run", "start", "--", "--open", "--configuration=$locale"))
  }

  create<NpmTask>("test") {
    group = "verification"
    dependsOn("assemble")

    setArgs(listOf("run", "test", "--", "--configuration=ci"))
  }
}
