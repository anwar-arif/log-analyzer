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
}
