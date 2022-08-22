package io.fitcentive.sdk.play.domain

import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}

case class AuthorizedUserWithoutId(username: String, firstName: Option[String], lastName: Option[String], email: String)

object AuthorizedUserWithoutId {
  implicit val decoder: Decoder[AuthorizedUserWithoutId] = new Decoder[AuthorizedUserWithoutId] {
    final def apply(c: HCursor): Result[AuthorizedUserWithoutId] =
      for {
        username <- c.downField("preferred_username").as[String]
        firstName <- c.downField("given_name").as[Option[String]]
        lastName <- c.downField("family_name").as[Option[String]]
        email <- c.downField("email").as[String]
      } yield AuthorizedUserWithoutId(username, firstName, lastName, email)
  }
}
