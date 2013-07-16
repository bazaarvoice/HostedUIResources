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

    private static URI getS3ContentURI(Configuration config, SubjectType subjectType, ContentType contentType, int page, boolean staging, String productID)
            throws URISyntaxException {

        final String s3Hostname = staging ? config.get(Configuration.STAGING_S3_HOSTNAME) : config.get(Configuration.PRODUCTION_S3_HOSTNAME);
        final String s3Key = config.get(Configuration.CLOUD_KEY);
        final String deploymentZoneId = config.get(Configuration.DEPLOYMENT_ZONE_ID);

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

    private static String getSmartSEOContentHTTP(Configuration config, SubjectType subjectType, ContentType contentType, int page, boolean staging, String productID) {
        String smartSEOPayload;

        try {
            URI targetUrl = getS3ContentURI(config, subjectType, contentType, page, staging, productID);
            _log.debug("Fetching : " + targetUrl);

            int connectionTimeout = Integer.parseInt(config.get(Configuration.CONNECT_TIMEOUT));
            int socketTimeout = Integer.parseInt(config.get(Configuration.SOCKET_TIMEOUT));
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

    private static String getSmartSEOContentFilesystem(Configuration config, SubjectType subjectType, ContentType contentType, int page, String productID) {
        if (config.get(Configuration.LOCAL_SEO_FILE_ROOT).isEmpty()) {
            throw new BVSEOException("Unable to read SEO file.  Please set correct root directory.");
        }

        final String deploymentZoneId = config.get(Configuration.DEPLOYMENT_ZONE_ID);
        String encodedProductID;
        try {
            encodedProductID = URLEncoder.encode(productID, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            encodedProductID = URLEncoder.encode(productID);
        }

        String path = config.get(Configuration.LOCAL_SEO_FILE_ROOT) + File.separator + deploymentZoneId + File.separator + contentType.uriValue() + File.separator + subjectType.uriValue() + File.separator + page + File.separator + encodedProductID + ".htm";

        _log.debug("SEO file path = " + path);

        try {
            return BazaarvoiceUtils.readFile(path);
        } catch (IOException ex) {
            throw new BVSEOException("Unable to read SEO file.");
        }
    }

    public static String getSmartSEOContent(Configuration config, String pageURL, SubjectType subjectType, ContentType contentType, int page, boolean staging, String productID) {
        String smartSEOPayload;

        if (config.getBoolean(Configuration.LOAD_SEO_FILES_LOCALLY)) {
            smartSEOPayload = getSmartSEOContentFilesystem(config, subjectType, contentType, page, productID);
        } else {
            smartSEOPayload = getSmartSEOContentHTTP(config, subjectType, contentType, page, staging, productID);
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
