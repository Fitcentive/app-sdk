package io.fitcentive.sdk.error

import java.util.UUID

case class EntityNotAccessible(reason: String) extends DomainError {
  override def code: UUID = UUID.fromString("87d733e8-40e5-4551-8055-09b9dac8b912")
}
