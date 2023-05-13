# Scalable Cat's Force Fabric

Fabric language module for [Scala](http://www.scala-lang.org/). Adds support for using a Scala `object` as the main mod
class and bundles the Scala libraries for you.

## Usage

Add it as a dependency:

```groovy
dependencies {
    implementation(group: 'org.scala-lang', name: 'scala-library', version: "2.13.10")
    // If you need scala3
    implementation(group: 'org.scala-lang', name: 'scala3-library_3', version: "3.2.2")
    // If you need cats. You also need to add repositories as below.
    implementation(group: "org.typelevel", name: "cats-core_2.13", version: project.catsVersion)
    implementation(group: "org.typelevel", name: "cats-kernel_2.13", version: project.catsVersion)
    
    modImplementation("com.kotori316:scalable-cats-force-fabric:1.0.0:dev")
}

// If you need cats.
repositories {
    maven {
        name = "Azure Kotori316"
        // See https://dev.azure.com/Kotori316/minecraft/_artifacts/feed/mods for versions
        url = uri("https://pkgs.dev.azure.com/Kotori316/minecraft/_packaging/mods/maven/v1")
        content {
            it.includeVersion("org.typelevel", "cats-core_2.13", project.catsVersion)
            it.includeVersion("org.typelevel", "cats-kernel_2.13", project.catsVersion)
        }
    }
}
```

Specify your entrypoint in your `fabric.mod.json` like so:

```json
{
  "entrypoints": {
    "main": [
      {
        "adapter": "scala",
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
    "fabric-language-scala": "*"
  }
}
```