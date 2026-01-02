package com.webscraper

import com.typesafe.scalalogging.LazyLogging
import com.webscraper.config.ScraperConfig
import com.webscraper.scraper.WebScraper
import com.webscraper.util.{DataExtractor, FileUtils}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Main extends App with LazyLogging {
  logger.info("Starting Web Scraper Application")

  try {
    // Load configuration
    val config = ScraperConfig.load()
    
    // Create scraper instance
    val scraper = new WebScraper(config)
    
    // Example usage: scrape a website
    logger.info(s"Scraping URL: ${config.targetUrl}")
    
    scraper.initialize()
    
    // Navigate to target URL
    scraper.navigateTo(config.targetUrl)
    
    // Example: Get page title
    val title = scraper.getPageTitle()
    logger.info(s"Page Title: $title")
    
    // Example: Extract data using CSS selectors
    val linkElements = scraper.findElementsByCssSelector("a")
    logger.info(s"Found ${linkElements.size} links on the page")
    
    // Extract link data
    val links = linkElements.flatMap(DataExtractor.extractLinkData)
    logger.info(s"Extracted ${links.size} valid links")
    
    // Create output directory
    FileUtils.createDirectory(config.outputDirectory)
    
    // Save results with timestamp
    val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
    val outputFile = s"${config.outputDirectory}\\scrape_results_$timestamp.txt"
    
    val resultsContent = new StringBuilder
    resultsContent.append(s"Scraping Results\n")
    resultsContent.append(s"URL: ${config.targetUrl}\n")
    resultsContent.append(s"Timestamp: ${LocalDateTime.now()}\n")
    resultsContent.append(s"Page Title: $title\n")
    resultsContent.append(s"Total Links Found: ${links.size}\n")
    resultsContent.append("\n" + "="*80 + "\n\n")
    
    links.foreach { link =>
      resultsContent.append(s"Text: ${link.text}\n")
      resultsContent.append(s"URL: ${link.href}\n")
      link.title.foreach(t => resultsContent.append(s"Title: $t\n"))
      resultsContent.append("\n")
    }
    
    FileUtils.writeToFile(outputFile, resultsContent.toString())
    logger.info(s"Results saved to: $outputFile")
    
    // Take screenshot
    val screenshotFile = s"${config.outputDirectory}\\screenshot_$timestamp.png"
    scraper.takeScreenshot(screenshotFile)
    logger.info(s"Screenshot saved to: $screenshotFile")
    
    // Close browser
    scraper.close()
    
    logger.info("Web Scraper Application completed successfully")
    
  } catch {
    case e: Exception =>
      logger.error("An error occurred during web scraping", e)
      sys.exit(1)
  }
}
