plugins {
    `java-library`
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val serverImageName: String by project

dependencies {
    implementation(project(":client"))
    implementation(libs.guava)
    implementation(libs.reactor.core)
}

pluginBundle {
    website = "https://wttech.github.io/habit"
    vcsUrl = "https://github.com/wttech/habit"
    tags = listOf("habit", "http", "proxy", "tester")
}

gradlePlugin {
    plugins {
        create("gradlePlugin") {
            id = "io.wttech.habit"
            description = "Http Black Box Tester environment manager"
            displayName = "Habit Gradle Plugin"
            implementationClass = "io.wttech.habit.gradle.HabitGradlePlugin"
        }
    }
}

tasks.register<Copy>("generateProperties") {

    val serverImageId = "${serverImageName}:${project.version}"

    from("src/main/templates")
    include("template.plugin.properties")
    into("$buildDir/resources/main")
    rename("template.plugin.properties", "plugin.properties")
    expand("serverImage" to serverImageId)
    filteringCharset = "UTF-8"
    inputs.property("serverImageId", serverImageId)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    val publishPluginMavenPublicationToSonatypeRepository by existing

    publishPluginMavenPublicationToSonatypeRepository {
        enabled = false
    }

    val publishGradlePluginPluginMarkerMavenPublicationToSonatypeRepository by existing

    publishGradlePluginPluginMarkerMavenPublicationToSonatypeRepository {
        enabled = false
    }

    val generateProperties by existing

    jar {
        dependsOn(generateProperties)
    }
}
