.NET BV SEO SDK
================
The .NET BV SEO SDK is compatible with .NET 3.5 and above.

There are two ways to use the BV SEO SDK: 
 - Reference the BvSeoSdk.dll found in BvSeoSdk_binaries folder, or 
 - Download the source and refer to BvSeoSdk project. 

To get the SEO content, all that needs to be done is:

```c#
String SEO_CONTENTS = new Bv(
                deploymentZoneID: "Main_Site-en_US", 
                product_id: "XXYYY", 
                //The page_url is optional
                //page_url: "http://www.example.com/store/products/data-gen-696yl2lg1kurmqxn88fqif5y2/",
                cloudKey: "agileville-78B2EF7DE83644CAB5F8C72F2D8C8491" // use value populated in "cloud key" field under "SEO configuration" in the config hub. 
                bv_product:  BvProduct.REVIEWS, 
                //bot_detection: false, //by default bot_detection is set to true
                user_agent: "msnbot") //Setting user_agent for testing. Leave this blank in production.
                .getSeoWithSdk(System.Web.HttpContext.Current.Request);

```
SEO_CONTENTS, above, contains the SEO HTML as a string which needs to be rendered on the product page inside the \<div id="BVRRContainer">\</div> 

To test, set user_agent parameter to a valid bot's name such as msnbot. In production environment, you must leave user_agent set to blank.

To run the sample example:
- Open DotNetMVCExample.sln in Visual Studio 2010 or later
- Hit Run icon or F5

Here is a full list of the parameters you can pass into BV class we instantiated above


Parameter Name | Default value | Example Value(s) | Required | Notes
------------ | ------------- | ------------ | ------------ | ------------
deploymentZoneID |  None | 1234-en_us | Yes | |
product_id |  None | test1 | Yes | |
bv_product | None | BvProduct.REVIEWS | Yes | Use BvProduct enum that comes with the BvSeoSdk dll. |
cloudKey |  None | 2b1d0e3b86ffa60cb2079dea11135c1e | Yes | |
staging |  TRUE | TRUE or FALSE | No | |
timeout_ms | 1000 | 500 | No | Integer in ms. Determines how much time the request will be given before timing out. 
page_url | HttpRequest.Url.OriginalString |  http://www.example.com/pdp/test1 | No | If a current URL is not provided the current page URL will be used instead.  You will want to provide the URL if you use query parameters or # in your URLs that you don't want Google to index. |
product_or_category | product | product, category | No | Reviews will always have this value set to product.  Used only for questions that can be submitted against a category or product. |
bot_regex_string | "(msnbot|googlebot|teoma|bingbot|yandexbot|yahoo)" | No | any regex valid expression | Regular expression used to determine whether or not the current request is a bot (checking against user agent header) |
bot_detection | true | true, false | No | Flag used to determine if bot detection is required. If set to false, it will always return the SEO content; otherwise, it will only return SEO contents when a bot is detected in the user agent. Default value is true. |
internalFilePath | None | C:\bv_seotools | No | This is the base folder of the downloaded zip file, if you do not wish to use the cloud content. |
includeDisplayIntegrationCode | false | true, false | No | Setting this to false will not include BV.ui js call in the response.

Troubleshooting
----------------

1. The SEO content for reviews show up, but not for question and answer (QA).
   
   Make sure that you are making a separate call to the SDK with bv_product parameter set to BVProduct.QUESTIONS. If content for reviews is coming back, then one way of quickly verifying that the problem is with the SEO cloud or not is to replace the call for BVProduct.REVIEWS to BVProduct.QUESTIONS. Depending on various .NET implementations, sometimes the ASP.NET page lifecycle may not hit certain piece of code-behind code. So, it is always good to make sure that your code is making all the calls to SDK that you intend to make. 

2. I only see SEO content for page 1 of my reviews. Page 2 links simply load the contents for page 1.
   
   This suggests that the SDK is not being able to parse the page number from the given HttpRequest URL, and is defaulting to Page 1. For example, if the URL being passed is http://example.com/?bvrrp=1234/reviews/product/2/1234.htm, then the SDK knows the page you are asking for is page 2. If you want to make sure that the SDK is looking at the correct URL, you can provide the value of the url in the page_url parameter.

3. Request is erroring out / timing out / etc.
   
   This could be due to firewall issues, or a missing SEO document. Please try going directly to our seo url. The url is of the following format: 
   
   For staging: seo-stg.bazaarvoice.com/seo_key/deployment_zone_id/bv_product/product/page/product_id.htm
   
   For production: seo.bazaarvoice.com/seo_key/deployment_zone_id/bv_product/product/page/product_id.htm
   
