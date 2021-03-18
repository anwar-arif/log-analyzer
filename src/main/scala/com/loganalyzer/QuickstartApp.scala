package com.loganalyzer

import akka.actor.typed._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.concat
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.util.Failure
import scala.util.Success

//#main-class
object QuickstartApp {
  val logger = LoggerFactory.getLogger("LogAnalyzerApp")
  //#start-http-server
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Akka HTTP still needs a classic ActorSystem to start
    import system.executionContext
    val host = system.settings.config.getString("app.http.host")
    val port = system.settings.config.getInt("app.http.port")

    val futureBinding = Http().newServerAt(host, port).bind(routes)

    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
    try {
      LogRepository.getConnection()
    } catch {
      case exception: Exception => logger.info(exception.getMessage)
    }
  }
  //#start-http-server
  def main(args: Array[String]): Unit = {
    //#server-bootstrapping
    import ExceptionHandlers.CustomExceptionHandler._
    val rootBehavior = Behaviors.setup[Nothing] { context =>
      val logRegistryActor = context.spawn(LogRegistry(), "LogRegistryActor")
      context.watch(logRegistryActor)

      val logRoutes = new LogRoutes(logRegistryActor)(context.system)

      val allRoutes = Route.seal(logRoutes.logRoutes)
      startHttpServer(allRoutes)(context.system)

      Behaviors.empty
    }

    val system = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer", ConfigFactory.load())
    //#server-bootstrapping
  }
}
//#main-class
