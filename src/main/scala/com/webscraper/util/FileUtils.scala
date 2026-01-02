package com.webscraper.util

import com.typesafe.scalalogging.LazyLogging

import java.io.{File, FileWriter, PrintWriter}
import scala.util.Try

/**
 * Utility class for file operations
 */
object FileUtils extends LazyLogging {
  
  /**
   * Write content to a file
   */
  def writeToFile(filePath: String, content: String): Boolean = {
    Try {
      val file = new File(filePath)
      file.getParentFile.mkdirs()
      
      val writer = new PrintWriter(new FileWriter(file))
      try {
        writer.write(content)
      } finally {
        writer.close()
      }
    } match {
      case scala.util.Success(_) =>
        logger.info(s"Successfully wrote to file: $filePath")
        true
      case scala.util.Failure(e) =>
        logger.error(s"Failed to write to file: $filePath", e)
        false
    }
  }
  
  /**
   * Append content to a file
   */
  def appendToFile(filePath: String, content: String): Boolean = {
    Try {
      val file = new File(filePath)
      file.getParentFile.mkdirs()
      
      val writer = new PrintWriter(new FileWriter(file, true))
      try {
        writer.write(content)
      } finally {
        writer.close()
      }
    } match {
      case scala.util.Success(_) =>
        logger.info(s"Successfully appended to file: $filePath")
        true
      case scala.util.Failure(e) =>
        logger.error(s"Failed to append to file: $filePath", e)
        false
    }
  }
  
  /**
   * Read content from a file
   */
  def readFromFile(filePath: String): Option[String] = {
    Try {
      scala.io.Source.fromFile(filePath).mkString
    } match {
      case scala.util.Success(content) =>
        logger.info(s"Successfully read from file: $filePath")
        Some(content)
      case scala.util.Failure(e) =>
        logger.error(s"Failed to read from file: $filePath", e)
        None
    }
  }
  
  /**
   * Check if a file exists
   */
  def fileExists(filePath: String): Boolean = {
    new File(filePath).exists()
  }
  
  /**
   * Create a directory if it doesn't exist
   */
  def createDirectory(dirPath: String): Boolean = {
    val dir = new File(dirPath)
    if (!dir.exists()) {
      dir.mkdirs()
    } else {
      true
    }
  }
}
