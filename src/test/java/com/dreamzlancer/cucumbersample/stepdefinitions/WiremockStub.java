package com.dreamzlancer.cucumbersample.stepdefinitions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import wiremock.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Slf4j
public class WiremockStub {

    private static WireMockServer wireMockServer;

    private static String url = "/users";

    @BeforeAll
    public static void beforeAll() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8089));
        wireMockServer.start();
    }

    @Before
    public void beforeScenario() {
        wireMockServer.resetAll();
    }

    @After
    public void afterScenario() {
        wireMockServer.resetAll();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @And("Wiremock server is running")
    public void wiremockServerIsRunning() {
        Assert.assertTrue("Wiremock server is not running", wireMockServer.isRunning());
    }
    @And("Add stub {string} and verify response as {int}")
    public void addStubAndVerify(String filePath, int statusCode) throws IOException {
        String responseBody = IOUtils.resourceToString("/wiremock/user/response/"+filePath, StandardCharsets.UTF_8);
        wireMockServer.stubFor(
                get(urlEqualTo(url))
                        .willReturn(okJson(responseBody)
                                .withStatus(statusCode))
        );

    }

    @And("Add stub for bank {string} and verify response as {int}")
    public void addStubForBankAndVerify(String filePath, int statusCode) throws IOException {
        String responseBody = IOUtils.resourceToString("/wiremock/user/response/"+filePath, StandardCharsets.UTF_8);
        wireMockServer.stubFor(
                get(urlEqualTo("/users/123/bank-details"))
                        .willReturn(okJson(responseBody)
                                .withStatus(statusCode))
        );

    }

}
