val installPackages = tasks.register<Exec>("installPackages") {
    commandLine = listOf("yarn")
    workingDir = file("./")
}

val buildProduction = tasks.register<Exec>("buildProduction") {
    dependsOn(installPackages)
    commandLine = listOf("yarn", "build:prod")
    outputs.upToDateWhen { false }
}
