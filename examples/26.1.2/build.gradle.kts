plugins {
    scala
    alias(libs.plugins.loom)
}

base {
    archivesName = "slp-example"
}
version = "1.0"
group = "com.kotori316"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

scala {
    scalaVersion = libs.versions.scala.get()
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "Main"
        url = uri("https://maven.kotori316.com")
        content {
            includeVersion("com.kotori316", "scalable-cats-force-fabric", libs.versions.slp.fabric.get())
        }
    }
}

dependencies {
    minecraft(libs.minecraft.v260102)
    implementation(libs.fabric.loader)
    implementation(libs.fabric.api.v260102)

    implementation(libs.bundles.scala)
    implementation(libs.bundles.cats)

    implementation(variantOf(libs.slp.fabric) {
        classifier("dev")
    })
}

loom {
    runs {
        create("gameTest") {
            displayName = "GameTest"
            server()
            jvmArguments.add("-ea")
            systemProperties.put("fabric-api.gametest", "")
            systemProperties.put("fabric-api.gametest.report-file", "run/test-results/test/game_test.xml")
        }
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
