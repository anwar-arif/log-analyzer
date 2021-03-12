package com.loganalyzer

import com.loganalyzer.utils.DateUtil
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.io.File
import scala.io.Source

object LogReader {
  private var status = "File processing isn't started yet"
  val logger = LoggerFactory.getLogger("LogReader")
  var logFileSize: Long = 0

  def readLogData() = {
    try {
      val config = ConfigFactory.load("application.conf")
      val filePath = config.getString("app.log-file.location")
      logFileSize = new File(filePath).length()
      val logSource = Source.fromFile(filePath)
      val fileContent = logSource.getLines.toSeq
      logSource.close()

      var dbLogs = Seq.empty[DbLogData]

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
        val epoch: Long = DateUtil.getEpoch(time)

        dbLogs :+ DbLogData(epoch, message)

      })

      LogRepository.insertLogs(dbLogs)
    } catch {
      case ex: Exception => {
        logger.error("Error while reading log data: " + ex.getMessage)
        None
      }
    }
  }
}
