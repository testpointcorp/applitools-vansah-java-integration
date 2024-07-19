# Applitools Integration with Vansah Test Management For Jira
Follow the instructions below to integrate [Applitools](https://applitools.com/) with [`Vansah Test Management`](https://marketplace.atlassian.com/apps/1224250/vansah-test-management-for-jira?tab=overview&hosting=cloud) for Jira using Java. The integration will allow you to send your Test Case results to Vansah

## Prerequisites
- Applitools - [Applitools](https://applitools.com/) project is already set up with the API token properly configured in the environment variables.
- Make sure you have installed all your Maven and TestNG dependencies.
	
- Vansah Binding - Download VansahNode.java file from this repo [`VansahNode.java`](https://github.com/testpointcorp/Vansah-API-Binding-Java/blob/prod/src/main/java/com/vansah/VansahNode.java).
- Make sure that [`Vansah`](https://marketplace.atlassian.com/apps/1224250/vansah-test-management-for-jira?tab=overview&hosting=cloud) is installed in your Jira workspace
- You need to Generate  [`Vansah connect`](https://docs.vansah.com/docs-base/generate-a-vansah-api-token-from-jira-cloud/) token to authenticate with Vansah APIs.

## Configuration
**Setting Environment Variables** - Store your Vansah API token as an environment variable for security. 

For Windows (use cmd)
```cmd
setx VANSAH_TOKEN "your_vansah_api_token_here"	
```
For macOS
```bash
echo export VANSAH_TOKEN="your_vansah_api_token_here" >> ~/.bash_profile
source ~/.bash_profile
```
For Linux (Ubuntu, Debian, etc.)
```bash
echo export VANSAH_TOKEN="your_vansah_api_token_here" >> ~/.bashrc
source ~/.bashrc
```

## Implementation
To enable Vansah Integration in Applitools project, follow these steps:

1. Make sure you have mentioned all your Maven and TestNG dependencies in the [pom.xml](https://github.com/testpointcorp/applitools-vansah-java-integration/blob/main/pom.xml)
       
	   <dependencies>
		<dependency>
			<groupId>com.mashape.unirest</groupId>
			<artifactId>unirest-java</artifactId>
			<version>1.4.9</version>
		</dependency>
		<dependency>
			<groupId>org.seleniumhq.selenium</groupId>
			<artifactId>selenium-java</artifactId>
			<version>4.8.0</version>
		</dependency>
		<dependency>
			<groupId>com.applitools</groupId>
			<artifactId>eyes-selenium-java5</artifactId>
			<version>5.57.0</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.testng</groupId>
			<artifactId>testng</artifactId>
			<version>7.7.1</version>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>io.github.bonigarcia</groupId>
			<artifactId>webdrivermanager</artifactId>
			<version>5.9.1</version>
		</dependency>

	   </dependencies>
	
	

2. Ensure that [`VansahBinding.java`](/src/test/java/com/vansah/VansahNode.java) is located in your package `/applitools-vansah/src/test/java`
2. Define Vansah constants in the Java Class file: 
    ```java
    
    private String testCaseKey; // Mandatory: Vansah Test Case Key

    // Mandatory: Provide Issue Key or Test Folder ID to which the Test Case is associated
    private String jiraIssueKey = "TEST-5"; 

    private String testedSprint = "TEST Sprint 1"; //The sprint during which the test was conducted
    private String testedEnv = "STAGING";    // The environment in which the test was conducted
    private String testedVersion = "v1"; // The version of the software tested

    private int result = 2; // Default value `2`: "passed"
    private VansahNode vansahTest; // VansahNode object to manage test details

	
    ```
3. Set up Vansah Variables in setupBeforeEachTest() with `@BeforeMethod` Annotation
    ```Java
    @BeforeMethod
     public void setupBeforeEachTest() throws MalformedURLException {
    // Set up Vansah Test Variables
    vansahTest = new VansahNode(); // Initialize a new VansahNode object

    // Set the Vansah token using an environment variable
    vansahTest.setVansahToken(System.getenv("VANSAH_TOKEN"));

    // Set the JIRA issue key to associate the test case with the appropriate issue or set the TESTFOLDERS_ID
    vansahTest.setJIRA_ISSUE_KEY(jiraIssueKey);

    // Set the sprint name to indicate which sprint the test is part of
    vansahTest.setSPRINT_NAME(testedSprint);

    // Set the environment name to specify the environment in which the test is executed
    vansahTest.setENVIRONMENT_NAME(testedEnv);

    // Set the release name to denote the version of the software being tested
    vansahTest.setRELEASE_NAME(testedVersion);
    
    ```
4. Add Test Case Key value to each of your Test
    ```java
    @Test
	public void landingPage() {

		//Vansah Test Case Key
		testCaseKey = "TEST-C8";
		/**
		 Your Test logic
		**/
	}

    ```
5.   Set up to `send` the results to Vansah in cleanUpAfterEachTest() with      `@AfterMethod` Annotation
     ```Java
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
**Please note :** If you encounter any other Exception, **catch** it here, set result=1 to log the result as **failed** in Vansah. 
### 
By following the above steps, your Applitools project will be equipped to send test run results directly to Vansah, streamlining your testing and reporting process.

Ensure that all files are placed and configured as described to facilitate successful integration.

For more details on Applitools, visit the [docs](https://applitools.com/docs/index.html).

For Vansah specific configurations and API details, please refer to the [Vansah API documentation](https://apidoc.vansah.com/).
