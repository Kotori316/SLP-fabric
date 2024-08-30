package com.kotori316.slp.example

import net.fabricmc.api.ModInitializer
import org.slf4j.{Logger, LoggerFactory}

class ScalaInitial2 extends ModInitializer {
  override def onInitialize(): Unit = {
    ScalaInitial2.LOGGER.info("Hello from {}", getClass.getName)
  }
}

object ScalaInitial2 {
  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[ScalaInitial2])
}
