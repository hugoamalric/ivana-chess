fun includeModule(name: String, subGroup: String? = null) {
    val projectName: String
    val projectDir: File
    if (subGroup == null) {
        projectName = "${rootProject.name}-$name"
        projectDir = File(name)
    } else {
        projectName = "${rootProject.name}-$subGroup-$name"
        projectDir = File(subGroup).resolve(name)
    }
    include(projectName)
    val project = project(":$projectName")
    project.projectDir = projectDir
    project.buildFileName = "$name.gradle.kts"
}

rootProject.name = "ivana-chess"

arrayOf("core").forEach { includeModule(it) }
