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

	private String testCaseKey; // Mandatory: Vansah Test Case Key
	// Mandatory: Provide Issue Key or Test Folder ID to which the Test Case is associated
	private String jiraIssueKey = "TEST-5"; 
	private String testedSprint = "TEST Sprint 1"; // The sprint during which the test was conducted
	private String testedEnv = "UAT";    // The environment in which the test was conducted
	private String testedVersion = "Version 0.0.1"; // The version of the software tested

	private int result = 2; // Default value `2`: "passed"
	private VansahNode vansahTest; // VansahNode object to manage test details

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
		vansahTest = new VansahNode(); // Initialize a new VansahNode object

		// Set the Vansah token using an environment variable
		vansahTest.setVansahToken(System.getenv("VANSAH_TOKEN"));

		// Set the JIRA issue key to associate the test case with the appropriate issue
		vansahTest.setJIRA_ISSUE_KEY(jiraIssueKey);

		// Set the sprint name to indicate which sprint the test is part of
		vansahTest.setSPRINT_NAME(testedSprint);

		// Set the environment name to specify the environment in which the test is executed
		vansahTest.setENVIRONMENT_NAME(testedEnv);

		// Set the release name to denote the version of the software being tested
		vansahTest.setRELEASE_NAME(testedVersion);

		// Read the Applitools API key from an environment variable.
		applitoolsApiKey = System.getenv("APPLITOOLS_API_KEY");

		// Create the Classic runner for local execution.
		runner = new ClassicRunner();

		// Create a new batch for tests.
		String runnerName = "Classic runner";
		batch = new BatchInfo("UAT Regression Testing on : " + runnerName);

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
	    options.addArguments("--headless"); // Enable headless mode
	    options.addArguments("--disable-gpu"); // Applicable to Windows OS

		// Open the browser with a local ChromeDriver instance.
		driver = new ChromeDriver(options);


		// Set an implicit wait of 10 seconds.
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
	        // Get all test results from the runner and print them.
	        TestResultsSummary allTestResults = runner.getAllTestResults();
	        System.out.println(allTestResults);
	    } catch (Exception e) {
	        // If any exception occurs, set the result to "failed" (1) and print the error message.
	        result = 1;
	        System.out.println(e.getMessage());
	    } catch (DiffsFoundException e) {
	        // If differences are found, set the result to "failed" (1) and print the error message.
	        result = 1;
	        System.out.println(e.getMessage());
	    } finally {
	        // Add the test result to Vansah from Jira Issue with the specified testCaseKey and result.
	        vansahTest.addQuickTestFromJiraIssue(testCaseKey, result);
	    }
	}

}
