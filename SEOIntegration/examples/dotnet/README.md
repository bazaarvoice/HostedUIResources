.NET BV SEO SDK
================
The .NET BV SEO SDK is compatible with .NET 3.5 and above.

There are two ways to use the BV SEO SDK: 
 - Reference the BvSeoSdk.dll found in BvSeoSdk_binaries folder, or 
 - Download the source and refer to BvSeoSdk project. 

To get the SEO content, call the BV class using the following format.  NOTE: all inputs are case sensitive.

```c#
String SEO_CONTENTS = new Bv(
                cloudKey: "company-cdfa682b84bef44672efed074093ccd3",
                deploymentZoneID: "Main_Site-en_US",
                product_id: "XXYYY", 
                page_url: "http://www.example.com/store/products/data-gen-696yl2lg1kurmqxn88fqif5y2/",
            	staging: false,
                bot_detection: false,
                bv_product: BvProduct.REVIEWS)
                .getSeoWithSdk(System.Web.HttpContext.Current.Request);

```
SEO_CONTENTS, above, contains the SEO HTML as a string which needs to be rendered on the product page inside the \<div id="BVRRContainer">\</div> or \<div id="BVReviewsContainer">\</div> for reviews; or \<div id="BVQAContainer"> for questions.  The BV SEO string must NOT be nested in additional HTML elements that may cause it to be hidden, such as \<noscript> tags.  This code is designed to interact with BV JavaScript or IFRAME injected content.

To run the sample example:
- Open DotNetMVCExample.sln in Visual Studio 2010 or later
- Hit Run icon or F5

Here is a full list of the parameters you can pass into BV class shown above.


Parameter Name | Default value | Example Value(s) | Required | Notes
------------ | ------------- | ------------ | ------------ | ------------
cloudKey |  None | company-cdfa682b84bef446 72efed074093ccd3 | Yes | Value will be provided by BV |
deploymentZoneID |  None | 1234-en_US | Yes | Value will be provided by BV |
product_id |  None | test1 | Yes | Value and case must match ExternalID in the BV product feed |
bv_product | None | BvProduct.REVIEWS or BvProduct.QUESTIONS | Yes | Use BvProduct enum that comes with the BvSeoSdk dll. |
staging |  true | true or false | Yes | Do not forget to set this to false when publishing to production. |
timeout_ms | 1000 | 1500 | No | Integer in ms. Determines how much time the request will be given before timing out. 
page_url | HttpRequest.Url. OriginalString |  http://www.example.com/ pdp/test1 | No | If a current URL is not provided the current page URL will be used instead.  You will want to provide the URL if you use query parameters or # in your URLs that you don't want Google to index. |
product_or_category | product | product, category | No | Reviews will always have this value set to product.  Used only for questions that can be submitted against a category or product. |
bot_regex_string | "(msnbot|google|teoma|bingbot|yandexbot|yahoo)" | No | any regex valid expression | Regular expression used to determine whether or not the current request is a bot (checking against user agent header) |
bot_detection | true | true, false | No | Flag used to determine if bot detection is required. Only use bot detection if cloud-based content retrieval averages greater than 350ms, as reported via the BVSEO comment tag that is written to the page source. |
internalFilePath | None | C:\bv_seotools | No | This is the base folder of the downloaded zip file, if you do not wish to use the cloud content. |

Testing
----------------
1.  Injection:  To test the BV SEO injection, load an active product page where you have confirmed that review content is available (or Q&A if applicable).  View source and search for bvDateModified.  If bvDateModified is present, BV SEO contents have been injected into the source.  The date stamp with bvDateModified should be less than 10 days old at all times.  If it's not, contact BV for assistance.

2.  Pagination:  To test search-friendly pagination, load an active product page where you have confirmed that 10+ reviews are available.  View source and search for a URL which contains the string bvrrp (for reviews) or bvqap (for Q&A).  You can often search for either bvseo-paginationLink, BVRRPageLink, or BVQAPageLink to help find these links.  Once finding the link, copy and paste the complete URL into a browser.  Check the BV SEO content in the source to make sure it is not the same that was displayed on page 1.

3.  Bot Detection:  If using bot_detection, be sure to test the functionality before sending code live.  To test, use a user agent switcher and include "googlebot" or "msnbot" in the user agent string.  Then, look in the page source and search for bvDateModified.  If bvDateModified is present, BV SEO contents have been injected into the source.


Troubleshooting
----------------

1. The SEO content for reviews show up, but not for question and answer (QA).
   
   Make sure that you are making a separate call to the SDK with bv_product parameter set to BVProduct.QUESTIONS. If content for reviews is coming back, then one way of quickly verifying that the problem is with the SEO cloud or not is to replace the call for BVProduct.REVIEWS to BVProduct.QUESTIONS. Depending on various .NET implementations, sometimes the ASP.NET page lifecycle may not hit certain piece of code-behind code. So, it is always good to make sure that your code is making all the calls to SDK that you intend to make. 

2. I only see SEO content for page 1 of my reviews. Page 2 links simply load the contents for page 1.
   
   This suggests that the SDK is not being able to parse the page number from the given HttpRequest URL, and is defaulting to Page 1. For example, if the URL being passed is http://example.com/?bvrrp=1234/reviews/product/2/1234.htm, then the SDK knows the page you are asking for is page 2. If you want to make sure that the SDK is looking at the correct URL, you can provide the value of the url in the page_url parameter.

3.  The SDK is reporting a 403 erroring

   The "403 error" message is expected behavior when SEO files for a product have not been published.  If a product has no review contnet, this message will be displayed in a source code comment.  If a page does have reviews and SEO publishing has been active for at least 36 hours, use the following techniques (in #4) to debug.

4. Request is erroring out / timing out / etc.
   
   This could be due to firewall issues, or a missing SEO document. Please try going directly to our seo url. The url is of the following format: 
   
   For staging: seo-stg.bazaarvoice.com/seo_key/deployment_zone_id/bv_product/product/page/product_id.htm
   
   For production: seo.bazaarvoice.com/seo_key/deployment_zone_id/bv_product/product/page/product_id.htm
   
