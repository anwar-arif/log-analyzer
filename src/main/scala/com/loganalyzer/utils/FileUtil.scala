package com.loganalyzer.utils

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.io.File

object FileUtil {
  val logger = LoggerFactory.getLogger("FileUtil")
//  create file if not exists
  def createFile(filePath: String): Unit = {
    val file = new File(filePath)
    file.createNewFile()
  }

  def fileSize(filePath: String): Long = {
    val file = new File(filePath)
    file.createNewFile()
    file.length()
  }

  def getDbFilePath(): String = {
    val config = ConfigFactory.load("application.conf")
    val dbAbsolutePath = config.getString("app.sqlite.absolutePath")
    val dbFileName = config.getString("app.sqlite.fileName")

    val rootDirectory = System.getProperty("user.dir") + dbAbsolutePath
    val directory = new File(rootDirectory)
    if (!directory.exists()) directory.mkdirs()

    val fullFilePath = rootDirectory + "/" + dbFileName

    val file = new File(fullFilePath)
    file.createNewFile()

    logger.info("Root directory: " + rootDirectory)
    logger.info("Db full path: " + fullFilePath)

    fullFilePath
  }
}
