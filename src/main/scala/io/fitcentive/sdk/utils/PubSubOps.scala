package io.fitcentive.sdk.utils

import com.google.pubsub.v1.PubsubMessage
import io.circe.Codec
import io.circe.parser.parse
import io.fitcentive.sdk.error.DomainError
import io.fitcentive.sdk.gcp.pubsub.PubSubMessageEnvelope
import io.fitcentive.sdk.utils.PubSubOps.PubSubDecodingError

import java.util.UUID

trait PubSubOps {
  implicit class PubSubMessage2In(value: PubsubMessage) {
    def decode[T](implicit codec: Codec[T]): Either[PubSubDecodingError, PubSubMessageEnvelope[T]] =
      parse(value.getData.toStringUtf8)
        .flatMap(_.as[PubSubMessageEnvelope[T]](PubSubMessageEnvelope.codec(codec)))
        .left
        .map(e => PubSubDecodingError(e.getMessage))

    def decodeUnsafe[T](implicit codec: Codec[T]): PubSubMessageEnvelope[T] =
      decode[T].fold(err => throw new Exception(err.reason), identity)
  }
}

object PubSubOps {
  case class PubSubDecodingError(reason: String) extends DomainError {
    override def code: UUID = UUID.fromString("c3c48f5a-1ac8-412f-8f3c-73cf6a2a82eb")
  }
}
