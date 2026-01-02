package com.webscraper.model

/**
 * Case class representing scraped data
 */
case class ScrapedData(
  title: String,
  url: String,
  content: String,
  metadata: Map[String, String] = Map.empty,
  timestamp: Long = System.currentTimeMillis()
)

/**
 * Case class for link data
 */
case class LinkData(
  text: String,
  href: String,
  title: Option[String] = None
)

/**
 * Case class for image data
 */
case class ImageData(
  src: String,
  alt: Option[String] = None,
  title: Option[String] = None
)
