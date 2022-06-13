package io.fitcentive.sdk.infrastructure

import io.fitcentive.sdk.domain.PublicKeyRepository

import java.security.PublicKey
import scala.collection.mutable

class CachedKeycloakPublicKeyRepository(underlying: PublicKeyRepository) extends PublicKeyRepository {

  type AuthRealm = String

  private val cache: mutable.Map[(AuthRealm, String), PublicKey] = mutable.Map.empty

  override def get(realm: AuthRealm, kid: String): Option[PublicKey] =
    cache.get(realm, kid) match {
      case None =>
        underlying.get(realm, kid) match {
          case None => None
          case Some(publicKey) =>
            cache.update((realm, kid), publicKey)
            Some(publicKey)
        }
      case Some(publicKey) =>
        Some(publicKey)
    }

}
