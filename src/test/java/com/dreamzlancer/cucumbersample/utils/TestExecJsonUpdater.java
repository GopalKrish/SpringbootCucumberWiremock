package com.dreamzlancer.cucumbersample.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;

public class TestExecJsonUpdater {
    public static void main(String[] args) throws Exception {
        // 1. Read Bamboo build number from system property
        String buildNumber = System.getProperty("bamboo.buildNumber", "UNKNOWN");

        // 2. Read template JSON
        ObjectMapper mapper = new ObjectMapper();
        File inputFile = new File("src/test/resources/testExecInfoJson.json");
        JsonNode root = mapper.readTree(inputFile);

        // 3. Update buildNumber
        if (root.isObject()) {
            ((ObjectNode) root).put("buildNumber", buildNumber);
        }

        // 4. Write updated JSON to target/
        File outputFile = new File("target/testExecInfoJson.json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, root);

        System.out.println("✅ Updated JSON with buildNumber=" + buildNumber +
                " → " + outputFile.getAbsolutePath());
    }
}
