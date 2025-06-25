package com.kotori316.slp.example

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.{Logger, LoggerFactory}

import scala.jdk.CollectionConverters.*
import scala.util.Properties

object ScalaInitial3 extends ModInitializer:
  private val LOGGER: Logger = LoggerFactory.getLogger(getClass)

  override def onInitialize(): Unit =
    LOGGER.info("Hello from {} with Scala {} at {}",
      getClass,
      Properties.versionString,
      FabricLoader.getInstance().getGameDir
    )
    val loadedMods = FabricLoader.getInstance().getAllMods.asScala
      .map(_.getMetadata.getId)
      .mkString(", ")
    LOGGER.info("Loaded mods: {}", loadedMods)
  end onInitialize