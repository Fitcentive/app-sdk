package io.fitcentive.sdk.error

import java.util.UUID

case class EntityConflictError(reason: String) extends DomainError {
  override def code: UUID = UUID.fromString("c68d9c79-1df9-46e2-9b92-16e930c68ecd")
}
