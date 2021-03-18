package com.loganalyzer

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.loganalyzer.Models.DataModel._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class LogRoutes(logRegistry: ActorRef[Command])(implicit val system: ActorSystem[_]) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._


  val logger = LoggerFactory.getLogger("LogRoutes")
  private implicit val timeout = Timeout.create(system.settings.config.getDuration("app.routes.ask-timeout"))

  def getStatus(): Future[GetStatusResponse] = {
    Future{
      LogRegistry.getStatus()
    }
  }

  def getFileSize(): Future[GetFileSizeResponse] = {
    Future {
      LogRegistry.getLogFileSize()
    }
  }

  def getData(logRequest: LogRequest): Future[GetLogDataResponse] = {
    Future{
      LogRegistry.getLogDataResponse(logRequest)
    }
  }

  def getHistogram(logRequest: LogRequest): Future[GetHistogramResponse] = {
    Future{
      LogRegistry.getHistogram(logRequest)
    }
  }

  val logRoutes: Route =
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
