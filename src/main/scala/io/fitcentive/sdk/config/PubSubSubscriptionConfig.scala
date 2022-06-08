package io.fitcentive.sdk.config

trait PubSubSubscriptionConfig {
  def subscriptions: Seq[String]
}
