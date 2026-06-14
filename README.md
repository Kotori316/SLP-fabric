# Scalable Cat's Force Fabric

Fabric language module for [Scala](http://www.scala-lang.org/). Adds support for using a Scala `object` as the main mod
class and bundles the Scala libraries for you.

## Usage

Add it as a dependency:

```groovy
dependencies {
    // Scala (no need to add Scala 2 dependency since 3.8.3)
    implementation('org.scala-lang:scala3-library_3:3.8.4')
    // (Optional) If you need cats — available from Maven Central.
    implementation('org.typelevel:cats-core_3:2.13.0')
    implementation('org.typelevel:cats-kernel_3:2.13.0')

    // If you write entrypoint in Scala. If entrypoint is Java, you can use the default (fabric) entrypoint loader.
    implementation("com.kotori316:scalable-cats-force-fabric:5.0.0:dev")
}

repositories {
    mavenCentral()
    maven {
        name = "Kotori316"
        // See https://maven.kotori316.com for versions
        url = uri("https://maven.kotori316.com")
        content {
            includeModule("com.kotori316", "scalable-cats-force-fabric")
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
    "kotori_scala": ">=5.0.0"
  }
}
```

## API

* [Scala](https://www.scala-lang.org/) — [GitHub](https://github.com/scala/scala) — licensed under the
  [Apache License, Version 2.0](https://www.scala-lang.org/license/).
* [Cats](https://typelevel.org/cats/) — [GitHub](https://github.com/typelevel/cats) — licensed under the
  [License](https://github.com/typelevel/cats/blob/master/COPYING).
  * SLP bundles the official Cats jars from Maven Central as-is. All packages, including the
    Java-reserved-word packages such as `cats.kernel.instances.int`, are available at runtime.

## Limitations

1. Avoid use of `Mod.EventBusSubscriber` in Java code. This will cause an exception in the `compileScala` task.

  * Use in Scala code will not throw an exception.

2. If you get the compile error "ambiguous reference to overloaded definition", specify the return type.

  * For example, `val offsetPos = pos.relative(direction)` can cause this error when `relative` is declared
    in both `BlockPos` and `Vec3i`. Specify the return type: `val offsetPos: BlockPos = pos.relative(direction)`
