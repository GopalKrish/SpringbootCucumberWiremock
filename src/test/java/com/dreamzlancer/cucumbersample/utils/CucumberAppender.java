package com.dreamzlancer.cucumbersample.utils;

import com.dreamzlancer.cucumbersample.stepdefinitions.ScenarioContext;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;

@Plugin(name = "CucumberAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class CucumberAppender extends AbstractAppender {

    protected CucumberAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    @PluginFactory
    public static CucumberAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Filter") final Filter filter,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions) {

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new CucumberAppender(name, filter, layout, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        Scenario scenario = ScenarioContext.getScenario();
        System.out.println(">>> Scenario in appender = " + scenario); // 👈 debug
        if (scenario != null) {
           String log = event.getLevel() + " - " + event.getMessage().getFormattedMessage() ;
            //scenario.log(event.getLevel() + " - " + event.getMessage().getFormattedMessage());
            scenario.attach(log.getBytes(), "text/html", "Test Log");
        }

        XrayLogger.log(event.getMessage().getFormattedMessage());
    }
}

