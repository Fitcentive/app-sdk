package io.fitcentive.sdk.utils

import java.util.concurrent.CompletableFuture
import scala.concurrent.Future
import scala.util.Try

trait JavaOps {

  implicit class JavaFuture2ScalaFuture[T](javaFuture: java.util.concurrent.Future[T]) {
    def asScala: Future[T] =
      Try {
        CompletableFuture.supplyAsync(() => javaFuture.get).asScala
      }.fold(Future.failed, identity)
  }
}
