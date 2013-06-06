package com.bazaarvoice.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BazaarvoiceUtils {

    protected static final Log _log = LogFactory.getLog(BazaarvoiceUtils.class);

    public static String getQueryString(String uri) {
        final URI _uri;
        try {
            _uri = new URI(uri);
        } catch (Exception ex) {
            _log.warn("Unable to parse URL: " + uri);
            return null;
        }
        return _uri.getQuery();
    }

    public static boolean validateURI(String uri) {
        final URI _uri;
        try {
            _uri = new URI(uri);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private static int matchPageNumber(Pattern pattern, String value) {
        Matcher m = pattern.matcher(value);
        if (m.matches()) {
            return Integer.parseInt(m.group(1));
        } else {
            return 1;
        }
    }

    public static int getPageNumber(String queryString) {
        if (queryString != null && queryString.length() > 0) {
            List<NameValuePair> parameters = URLEncodedUtils.parse (queryString, Charset.forName("UTF-8"));
            for(NameValuePair parameter : parameters) {
                if (parameter.getName().equals("bvrrp") || parameter.getName().equals("bvqap") || parameter.getName().equals("bvsyp")) {
                    final Pattern p = Pattern.compile("^[^/]+/\\w+/\\w+/(\\d+)/[^/]+\\.htm$");
                    return matchPageNumber(p, parameter.getValue());
                } else if (parameter.getName().equals("bvpage")) {
                    final Pattern p = Pattern.compile("^\\w+/(\\d+)$");
                    return matchPageNumber(p, parameter.getValue());
                }
            }
        }
        return 1;
    }

    /**
     * Attempt to generate a new base URL for the client if one is not provided.  This will strip out any Bazaarvoice parameters from the URL in addition to the URL fragment/hash.
     *
     * @param fullURL the full URL of the current request (in UTF-8 encoding)
     * @return
     */
    public static String getBaseURL(String fullURL) {

        final URI uri;
        try {
            uri = new URI(fullURL);
        } catch (URISyntaxException e) {
            _log.warn("Unable to parse URL: " + fullURL);
            return fullURL;
        }

        try {
            String newQuery = null;
            if (uri.getQuery() != null && uri.getQuery().length() > 0) {
                List<NameValuePair> newParameters = new ArrayList<NameValuePair>();
                List<NameValuePair> parameters =  URLEncodedUtils.parse(uri.getQuery(), Charset.forName("UTF-8"));
                final List<String> bvParameters = Arrays.asList("bvrrp", "bvsyp", "bvqap", "bvpage");
                for (NameValuePair parameter : parameters) {
                    if (!bvParameters.contains(parameter.getName())) {
                        newParameters.add(parameter);
                    }
                }
                newQuery = newParameters.size() > 0 ? URLEncodedUtils.format(newParameters, Charset.forName("UTF-8")) : null;

            }
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), newQuery, null).toString();
        } catch (URISyntaxException e) {
            _log.warn("Unable to generate base URL", e);
            return fullURL;
        }
    }

    public static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.forName("UTF-8").decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }
}
