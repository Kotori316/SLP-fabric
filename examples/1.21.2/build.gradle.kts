plugins {
    scala
    alias(libs.plugins.loom)
}

base {
    archivesName = "slp-example"
}
version = "1.0"
group = "com.kotori316"

repositories {
    mavenLocal()
    maven {
        name = "Main"
        url = uri("https://maven.kotori316.com")
        content {
            includeVersion("com.kotori316", "scalable-cats-force-fabric", libs.versions.slp.fabric.get())
            includeVersion("org.typelevel", "cats-core_3", libs.versions.cats.get())
            includeVersion("org.typelevel", "cats-kernel_3", libs.versions.cats.get())
            includeVersion("org.typelevel", "cats-free_3", libs.versions.cats.get())
        }
    }
}

dependencies {
    minecraft(libs.minecraft.v1212)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api.v1212)

    implementation(libs.bundles.scala)
    implementation(libs.bundles.cats)

    modImplementation(variantOf(libs.slp.fabric) {
        classifier("dev")
    })
}

loom {
    runs {
        create("gameTest") {
            name = "GameTest"
            server()
            vmArg("-ea")
            property("fabric-api.gametest")
            property("fabric-api.gametest.report-file", "run/test-results/test/game_test.xml")
        }
    }
}
