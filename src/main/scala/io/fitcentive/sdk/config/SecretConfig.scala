package io.fitcentive.sdk.config

import com.typesafe.config.Config

case class SecretConfig(serviceSecret: String)

object SecretConfig {
  def fromConfig(config: Config): SecretConfig =
    SecretConfig(serviceSecret = config.getString("server.internal-service-secret"))
}
