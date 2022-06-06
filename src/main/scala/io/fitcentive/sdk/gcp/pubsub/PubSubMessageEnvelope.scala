package io.fitcentive.sdk.gcp.pubsub

import io.circe.Codec
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.util.UUID

case class PubSubMessageEnvelope[T](topic: String, payload: T, id: UUID = UUID.randomUUID)

object PubSubMessageEnvelope {
  implicit def codec[T](implicit payloadCodec: Codec[T]): Codec[PubSubMessageEnvelope[T]] =
    Codec.from(deriveDecoder[PubSubMessageEnvelope[T]], deriveEncoder[PubSubMessageEnvelope[T]])
}
