package io.fitcentive.sdk.domain

import java.security.PublicKey

trait PublicKeyRepository {
  def get(realm: String, kid: String): Option[PublicKey]
}
