package com.webscraper.examples

import com.typesafe.scalalogging.LazyLogging
import com.webscraper.config.ScraperConfig
import com.webscraper.scraper.WebScraper
import com.webscraper.util.{DataExtractor, FileUtils}
import com.webscraper.model.LinkData

/**
 * Example scraper that extracts all links from a webpage
 */
object LinkScraper extends App with LazyLogging {
  
  logger.info("Starting Link Scraper")
  
  // Create custom configuration
  val config = ScraperConfig(
    targetUrl = "https://example.com",
    browserType = "chrome",
    headless = false,
    implicitWaitSeconds = 10,
    explicitWaitSeconds = 20,
    pageLoadDelayMs = 2000,
    clickDelayMs = 500,
    userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
    outputDirectory = "C:\\output"
  )
  
  val scraper = new WebScraper(config)
  
  try {
    // Initialize browser
    scraper.initialize()
    
    // Navigate to target URL
    scraper.navigateTo(config.targetUrl)
    
    // Wait for page to load
    Thread.sleep(2000)
    
    // Find all links
    val linkElements = scraper.findElementsByCssSelector("a")
    logger.info(s"Found ${linkElements.size} links")
    
    // Extract link data
    val links: List[LinkData] = linkElements.flatMap(DataExtractor.extractLinkData)
    
    // Process and display links
    links.take(10).foreach { link =>
      logger.info(s"Link: ${link.text} -> ${link.href}")
    }
    
    // Ensure output directory exists
    FileUtils.createDirectory(config.outputDirectory)
    
    // Save results to file
    val output = links.map { link =>
      s"${link.text}|${link.href}|${link.title.getOrElse("")}"
    }.mkString("\n")
    
    FileUtils.writeToFile(s"${config.outputDirectory}\\links.txt", output)
    logger.info(s"Links saved to ${config.outputDirectory}\\links.txt")

    
  } catch {
    case e: Exception =>
      logger.error("Error during scraping", e)
  } finally {
    scraper.close()
    logger.info("Link Scraper completed")
  }
}
