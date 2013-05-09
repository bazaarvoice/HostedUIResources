package com.bazaarvoice.util;

import com.bazaarvoice.Configuration;
import com.bazaarvoice.model.BVSEOException;
import com.bazaarvoice.model.ContentType;
import com.bazaarvoice.model.SubjectType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;

import java.io.File;
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
        final String s3Key = Configuration.get("cloudKey");
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

    private static String getSmartSEOContentHTTP(SubjectType subjectType, ContentType contentType, int page, boolean staging, String productID) {
        String smartSEOPayload;

        try {
            URI targetUrl = getS3ContentURI(subjectType, contentType, page, staging, productID);
            _log.debug("Fetching : " + targetUrl);

            int connectionTimeout = Integer.parseInt(Configuration.get("connectTimeout"));
            int socketTimeout = Integer.parseInt(Configuration.get("socketTimeout"));
            smartSEOPayload = Request.Get(targetUrl).connectTimeout(connectionTimeout).socketTimeout(socketTimeout).execute().returnContent().asString();
        } catch (ClientProtocolException e) {
            _log.error("Unable to download SmartSEO content via HTTP.", e);
            throw new BVSEOException("Unable to download SmartSEO content from S3.");
        } catch (IOException e) {
            _log.error("Unable to download SmartSEO content via HTTP.", e);
            throw new BVSEOException("Unable to download SmartSEO content from S3.");
        } catch (URISyntaxException e) {
            _log.error("Unable to download SmartSEO content via HTTP.  Invalid URL.", e);
            throw new BVSEOException("Unable to download SmartSEO content via HTTP.  Invalid URL.");
        }

        return smartSEOPayload;
    }

    private static String getSmartSEOContentFilesystem(SubjectType subjectType, ContentType contentType, int page, String productID) {
        if (Configuration.get("localSEOFileRoot").isEmpty()) {
            throw new BVSEOException("Unable to read SEO file.  Please set correct root directory.");
        }

        final String deploymentZoneId = Configuration.get("deploymentZoneId");
        String encodedProductID;
        try {
            encodedProductID = URLEncoder.encode(productID, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            encodedProductID = URLEncoder.encode(productID);
        }

        String path = Configuration.get("localSEOFileRoot") + File.separator + deploymentZoneId + File.separator + contentType.uriValue() + File.separator + subjectType.uriValue() + File.separator + page + File.separator + encodedProductID + ".htm";

        _log.debug("SEO file path = " + path);

        try {
            return BazaarvoiceUtils.readFile(path);
        } catch (IOException ex) {
            throw new BVSEOException("Unable to read SEO file.");
        }
    }

    public static String getSmartSEOContent(String pageURL, SubjectType subjectType, ContentType contentType, int page, boolean staging, String productID) {
        String smartSEOPayload;

        if (Configuration.getBoolean("loadSEOFilesLocally")) {
            smartSEOPayload = getSmartSEOContentFilesystem(subjectType, contentType, page, productID);
        } else {
            smartSEOPayload = getSmartSEOContentHTTP(subjectType, contentType, page, staging, productID);
        }

        if (smartSEOPayload.contains("{INSERT_PAGE_URI}")) {
            if(!BazaarvoiceUtils.validateURI(pageURL)) {
                throw new BVSEOException("The current page URL is required and invalid");
            }
            smartSEOPayload = smartSEOPayload.replace("{INSERT_PAGE_URI}", pageURL + (pageURL.contains("?") ? "&" : "?"));
        }
        return smartSEOPayload;
    }
}
