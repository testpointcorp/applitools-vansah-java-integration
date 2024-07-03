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

	// Test constants
	private final static boolean USE_ULTRAFAST_GRID = false;
	private final static boolean USE_EXECUTION_CLOUD = false;

	// Test control inputs to read once and share for all tests
	private static String applitoolsApiKey;
	private static boolean headless;

	// Applitools objects to share for all tests
	private static BatchInfo batch;
	private static Configuration config;
	private static EyesRunner runner;

	// Test-specific objects
	private WebDriver driver;
	private Eyes eyes;

	@BeforeMethod()
	public void setup() throws MalformedURLException {
		
		// Set up Vansah Test Variables
		vansahTest = new VansahNode();
		vansahTest.setVansahToken(System.getenv("VANSAH_TOKEN"));
		vansahTest.setJIRA_ISSUE_KEY(jiraIssueKey);
		vansahTest.setSPRINT_NAME(testSprint);
		
		// Read the Applitools API key from an environment variable.
		applitoolsApiKey = System.getenv("APPLITOOLS_API_KEY");

		// Use headless mode for Continuous Integration (CI) execution.
		// Use headed mode for local development.
		headless = false;

		if (USE_ULTRAFAST_GRID) {
			// Create the runner for the Ultrafast Grid.
			// Concurrency refers to the number of visual checkpoints Applitools will perform in parallel.
			// Warning: If you have a free account, then concurrency will be limited to 1.
			runner = new VisualGridRunner(new RunnerOptions().testConcurrency(1));
		}
		else {
			// Create the Classic runner for local execution.
			runner = new ClassicRunner();
		}

		// Create a new batch for tests.
		// A batch is the collection of visual checkpoints for a test suite.
		// Batches are displayed in the Eyes Test Manager, so use meaningful names.
		String runnerName = (USE_ULTRAFAST_GRID) ? "Ultrafast Grid" : "Classic runner";
		batch = new BatchInfo("UAT Regression Testin on : " + runnerName);

		// Create a configuration for Applitools Eyes.
		config = new Configuration();

		// Set the Applitools API key so test results are uploaded to your account.
		// If you don't explicitly set the API key with this call,
		// then the SDK will automatically read the `APPLITOOLS_API_KEY` environment variable to fetch it.
		config.setApiKey(applitoolsApiKey);

		// Set the batch for the config.
		config.setBatch(batch);

		// If running tests on the Ultrafast Grid, configure browsers.
		if (USE_ULTRAFAST_GRID) {
			// Add 3 desktop browsers with different viewports for cross-browser testing in the Ultrafast Grid.
			config.addBrowser(1024, 768, BrowserType.CHROME);
		}
		//WebDriver Manager
		WebDriverManager.chromedriver().setup();


		// This method sets up each test with its own ChromeDriver and Applitools Eyes objects.


		// Create ChromeDriver options
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");

		if (USE_EXECUTION_CLOUD) {
			// Open the browser remotely in the Execution Cloud.
			driver = new RemoteWebDriver(new URL(Eyes.getExecutionCloudURL()), options);
		}
		else {
			// Open the browser with a local ChromeDriver instance.
			driver = new ChromeDriver(options);
		}

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

		// Open Eyes to start visual testing.
		// It is a recommended practice to set all four inputs:
		
	}
	
	@Test()
	public void login() {
		//Vansah Test Case Key
		testCaseKey = "TEST-C9";
		eyes.open(

				// WebDriver object to "watch".
				driver,

				// The name of the application under test.
				// All tests for the same app should share the same app name.
				// Set this name wisely: Applitools features rely on a shared app name across tests.
				"Test App",

				// The name of the test case for the given application.
				// Additional unique characteristics of the test may also be specified as part of the test name,
				// such as localization information ("Home Page - EN") or different user permissions ("Login by admin"). 
				testCaseKey,

				// The viewport size for the local browser.
				// Eyes will resize the web browser to match the requested viewport size.
				// This parameter is optional but encouraged in order to produce consistent results.
				new RectangleSize(1200, 600));
		
		// This test covers login for the Applitools demo site, which is a dummy banking app.
		// Load the login page.
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
		eyes.open(

				// WebDriver object to "watch".
				driver,

				// The name of the application under test.
				// All tests for the same app should share the same app name.
				// Set this name wisely: Applitools features rely on a shared app name across tests.
				"Test App",

				// The name of the test case for the given application.
				// Additional unique characteristics of the test may also be specified as part of the test name,
				// such as localization information ("Home Page - EN") or different user permissions ("Login by admin"). 
				testCaseKey,

				// The viewport size for the local browser.
				// Eyes will resize the web browser to match the requested viewport size.
				// This parameter is optional but encouraged in order to produce consistent results.
				new RectangleSize(1200, 600));
		
		// Load the login page.
		driver.get("https://selenium.vansah.io/");

		// Verify the full login page loaded correctly.
		eyes.check(Target.window().fully().withName("Vansah Home Page"));
	}

	@AfterMethod
	public void cleanUpTest() throws Exception {

		// Close Eyes to tell the server it should display the results.
		eyes.closeAsync();

		// Quit the WebDriver instance.
		driver.quit();
		// Close the batch and report visual differences to the console.
		// Note that it forces JUnit to wait synchronously for all visual checkpoints to complete.
		try {
		TestResultsSummary allTestResults = runner.getAllTestResults();
		System.out.println(allTestResults);}
		catch(DiffsFoundException e) {
			result = 1;
			System.out.println(e.getMessage());
		}finally {
			vansahTest.addQuickTestFromJiraIssue(testCaseKey, result);
		}

		// Warning: `eyes.closeAsync()` will NOT wait for visual checkpoints to complete.
		// You will need to check the Eyes Test Manager for visual results per checkpoint.
		// Note that "unresolved" and "failed" visual checkpoints will not cause the JUnit test to fail.

		// If you want the JUnit test to wait synchronously for all checkpoints to complete, then use `eyes.close()`.
		// If any checkpoints are unresolved or failed, then `eyes.close()` will make the JUnit test fail.
	}

}
