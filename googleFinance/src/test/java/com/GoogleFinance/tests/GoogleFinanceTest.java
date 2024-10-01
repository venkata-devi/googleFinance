package com.GoogleFinance.tests;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.googleFinance.config.ConfigLoader;

public class GoogleFinanceTest {

	private WebDriver driver;
	private ConfigLoader config;
	private Set<String> expectedStockSymbols = new HashSet<String>();
	private String expectedPageTitle;
	private Set<String> actualStockSymbols;

	@BeforeClass
	public void setUp() {

		// Load ChromeDriver from the resource path
		URL chromeDriverUrl = getClass().getClassLoader().getResource("chromedriver");
		if (chromeDriverUrl == null) {
			throw new IllegalStateException("Cannot find chromedriver in resources");
		}
		//File chromeDriverFile = new File(chromeDriverUrl.getFile());
		config = ConfigLoader.getInstance();
		System.setProperty("webdriver.chrome.driver", config.getProperty("webdriver.chrome.driver"));

		
		// System.setProperty("webdriver.chrome.driver",
		// config.getProperty("webdriver.chrome.driver"));
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		List<String> stockSymbols = Arrays.asList(config.getProperty("expected.stock.symbols").split(","));
		for (String symbol : stockSymbols) {
			expectedStockSymbols.add(symbol.trim());
		}

		expectedPageTitle = config.getProperty("expected.pageTitle");

		// Open Google Finance webpage
		driver.get(config.getProperty("url.google.finance"));
		this.actualStockSymbols = this.extractStockSymbols();

	}

	@Test
	public void validatePageTitle() {
		// Verify the page title
		String pageTitle = driver.getTitle();
		Assert.assertTrue(pageTitle.contains(expectedPageTitle),
				"Page title does not contain `" + expectedPageTitle + "`");
	}

	@Test
	public void validateStockSymbols() {
		Set<String> expectedSet = new HashSet<>(expectedStockSymbols);

		// Symbols in actual but not expected
		Set<String> inActualButNotExpected = new HashSet<>(actualStockSymbols);
		inActualButNotExpected.removeAll(expectedSet);
		System.out.println("Stock symbols in actual but not expected: " + inActualButNotExpected);
		Assert.assertTrue(inActualButNotExpected.isEmpty(),
				"There are symbols in actual but not expected and the symbols are: " + inActualButNotExpected);

		// Symbols in expected but not actual
		Set<String> inExpectedButNotActual = new HashSet<>(expectedSet);
		inExpectedButNotActual.removeAll(actualStockSymbols);
		System.out.println("Stock symbols in expected but not actual: " + inExpectedButNotActual);
		Assert.assertTrue(inExpectedButNotActual.isEmpty(),
				"There are symbols in expected but not actual and the symbols are: " + inExpectedButNotActual);
	}

	private Set<String> extractStockSymbols() {
		List<WebElement> stockElements = driver.findElements(By.xpath(config.getProperty("locator.stock.symbols")));
		Set<String> stockSymbols = new HashSet<>();
		Pattern pattern = Pattern.compile("quote/([^:]+)");

		for (WebElement element : stockElements) {
			String href = element.getAttribute("href");
			Matcher matcher = pattern.matcher(href);
			if (matcher.find()) {
				String stockSymbol = matcher.group(1);
				stockSymbols.add(stockSymbol);
			}
		}
		System.out.println("Extracted Symbols from the page : " + stockSymbols);
		return stockSymbols;
	}

	@AfterClass
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}
}
