import com.bazaarvoice.BazaarvoiceDisplayHelper;
import com.bazaarvoice.Configuration;
import com.bazaarvoice.model.ContentType;
import com.bazaarvoice.model.SubjectType;
import com.bazaarvoice.util.BazaarvoiceUtils;

import java.io.IOException;

public class Example {

    public static void main(String [] args) {
        // Request based parameters
        final String userAgent = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";

        // Environment based variables
        final boolean staging = true;
        final Configuration configWithIntegrationCode = Configuration.newInstance().setIncludeDisplayIntegrationCode(true);
        final BazaarvoiceDisplayHelper bazaarvoiceDisplayHelper = new BazaarvoiceDisplayHelper(configWithIntegrationCode);

        // Page related variables
        final String baseURI = "http://www.example.com/store/products/data-gen-696yl2lg1kurmqxn88fqif5y2/";
        final String pageURI = "http://www.example.com/store/products/data-gen-696yl2lg1kurmqxn88fqif5y2/?utm_campaign=bazaarvoice&utm_medium=SearchVoice&utm_source=RatingsAndReviews&utm_content=Default&bvrrp=12325/reviews/product/2/data-gen-696yl2lg1kurmqxn88fqif5y2.htm";
        final ContentType contentType = ContentType.REVIEWS;
        final SubjectType subjectType = SubjectType.PRODUCT;
        final String subjectId = "data-gen-696yl2lg1kurmqxn88fqif5y2";

        String htmlContent="%s";
        try {
            htmlContent = BazaarvoiceUtils.readFile("index.html");
        } catch (IOException ex) {
            System.err.println("Unable to load HTML file");
            System.exit(-1);
        }

        try {
            if (args.length > 0 && args[0].length() > 0) {
                System.out.format(htmlContent, bazaarvoiceDisplayHelper.getBVContent(args[0], baseURI, BazaarvoiceUtils.getQueryString(pageURI), contentType, subjectType, subjectId, staging));
            } else {
                System.out.format(htmlContent, bazaarvoiceDisplayHelper.getBVContent(userAgent, baseURI, BazaarvoiceUtils.getQueryString(pageURI), contentType, subjectType, subjectId, staging));
            }
        } catch (Exception ex) {
            System.err.println("Unable to load Bazaarvoice content:\n");
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
