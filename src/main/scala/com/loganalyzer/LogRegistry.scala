package com.loganalyzer

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.DateTime
import com.loganalyzer.Models.Model._
import com.loganalyzer.utils.DateUtil
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.Date
import scala.collection.immutable


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
        replyTo ! getHistogramResponse(logRequest)
        Behaviors.same
    }
  }

  def getLogFileSize(): GetFileSizeResponse = {
    GetFileSizeResponse(LogReader.logFileSize)
  }

  def getLogDataResponse(logRequest: LogRequest): GetLogDataResponse = {
    logger.info("Request: " + logRequest.dateTimeFrom)
    LogRepository.getLogs(logRequest)
  }

  def getHistogramResponse(logRequest: LogRequest): GetLogDataResponse = {
    GetLogDataResponse(Seq.empty[LogData])
  }
}
