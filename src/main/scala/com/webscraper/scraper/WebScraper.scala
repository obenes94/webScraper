package com.webscraper.scraper

import com.typesafe.scalalogging.LazyLogging
import com.webscraper.config.ScraperConfig
import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters._

/**
 * Main web scraper class that handles browser automation and data extraction
 */
class WebScraper(config: ScraperConfig) extends LazyLogging {
  
  private var driver: Option[WebDriver] = None
  private var webDriverWait: Option[WebDriverWait] = None
  
  /**
   * Initialize the WebDriver based on configuration
   */
  def initialize(): Unit = {
    logger.info(s"Initializing ${config.browserType} WebDriver")
    
    val webDriver = config.browserType.toLowerCase match {
      case "chrome" =>
        WebDriverManager.chromedriver().setup()
        val options = new ChromeOptions()
        if (config.headless) {
          options.addArguments("--headless")
          options.addArguments("--no-sandbox")
          options.addArguments("--disable-dev-shm-usage")
        }
        options.addArguments("--disable-blink-features=AutomationControlled")
        options.addArguments(s"user-agent=${config.userAgent}")
        new ChromeDriver(options)
        
      case "firefox" =>
        WebDriverManager.firefoxdriver().setup()
        val options = new FirefoxOptions()
        if (config.headless) {
          options.addArguments("--headless")
        }
        new FirefoxDriver(options)
        
      case _ =>
        throw new IllegalArgumentException(s"Unsupported browser: ${config.browserType}")
    }
    
    webDriver.manage().timeouts().implicitlyWait(config.implicitWaitSeconds, TimeUnit.SECONDS)
    webDriver.manage().window().maximize()
    
    driver = Some(webDriver)
    webDriverWait = Some(new WebDriverWait(webDriver, config.explicitWaitSeconds))
    
    logger.info("WebDriver initialized successfully")
  }
  
  /**
   * Navigate to a specific URL
   */
  def navigateTo(url: String): Unit = {
    logger.info(s"Navigating to: $url")
    driver.foreach(_.get(url))
    Thread.sleep(config.pageLoadDelayMs)
  }
  
  /**
   * Get the current page title
   */
  def getPageTitle(): String = {
    driver.map(_.getTitle).getOrElse("")
  }
  
  /**
   * Get the current page URL
   */
  def getCurrentUrl(): String = {
    driver.map(_.getCurrentUrl).getOrElse("")
  }
  
  /**
   * Find elements by CSS selector
   */
  def findElementsByCssSelector(selector: String): List[WebElement] = {
    logger.debug(s"Finding elements by CSS selector: $selector")
    driver.map(_.findElements(By.cssSelector(selector)).asScala.toList).getOrElse(List.empty)
  }
  
  /**
   * Find a single element by CSS selector
   */
  def findElementByCssSelector(selector: String): Option[WebElement] = {
    try {
      driver.flatMap(d => Option(d.findElement(By.cssSelector(selector))))
    } catch {
      case _: Exception => None
    }
  }
  
  /**
   * Find elements by XPath
   */
  def findElementsByXPath(xpath: String): List[WebElement] = {
    logger.debug(s"Finding elements by XPath: $xpath")
    driver.map(_.findElements(By.xpath(xpath)).asScala.toList).getOrElse(List.empty)
  }
  
  /**
   * Find a single element by XPath
   */
  def findElementByXPath(xpath: String): Option[WebElement] = {
    try {
      driver.flatMap(d => Option(d.findElement(By.xpath(xpath))))
    } catch {
      case _: Exception => None
    }
  }
  
  /**
   * Wait for an element to be visible
   */
  def waitForElement(selector: String, byCss: Boolean = true): Option[WebElement] = {
    try {
      val locator = if (byCss) By.cssSelector(selector) else By.xpath(selector)
      webDriverWait.map(_.until(ExpectedConditions.visibilityOfElementLocated(locator)))
    } catch {
      case e: Exception =>
        logger.warn(s"Element not found: $selector", e)
        None
    }
  }
  
  /**
   * Click on an element
   */
  def clickElement(element: WebElement): Unit = {
    try {
      element.click()
      Thread.sleep(config.clickDelayMs)
    } catch {
      case e: Exception =>
        logger.error("Failed to click element", e)
    }
  }
  
  /**
   * Extract text from an element
   */
  def getElementText(element: WebElement): String = {
    try {
      element.getText
    } catch {
      case e: Exception =>
        logger.error("Failed to extract text from element", e)
        ""
    }
  }
  
  /**
   * Get attribute value from an element
   */
  def getElementAttribute(element: WebElement, attribute: String): String = {
    try {
      element.getAttribute(attribute)
    } catch {
      case e: Exception =>
        logger.error(s"Failed to get attribute '$attribute' from element", e)
        ""
    }
  }
  
  /**
   * Execute JavaScript code
   */
  def executeScript(script: String, args: Any*): Any = {
    driver match {
      case Some(d) =>
        import org.openqa.selenium.JavascriptExecutor
        d.asInstanceOf[JavascriptExecutor].executeScript(script, args.map(_.asInstanceOf[Object]): _*)
      case None =>
        logger.error("WebDriver not initialized")
        null
    }
  }
  
  /**
   * Take a screenshot and save to file
   */
  def takeScreenshot(filePath: String): Unit = {
    import org.openqa.selenium.OutputType
    import org.openqa.selenium.TakesScreenshot
    import java.io.File
    import java.nio.file.{Files, Paths}
    
    try {
      driver match {
        case Some(d) =>
          val screenshot = d.asInstanceOf[TakesScreenshot].getScreenshotAs(OutputType.BYTES)
          Files.write(Paths.get(filePath), screenshot)
          logger.info(s"Screenshot saved to: $filePath")
        case None =>
          logger.error("WebDriver not initialized")
      }
    } catch {
      case e: Exception =>
        logger.error(s"Failed to take screenshot: $filePath", e)
    }
  }
  
  /**
   * Get page source HTML
   */
  def getPageSource(): String = {
    driver.map(_.getPageSource).getOrElse("")
  }
  
  /**
   * Close the browser and clean up resources
   */
  def close(): Unit = {
    logger.info("Closing WebDriver")
    driver.foreach(_.quit())
    driver = None
    webDriverWait = None
  }
}
