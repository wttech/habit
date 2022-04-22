import org.gradle.api.tasks.bundling.Compression

plugins {
    base
    id("com.github.node-gradle.node")
}

val installPackages = tasks.register<Exec>("installPackages") {
    commandLine = listOf("yarn")
    workingDir = file("./")
}

val buildDocumentation = tasks.register<Exec>("buildDocumentation") {
    dependsOn(installPackages)
    commandLine = listOf("yarn", "build:prod")
    outputs.upToDateWhen { false }
}

val zip = tasks.register<Tar>("packageDistribution") {
    dependsOn(buildDocumentation)
    archiveFileName.set("habit-docs.tar.gz")
    compression = Compression.GZIP
    from("/dist")
}
