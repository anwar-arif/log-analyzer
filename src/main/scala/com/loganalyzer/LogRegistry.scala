package com.loganalyzer

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.model.DateTime

import java.util.Date
import scala.collection.immutable

final case class Log(dateTimeFrom: DateTime, dateTimeUntil: DateTime, message: String)
final case class Logs(logs: immutable.Seq[Log])
final case class LogData(dateTime: DateTime, message: String)

object LogRegistry {
  sealed trait Command

  // model class
  final case class LogRequest(dateTimeFrom: DateTime, dateTimeUntil: DateTime, phrase: String)
  final case class LogResponse(dateTime: DateTime, message: String, highlightText: Seq[HighlightText])
  final case class HighlightText(fromPosition: Int, toPosition: Int)
  final case class HistogramBar(dateTime: DateTime, counts: Int)

  // request class
  final case class GetStatus(replyTo: ActorRef[GetStatusResponse]) extends Command
  final case class GetFileSize(replyTo: ActorRef[GetFileSizeResponse]) extends Command
  final case class GetLog(logRequest: LogRequest, replyTo: ActorRef[GetLogResponse]) extends Command
  final case class GetHistogram(logRequest: LogRequest, replyTo: ActorRef[GetHistogramResponse]) extends Command

  // response class
  final case class GetStatusResponse(status: String)
  final case class GetFileSizeResponse(size: Long)
  final case class GetLogResponse(data: Option[Seq[LogResponse]], dateTimeFrom: DateTime, dateTimeUntil: DateTime, phrase: String)
  final case class GetHistogramResponse(histogram: Option[Seq[HistogramBar]], dateTimeFrom: DateTime, dateTimeUntil: DateTime, phrase: String)

  def apply(): Behavior[Command] = registry(Seq.empty)

  private def registry(logData: Seq[LogData]): Behavior[Command] = {
    Behaviors.receiveMessage {
      case GetStatus(replyTo) =>
        replyTo ! GetStatusResponse("Okay")
        Behaviors.same
    }
  }
}
