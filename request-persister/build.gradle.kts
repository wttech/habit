val buildImage = tasks.register<Exec>("publishToLocalDocker") {
    commandLine = listOf("./build.sh", project.version.toString())
}

tasks.register<Exec>("publishToDockerHub") {
    dependsOn(buildImage)
    commandLine = listOf("./push.sh", project.version.toString())
}
