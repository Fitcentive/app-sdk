package io.fitcentive.sdk.utils

import io.fitcentive.sdk.error.DomainError
import io.fitcentive.sdk.play.domain.UserRequest
import play.api.libs.json.{JsError, JsSuccess, JsValue, Reads}
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, Forbidden}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait PlayControllerOps {

  self: DomainErrorHandler =>

  def handleEitherResult[DomainResult](
    result: Either[DomainError, DomainResult]
  )(ifSuccess: DomainResult => Result)(implicit ec: ExecutionContext): Result = {
    result match {
      case Left(error)  => domainErrorHandler(error)
      case Right(value) => ifSuccess(value)
    }
  }

  def validateJson[A](
    jsonOpt: Option[JsValue]
  )(block: A => Future[Result])(implicit reads: Reads[A]): Future[Result] = {
    jsonOpt.fold(Future.successful(BadRequest("Required JSON body not found"))) { json =>
      json.validate[A] match {
        case value: JsSuccess[A] => block(value.get)
        case error: JsError      => Future.successful(BadRequest(s"Failed to validate JSON, error: $error"))
      }
    }
  }

  def rejectIfNotEntitled[A](block: => Future[Result])(implicit request: UserRequest[A], userId: UUID): Future[Result] =
    request.authorizedUser.userId match {
      case `userId` => block
      case _        => Future.successful(Forbidden("Not allowed!"))
    }
}
