package io.fitcentive.sdk.utils

import java.util.concurrent.CompletableFuture
import scala.compat.java8.FutureConverters

import scala.concurrent.Future
import scala.util.Try

trait JavaOps {

  implicit class JavaFuture2ScalaFuture[T](javaFuture: java.util.concurrent.Future[T]) {
    def asScala: Future[T] =
      Try {
        FutureConverters.toScala(CompletableFuture.supplyAsync(() => javaFuture.get))
      }.fold(Future.failed, identity)
  }
}
``