package io.fitcentive.sdk.gcp.pubsub

import com.google.api.gax.core.{CredentialsProvider, FixedCredentialsProvider}
import com.google.api.gax.rpc.AlreadyExistsException
import com.google.auth.Credentials
import com.google.cloud.pubsub.v1.{Publisher, TopicAdminClient, TopicAdminSettings}
import com.google.protobuf.ByteString
import com.google.pubsub.v1.{PubsubMessage, TopicName}
import io.circe.Encoder
import io.fitcentive.sdk.logging.AppLogger
import io.fitcentive.sdk.utils.JavaOps

import java.util.concurrent.TimeUnit
import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.chaining._

import io.circe.syntax._
import io.circe.generic.auto._

class PubSubPublisher(credentials: Credentials, project: String) extends AppLogger with JavaOps with AutoCloseable {
  private val ackDeadlineInSeconds = 10
  private val publishers: mutable.Map[String, Publisher] = mutable.Map()

  def createTopic(topic: String): Future[Unit] =
    TopicAdminClient
      .create {
        TopicAdminSettings
          .newBuilder()
          .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
          .build()
      }
      .pipe { client =>
        Future {
          logInfo(s"Creating topic: $topic")
          client
            .tap(_.createTopic(TopicName.ofProjectTopicName(project, topic).toString))
            .tap(_.shutdown())
            .awaitTermination(ackDeadlineInSeconds, TimeUnit.SECONDS)
        }
          .map(_ => ())
          .recoverWith {
            case _: AlreadyExistsException =>
              Future {
                client
                  .tap(_.shutdown())
                  .awaitTermination(ackDeadlineInSeconds, TimeUnit.SECONDS)
              }

            case ex =>
              Future {
                client
                  .tap(_.shutdown())
                  .awaitTermination(ackDeadlineInSeconds, TimeUnit.SECONDS)
              }.flatMap(_ => Future.failed(ex))
          }
      }

  def publish[T](topic: String, payload: T)(implicit encoder: Encoder[T]): Future[Unit] =
    publish(PubSubMessageEnvelope(topic, payload))

  def publish[T](env: PubSubMessageEnvelope[T])(implicit encoder: Encoder[T]): Future[Unit] =
    publishers
      .get(env.topic)
      .map(Future.successful)
      .getOrElse {
        for {
          _ <- createTopic(env.topic)
          publisher <- Future {
            logInfo(s"Creating publisher: ${env.topic}")
            com.google.cloud.pubsub.v1.Publisher
              .newBuilder(TopicName.ofProjectTopicName(project, env.topic).toString)
              .setCredentialsProvider {
                new CredentialsProvider {
                  override def getCredentials: Credentials = credentials
                }
              }
              .build
          }
          _ <- Future(publishers.update(env.topic, publisher))
        } yield publisher
      }
      .map { publisher =>
        logInfo(s"Attempting to publish message to PubSub topic ${env.topic}: ${env.payload}")
        publisher
          .publish(PubsubMessage.newBuilder.setData(ByteString.copyFromUtf8(env.asJson.toString)).build)
          .asScala
          .map(_ => ())
      }

  override def close(): Unit =
    publishers.values.foreach(_.tap(_.shutdown()).awaitTermination(ackDeadlineInSeconds, TimeUnit.SECONDS))
}
