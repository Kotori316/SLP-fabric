package com.kotori316.slp.example

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object CatsInitial extends ModInitializer:
  private val LOGGER = LoggerFactory.getLogger(getClass)

  override def onInitialize(): Unit =
    LOGGER.info("Cats works: {}", catsDemo)

  private def catsDemo: String =
    import cats.Id
    import cats.arrow.FunctionK
    import cats.data.NonEmptyList
    import cats.free.Free
    import cats.implicits.*

    val nel = NonEmptyList.of(1, 2, 3)
    val sum = nel.toList.combineAll
    val program = Free.pure[Id, Int](sum).map(_ + nel.head)
    val freeResult = program.foldMap(FunctionK.id[Id])
    s"NonEmptyList=${nel.toList.mkString(",")}, Monoid sum=$sum, Free result=$freeResult"
end CatsInitial
