package com.kotori316.slp.example

import net.fabricmc.api.ModInitializer
import org.slf4j.{Logger, LoggerFactory}

class ScalaInitial extends ModInitializer {
  override def onInitialize(): Unit = {
    ScalaInitial.LOGGER.info("Hello from {}", getClass.getName)
  }
}

object ScalaInitial {
  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[ScalaInitial])
}
