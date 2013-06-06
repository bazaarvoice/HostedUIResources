package com.bazaarvoice.model;

public enum ContentType {
    REVIEWS("reviewsIntegrationScript"),
    QUESTIONS("questionsIntegrationScript"),
    STORIES("storiesIntegrationScript");

    private final String _integrationScriptProperty;

    ContentType(String integrationScriptProperty) {
        _integrationScriptProperty = integrationScriptProperty;
    }

    public String uriValue() {
        return this.toString().toLowerCase();
    }

    public String getIntegrationScriptProperty() {
        return _integrationScriptProperty;
    }
}
