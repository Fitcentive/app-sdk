package io.fitcentive.sdk.gcp.pubsub

import com.google.api.gax.core.{CredentialsProvider, FixedCredentialsProvider}
import com.google.api.gax.rpc.AlreadyExistsException
import com.google.auth.Credentials
import com.google.cloud.pubsub.v1._
import com.google.protobuf.Duration
import com.google.pubsub.v1.{PubsubMessage, SubscriptionName, TopicName}
import io.fitcentive.sdk.logging.AppLogger

import java.util.concurrent.TimeUnit
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util._
import scala.util.chaining._

class PubSubSubscriber(credentials: Credentials, project: String) extends AppLogger with AutoCloseable {
  private val ackDeadlineInSeconds = 10
  private val oneWeekInSeconds = 604800
  private val awaitTerminationInSeconds = 10
  private val minimumBackoffInSeconds = 1
  private val maximumBackoffInSeconds = 180
  private val environmentLabel = "env"

  private val subscribers: mutable.Map[String, com.google.cloud.pubsub.v1.Subscriber] = mutable.Map()

  def createSubscription(env: String, subscription: String, topic: String): Future[Unit] =
    SubscriptionAdminClient
      .create {
        SubscriptionAdminSettings
          .newBuilder()
          .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
          .build()
      }
      .pipe { client =>
        Future {
          client
            .tap(_.createSubscription {
              com.google.pubsub.v1.Subscription
                .newBuilder()
                .putLabels(environmentLabel, env)
                .setName(SubscriptionName.of(project, subscription).toString)
                .setTopic(TopicName.ofProjectTopicName(project, topic).toString)
                .setAckDeadlineSeconds(ackDeadlineInSeconds)
                .setMessageRetentionDuration(Duration.newBuilder().setSeconds(oneWeekInSeconds).build())
                .setExpirationPolicy(com.google.pubsub.v1.ExpirationPolicy.newBuilder().build())
                .setRetryPolicy {
                  com.google.pubsub.v1.RetryPolicy
                    .newBuilder()
                    .setMinimumBackoff(Duration.newBuilder().setSeconds(minimumBackoffInSeconds).build())
                    .setMaximumBackoff(Duration.newBuilder().setSeconds(maximumBackoffInSeconds).build())
                }
                .build()
            })
            .tap(_.shutdown())
            .awaitTermination(awaitTerminationInSeconds, TimeUnit.SECONDS)
        }
          .map(_ => ())
          .recoverWith {
            case _: AlreadyExistsException =>
              Future {
                client
                  .tap(_.shutdown())
                  .awaitTermination(awaitTerminationInSeconds, TimeUnit.SECONDS)
              }

            case ex =>
              Future {
                client
                  .tap(_.shutdown())
                  .awaitTermination(awaitTerminationInSeconds, TimeUnit.SECONDS)
              }.flatMap(_ => Future.failed(ex))
          }
      }

  def subscribe[T](env: String, subscription: String, topic: String)(
    fn: PubSubMessageEnvelope[T] => Future[_]
  )(implicit converter: PubSubMessageConverter[T]): Future[Unit] =
    _subscribe(env, subscription, topic)(fn, _ => true)

  def subscribeWithFilter[T](env: String, subscription: String, topic: String)(
    fn: PubSubMessageEnvelope[T] => Future[_],
    predicate: PubsubMessage => Boolean
  )(implicit converter: PubSubMessageConverter[T]): Future[Unit] =
    _subscribe(env, subscription, topic)(fn, predicate)

  private def _subscribe[T](env: String, subscription: String, topic: String)(
    fn: PubSubMessageEnvelope[T] => Future[_],
    predicate: PubsubMessage => Boolean
  )(implicit converter: PubSubMessageConverter[T]): Future[Unit] =
    for {
      _ <- createSubscription(env, subscription, topic)
      subscriber <- Future {
        logInfo(s"Creating subscriber: $subscription")
        com.google.cloud.pubsub.v1.Subscriber
          .newBuilder(SubscriptionName.of(project, subscription).toString, receiver(fn, predicate))
          .setCredentialsProvider {
            new CredentialsProvider {
              override def getCredentials: Credentials = credentials
            }
          }
          .build()
      }
      _ <- Future(subscriber.startAsync().awaitRunning())
        .recoverWith {
          case ex =>
            logError("Error starting subscriber!", ex)
            Future(subscriber.stopAsync().awaitTerminated()).flatMap(_ => Future.failed(ex))
        }
      _ <- Future(subscribers.update(subscription, subscriber))
    } yield ()

  private def receiver[T](fn: PubSubMessageEnvelope[T] => Future[_], predicate: PubsubMessage => Boolean)(implicit
    converter: PubSubMessageConverter[T]
  ): MessageReceiver = {
    case (message, consumer) if predicate(message) =>
      Try(converter(message))
        .pipe(_.fold(Future.failed, Future.successful))
        .pipe(_.flatMap(fn))
        .map(_ => consumer.ack())
        .recover {
          case ex =>
            logError("Error processing PubSubMessage!", ex)
            consumer.nack()
            ()
        }

    case (_, consumer) =>
      logWarning(s"Filtered PubSubMessage!")
      consumer.ack()
  }

  override def close(): Unit =
    subscribers.values.foreach(_.stopAsync().awaitTerminated())
}
