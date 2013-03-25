package com.bazaarvoice.util;

import com.bazaarvoice.Configuration;
import com.bazaarvoice.model.ContentType;
import com.bazaarvoice.model.SubjectType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class SmartSEOS3Client {
    private static final Log _log = LogFactory.getLog(SmartSEOS3Client.class);

    private static URI getS3ContentURI(SubjectType subjectType, ContentType contentType, int page, boolean staging, String productID)
            throws URISyntaxException {

        final String s3Hostname = staging ? Configuration.get("stagingS3Hostname") : Configuration.get("productionS3Hostname");
        final String s3Key = staging ? Configuration.get("stagingS3Key") : Configuration.get("productionS3Key");
        final String deploymentZoneId = Configuration.get("deploymentZoneId");

        String encodedProductID;
        try {
            encodedProductID = URLEncoder.encode(productID, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            encodedProductID = URLEncoder.encode(productID);
        }

        String path = "/" + s3Key + "/" + deploymentZoneId + "/" + contentType.uriValue() + "/" + subjectType.uriValue() + "/" + page + "/" + encodedProductID + ".htm";
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(s3Hostname).setPath(path);
        return builder.build();
    }

    public static String getSmartSEOContent(String pageURL, SubjectType subjectType, ContentType contentType, int page, boolean staging, String productID) {
        String smartSEOPayload;

        try {
            URI targetUrl = getS3ContentURI(subjectType, contentType, page, staging, productID);
            _log.debug("Fetching : " + targetUrl);

            int connectionTimeout = Integer.parseInt(Configuration.get("connectTimeout"));
            int socketTimeout = Integer.parseInt(Configuration.get("socketTimeout"));
            smartSEOPayload = Request.Get(targetUrl).connectTimeout(connectionTimeout).socketTimeout(socketTimeout).execute().returnContent().asString();
            if (smartSEOPayload.contains("{INSERT_PAGE_URI}")) {
                if(!BazaarvoiceUtils.validateURI(pageURL)) {
                    throw new IllegalArgumentException("The current page URL is required and invalid");
                }
                smartSEOPayload = smartSEOPayload.replace("{INSERT_PAGE_URI}", pageURL + (pageURL.contains("?") ? "&" : "?"));
            }
        } catch (ClientProtocolException e) {
            _log.error("Unable to download SmartSEO content from S3.", e);
            return "";
        } catch (IOException e) {
            _log.error("Unable to download SmartSEO content from S3.", e);
            return "";
        } catch (URISyntaxException e) {
            _log.error("Unable to download SmartSEO content from S3.  Invalid URL.", e);
            return "";
        }

        return smartSEOPayload;
    }

}
