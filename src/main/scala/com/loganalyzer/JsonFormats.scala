package com.loganalyzer

import com.loganalyzer.LogRegistry.GetStatusResponse
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

  implicit val logDataJsonFormat =


}
//#json-formats
