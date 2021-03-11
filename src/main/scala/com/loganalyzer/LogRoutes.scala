package com.loganalyzer

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.loganalyzer.LogRegistry.{GetFileSize, GetFileSizeResponse, GetHistogram, GetHistogramResponse, GetLogData, GetLogDataResponse, GetStatus, GetStatusResponse}

import scala.concurrent.Future

class LogRoutes(logRegistry: ActorRef[LogRegistry.Command])(implicit val system: ActorSystem[_]) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("app.routes.ask-timeout"))

  def getStatus(): Future[GetStatusResponse] =
    logRegistry.ask(GetStatus)

  def getFileSize(): Future[GetFileSizeResponse] =
    logRegistry.ask(GetFileSize)

  def getData(logRequest: LogRequest): Future[GetLogDataResponse] =
    logRegistry.ask(GetLogData(logRequest, _))

  def getHistogram(logRequest: LogRequest): Future[GetHistogramResponse] =
    logRegistry.ask(GetHistogram(logRequest, _))


  val logRoutes: Route = {
    pathPrefix("api") {
      concat(
        pathPrefix("get_status") {
          concat(
            pathEnd {
              get(
                complete(getStatus())
              )
            }
          )
        },
        pathPrefix("get_size") {
          concat(
            pathEnd {
              get(
                complete(getFileSize())
              )
            }
          )
        },
        pathPrefix("data") {
          concat(
            pathEnd {
              post(
                entity(as[LogRequest]) { logRequest =>
                  onSuccess(getData(logRequest)) { response =>
                    complete(response)
                  }
                }
              )
            }
          )
        },
        pathPrefix("histogram") {
          concat(
            pathEnd {
              post(
                entity(as[LogRequest]) { logRequest =>
                  onSuccess(getHistogram(logRequest)) { response =>
                    complete(response)
                  }
                }
              )
            }
          )
        }
      )
    }
  }
}
