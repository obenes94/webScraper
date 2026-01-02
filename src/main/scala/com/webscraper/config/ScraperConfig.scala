package com.webscraper.config

import com.typesafe.config.ConfigFactory

/**
 * Configuration class for the web scraper
 */
case class ScraperConfig(
  targetUrl: String,
  browserType: String,
  headless: Boolean,
  implicitWaitSeconds: Long,
  explicitWaitSeconds: Long,
  pageLoadDelayMs: Long,
  clickDelayMs: Long,
  userAgent: String,
  outputDirectory: String
)

object ScraperConfig {
  
  /**
   * Load configuration from application.conf
   */
  def load(): ScraperConfig = {
    val config = ConfigFactory.load()
    
    ScraperConfig(
      targetUrl = config.getString("scraper.target-url"),
      browserType = config.getString("scraper.browser-type"),
      headless = config.getBoolean("scraper.headless"),
      implicitWaitSeconds = config.getLong("scraper.timeouts.implicit-wait-seconds"),
      explicitWaitSeconds = config.getLong("scraper.timeouts.explicit-wait-seconds"),
      pageLoadDelayMs = config.getLong("scraper.delays.page-load-ms"),
      clickDelayMs = config.getLong("scraper.delays.click-ms"),
      userAgent = config.getString("scraper.user-agent"),
      outputDirectory = config.getString("scraper.output-directory")
    )
  }
  
}
