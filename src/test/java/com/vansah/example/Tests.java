package com.vansah.example;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.annotations.*;

import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.exceptions.DiffsFoundException;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.vansah.VansahNode;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class Tests {

	//Vansah constants
	private String testCaseKey;
	private String jiraIssueKey = "TEST-5";
	private String testSprint = "TEST Sprint 1";
	private int result = 2;
	private VansahNode vansahTest;

	// Test control inputs to read once and share for all tests
	private static String applitoolsApiKey;

	// Applitools objects to share for all tests
	private static BatchInfo batch;
	private static Configuration config;
	private static EyesRunner runner;

	// Test-specific objects
	private WebDriver driver;
	private Eyes eyes;

	@BeforeMethod()
	public void setupBeforeEachTest() throws MalformedURLException {

		// Set up Vansah Test Variables
		vansahTest = new VansahNode();
		vansahTest.setVansahToken(System.getenv("VANSAH_TOKEN"));
		vansahTest.setJIRA_ISSUE_KEY(jiraIssueKey);
		vansahTest.setSPRINT_NAME(testSprint);

		// Read the Applitools API key from an environment variable.
		applitoolsApiKey = System.getenv("APPLITOOLS_API_KEY");

		// Create the Classic runner for local execution.
		runner = new ClassicRunner();

		// Create a new batch for tests.
		String runnerName = "Classic runner";
		batch = new BatchInfo("UAT Regression Testin on : " + runnerName);

		// Create a configuration for Applitools Eyes.
		config = new Configuration();

		// Set the Applitools API key so test results are uploaded to your account.
		config.setApiKey(applitoolsApiKey);

		// Set the batch for the config.
		config.setBatch(batch);

		//WebDriver Manager
		WebDriverManager.chromedriver().setup();


		// Create ChromeDriver options
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");

		// Open the browser with a local ChromeDriver instance.
		driver = new ChromeDriver(options);


		// Set an implicit wait of 10 seconds.
		// For larger projects, use explicit waits for better control.
		// https://www.selenium.dev/documentation/webdriver/waits/
		// The following call works for Selenium 4:
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

		// If you are using Selenium 3, use the following call instead:
		// driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		// Create the Applitools Eyes object connected to the runner and set its configuration.
		eyes = new Eyes(runner);
		eyes.setConfiguration(config);

	}

	@Test()
	public void login() {

		//Vansah Test Case Key
		testCaseKey = "TEST-C9";

		//Start a Test
		eyes.open(driver,"Test App",testCaseKey,new RectangleSize(1200, 600));

		// Load the Selenium Vansah Test page.
		driver.get("https://selenium.vansah.io/");

		// Verify the full login page loaded correctly.
		eyes.check(Target.window().fully().withName("Vansah Home Page"));

		// Click on Try Now Button.
		driver.findElement(By.id("vansah-trynow")).click();

		// Verify the full main page loaded correctly.
		eyes.check(Target.window().fully().withName("Vansah MarketPlace").layout());
	}
	@Test()
	public void landingPage() {

		//Vansah Test Case Key
		testCaseKey = "TEST-C8";

		//Start a Test
		eyes.open(driver,"Test App",testCaseKey,new RectangleSize(1200, 600));

		// Load the login page.
		driver.get("https://selenium.vansah.io/");

		// Verify the full login page loaded correctly.
		eyes.check(Target.window().fully().withName("Vansah Home Page"));
	}

	@AfterMethod
	public void cleanUpAfterEachTest() throws Exception {

		// Close Eyes to tell the server it should display the results.
		eyes.closeAsync();

		// Quit the WebDriver instance.
		driver.quit();
		try {
			TestResultsSummary allTestResults = runner.getAllTestResults();
			System.out.println(allTestResults);}
		catch(DiffsFoundException e) {
			result = 1;
			System.out.println(e.getMessage());
		}finally {
			vansahTest.addQuickTestFromJiraIssue(testCaseKey, result);
		}
	}

}
