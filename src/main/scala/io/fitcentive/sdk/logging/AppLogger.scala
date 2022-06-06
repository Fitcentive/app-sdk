package io.fitcentive.sdk.logging

import com.typesafe.scalalogging.StrictLogging

trait AppLogger extends StrictLogging {
  def logDebug(msg: String): Unit =
    logger.debug(msg)

  def logInfo(msg: String): Unit =
    logger.info(msg)

  def logWarning(msg: String): Unit =
    logger.warn(msg)

  def logWarning(msg: String, cause: Throwable): Unit =
    logger.warn(msg, cause)

  def logError(msg: String): Unit =
    logger.error(msg)

  def logError(msg: String, cause: Throwable): Unit =
    logger.error(msg, cause)
}
