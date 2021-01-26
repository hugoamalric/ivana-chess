import com.moowork.gradle.node.npm.NpmTask

plugins {
  id("com.github.node-gradle.node") version "2.2.0"
}

node {
  version = "14.15.4"
  npmVersion = "6.14.10"

  download = true
}

tasks {
  getByName("npm_install") {
    group = "build"

    outputs.upToDateWhen { projectDir.resolve("node_modules").exists() }
  }

  create<NpmTask>("assemble") {
    group = "build"
    dependsOn("npm_install")

    setArgs(listOf("run", "build", "--prod"))

    outputs.upToDateWhen { buildDir.exists() }
  }

  create<NpmTask>("test") {
    group = "verification"
    dependsOn("assemble")

    setArgs(listOf("run", "test", "--", "--configuration=ci"))
  }

  create<NpmTask>("lint") {
    group = "verification"
    dependsOn("assemble")

    setArgs(listOf("run", "lint"))
  }

  create("check") {
    group = "verification"
    dependsOn("lint", "test")
  }

  create("clean") {
    group = "build"

    doLast {
      buildDir.deleteRecursively()
      projectDir.resolve("node_modules").deleteRecursively()
    }
  }

  create<NpmTask>("serve") {
    group = "application"
    dependsOn("assemble")

    setArgs(listOf("run", "start", "--open"))
  }
}
