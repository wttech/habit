plugins {
    `java-library`
    `maven-publish`
    signing
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(libs.guava)
    api(libs.bundles.jackson)
    implementation(libs.ok.http)
    implementation(libs.ok.http.sse)
    implementation(libs.commons.io)
    implementation(libs.commons.compress)
    api(libs.assertj)
    implementation(libs.bundles.logging)
    testImplementation(libs.bundles.testing)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.getByName<Javadoc>("javadoc") {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

publishing {
    publications {
        create<MavenPublication>("client") {
            artifactId = "client"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Habit API client")
                description.set("Library for communicating with Habit Server API")
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
    sign(publishing.publications["client"])
}
