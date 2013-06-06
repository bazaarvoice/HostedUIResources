.NET BV SEO SDK
================

There are two ways to use the BV SEO SDK: 
 - Reference the BvSeoSdk.dll found in BvSeoSdk_binaries folder, or 
 - Download the source and refer to BvSeoSdk project. 

To get the SEO content, all that needs to be done is:

```c#
String SEO_CONTENTS = new Bv(
                deploymentZoneID: "XXXX-en_us",
                product_id: "XXYYY", 
                //The page_url is optional
                //page_url: "http://www.example.com/store/products/data-gen-696yl2lg1kurmqxn88fqif5y2/",
                cloudKey: "sdkfkjflsjdfl", 
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

