package io.fitcentive.sdk.play.domain

import play.api.mvc.{Request, WrappedRequest}

case class NewSsoUserRequest[A](newSsoUser: AuthorizedUserWithoutId, request: Request[A])
  extends WrappedRequest[A](request)
