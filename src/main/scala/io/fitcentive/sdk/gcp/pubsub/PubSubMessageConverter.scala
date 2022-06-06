package io.fitcentive.sdk.gcp.pubsub

import com.google.pubsub.v1.PubsubMessage

trait PubSubMessageConverter[T] {
  def apply(message: PubsubMessage): PubSubMessageEnvelope[T]
}
