package com.converted.currency;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CurrencyConvertor {

	private static final String WEBDRIVER_CHROME_DRIVER = "webdriver.chrome.driver";
	WebDriver driver;

	@BeforeClass
	public void openWebPage() {
		System.setProperty(WEBDRIVER_CHROME_DRIVER, "D:\\workspace\\FirstProject\\driver\\chromedriver.exe"); // Set
																												// driver
																												// property
		driver = new ChromeDriver();
		driver.get("https://www.xe.com/currencyconverter"); // open web site on browser
		driver.manage().window().maximize();
	}

	@Test
	public void currencyConvertedTest() throws IOException, InterruptedException {
		String[] inputs = { "USD", "EUR", "GBP", "SGD", "AUD", "CAD" };
		Object[][] data = new Object[inputs.length][3];
		WebDriverWait wait = new WebDriverWait(driver, 10);
		WebElement advertisement = explicitlyWaitOnClickable(wait,
				"//button[@id=\"yie-close-button-32ec8347-ab2e-50a5-8a0e-078bb5725b5d\"]");
		advertisement.click();
		WebElement selectFromCurrency = explicitlyWaitOnClickable(wait, "//input[@id=\"midmarketFromCurrency\"]");
		selectFromCurrency.click();
		selectFromCurrency.sendKeys("INR");
		List<WebElement> availableFromOptionsList = explicitlyWaitList(wait,
				"//ul[@id=\"midmarketFromCurrency-listbox\"]/li");
		for (WebElement option : availableFromOptionsList) {
			if (option.getText().contains("INR -")) {
				option.click();
				break;
			}
		}
		int i = 0;
		for (String str : inputs) {
			int j = 0;
			data[i][j++] = "INR";
			data[i][j++] = str;
			WebElement selectToCurrency = explicitlyWaitOnClickable(wait, "//input[@id=\"midmarketToCurrency\"]");
			selectToCurrency.click();
			selectToCurrency.sendKeys(str);
			// wait
			List<WebElement> availableToOptionsList = explicitlyWaitList(wait,
					"//ul[@id=\"midmarketToCurrency-listbox\"]/li");

			for (WebElement option : availableToOptionsList) {
				if (option.getText().contains(str + " -")) {
					option.click();
					WebElement button = explicitlyWait(wait, "//a[@class=\"BaseButton-fEuaOx bdGGGb\"]"); // find submit
																											// button
					button.click();
					break;
				}
			}
			WebElement result = explicitlyWait(wait, "(//h1[@id=\"main-heading\"])[1]");
			String[] value = result.getText().split(" ");
			for (int l = 0; l < value.length; l++) {
				if ("=".equals(value[l])) {
					data[i++][j++] = value[l + 1].trim();
					break;
				}
			}
		}
		prepareExcel(data);
		for (int k = 0; k < inputs.length; k++) {
			for (Object ob : data[k]) {
				System.out.print(ob + "  ");
			}
			System.out.println();
		}
	}

	private void prepareExcel(Object[][] data) throws IOException {
		Workbook workbook = null;
		OutputStream fis = null;
		try {
			fis = new FileOutputStream("C:\\Users\\nipun\\Desktop\\AutomationTest.xlsx");
			workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("CurrencyConvertor");
			for (int i = 0; i < data.length; i++) {
				Row row = sheet.createRow(i);
				for (int j = 0; j < 3; j++) {
					Cell cell = row.createCell(j);
					cell.setCellValue(data[i][j] + "");
				}
			}
			workbook.write(fis);
		} finally {
			fis.close();
			workbook.close();
		}
	}

	public WebElement explicitlyWait(WebDriverWait wait, String xPath) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));

	}

	public List<WebElement> explicitlyWaitList(WebDriverWait wait, String xPath) {
		return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(xPath)));

	}

	public WebElement explicitlyWaitOnClickable(WebDriverWait wait, String xPath) {
		return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xPath)));
	}

	@AfterTest
	public void distroy() {
		driver.quit();
	}

}
