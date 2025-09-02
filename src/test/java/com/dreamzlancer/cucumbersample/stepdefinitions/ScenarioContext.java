package com.dreamzlancer.cucumbersample.stepdefinitions;
import io.cucumber.java.Scenario;

public class ScenarioContext {
    private static final ThreadLocal<Scenario> currentScenario = new ThreadLocal<>();

    public static void setScenario(Scenario scenario) {
        currentScenario.set(scenario);
    }

    public static Scenario getScenario() {
        return currentScenario.get();
    }

    public static void clear() {
        currentScenario.remove();
    }
}