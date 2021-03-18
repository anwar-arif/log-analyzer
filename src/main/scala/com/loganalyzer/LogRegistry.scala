package com.loganalyzer

import akka.actor.Status.Failure
import akka.actor.typed.{Behavior, SupervisorStrategy}
import akka.actor.typed.scaladsl.Behaviors
import com.loganalyzer.Models.DataModel._
import com.loganalyzer.utils.DateUtil
import org.slf4j.LoggerFactory

object LogRegistry {
  val logger = LoggerFactory.getLogger("LogRegistry")

  def apply(): Behavior[Command] = Behaviors.supervise(registry())
    .onFailure[Throwable](SupervisorStrategy.restart)

  private def registry(): Behavior[Command] = {
    Behaviors.receiveMessage {
      case GetStatus(replyTo) =>
        replyTo ! GetStatusResponse("Okay")
        Behaviors.same
      case GetFileSize(replyTo) => {
        replyTo ! getLogFileSize()
        Behaviors.same
      }
      case GetLogData(logRequest: LogRequest, replyTo) =>
        replyTo ! getLogDataResponse(logRequest)
        Behaviors.same
      case GetHistogram(logRequest: LogRequest, replyTo) =>
        replyTo ! getLogDataResponse(logRequest)
        Behaviors.same
    }
  }

  def getStatus(): GetStatusResponse = {
    GetStatusResponse(LogReader.status.toString)
  }

  def getLogFileSize(): GetFileSizeResponse = {
    GetFileSizeResponse(LogReader.logFileSize)
  }

  def getLogDataResponse(logRequest: LogRequest): GetLogDataResponse = {
    LogRepository.getLogDataResponse(logRequest)
  }

  def getHistogram(logRequest: LogRequest): GetHistogramResponse = {
    LogRepository.getHistogram(logRequest)
  }
}
