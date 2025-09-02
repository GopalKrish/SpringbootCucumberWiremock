package com.dreamzlancer.cucumbersample.utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class XrayEvidenceClient {

    private final String jiraBaseUrl;
    private final String username;
    private final String apiToken;

    public XrayEvidenceClient(String jiraBaseUrl, String username, String apiToken) {
        this.jiraBaseUrl = jiraBaseUrl;
        this.username = username;
        this.apiToken = apiToken;
    }

    public void addEvidenceToTest(String testExecutionKey, String testKey,
                                  String evidenceType, String content, String filename) {

        String evidenceJson = String.format("""
            {
                "testExecutionKey": "%s",
                "testKey": "%s",
                "evidence": {
                    "data": "%s",
                    "filename": "%s",
                    "contentType": "%s"
                }
            }
            """, testExecutionKey, testKey, content, filename, getContentType(evidenceType));

        Response response = RestAssured.given()
                .baseUri(jiraBaseUrl)
                .auth().preemptive().basic(username, apiToken)
                .contentType(ContentType.JSON)
                .body(evidenceJson)
                .when()
                .post("/rest/raven/1.0/api/testexec/" + testExecutionKey + "/test/" + testKey + "/evidence")
                .then()
                .extract().response();

        if (response.getStatusCode() != 200) {
            System.err.println("Failed to add evidence: " + response.getBody().asString());
        }
    }

    private String getContentType(String evidenceType) {
        switch (evidenceType.toLowerCase()) {
            case "json": return "application/json";
            case "html": return "text/html";
            case "xml": return "application/xml";
            default: return "text/plain";
        }
    }
}