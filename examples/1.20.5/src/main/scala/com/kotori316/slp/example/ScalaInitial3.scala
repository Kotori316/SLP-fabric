package com.kotori316.slp.example

import net.fabricmc.api.ModInitializer
import org.slf4j.{Logger, LoggerFactory}

import scala.util.Properties

object ScalaInitial3 extends ModInitializer:
  private val LOGGER: Logger = LoggerFactory.getLogger(getClass)

  override def onInitialize(): Unit =
    LOGGER.info("Hello from {} with Scala {}",
      getClass,
      Properties.versionString)
