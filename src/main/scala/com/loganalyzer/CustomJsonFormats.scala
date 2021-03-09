package com.loganalyzer

import spray.json.{DefaultJsonProtocol, JsString, JsValue, RootJsonFormat}

import java.text.SimpleDateFormat
import java.util.Date

object CustomJsonProtocol extends DefaultJsonProtocol {
  final val dateFormat = "dd/MM/yyyy"

  implicit object DateJsonFormat extends RootJsonFormat[Date] {
    override def write(obj: Date): JsValue = JsString(obj.toString)

    override def read(json: JsValue): Date = json match {
//      TODO: Need to handle date format error
      case JsString(json) => {
        new SimpleDateFormat(dateFormat).parse(json)
      }
    }
  }
}
