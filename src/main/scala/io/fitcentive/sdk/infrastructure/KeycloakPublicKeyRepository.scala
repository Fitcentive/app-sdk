package io.fitcentive.sdk.infrastructure

import io.circe.Json
import io.fitcentive.sdk.domain.PublicKeyRepository
import org.keycloak.adapters.KeycloakDeploymentBuilder

import java.io.ByteArrayInputStream
import java.security.PublicKey

// todo - cache this in memory somehow
class KeycloakPublicKeyRepository(keycloakServerUrl: String) extends PublicKeyRepository {

  def get(realm: String, kid: String): Option[PublicKey] = {
    val jsonConfig = adapterConfiguration(realm, keycloakServerUrl)
    val is = new ByteArrayInputStream(jsonConfig.noSpaces.getBytes)
    val deployment = KeycloakDeploymentBuilder.build(is)

    val locator = deployment.getPublicKeyLocator
    val publicKey = locator.getPublicKey(kid, deployment)
    Option(publicKey)
  }

  private def adapterConfiguration(realm: String, serverUrl: String): Json =
    Json.obj(
      "auth-server-url" -> Json.fromString(serverUrl),
      "realm" -> Json.fromString(realm),
      "resource" -> Json.fromString("ignored"),
    )
}
