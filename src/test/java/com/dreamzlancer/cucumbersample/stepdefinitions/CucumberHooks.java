package com.dreamzlancer.cucumbersample.stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;

public class CucumberHooks {

    @Before
    public void beforeScenario(Scenario scenario) {
        System.out.println(">>> Before hook fired for: " + scenario.getName());
        ScenarioContext.setScenario(scenario);
    }

    @After
    public void afterScenario(Scenario scenario) {
        System.out.println(">>> Before hook fired for: " + scenario.getName());
        ScenarioContext.clear();
    }
}