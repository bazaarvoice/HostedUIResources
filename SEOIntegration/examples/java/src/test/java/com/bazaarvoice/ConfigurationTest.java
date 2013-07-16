package com.bazaarvoice;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigurationTest
{
    @Test
    public void testRequiredCoreProperties() {
        testStringProperty("stagingS3Hostname");
        testStringProperty("productionS3Hostname");
        testStringProperty("crawlerAgentPattern");
        testStringProperty("reviewsIntegrationScript");
        testStringProperty("questionsIntegrationScript");
        testStringProperty("storiesIntegrationScript");
    }

    @Test
    public void testRequiredUserProperties() {
        testStringProperty("deploymentZoneId");
        testStringProperty("cloudKey");
        testIntProperty("connectTimeout");
        testIntProperty("socketTimeout");
        testBooleanProperty("includeDisplayIntegrationCode");
    }

    private String testStringProperty(String key) {
        String property = Configuration.newInstance().get(key);
        Assert.assertTrue(property != null && !property.isEmpty(), "Required property '" + key + "'is not defined.");
        return property;
    }

    private void testIntProperty(String key) {
        String property = testStringProperty(key);
        Assert.assertTrue(Integer.parseInt(property) >= 0, "Property '" + key + "' is not a valid non-negative integer.");
    }

    private void testBooleanProperty(String key) {
        String property = testStringProperty(key);
        Assert.assertTrue(property.equalsIgnoreCase("true") || property.equalsIgnoreCase("false"), "Property '" + key + "' should be either 'true' or 'false'.");
    }
}
