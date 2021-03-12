package com.loganalyzer.Models

import akka.actor.typed.ActorRef

object Model {

  // model class
  case class LogData(date: Long, message: String)
  final case class LogRequest(dateTimeFrom: String, dateTimeUntil: String, phrase: String)

  // request class
  sealed trait Command

  final case class GetStatus(replyTo: ActorRef[GetStatusResponse]) extends Command
  final case class GetFileSize(replyTo: ActorRef[GetFileSizeResponse]) extends Command
  final case class GetLogData(logRequest: LogRequest, replyTo: ActorRef[GetLogDataResponse]) extends Command
  final case class GetHistogram(logRequest: LogRequest, replyTo: ActorRef[GetLogDataResponse]) extends Command

  // response class
  final case class GetStatusResponse(status: String)
  final case class GetFileSizeResponse(size: Long)
  final case class GetLogDataResponse(data: Seq[LogData])
}
