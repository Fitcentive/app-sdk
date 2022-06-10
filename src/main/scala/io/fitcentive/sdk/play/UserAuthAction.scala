package io.fitcentive.sdk.play

import io.fitcentive.sdk.domain.TokenValidationService
import io.fitcentive.sdk.play.domain.{AuthorizedUser, UserRequest}
import play.api.http.HeaderNames
import play.api.mvc.{ActionBuilder, AnyContent, BodyParsers, Request, Result, Results}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UserAuthAction @Inject() (val parser: BodyParsers.Default, tokenValidationService: TokenValidationService)(
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[UserRequest, AnyContent] {

  private val headerTokenRegex = """Bearer (.+?)""".r

  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] =
    extractBearerToken(request) map { token =>
      tokenValidationService.validateJwt[AuthorizedUser](token) match {
        case Right(authorizedUser) => block(domain.UserRequest(authorizedUser, request))
        case Left(error)           => Future.successful(Results.Unauthorized(error.reason))
      }
    } getOrElse Future.successful(Results.Unauthorized)

  private def extractBearerToken[A](request: Request[A]): Option[String] =
    request.headers.get(HeaderNames.AUTHORIZATION) collect {
      case headerTokenRegex(token) => token
    }
}
