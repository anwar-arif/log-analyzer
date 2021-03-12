package com.loganalyzer

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.DateTime

import java.util.Date
import scala.collection.immutable

final case class JsonLogData(dateTime: Date, message: String)

// model class
final case class LogRequest(dateTimeFrom: Date, dateTimeUntil: Date, phrase: String)
final case class LogData(dateTime: Date, message: String, highlightText: Seq[HighlightText])
final case class HighlightText(fromPosition: Int, toPosition: Int)
final case class HistogramBar(dateTime: Date, counts: Int)

object LogRegistry {
  sealed trait Command

  // request class
  final case class GetStatus(replyTo: ActorRef[GetStatusResponse]) extends Command
  final case class GetFileSize(replyTo: ActorRef[GetFileSizeResponse]) extends Command
  final case class GetLogData(logRequest: LogRequest, replyTo: ActorRef[GetLogDataResponse]) extends Command
  final case class GetHistogram(logRequest: LogRequest, replyTo: ActorRef[GetHistogramResponse]) extends Command

  // response class
  final case class GetStatusResponse(status: String)
  final case class GetFileSizeResponse(size: Long)
  final case class GetLogDataResponse(data: Seq[LogData], dateTimeFrom: Date, dateTimeUntil: Date, phrase: String)
  final case class GetHistogramResponse(histogram: Seq[HistogramBar], dateTimeFrom: Date, dateTimeUntil: Date, phrase: String)

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
    GetLogDataResponse(
      Seq(
        LogData(
          logRequest.dateTimeFrom,
          "Dummy Message",
          Seq(
            HighlightText(0, 4)
          )
        )
      ),
      logRequest.dateTimeFrom,
      logRequest.dateTimeUntil,
      logRequest.phrase
    )

  }

  def getHistogramResponse(logRequest: LogRequest): GetHistogramResponse = {
    GetHistogramResponse(
      Seq(
        HistogramBar(
          new Date(2021, 3, 10),
          15
        )
      ),
      new Date(2021, 3, 5),
      new Date(2021, 3, 15),
      "actor"
    )
  }
}
