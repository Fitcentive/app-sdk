package io.fitcentive.sdk.domain

import io.circe.Decoder
import io.fitcentive.sdk.error.JwtValidationError

trait TokenValidationService {
  def validateJwt[T](
    token: String,
    subject: Option[String] = Option.empty,
    audience: Option[Set[String]] = Option.empty
  )(implicit decoder: Decoder[T]): Either[JwtValidationError, T]

  def validateServiceSecret(token: String): Boolean
}
