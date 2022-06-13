package io.fitcentive.sdk.config

import com.typesafe.config.Config

case class JwtConfig(issuer: String)

object JwtConfig {
  def apply(config: Config): JwtConfig =
    JwtConfig(issuer = config.getString("issuer"))
}
