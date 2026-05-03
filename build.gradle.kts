import com.kotori316.plugin.cf.CallVersionCheckFunctionTask
import com.kotori316.plugin.cf.CallVersionFunctionTask
import me.modmuss50.mpp.ReleaseType

plugins {
    scala
    `maven-publish`
    alias(libs.plugins.loom)
    alias(libs.plugins.publish.all)
    alias(libs.plugins.cf)
    // alias(libs.plugins.shadow)
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

    api(libs.scala)
    include(libs.scala)
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

tasks.named<Jar>("sourcesJar") {
    manifest {
        attributes("Implementation-Version" to project.version)
    }
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${archivesBaseName}" }
    }
    archiveClassifier = ""
}

val devJar = tasks.register<Jar>("devJar") {
    description = "Generate a development jar without JarJar embedding"
    archiveClassifier = "dev"
    from(sourceSets.main.get().output)
    from("LICENSE") {
        rename { "${it}_${archivesBaseName}" }
    }
    manifest {
        attributes("Implementation-Version" to project.version)
    }
}

/*tasks.shadowJar {
    dependencies {
        include(dependency("org.scala-lang:scala-library"))
        include(dependency("org.scala-lang:scala3-library_3"))
        include(dependency("org.typelevel:cats-core_3"))
        include(dependency("org.typelevel:cats-kernel_3"))
        include(dependency("org.typelevel:cats-free_3"))
    }
    archiveClassifier = "shadow"
}*/

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "9.5.0"
}

tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}

val releaseDebug = (System.getenv("RELEASE_DEBUG") ?: "true").toBoolean()

// configure the maven publication
publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(devJar)
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

- Scala ${libs.versions.scala.get()}
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
        accessToken = provider {
            (project.findProperty("curseforge_additional-enchanted-miner_key") ?: System.getenv("CURSE_TOKEN")
            ?: "") as String
        }
        projectId = "320926"
        minecraftVersionRange {
            start = mcStartVersion
            end = "latest"
        }
    }
    modrinth {
        accessToken =
            provider { (project.findProperty("modrinthToken") ?: System.getenv("MODRINTH_TOKEN") ?: "") as String }
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

tasks.register("checkBinaryContent") {
    group = "verification"
    description = "Checks if the built JAR contains Scala classes (Shadow fat JAR) or nested Scala JARs (JarInJar)."
    dependsOn(tasks.jar)
    tasks.findByName("shadowJar")?.let { dependsOn(it) }

    doLast {
        var anyCheckPassed = false

        // Shadow fat JAR: scala classes are embedded directly at root
        val shadowJarFile = (tasks.findByName("shadowJar") as? AbstractArchiveTask)
            ?.archiveFile?.get()?.asFile
        if (shadowJarFile == null) {
            println("Shadow JAR: shadowJar task not found, skipping.")
        } else if (!shadowJarFile.exists()) {
            println("Shadow JAR: ${shadowJarFile.name} does not exist, skipping.")
        } else {
            var count = 0
            project.zipTree(shadowJarFile).visit {
                val path = relativePath.pathString
                if (!isDirectory && path.startsWith("scala/") && (path.endsWith(".class") || path.endsWith(".tasty"))) {
                    count++
                }
            }
            if (count in 1..4) {
                throw GradleException("Shadow JAR ${shadowJarFile.name} has too few scala files: $count (expected at least 5)")
            }
            if (count >= 5) {
                println("Verified ${shadowJarFile.name} (Shadow): Found $count scala files.")
                anyCheckPassed = true
            } else {
                println("Shadow JAR ${shadowJarFile.name}: no scala/ classes found, skipping.")
            }
        }

        // JarInJar: nested scala*.jar files under META-INF/jars/
        val jarFile = tasks.jar.get().archiveFile.get().asFile
        if (jarFile.exists()) {
            val scalaJars = project.zipTree(jarFile).matching {
                include("META-INF/jars/scala*.jar")
            }.files

            if (scalaJars.isEmpty()) {
                println("JarInJar: no scala*.jar found in META-INF/jars/ of ${jarFile.name}, skipping.")
            } else {
                scalaJars.forEach { scalaJar ->
                    var count = 0
                    project.zipTree(scalaJar).visit {
                        val path = relativePath.pathString
                        if (!isDirectory && path.startsWith("scala/") && (path.endsWith(".class") || path.endsWith(".tasty"))) {
                            count++
                        }
                    }
                    if (count < 5) {
                        throw GradleException("Error: 'scala' directory not found or has too few .class/.tasty files in ${scalaJar.name} (Found $count, expected at least 5)")
                    }
                    println("Verified ${scalaJar.name} (JarInJar): Found $count files in 'scala/' directory.")
                }
                anyCheckPassed = true
            }
        }

        if (!anyCheckPassed) {
            throw GradleException("No Scala content found: Shadow JAR has no scala/ classes and no scala*.jar in META-INF/jars/")
        }
        println("All binary checks passed successfully!")
    }
}
