# Applitools Integration with Vansah Test Management For Jira
This tutorial guides you through the process of integrating Applitools with Vansah Test Management for Jira to automatically send your test case results.

By following this setup, you can streamline your testing workflow, ensuring that test outcomes are recorded directly in your Jira workspace.
## Prerequisites
- Applitools - [Applitools](https://applitools.com/) project is already setup.
- Vansah Binding - Download VansahNode.java file from this repo [`VansahNode.java`](https://github.com/testpointcorp/Vansah-API-Binding-Java/blob/prod/src/main/java/com/vansah/VansahNode.java).
- Make sure that [`Vansah`](https://marketplace.atlassian.com/apps/1224250/vansah-test-management-for-jira?tab=overview&hosting=cloud) is installed in your Jira workspace
- You need to Generate **Vansah** [`connect`](https://docs.vansah.com/docs-base/generate-a-vansah-api-token-from-jira-cloud/) token to authenticate with Vansah APIs.

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

1. **Place the VansahBinding.java File**: Ensure that [`VansahBinding.java`](/src/test/java/com/vansah/VansahNode.java) is located in `/applitools-vansah/src/test/java`
2. Define Vansah constants: 
    ```java
    
    private String testCaseKey; // Mandatory: Vansah Test Case Key

    // Mandatory: Provide Issue Key or Test Folder ID to which the Test Case is associated
    private String jiraIssueKey = "TEST-5"; 

    private String testedSprint = "TEST Sprint 1"; // The sprint during which the test was conducted
    private String testedEnv = "TEST Sprint 1";    // The environment in which the test was conducted
    private String testedVersion = "TEST Sprint 1"; // The version of the software tested

    private int result = 2; // Default value `2`: "passed"
    private VansahNode vansahTest; // VansahNode object to manage test details

	
    ```
3. Set up Vansah Variables in setupBeforeEachTest() with `@BeforeMethod` Annotation
    ```Java
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

    ```
4. Set up `sending` results to Vansah in cleanUpAfterEachTest() with `@AfterMethod` Annotation
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
    ```

### Conclusion
By following the above steps, your Applitools project will be equipped to send test run results directly to Vansah, streamlining your testing and reporting process.

Ensure that all files are placed and configured as described to facilitate successful integration.

For more details on Applitools, visit the [docs](https://applitools.com/docs/index.html).

For Vansah specific configurations and API details, please refer to the [Vansah API documentation](https://apidoc.vansah.com/).
