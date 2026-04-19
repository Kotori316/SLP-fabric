import com.kotori316.plugin.cf.CallVersionCheckFunctionTask
import com.kotori316.plugin.cf.CallVersionFunctionTask
import me.modmuss50.mpp.ReleaseType

plugins {
    scala
    `maven-publish`
    alias(libs.plugins.loom)
    alias(libs.plugins.publish.all)
    alias(libs.plugins.cf)
}

val archivesBaseName: String by project
val modVersion: String by project
val mavenGroup: String by project
val urlOfGitHub: String by project
val branch: String by project
val mcStartVersion: String by project

base {
    archivesName.set(archivesBaseName)
}
version = modVersion
group = mavenGroup

repositories {
    maven {
        name = "Main"
        url = uri("https://maven.kotori316.com")
        content {
            includeVersion("org.typelevel", "cats-core_3", libs.versions.cats.get())
            includeVersion("org.typelevel", "cats-kernel_3", libs.versions.cats.get())
            includeVersion("org.typelevel", "cats-free_3", libs.versions.cats.get())
        }
    }
}

dependencies {
    minecraft(libs.minecraft.main)
    implementation(libs.fabric.loader)

    api(libs.scala3)
    include(libs.scala3)
    api(libs.scala2)
    include(libs.scala2)
    api(libs.cats.core)
    include(libs.cats.core)
    api(libs.cats.kernel)
    include(libs.cats.kernel)
    api(libs.cats.free)
    include(libs.cats.free)
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to project.version))
    }
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${archivesBaseName}" }
    }
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "9.4.1"
}

tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}

tasks.named("remapJar") {
    mustRunAfter(tasks.named("compileTestScala"))
}

val releaseDebug = (System.getenv("RELEASE_DEBUG") ?: "true").toBoolean()

// configure the maven publication
publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = archivesBaseName
            pom {
                name = archivesBaseName
                description = "SLP for Minecraft with Fabric"
                url = urlOfGitHub
                packaging = "jar"
                withXml {
                    val notRequiredArtifacts = listOf("fabric-loader", "fabric-api")
                    val pomNode = asNode()
                    val dependenciesNodeList = pomNode.get("dependencies") as? groovy.util.NodeList
                    if (!dependenciesNodeList.isNullOrEmpty()) {
                        val dependenciesNode = dependenciesNodeList[0] as groovy.util.Node
                        val children = dependenciesNode.children().toMutableList()
                        children.forEach { dep ->
                            val node = dep as groovy.util.Node
                            val artifactIdNodeList = node.get("artifactId") as? groovy.util.NodeList
                            if (!artifactIdNodeList.isNullOrEmpty()) {
                                val artifactId = artifactIdNodeList[0].toString()
                                if (notRequiredArtifacts.any { artifactId.contains(it) }) {
                                    dependenciesNode.remove(node)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        val u = project.findProperty("maven_username") as String? ?: System.getenv("MAVEN_USERNAME") ?: ""
        val p = project.findProperty("maven_password") as String? ?: System.getenv("MAVEN_PASSWORD") ?: ""
        if (u != "" && p != "" && !releaseDebug) {
            maven {
                name = "kotori316-maven"
                // For users: Use https://maven.kotori316.com to get artifacts
                url = uri("https://maven2.kotori316.com/production/maven")
                credentials {
                    username = u
                    password = p
                }
            }
        }
        if (System.getenv("CI") == null) {
            maven {
                name = "MavenTestGCP"
                url = uri("gcs://kotori316-maven-test-storage/maven/")
            }
        }
    }
}

fun createChangelog(): String {
    return """
## Scalable Cat's Force Fabric

Provides scala language adapter to Fabric.

### Build dependencies:

- Scala ${libs.versions.scala2.get()}
- Scala3 ${libs.versions.scala3.get()}
- Cats Kernel ${libs.versions.cats.get()}
- Cats Core ${libs.versions.cats.get()}
- Cats Free ${libs.versions.cats.get()}
- Fabric Loader ${libs.versions.fabric.loader.get()}

### Repositry

${urlOfGitHub}/tree/${branch}
""".trimIndent()
}

publishMods {
    dryRun = releaseDebug
    type = ReleaseType.STABLE
    file = tasks.jar.flatMap { it.archiveFile }
    additionalFiles.from(
        tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile },
    )
    modLoaders = listOf("fabric")
    displayName = "${project.version}-fabric"
    changelog = createChangelog()

    curseforge {
        accessToken = provider { (project.findProperty("curseforge_additional-enchanted-miner_key") ?: System.getenv("CURSE_TOKEN") ?: "") as String }
        projectId = "320926"
        minecraftVersionRange {
            start = mcStartVersion
            end = "latest"
        }
    }
    modrinth {
        accessToken = provider { (project.findProperty("modrinthToken") ?: System.getenv("MODRINTH_TOKEN") ?: "") as String }
        projectId = "zr0QMQMo"
        minecraftVersionRange {
            start = mcStartVersion
            end = "latest"
            includeSnapshots = false
        }
    }
    github {
        accessToken = provider { (project.findProperty("githubToken") ?: System.getenv("REPO_TOKEN") ?: "") as String }
        repository = "Kotori316/SLP-fabric"
        commitish = branch
        tagName = "v${project.version}"
        displayName = "v${project.version}"
    }
}

tasks.register("registerVersion", CallVersionFunctionTask::class) {
    functionEndpoint = CallVersionFunctionTask.readVersionFunctionEndpoint(project)
    gameVersion = libs.versions.minecraft.main.get()
    platform = "fabric"
    platformVersion = libs.versions.fabric.loader.get()
    modName = "ScalableCatsForce-Fabric"
    changelog = createChangelog()
    homepage = "https://modrinth.com/mod/scalable-cats-force"
    isDryRun = releaseDebug
}

tasks.register("checkReleaseVersion", CallVersionCheckFunctionTask::class) {
    gameVersion = libs.versions.minecraft.main.get()
    platform = "fabric"
    modName = "ScalableCatsForce-Fabric"
    version = modVersion
    failIfExists = !releaseDebug
}
