package com.loganalyzer

import com.loganalyzer.Enums.StatusEnum
import com.loganalyzer.Enums.StatusEnum.Status
import com.loganalyzer.Models.DataModel.LogData
import com.loganalyzer.utils.{DateUtil, FileUtil}
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.io.File
import scala.io.Source

object LogReader {
  var status: Status = StatusEnum.InProgress

  val logger = LoggerFactory.getLogger("LogReader")
  var logFileSize: Long = 0

  def readLogData(): Unit = {
    try {
      val config = ConfigFactory.load("application.conf")
      val filePath = config.getString("app.log-file.location")

      FileUtil.createFile(filePath)
      logFileSize = FileUtil.fileSize(filePath)

      val logSource = Source.fromFile(filePath)
      val fileContent = logSource.getLines.toSeq
      logSource.close()

      var dbLogs = List[LogData]()

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
        if (!DateUtil.isValidDateFormat(time)) {
          throw new Exception("Couldn't parse: " + time)
        }
        message = message.strip()
        val epoch: Long = DateUtil.getEpoch(time)
        logger.info("Date: " + DateUtil.getDate(epoch) + " epoch: " + epoch)
        dbLogs = dbLogs :+ LogData(epoch, message)
      })

      LogRepository.insertLogs(dbLogs)

      status = StatusEnum.Okay

    } catch {
      case exception: Exception => {
        status = StatusEnum.Failed
        logger.error("Error while reading log data: " + exception.getMessage)
        throw exception
      }
    }
  }
}
