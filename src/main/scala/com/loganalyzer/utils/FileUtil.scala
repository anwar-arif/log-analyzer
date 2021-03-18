package com.loganalyzer.utils

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import java.io.File

object FileUtil {
  val logger = LoggerFactory.getLogger("FileUtil")
  val str = "src/main/sqlite/db"
  val dbAbsolutePath = str.split('/').foldLeft("")((result, cur) => {
    result + File.separator + cur
  })
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
    val dbFileName = config.getString("app.sqlite.fileName")

    val rootDirectory = System.getProperty("user.dir") + dbAbsolutePath
    val directory = new File(rootDirectory)
    if (!directory.exists()) directory.mkdirs()

    val fullFilePath = rootDirectory + File.separator + dbFileName

    val file = new File(fullFilePath)
    file.createNewFile()

    logger.info("Root directory: " + rootDirectory)
    logger.info("Db full path: " + fullFilePath)

    fullFilePath
  }
}
