package com.loganalyzer

import akka.actor.typed.ActorSystem

import java.text.SimpleDateFormat
import java.util.Date
import scala.io.Source

object LogReader {
  private var status = "File processing isn't started yet"
  val dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss")

  def readLogData()(implicit system: ActorSystem[_]) = {
    import JsonFormats._
    import spray.json._
    import DefaultJsonProtocol._

    val filePath = system.settings.config.getString("app.log-file.location")

    val logSource = Source.fromFile(filePath)
    val fileContent = logSource.getLines.toSeq
    logSource.close()

    fileContent.foreach(line => {
      val words = line.split(' ')
      var wordCounter = 0
      var time, message = ""
      words.foreach(word => {
        wordCounter += 1
        if (wordCounter <= 3) time += word + " "
        else message += word + " "
      })

      time = time.strip()
      message = message.strip()
      val date: Date = dateFormat.parse(time)
      val epoch: Long = date.getTime

      println("time: " + epoch)
      println("message: " + message)
      println()

    })
    system.log.info("file content: " + fileContent)
  }
}
