import com.moowork.gradle.node.npm.NpmTask

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
    dependsOn("npm_install")

    setArgs(listOf("run", "build", "--prod"))

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
    dependsOn("lint")
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

  create<Tar>("distTar") {
    dependsOn("build")

    destinationDirectory.set(buildDir)
    archiveBaseName.set(project.name)
    archiveVersion.set(project.version.toString())

    from(buildDir.resolve(distDirname))
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

  create<NpmTask>("serve") {
    group = "application"
    dependsOn("assemble")

    setArgs(listOf("run", "start", "--open"))
  }

  create<NpmTask>("test") {
    group = "verification"
    dependsOn("assemble")

    setArgs(listOf("run", "test", "--", "--configuration=ci"))
  }
}
