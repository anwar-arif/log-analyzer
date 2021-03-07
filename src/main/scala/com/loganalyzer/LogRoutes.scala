package com.loganalyzer

import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.loganalyzer.LogRegistry.{GetStatus, GetStatusResponse}

import scala.concurrent.Future

class LogRoutes(logRegistry: ActorRef[LogRegistry.Command])(implicit val system: ActorSystem[_]) {
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import JsonFormats._

  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))

  def getStatus(): Future[GetStatusResponse] =
    logRegistry.ask(GetStatus)


  val logRoutes: Route = {
    pathPrefix("api") {
      pathPrefix("get_status") {
        concat(
          pathEnd {
            get(
              complete(getStatus())
            )
          }
        )
      }
    }
  }
}
