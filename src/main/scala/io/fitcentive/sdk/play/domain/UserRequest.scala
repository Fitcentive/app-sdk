package io.fitcentive.sdk.play.domain

import play.api.mvc.{Request, WrappedRequest}

case class UserRequest[A](authorizedUser: AuthorizedUser, request: Request[A]) extends WrappedRequest[A](request)
