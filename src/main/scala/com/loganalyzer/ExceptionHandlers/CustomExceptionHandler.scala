package com.loganalyzer.ExceptionHandlers

import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.{complete, extractUri}
import akka.http.scaladsl.server.ExceptionHandler

object CustomExceptionHandler {
  implicit def implicitExceptionHandler: ExceptionHandler = ExceptionHandler {
    case ex: Throwable => {
      complete(HttpResponse(InternalServerError, entity = ex.getMessage))
    }
    case ex: ArithmeticException => {
      complete(HttpResponse(InternalServerError, entity = ex.getMessage))
    }
  }

//  implicit def customExceptionHandler: ExceptionHandler =
//    ExceptionHandler {
//      case _: RuntimeException =>
//        extractUri { uri =>
//          complete(HttpResponse(InternalServerError, entity = s"$uri: Runtime exception!!!"))
//        }
//    }
}
