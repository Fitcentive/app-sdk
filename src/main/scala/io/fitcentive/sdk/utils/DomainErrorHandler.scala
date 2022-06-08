package io.fitcentive.sdk.utils

import io.fitcentive.sdk.error.DomainError
import play.api.mvc.Result

trait DomainErrorHandler {
  def resultErrorAsyncHandler: PartialFunction[Throwable, Result]
  def domainErrorHandler: PartialFunction[DomainError, Result]
}
