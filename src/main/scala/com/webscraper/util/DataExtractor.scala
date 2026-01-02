package com.webscraper.util

import com.typesafe.scalalogging.LazyLogging
import org.openqa.selenium.WebElement
import com.webscraper.model.{LinkData, ImageData}

import scala.util.{Try, Success, Failure}

/**
 * Utility class for data extraction from web elements
 */
object DataExtractor extends LazyLogging {
  
  /**
   * Extract link data from a WebElement
   */
  def extractLinkData(element: WebElement): Option[LinkData] = {
    Try {
      val href = element.getAttribute("href")
      val text = element.getText
      val title = Option(element.getAttribute("title"))
      
      LinkData(text, href, title)
    } match {
      case Success(data) => Some(data)
      case Failure(e) =>
        logger.warn("Failed to extract link data", e)
        None
    }
  }
  
  /**
   * Extract image data from a WebElement
   */
  def extractImageData(element: WebElement): Option[ImageData] = {
    Try {
      val src = element.getAttribute("src")
      val alt = Option(element.getAttribute("alt"))
      val title = Option(element.getAttribute("title"))
      
      ImageData(src, alt, title)
    } match {
      case Success(data) => Some(data)
      case Failure(e) =>
        logger.warn("Failed to extract image data", e)
        None
    }
  }
  
  /**
   * Clean and normalize text
   */
  def cleanText(text: String): String = {
    text
      .trim
      .replaceAll("\\s+", " ")
      .replaceAll("\\n+", "\n")
  }
  
  /**
   * Extract all text content from elements
   */
  def extractTexts(elements: List[WebElement]): List[String] = {
    elements.flatMap { element =>
      Try(element.getText).toOption.map(cleanText)
    }
  }
  
  /**
   * Check if a URL is absolute
   */
  def isAbsoluteUrl(url: String): Boolean = {
    url.startsWith("http://") || url.startsWith("https://")
  }
  
  /**
   * Convert relative URL to absolute URL
   */
  def toAbsoluteUrl(baseUrl: String, relativeUrl: String): String = {
    if (isAbsoluteUrl(relativeUrl)) {
      relativeUrl
    } else {
      val base = if (baseUrl.endsWith("/")) baseUrl.dropRight(1) else baseUrl
      val relative = if (relativeUrl.startsWith("/")) relativeUrl else s"/$relativeUrl"
      s"$base$relative"
    }
  }
}
