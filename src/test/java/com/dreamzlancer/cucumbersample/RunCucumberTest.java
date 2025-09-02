package com.dreamzlancer.cucumbersample;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters({
        @ConfigurationParameter(key = GLUE_PROPERTY_NAME,
                value = "com.dreamzlancer.cucumbersample.stepdefinitions, com.dreamzlancer.cucumbersample.configuration"),
        @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
                value = "pretty, html:target/cucumber-reports/cucumber-reports.html, json:target/cucumber-reports/cucumber.json, junit:target/cucumber-reports/cucumber-reports.xml"),
        @ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME,
                value = "not @ignored"),
        @ConfigurationParameter(key = JUNIT_PLATFORM_NAMING_STRATEGY_PROPERTY_NAME,
                value = "long")
})
public class RunCucumberTest {
        // Empty class - annotations configure everything
}