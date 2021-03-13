package com.loganalyzer

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.loganalyzer.Models.DataModel._
import org.slf4j.LoggerFactory

object LogRegistry {
  val logger = LoggerFactory.getLogger("LogRegistry")

  def apply(): Behavior[Command] = registry(Seq.empty)

  private def registry(logData: Seq[LogData]): Behavior[Command] = {
    Behaviors.receiveMessage {
      case GetStatus(replyTo) =>
        replyTo ! GetStatusResponse("Okay")
        Behaviors.same
      case GetFileSize(replyTo) =>
        replyTo ! getLogFileSize()
        Behaviors.same
      case GetLogData(logRequest: LogRequest, replyTo) =>
        replyTo ! getLogDataResponse(logRequest)
        Behaviors.same
      case GetHistogram(logRequest: LogRequest, replyTo) =>
        replyTo ! getLogDataResponse(logRequest)
        Behaviors.same
    }
  }

  def getLogFileSize(): GetFileSizeResponse = {
    GetFileSizeResponse(LogReader.getLogFileSize())
    throw new Exception("Custom Exception")
  }

  def getLogDataResponse(logRequest: LogRequest): GetLogDataResponse = {
    logger.info("Request: " + logRequest.dateTimeFrom)
//     LogRepository.getLogs(logRequest)
    throw new Exception("Custom exception")
  }
}
