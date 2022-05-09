val buildImage = tasks.register<Exec>("publishToLocalDocker") {
    commandLine = listOf("./build.sh", project.version.toString())
    inputs.files(fileTree("").exclude("build.gradle.kts"))
            .withPropertyName("sourceFiles")
}

tasks.register<Exec>("publishToDocker") {
    dependsOn(buildImage)
    commandLine = listOf("./push.sh", project.version.toString())
}
