package com.bazaarvoice;

import com.bazaarvoice.model.BVSEOException;
import com.bazaarvoice.model.ContentType;
import com.bazaarvoice.model.SubjectType;
import com.bazaarvoice.util.BazaarvoiceUtils;
import com.bazaarvoice.util.SmartSEOS3Client;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class BazaarvoiceDisplayHelper {

    private Configuration _config;

    public BazaarvoiceDisplayHelper(Configuration config) {
        _config = config;
    }

    private static String getIntegrationCode(Configuration config, ContentType contentType, SubjectType subjectType, String subjectId) {
        String[] params = {subjectType.uriValue(), subjectId};
        return MessageFormat.format(config.get(contentType.getIntegrationScriptProperty()), params);
    }

    protected static boolean showUserAgentSEOContent(Configuration config, String userAgent) {
        final String crawlerAgentPattern = config.get(Configuration.CRAWLER_AGENT_PATTERN);
        final Pattern pattern = Pattern.compile(crawlerAgentPattern, Pattern.CASE_INSENSITIVE);

        return userAgent != null && (pattern.matcher(userAgent).matches() || userAgent.toLowerCase().contains("google"));
    }

    private static String getLogComment(Configuration config,  String message) {
        return "\n<!-- BVSEO|dz:" + config.get(Configuration.DEPLOYMENT_ZONE_ID) + "|sdk: v" + config.get(Configuration.VERSION) + "-j|" + message + " -->\n";
    }

    /**
     * This static method returns the correct BV content based on request specific parameters and a given configuration.
     * This method assumes that the caller can provide the entire query string as well as a base/canonical URL for the page being served (Bazaarvoice parameters will be appended).
     *
     * @param config      the configuration to use for the request being served
     * @param userAgent   the user agent string for the current request.  If this agent is considered a search bot, the SmartSEO content will be included in the page.
     * @param baseURL     the base or canonical URL of the page requesting the SmartSEO content.  This will be inserted into the SmartSEO content where needed (i.e. pagination links).
     *                    This should not include any Bazaarvoice specific parameters
     * @param queryString The full query string for this request including all URL parameters
     * @param contentType the type of content that should be included (reviews, questions/answers or stories)
     * @param subjectType the type of subject (product or category) that the content was written against
     * @param subjectId   the product/cagegory ID that the content was written against.
     * @param staging     true if the code is currently running in the staging environment.
     *                    If set, please note that you should also pass in valid product/category IDs for your staging environment.
     * @return            the SmartSEO content for this page.  An empty string will be returned if the integration code is not included and there is no content or an error retrieving it.
     */
    static public String getBVContent(Configuration config, String userAgent, String baseURL, String queryString, ContentType contentType, SubjectType subjectType, String subjectId, boolean staging) {
        if(contentType == ContentType.REVIEWS && subjectType == SubjectType.CATEGORY) {
            throw new IllegalArgumentException("Reviews on categories are not supported.");
        }

        StringBuilder sb = new StringBuilder();

        try {
            if (config.getBoolean(Configuration.INCLUDE_DISPLAY_INTEGRATION_CODE)) {
                sb.append(getIntegrationCode(config, contentType, subjectType, subjectId));
            }

            if (!config.getBoolean(Configuration.BOT_DETECTION) || queryString.contains("bvreveal") || showUserAgentSEOContent(config, userAgent)) {
                long startTime = System.currentTimeMillis();
                sb.append(SmartSEOS3Client.getSmartSEOContent(config, baseURL, subjectType, contentType, BazaarvoiceUtils.getPageNumber(queryString), staging, subjectId));
                long endTime = System.currentTimeMillis();
                sb.append(getLogComment(config, "timer " + Long.toString(endTime - startTime) + "ms"));
                if (queryString.contains("bvreveal=debug")) {
                    sb.append(getLogComment(config,
                            "\n    userAgent: " + userAgent +
                                    "\n    baseURL: " + baseURL +
                                    "\n    queryString: " + queryString +
                                    "\n    contentType: " + contentType +
                                    "\n    subjectType: " + subjectType +
                                    "\n    subjectId: " + subjectId +
                                    "\n    staging: " + Boolean.toString(staging) +
                                    "\n    pattern: " + config.get(Configuration.CRAWLER_AGENT_PATTERN) +
                                    "\n    detectionEnabled: " + config.get(Configuration.BOT_DETECTION) + "\n"
                    ));
                }
            } else {
                sb.append(getLogComment(config, "JavaScript-only Display"));
            }
        } catch (BVSEOException e) {
            return getLogComment(config, "Error: " + e.getMessage());
        }
        return sb.toString();
    }

    /**
     * This call relies on the default configuration specific for this BazaarvoiceDisplayHelper instance.
     * This method assumes that the caller can provide the entire query string as well as a base/canonical URL for the page being served (Bazaarvoice parameters will be appended).
     *
     * @param userAgent   the user agent string for the current request.  If this agent is considered a search bot, the SmartSEO content will be included in the page.
     * @param baseURL     the base or canonical URL of the page requesting the SmartSEO content.  This will be inserted into the SmartSEO content where needed (i.e. pagination links).
     *                    This should not include any Bazaarvoice specific parameters
     * @param queryString The full query string for this request including all URL parameters
     * @param contentType the type of content that should be included (reviews, questions/answers or stories)
     * @param subjectType the type of subject (product or category) that the content was written against
     * @param subjectId   the product/cagegory ID that the content was written against.
     * @param staging     true if the code is currently running in the staging environment.
     *                    If set, please note that you should also pass in valid product/category IDs for your staging environment.
     * @return            the SmartSEO content for this page.  An empty string will be returned if the integration code is not included and there is no content or an error retrieving it.
     */
    public String getBVContent(String userAgent, String baseURL, String queryString, ContentType contentType, SubjectType subjectType, String subjectId, boolean staging) {
        return getBVContent(_config, userAgent, baseURL, queryString, contentType, subjectType, subjectId, staging);
    }

    /**
     * This static method returns the correct BV content based on request specific parameters and a given configuration.
     * This method assumes that the caller cannot provide a base/canonical URL for the page and will just manipulate any Bazaarvoice specific parameters it finds.
     *
     * @param config      the configuration to use for the request being served
     * @param userAgent   the user agent string for the current request.  If this agent is considered a search bot, the SmartSEO content will be included in the page.
     * @param fullURL     the full URL of the page requesting the SmartSEO content.  It's preferable to explicitly pass in a base/canonical URL and the query string via
     *                    {@link #getBVContent(String, String, String, com.bazaarvoice.model.ContentType, com.bazaarvoice.model.SubjectType, String, boolean)} .  If
     *                    this format is used we will include all query parameters in review pagination links.
     * @param contentType the type of content that should be included (reviews, questions/answers or stories)
     * @param subjectType the type of subject (product or category) that the content was written against
     * @param subjectId   the product/cagegory ID that the content was written against.
     * @param staging     true if the code is currently running in the staging environment.
     *                    If set, please note that you should also pass in valid product/category IDs for your staging environment.
     * @return            the SmartSEO content for this page.  An empty string will be returned if the integration code is not included and there is no content or an error retrieving it.
     */
    static public String getBVContent(Configuration config, String userAgent, String fullURL, ContentType contentType, SubjectType subjectType, String subjectId, boolean staging) {
        return getBVContent(config, userAgent, BazaarvoiceUtils.getBaseURL(fullURL), BazaarvoiceUtils.getQueryString(fullURL), contentType, subjectType, subjectId, staging);
    }

    /**
     * This call relies on the default configuration specific for this BazaarvoiceDisplayHelper instance.
     * This method assumes that the caller cannot provide a base/canonical URL for the page and will just manipulate any Bazaarvoice specific parameters it finds.
     *
     * @param userAgent   the user agent string for the current request.  If this agent is considered a search bot, the SmartSEO content will be included in the page.
     * @param fullURL     the full URL of the page requesting the SmartSEO content.  It's preferable to explicitly pass in a base/canonical URL and the query string via
     *                    {@link #getBVContent(String, String, String, com.bazaarvoice.model.ContentType, com.bazaarvoice.model.SubjectType, String, boolean)} .  If
     *                    this format is used we will include all query parameters in review pagination links.
     * @param contentType the type of content that should be included (reviews, questions/answers or stories)
     * @param subjectType the type of subject (product or category) that the content was written against
     * @param subjectId   the product/cagegory ID that the content was written against.
     * @param staging     true if the code is currently running in the staging environment.
     *                    If set, please note that you should also pass in valid product/category IDs for your staging environment.
     * @return            the SmartSEO content for this page.  An empty string will be returned if the integration code is not included and there is no content or an error retrieving it.
     */
    public String getBVContent(String userAgent, String fullURL, ContentType contentType, SubjectType subjectType, String subjectId, boolean staging) {
        return getBVContent(_config, userAgent, fullURL, contentType, subjectType, subjectId, staging);
    }
}
