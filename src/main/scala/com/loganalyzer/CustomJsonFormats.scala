package com.loganalyzer

import spray.json.{DefaultJsonProtocol, JsArray, JsString, JsValue, RootJsonFormat, deserializationError}

import java.text.SimpleDateFormat
import java.util.Date

object CustomJsonFormats extends DefaultJsonProtocol {
  final val dateFormat = "dd/MM/YYYY"

  implicit object DateJsonFormat extends RootJsonFormat[Date] {
    override def write(obj: Date): JsValue = JsString(obj.formatted(dateFormat))

    override def read(json: JsValue): Date = json match {
//      TODO: Need to handle date format error
      case JsString(json) => {
        new SimpleDateFormat(dateFormat).parse(json)
      }
      case _ => deserializationError(s"Expected date format $dateFormat")
    }
  }

  implicit object ThrowableJsonFormat extends RootJsonFormat[Throwable] {
    override def read(json: JsValue): Throwable = throw new Exception(json.toString())

    override def write(obj: Throwable): JsValue = JsString(obj.getMessage)
  }

  implicit object ExceptionJsonFormat extends RootJsonFormat[Exception] {
    override def read(json: JsValue): Exception = new Exception(json.toString())

    override def write(obj: Exception): JsValue = JsString(obj.getMessage)
  }
}
