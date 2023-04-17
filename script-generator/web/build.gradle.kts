import io.ktor.plugin.features.*

plugins {
    kotlin("plugin.serialization")
    id("io.ktor.plugin") version "2.2.4"
    buildsrc.convention.`kotlin-jvm`
    application
}

dependencies {
    implementation(projects.scriptGenerator.logic)

    implementation(platform("io.ktor:ktor-bom:2.2.4"))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-simple:2.0.6")
}

application {
    mainClass.set("io.github.typesafegithub.workflows.scriptgenerator.rest.MainKt")
}

val dockerAppName = "github-workflows-kt-script-generator-server"

ktor {
    docker {
        localImageName.set(dockerAppName)
        imageTag.set(projects.library.version)

        externalRegistry.set(
            DockerImageRegistry.dockerHub(
                appName = provider { dockerAppName },
                username = providers.environmentVariable("DOCKERHUB_USERNAME"),
                password = providers.environmentVariable("DOCKERHUB_PASSWORD"),
            ),
        )
    }
}