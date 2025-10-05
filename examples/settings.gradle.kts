pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://maven.fabricmc.net") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "SLP-fabric-examples"
include("1.20.5")
include("1.21.1")
include("1.21.2")
include("1.21.4")
include("1.21.5")
include("1.21.6")
include("1.21.8")
include("1.21.10")
