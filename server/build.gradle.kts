plugins {
    java
    `maven-publish`
    id("org.springframework.boot")
    // Maven encrypted password support
    id("net.linguica.maven-settings")
    id("com.google.cloud.tools.jib")
    id("com.github.node-gradle.node")
    id("io.freefair.lombok")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    annotationProcessor(platform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
    annotationProcessor(libs.spring.boot.configuration.processor)
    implementation(libs.spring.boot.webflux)
    implementation(libs.spring.boot.actuator)
    implementation(libs.springdoc)
    implementation(libs.guava)
    implementation(libs.natty)
    implementation(libs.nitrite)
    // required for volume creation with labels
    implementation(libs.docker.client)
    // because of spotify docker client, otherwise org/glassfish/hk2/api/MultiException is thrown on startup
    // to be removed after migration to official docker client
    implementation(libs.jersey)
    // required for docker exec
    implementation(libs.docker.java.client)
    implementation(libs.bundles.jackson)
    implementation(libs.bundles.logging)
    implementation(libs.commons.compress)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.bundles.testing)
    testImplementation(libs.reactor.test)
}

val jar by tasks.existing
val bootJar by tasks.existing
val classes by tasks.existing

val generateBuildHash = tasks.register<Copy>("generateBuildHash") {
    dependsOn(classes)
    outputs.upToDateWhen {
        classes.get().state.upToDate
    }
    from("src/main/templates")
    include("buildHashTemplate")
    into("$buildDir/resources/main")
    rename("buildHashTemplate", "buildHash")
    // Substitute property tokens in files
    val version = (Math.random()*1000000000).toInt()
    expand("buildVersion" to version)
    filteringCharset = "UTF-8"
}

val generateImageConfiguration = tasks.register<Copy>("generateImageConfiguration") {
    from("src/main/templates")
    include("application-images-template.yml")
    into("$buildDir/resources/main")
    rename("application-images-template.yml", "application-images.yml")
    expand("version" to project.version)
    filteringCharset = "UTF-8"
    inputs.property("version", project.version)
}

val copyUI = tasks.register<Copy>("copyUI") {
    dependsOn(":server-ui:buildProduction")
    from(project(":server-ui").buildDir)
    into(layout.buildDirectory.dir("resources/main/public"))
}

jar {
    enabled = true
    dependsOn(generateBuildHash)
    dependsOn(generateImageConfiguration)
    dependsOn(copyUI)
}

bootJar {
    dependsOn(generateBuildHash)
    dependsOn(generateImageConfiguration)
    dependsOn(copyUI)
}

val publishToLocalDocker = tasks.register<Exec>("publishToLocalDocker") {
    dependsOn(bootJar)
    commandLine = listOf("./buildImage.sh", project.version.toString())
}

val publishToDockerHub = tasks.register<Exec>("publishToDockerHub") {
    dependsOn(publishToLocalDocker)
    commandLine = listOf("./push.sh", project.version.toString())
}

jib {
    to {
        image = "habitester/habit-server:${project.version}"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
