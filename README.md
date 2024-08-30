# Scalable Cat's Force Fabric

Fabric language module for [Scala](http://www.scala-lang.org/). Adds support for using a Scala `object` as the main mod
class and bundles the Scala libraries for you.

## Usage

Add it as a dependency:

```groovy
dependencies {
    implementation(group: 'org.scala-lang', name: 'scala-library', version: "2.13.14")
    // If you need scala3
    implementation(group: 'org.scala-lang', name: 'scala3-library_3', version: "3.5.0")
    // If you need cats. You also need to add repositories as below.
    implementation(group: "org.typelevel", name: "cats-core_3_", version: project.catsVersion)
    implementation(group: "org.typelevel", name: "cats-kernel_3", version: project.catsVersion)
    
    // If you write entrypoint in Scala. If entrypoint is Java, you can use default(fabric) entrypoint loader.
    modImplementation("com.kotori316:scalable-cats-force-fabric:3.0.0:dev")
}

// If you need cats.
repositories {
    maven {
        name = "Azure Kotori316"
        // See https://dev.azure.com/Kotori316/minecraft/_artifacts/feed/mods for versions
        url = uri("https://maven.kotori316.com")
        content {
            includeGroup("com.kotori316")
            includeVersion("org.typelevel", "cats-core_3", project.catsVersion)
            includeVersion("org.typelevel", "cats-kernel_3", project.catsVersion)
        }
    }
}
```

(If you want to write entrypoint in scala)
Specify your entrypoint in your `fabric.mod.json` like so:

```json
{
  "entrypoints": {
    "main": [
      {
        "adapter": "kotori_scala",
        "value": "package.ClassName"
      }
    ]
  }
}
```

Add a dependency entry to your `fabric.mod.json` file:

```json
{
  "requires": {
    "kotori_scala": ">=1.0.0"
  }
}
```
