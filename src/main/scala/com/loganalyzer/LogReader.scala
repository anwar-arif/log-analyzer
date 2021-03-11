package com.loganalyzer

import akka.actor.typed.ActorSystem
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat
import java.util.Date
import scala.io.Source

object LogReader {
  private var status = "File processing isn't started yet"
  val dateFormat = new SimpleDateFormat("MMM dd HH:mm:ss")
  val logger = LoggerFactory.getLogger("LogReader")

  def readLogData() = {
    try {
      val config = ConfigFactory.load("application.conf")
      val filePath = config.getString("app.log-file.location")
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

        logger.info("time in epoch: " + epoch)

        val epochToDate = dateFormat.format(new Date(epoch))
        logger.info("time in date: " + epochToDate)

        logger.info("message: " + message)
        println()

      })
      logger.info("file content: " + fileContent)
    } catch {
      case _: Exception => None
    }
  }
}
