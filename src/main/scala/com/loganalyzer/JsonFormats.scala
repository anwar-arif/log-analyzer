package com.loganalyzer

import com.loganalyzer.Models.DataModel.{GetHistogramResponse, _}

//#json-formats

object JsonFormats {
  import spray.json.DefaultJsonProtocol._

  implicit val statusJsonFormat = jsonFormat1(GetStatusResponse)

  implicit val logDataJsonFormat = jsonFormat2(LogData)

  implicit val logRequestJsonFormat = jsonFormat3(LogRequest)

  implicit val getFileSizeResponseJsonFormat = jsonFormat1(GetFileSizeResponse)

  implicit val highlightText = jsonFormat2(HighlightText)

  implicit val highlightTextResponse = jsonFormat3(HighlightTextResponse)

  implicit val getLogDataResponseJsonFormat = jsonFormat4(GetLogDataResponse)

  implicit val histogramJsonFormat = jsonFormat2(Histogram)

  implicit val getHistogramResponseJsonFormat = jsonFormat4(GetHistogramResponse)
}
//#json-formats
