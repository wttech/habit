val serverImageName: String by project

tasks.register<Exec>("installServer") {
    val version = project.version.toString()
    val serverImage = "$serverImageName:$version"
    commandLine = listOf("bash", "./install.sh", serverImage)
}

tasks.register<Exec>("updateServer") {
    dependsOn(":server:publishToLocalDocker")
    commandLine = listOf("bash", "./updateServerService.sh")
}

tasks.register<Exec>("remove") {
    commandLine = listOf("bash", "./remove.sh")
}
