package io.cucumber.examples.spring.application

import io.cucumber.core.options.Constants
import org.junit.platform.suite.api.ConfigurationParameter

import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("org/elaastic/questions")
class RunCucumberTest