package com.bazaarvoice.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class BazaarvoiceUtilsTest {

    @Test
    public void testGetPageNumber() {
        Assert.assertEquals(BazaarvoiceUtils.getPageNumber("foo=bar&campaignId=fallpromotion&bvrrp=1234-en_gb/reviews/product/2/15864.htm&heavy=light"), 2);
        Assert.assertEquals(BazaarvoiceUtils.getPageNumber("foo=bar&campaignId=fallpromotion&heavy=light"), 1); // no bvrrp
        Assert.assertEquals(BazaarvoiceUtils.getPageNumber("foo=bar&campaignId=fallpromotion&bvrrp=1234-en_gb/product/2/15864.htm&heavy=light"), 1);  // malformed bvrrp

        Assert.assertEquals(BazaarvoiceUtils.getPageNumber("foo=bar&campaignId=fallpromotion&bvpage=reviews/5&heavy=light"), 5);
    }

    @Test
    public void testGetQueryString() {
        Assert.assertEquals(BazaarvoiceUtils.getQueryString(
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"),
                "ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"
        );
        Assert.assertEquals(BazaarvoiceUtils.getQueryString(
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846#hashtag"),
                "ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"
        );
    }

    @Test
    public void testGetBaseURL() {
        Assert.assertEquals(BazaarvoiceUtils.getBaseURL(
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846&bvrrp=1234-en_gb/reviews/product/2/15864.htm"),
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"
        );
        Assert.assertEquals(BazaarvoiceUtils.getBaseURL(
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&bvrrp=1234-en_gb/reviews/product/2/15864.htm&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"),
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"
        );
        Assert.assertEquals(BazaarvoiceUtils.getBaseURL(
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?bvrrp=1234-en_gb/reviews/product/2/15864.htm&ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"),
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"
        );
        Assert.assertEquals(BazaarvoiceUtils.getBaseURL(
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"),
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"
        );
        Assert.assertEquals(BazaarvoiceUtils.getBaseURL(
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846#hashyhash"),
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?ie=UTF8&nav_sdd=aps&pf_rd_m=ATVPDKIKX0DER&pf_rd_s=center-1&pf_rd_r=1SGDPXARF8BWA01DTQ4Z&pf_rd_t=101&pf_rd_p=1493999442&pf_rd_i=507846"
        );
        Assert.assertEquals(BazaarvoiceUtils.getBaseURL(
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228?bvrrp=1234-en_gb/reviews/product/2/15864.htm"),
                "http://www.bazaarvoice.com/gp/product/B0083PWAPW/ref=kin_dev_gw_dual_t/176-0215481-1627228"
        );
    }
}
