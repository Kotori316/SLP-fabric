package com.kotori316.slp.example

import cats.data.{Chain, Writer}
import net.fabricmc.api.ModInitializer
import org.slf4j.{Logger, LoggerFactory}

object Initial extends ModInitializer {
  private val LOGGER: Logger = LoggerFactory.getLogger(getClass)

  override def onInitialize(): Unit = {
    val w = writer(4)
      .ap(plus(2))
      .ap(plus(5))
    locally {
      val (log, result) = w.run
      LOGGER.info("Result1: {}, Log: {}", Int.box(result), log)
    }

    val w2 = for {
      i1 <- writer(5)
      i2 <- writer(3)
      i3 <- writer(8)
    } yield i1 + i2 + i3
    locally {
      val (log, result) = w2.run
      LOGGER.info("Result2: {}, Log: {}", Int.box(result), log)
    }
  }

  private def writer(i: Int): Writer[Chain[Int], Int] = {
    Writer(Chain(i), i)
  }

  private def plus(i: Int): Writer[Chain[Int], Int => Int] = {
    Writer(Chain(i), _ + i)
  }
}
