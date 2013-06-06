package com.bazaarvoice;


import org.testng.Assert;
import org.testng.annotations.Test;

public class BazaarvoiceDisplayHelperTest
{
    @Test
    public void testPatterns() {
        Assert.assertTrue(BazaarvoiceDisplayHelper.showUserAgentSEOContent("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"));
        Assert.assertFalse(BazaarvoiceDisplayHelper.showUserAgentSEOContent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17"));
    }
}
