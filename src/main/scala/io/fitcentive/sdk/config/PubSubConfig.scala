package io.fitcentive.sdk.config

trait PubSubConfig {
  def topicsConfig: PubSubTopicConfig
  def subscriptionsConfig: PubSubSubscriptionConfig
}
