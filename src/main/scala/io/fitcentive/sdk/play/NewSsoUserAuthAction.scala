package io.fitcentive.sdk.play

import io.fitcentive.sdk.domain.TokenValidationService
import io.fitcentive.sdk.play.domain.{AuthorizedUser, AuthorizedUserWithoutId, NewSsoUserRequest, UserRequest}
import play.api.http.HeaderNames
import play.api.mvc.{ActionBuilder, AnyContent, BodyParsers, Request, Result, Results}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
  *  When a new user logs in for the first time via mobile app, due to the recommended authentication client flow,
  *  the redirectUri is something local, instead of the http(s) redirect
  *  When that happens, we get an access_token
  */
class NewSsoUserAuthAction @Inject() (val parser: BodyParsers.Default, tokenValidationService: TokenValidationService)(
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[NewSsoUserRequest, AnyContent] {

  private val headerTokenRegex = """Bearer (.+?)""".r

  override def invokeBlock[A](request: Request[A], block: NewSsoUserRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request) map { token =>
      tokenValidationService.validateJwt[AuthorizedUserWithoutId](token) match {
        case Right(authorizedUser) => block(domain.NewSsoUserRequest(authorizedUser, request))
        case Left(error)           => Future.successful(Results.Unauthorized(error.reason))
      }
    } getOrElse Future.successful(Results.Unauthorized)

  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }
}
