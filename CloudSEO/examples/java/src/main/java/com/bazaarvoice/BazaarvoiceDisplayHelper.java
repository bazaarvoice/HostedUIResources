package com.bazaarvoice;

import com.bazaarvoice.model.ContentType;
import com.bazaarvoice.model.SubjectType;
import com.bazaarvoice.util.BazaarvoiceUtils;
import com.bazaarvoice.util.SmartSEOS3Client;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BazaarvoiceDisplayHelper {

    protected BazaarvoiceDisplayHelper() {

    }

    private static String getIntegrationCode(ContentType contentType, SubjectType subjectType, String subjectId) {
        String[] params = {subjectType.uriValue(), subjectId};
        return MessageFormat.format(Configuration.get(contentType.getIntegrationScriptProperty()), params);
    }

    protected static boolean showUserAgentSEOContent(String userAgent) {
        final String crawlerAgentPattern = Configuration.get("crawlerAgentPattern");
        final Pattern pattern = Pattern.compile(crawlerAgentPattern, Pattern.CASE_INSENSITIVE);

        return userAgent != null && pattern.matcher(userAgent).matches();
    }

    private static String getLogComment(String message) {
        return "\n<!-- BVSEO|dz:" + Configuration.get("deploymentZoneId") + "|sdk: v" + Configuration.get("version") + "-j|" + message + " -->\n";
    }

    /**
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
    public static String getBVContent(String userAgent, String baseURL, String queryString, ContentType contentType, SubjectType subjectType, String subjectId, boolean staging) {
        if(contentType == ContentType.REVIEWS && subjectType == SubjectType.CATEGORY) {
            throw new IllegalArgumentException("Reviews on categories are not supported.");
        }

        StringBuilder sb = new StringBuilder();
        if (Configuration.getBoolean("includeDisplayIntegrationCode")) {
            sb.append(getIntegrationCode(contentType, subjectType, subjectId));
        }

        if (!Configuration.getBoolean("botDetection") || queryString.contains("bvreveal") || showUserAgentSEOContent(userAgent)) {
            long startTime = System.currentTimeMillis();
            sb.append(SmartSEOS3Client.getSmartSEOContent(baseURL, subjectType, contentType, BazaarvoiceUtils.getPageNumber(queryString), staging, subjectId));
            long endTime = System.currentTimeMillis();
            sb.append(getLogComment("timer " + Long.toString(endTime - startTime) + "ms"));
        } else {
            sb.append(getLogComment("JavaScript-only Display"));
        }
        return sb.toString();
    }

    /**
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
    public static String getBVContent(String userAgent, String fullURL, ContentType contentType, SubjectType subjectType, String subjectId, boolean staging) {
        return getBVContent(userAgent, BazaarvoiceUtils.getBaseURL(fullURL), BazaarvoiceUtils.getQueryString(fullURL), contentType, subjectType, subjectId, staging);
    }

}
