plugins {
    `java-library`
    `maven-publish`
    id("net.linguica.maven-settings")
    signing
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

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    api(project(":client"))
    implementation(libs.bundles.logging)
    implementation(libs.junit.api)
    implementation(libs.junit.params)
    testImplementation(libs.junit.engine)
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("junit5") {
            from(components["java"])
            artifactId  = "junit5"

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Habit JUnit 5 Plugin")
                description.set("JUnit5 plugin for seamless integration with Habit")
                url.set("https://wttech.github.io/habit")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("tokrug")
                        name.set("Tomasz Krug")
                        email.set("tomasz.krug@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/wttech/habit.git")
                    url.set("https://github.com/wttech/habit")
                }
            }
        }
    }
}

signing {
    setRequired({
        (!version.toString().endsWith("SNAPSHOT") && gradle.taskGraph.hasTask("publish"))
    })
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["junit5"])
}
