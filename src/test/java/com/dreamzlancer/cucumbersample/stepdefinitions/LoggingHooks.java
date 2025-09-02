package com.dreamzlancer.cucumbersample.stepdefinitions;


import com.dreamzlancer.cucumbersample.utils.XrayLogger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class LoggingHooks {

    @Before
    public void beforeScenario(Scenario scenario) {
        // Clear any previous logs at the start of each scenario
        XrayLogger.clear();
        XrayLogger.log("Starting scenario: " + scenario.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        // Attach all accumulated logs at the end of the scenario
        XrayLogger.log("Finished scenario: " + scenario.getName() + " with status: " + scenario.getStatus());
        XrayLogger.flushToXray(scenario);
    }
}