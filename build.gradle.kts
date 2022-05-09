import pl.allegro.tech.build.axion.release.domain.VersionConfig

plugins {
    id("pl.allegro.tech.build.axion-release")
    id("io.github.gradle-nexus.publish-plugin")
}

configure<VersionConfig> {
    checks.setAheadOfRemote(false)
    checks.isSnapshotDependencies = false

    hooks.pre("fileUpdate", mutableMapOf(
            "files" to listOf("server/src/docs/antora/antora.yml"),
            "pattern" to KotlinClosure2<String, pl.allegro.tech.build.axion.release.domain.hooks.HookContext, String>({ v, _ -> "version: '.*'" }),
            "replacement" to KotlinClosure2<String, pl.allegro.tech.build.axion.release.domain.hooks.HookContext, String>({ v, _ -> "version: '$v'" })
    ))
    hooks.pre("fileUpdate", mutableMapOf(
        "files" to listOf("docs-site/package.json", "server-ui/package.json"),
        "pattern" to KotlinClosure2<String, pl.allegro.tech.build.axion.release.domain.hooks.HookContext, String>({ v, _ -> "\"version\": \".*\"" }),
        "replacement" to KotlinClosure2<String, pl.allegro.tech.build.axion.release.domain.hooks.HookContext, String>({ v, _ -> "\"version\": \"$v\"" })
    ))
    hooks.pre("commit")
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

val rootVersion = scmVersion.version

allprojects {

    project.group = "io.wttech.habit"
    project.version = rootVersion

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
