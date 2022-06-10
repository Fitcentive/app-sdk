package io.fitcentive.sdk.play

import io.fitcentive.sdk.domain.TokenValidationService
import play.api.mvc.{ActionBuilder, AnyContent, BodyParsers, Request, Result, Results}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class InternalAuthAction @Inject() (val parser: BodyParsers.Default, tokenValidationService: TokenValidationService)(
  implicit val executionContext: ExecutionContext
) extends ActionBuilder[Request, AnyContent] {

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] =
    extractServiceSecret(request) map { token =>
      tokenValidationService.validateServiceSecret(token) match {
        case true  => block(request)
        case false => Future.successful(Results.Unauthorized("Bad service secret"))
      }
    } getOrElse Future.successful(Results.Unauthorized)

  private def extractServiceSecret[A](request: Request[A]): Option[String] =
    request.headers.get("Service-Secret")
}
