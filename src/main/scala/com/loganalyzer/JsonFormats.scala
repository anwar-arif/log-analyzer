package com.loganalyzer

import com.loganalyzer.CustomJsonFormats.DateJsonFormat
import com.loganalyzer.LogRegistry.{GetFileSizeResponse, GetHistogramResponse, GetLogDataResponse, GetStatusResponse}
import com.loganalyzer.UserRegistry.ActionPerformed

//#json-formats

object JsonFormats {
  import spray.json.DefaultJsonProtocol._

  implicit val userJsonFormat = jsonFormat3(User)

  implicit val usersJsonFormat = jsonFormat1(Users)

  implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

  implicit val statusJsonFormat = jsonFormat1(GetStatusResponse)

  implicit val highlightTextJsonFormat = jsonFormat2(HighlightText)

  implicit val logDataJsonFormat = jsonFormat3(LogData)

  implicit val logRequestJsonFormat = jsonFormat3(LogRequest)

  implicit val getFileSizeResponseJsonFormat = jsonFormat1(GetFileSizeResponse)

  implicit val histogramBarJsonFormat = jsonFormat2(HistogramBar)

  implicit val getLogDataResponseJsonFormat = jsonFormat4(GetLogDataResponse)

  implicit val getHistogramResponse = jsonFormat4(GetHistogramResponse)

  implicit val jsonLogData = jsonFormat2(JsonLogData)
}
//#json-formats
