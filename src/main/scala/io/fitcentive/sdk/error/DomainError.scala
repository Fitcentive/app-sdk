package io.fitcentive.sdk.error

import java.util.UUID

trait DomainError {
  def code: UUID
  def reason: String
}
