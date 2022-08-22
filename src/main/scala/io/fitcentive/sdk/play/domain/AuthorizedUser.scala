package io.fitcentive.sdk.play.domain

import io.circe.Decoder.Result
import io.circe.{Decoder, HCursor}

import java.util.UUID

case class AuthorizedUser(
  username: String,
  userId: UUID,
  firstName: Option[String],
  lastName: Option[String],
  email: String
)

object AuthorizedUser {
  implicit val decoder: Decoder[AuthorizedUser] = new Decoder[AuthorizedUser] {
    final def apply(c: HCursor): Result[AuthorizedUser] =
      for {
        username <- c.downField("preferred_username").as[String]
        userId <- c.downField("user_id").as[UUID]
        firstName <- c.downField("given_name").as[Option[String]]
        lastName <- c.downField("family_name").as[Option[String]]
        email <- c.downField("email").as[String]
      } yield AuthorizedUser(username, userId, firstName, lastName, email)
  }
}
