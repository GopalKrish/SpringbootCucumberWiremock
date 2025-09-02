package com.dreamzlancer.cucumbersample.stepdefinitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.http.Request;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.dreamzlancer.cucumbersample.utils.JsonTemplateReader;
import com.dreamzlancer.cucumbersample.utils.LoggingUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserStepDefinitions {

    private static final Logger log = LogManager.getLogger(UserStepDefinitions.class);
    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ResponseEntity<String> response;
    private String lastUserId;
    private Map<String, String> currentTestData = new HashMap<>();

    @Before
    public void setup() {
        lastUserId = null;
        currentTestData.clear();
        //log.info("=== SETUP: Reset test state ===");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Given("the API is running successfully")
    public void verifyApiRunning() {
        assertTrue(port > 0, "Server port should be assigned");
        log.info("API running on port: {}", port);
    }

    @When("I send a GET request to {string}")
    public void sendGetRequest(String endpoint) {
        String url = getBaseUrl() + endpoint;


        if (endpoint.contains("{id}") && lastUserId != null) {
            url = url.replace("{id}", lastUserId);
            log.debug("GET request to: {} (using ID: {})", url, lastUserId);
        } else {
            log.debug("GET request to: {}", url);
        }

        LoggingUtils.logRequest("GET", url, null);
        response = restTemplate.getForEntity(url, String.class);
        LoggingUtils.logResponse(response);
    }

    @When("I send a POST request to {string} with the following user data:")
    public void sendPostRequestWithTable(String endpoint, DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps();
        Map<String, String> userData = data.get(0);
        currentTestData.putAll(userData);

        String requestBody = JsonTemplateReader.readTemplate(
                "templates/user-create.json",
                userData
        );

        String url = getBaseUrl() + endpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        LoggingUtils.logRequest("POST", url, requestBody);
        response = restTemplate.postForEntity(url, request, String.class);
        LoggingUtils.logResponse(response);

        extractAndStoreUserId();
    }

    @Given("a user exists with the following data:")
    public void createUserWithTable(DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps();
        Map<String, String> userData = data.get(0);
        currentTestData.putAll(userData);

        String requestBody = JsonTemplateReader.readTemplate(
                "templates/user-create.json",
                userData
        );

        String url = getBaseUrl() + "/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        log.info("Creating user with data: {}", userData);
        LoggingUtils.logRequest("POST", url, requestBody);

        ResponseEntity<String> createResponse = restTemplate.postForEntity(url, request, String.class);
        LoggingUtils.logResponse(createResponse);

        extractAndStoreUserId(createResponse.getBody());

        // Verify the user was actually created by fetching it
        if (lastUserId != null) {
            String getUserUrl = getBaseUrl() + "/users/" + lastUserId;
            log.debug("Verifying user creation by fetching: {}", getUserUrl);
            ResponseEntity<String> verificationResponse = restTemplate.getForEntity(getUserUrl, String.class);
            log.debug("User verification response: {}", verificationResponse.getStatusCode());
        }
    }

    @When("I send a PUT request to {string} with the updated data:")
    public void sendPutRequestWithTable(String endpoint, DataTable dataTable) throws IOException {
        List<Map<String, String>> data = dataTable.asMaps();
        Map<String, String> userData = data.get(0);
        currentTestData.putAll(userData);

        if (lastUserId == null) {
            log.error("Cannot send PUT request - no user ID available");
            throw new IllegalStateException("Cannot send PUT request - no user ID available");
        }

        String requestBody = JsonTemplateReader.readTemplate(
                "templates/user-create.json",
                userData
        );

        String url = getBaseUrl() + endpoint.replace("{id}", lastUserId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        LoggingUtils.logRequest("PUT", url, requestBody);
        response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
        LoggingUtils.logResponse(response);
    }

    @When("I send a DELETE request to {string}")
    public void sendDeleteRequest(String endpoint) {
        String url = getBaseUrl() + endpoint;

        if (endpoint.contains("{id}") && lastUserId != null) {
            url = url.replace("{id}", lastUserId);
        }

        LoggingUtils.logRequest("DELETE", url, null);
        response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
        LoggingUtils.logResponse(response);
    }

    private void extractAndStoreUserId() {
        extractAndStoreUserId(response.getBody());
    }

    private void extractAndStoreUserId(String responseBody) {
        if (responseBody != null) {
            log.debug("Extracting ID from response: {}", responseBody);

            try {
                // Use Jackson to parse JSON properly
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                Object idObj = responseMap.get("id");
                if (idObj != null) {
                    lastUserId = idObj.toString();
                    currentTestData.put("id", lastUserId);
                    log.info("Extracted User ID: {}", lastUserId);
                    return;
                }
            } catch (Exception e) {
                log.warn("JSON parsing failed: {}", e.getMessage());
            }

            // Fallback to string manipulation
            if (responseBody.contains("\"id\"")) {
                String[] parts = responseBody.split("\"id\"");
                if (parts.length > 1) {
                    String idPart = parts[1].replaceAll("[^0-9]", " ").trim();
                    String[] numbers = idPart.split("\\s+");
                    if (numbers.length > 0) {
                        lastUserId = numbers[0];
                        currentTestData.put("id", lastUserId);
                        log.info("Extracted User ID (fallback): {}", lastUserId);
                    }
                }
            }
        } else {
            log.warn("Response body is null - cannot extract ID");
        }
    }

    @Then("the response status should be {int}")
    public void verifyResponseStatus(int expectedStatus) {
        assertNotNull(response, "Response should not be null");
        assertEquals(expectedStatus, response.getStatusCode().value());
        log.debug("Verified response status: {}", expectedStatus);
    }

    @Then("the response should contain the created user data:")
    public void verifyResponseContainsCreatedUserData(DataTable dataTable) throws IOException {
        verifyResponseAgainstTemplate(dataTable, "templates/user-response.json");
    }

    @Then("the response should contain the user data:")
    public void verifyResponseContainsUserData(DataTable dataTable) throws IOException {
        verifyResponseAgainstTemplate(dataTable, "templates/user-response.json");
    }

    @Then("the response should contain the updated user data:")
    public void verifyResponseContainsUpdatedUserData(DataTable dataTable) throws IOException {
        verifyResponseAgainstTemplate(dataTable, "templates/user-response.json");
    }

    private void verifyResponseAgainstTemplate(DataTable dataTable, String templatePath) throws IOException {
        List<Map<String, String>> expectedData = dataTable.asMaps();
        Map<String, String> expectedValues = expectedData.get(0);

        currentTestData.putAll(expectedValues);

        String expectedJson = JsonTemplateReader.readTemplate(templatePath, currentTestData);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getBody(), "Response body should not be null");

        String actualJson = response.getBody();
        log.debug("Expected JSON: {}", expectedJson);
        log.debug("Actual JSON: {}", actualJson);

        for (Map.Entry<String, String> entry : expectedValues.entrySet()) {
            String expectedValue = entry.getValue();
            assertTrue(actualJson.contains(expectedValue),
                    "Response should contain " + entry.getKey() + ": " + expectedValue);
        }

        log.debug("Response verification successful");
    }

    @Then("the response should have a valid id")
    public void verifyResponseHasValidId() {
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getBody(), "Response body should not be null");

        String responseBody = response.getBody();
        assertTrue(responseBody.contains("\"id\":"), "Response should contain an ID");

        Pattern pattern = Pattern.compile("\"id\":\\s*(\\d+)");
        Matcher matcher = pattern.matcher(responseBody);
        assertTrue(matcher.find(), "ID should be a valid number");

        String id = matcher.group(1);
        assertTrue(Long.parseLong(id) > 0, "ID should be positive");
        lastUserId = id;
        currentTestData.put("id", id);

        log.info("Verified valid ID: {}", id);
    }

    @Then("the user should no longer exist")
    public void verifyUserNoLongerExists() {
        if (lastUserId == null) {
            log.error("Cannot verify user deletion - no user ID available");
            throw new IllegalStateException("Cannot verify user deletion - no user ID available");
        }

        String url = getBaseUrl() + "/users/" + lastUserId;
        log.debug("Verifying user deletion at: {}", url);
        ResponseEntity<String> getResponse = restTemplate.getForEntity(url, String.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode(),
                "User should not exist after deletion");

        log.info("User {} successfully deleted", lastUserId);
    }

    @Then("the response should be an empty array")
    public void verifyEmptyArrayResponse() {
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getBody(), "Response body should not be null");

        String responseBody = response.getBody().trim();
        assertTrue(responseBody.equals("[]") || responseBody.equals(""),
                "Response should be an empty array, but was: " + responseBody);

        log.debug("Verified empty array response");
    }

    @Then("debug: show current state")
    public void debugShowState() {
        log.info("=== CURRENT STATE ===");
        log.info("Last User ID: {}", lastUserId);
        log.info("Current Test Data: {}", currentTestData);
        log.info("Response Status: {}", response != null ? response.getStatusCode() : "null");
        log.info("Response Body: {}", response != null ? response.getBody() : "null");
        log.info("====================");
    }

    @Given("I request bank details for user ID {int}")
    public void iRequestBankDetailsForUserID(int userId) {

        String baseUrl = "http://localhost:" + port + "/api/users/"+ userId + "/bank-details";
        response = restTemplate.getForEntity(baseUrl, String.class);
    }

    @And("Verify the users bank response data")
    public void verifyTheUsersBankResponseData() throws JsonProcessingException {
        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(responseBody);
        assertTrue(responseJson.has("accountNumber"), "Response should contain timestamp");
        assertTrue(responseJson.has("bankName"), "Response should contain status");
        assertTrue(responseJson.has("branchCode"), "Response should contain errors");
        assertTrue(responseJson.has("ifscCode"), "Response should contain errors");
        assertTrue(responseJson.has("lastUpdated"), "Response should contain errors");
    }
}