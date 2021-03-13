package com.loganalyzer

import com.loganalyzer.Models.DataModel._
import com.loganalyzer.UserRegistry.ActionPerformed

//#json-formats

object JsonFormats {
  import spray.json.DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)

  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val statusJsonFormat = jsonFormat1(GetStatusResponse)

  implicit val logDataJsonFormat = jsonFormat2(LogData)

  implicit val logRequestJsonFormat = jsonFormat3(LogRequest)

  implicit val getFileSizeResponseJsonFormat = jsonFormat1(GetFileSizeResponse)

  implicit val getLogDataResponseJsonFormat = jsonFormat1(GetLogDataResponse)
}
//#json-formats
