package com.loganalyzer

import com.loganalyzer.CustomJsonFormats.DateJsonFormat
import com.loganalyzer.LogRegistry.{GetFileSizeResponse, GetLogDataResponse, GetStatusResponse, LogRequest}
import com.loganalyzer.UserRegistry.ActionPerformed

//#json-formats
import spray.json.DefaultJsonProtocol

object JsonFormats  {
  // import the default encoders for primitive types (Int, String, Lists etc)
  import DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)

  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val statusJsonFormat = jsonFormat1(GetStatusResponse)

  implicit val logDataJsonFormat = jsonFormat2(LogData)

  implicit val logRequestJsonFormat = jsonFormat3(LogRequest)

  implicit val getFileSizeResponseJsonFormat = jsonFormat1(GetFileSizeResponse)

}
//#json-formats
