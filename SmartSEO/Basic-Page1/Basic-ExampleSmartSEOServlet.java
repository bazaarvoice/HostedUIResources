package com.bazaarvoice;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class ExampleSmartSEOServlet extends HttpServlet{

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletOutputStream out = resp.getOutputStream();
        out.println("<html><head><title>sample SmartSEO page</title></head><body>");

        out.println(getBazaarvoiceInjectionContainer("1234-en_us", "123456", "/path/to/smartseo/"));

        out.println("</body></html>");
    }

    /**
     * Retrieve the Bazaarvoice container div and all indexable Smart SEO content to be rendered on the page.
     *
     * @param displayCode The display code for which Smart SEO content is generated.  This should be the same display
     *                    code that is the root directory of the Smart SEO archive retrieved from the Bazaarvoice server.
     * @param productId   The ID of the product for which this product details page is being rendered.
     * @param smartSEOPathPrefix   The absolute path to the location where your Smart SEO archive has been extracted.
     *                             This absolute path should not contain your display code.
     * @return The Bazaarvoice container div and all indexable Smart SEO content to be rendered on the page.
     */
    private String getBazaarvoiceInjectionContainer(String displayCode, String productId, String smartSEOPathPrefix) {
        StringBuffer cntnrBuf = new StringBuffer();

        cntnrBuf.append("<div id=\"BVRRContainer\">\n");

        try {

            //Make sure the smartSEOPathPrefix ends with a trailing slash
            if (!smartSEOPathPrefix.endsWith("/")) {
                smartSEOPathPrefix += "/";
            }

            //Get the name of the file from disk that we should use to include in the page.
            File rrSmartSEOFile = new File(smartSEOPathPrefix + displayCode + "/reviews/product/1/" + URLEncoder.encode(productId, "UTF-8").replace("+", "%20") + ".htm");
            if (rrSmartSEOFile.exists() && rrSmartSEOFile.isFile()) {
                //Read in the file contents
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(rrSmartSEOFile), "UTF-8"));
                String line = null;
                String sep = System.getProperty("line.separator");
                while ((line = reader.readLine()) != null) {
                    cntnrBuf.append(line + sep);
                }
            }


        } catch(Exception e) {
            //log exception
            e.printStackTrace();
        } finally {
            cntnrBuf.append("</div>\n");
        }

        return cntnrBuf.toString();
    }

}
