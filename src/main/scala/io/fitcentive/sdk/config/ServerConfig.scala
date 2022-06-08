package io.fitcentive.sdk.config

import com.typesafe.config.Config

case class ServerConfig(host: String, port: String) {
  val serverUrl: String = s"$host:$port"
}

object ServerConfig {
  def fromConfig(config: Config): ServerConfig =
    ServerConfig(host = config.getString("host"), port = config.getString("port"))
}
