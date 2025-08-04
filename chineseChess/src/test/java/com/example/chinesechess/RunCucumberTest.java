package com.example.chinesechess;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import org.junit.platform.suite.api.IncludeTags; // Import IncludeTags

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.example.chinesechess.steps")
// @IncludeTags("General") // Add this line to run only scenarios tagged with @General
public class RunCucumberTest {
}
