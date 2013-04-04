#Bazaarvoice Cloud SEO SDK


>NOTE: This method is currently in Beta and still being piloted.  Please contact Michael DeHaven or Charles Himmer
>for questions about usage / limitations. 

Product reviews, questions, and answers can provide an immense amount of SEO value for product detail pages.  
The language and keywords users use when writing reviews, questions, or answers often match users search keywords
and can help increase traffic to your product detail pages.  That and search engines like to see fresh content
and reviews, questions, and answers are a great way of providing a steady stream of fresh content to often stagnant
product information pages. 

In order for search engines to crawl and index Bazaarvoice captured content, the content needs to be server side
included on the product detail pages.  This is required because search engines do not index JavaScript rendered 
content which is what your end users will see when they view your product page using a web browser.  A good test 
to see what search engines see when they crawl a product page is to open your product page in a browser, right 
click and view the source of that page.  This will allow you to see the markup that search engines will index. You
will notice that JavaScript rendered markup is not present (i.e. look for the BVRRContainer or BVQAContainer specifically).

### Cloud SEO
Bazaarvoice's Cloud SEO allows you to server side include Bazaarvoice captured content so product detail pages 
get the maximum SEO value possible from user submitted reviews, questions or answers. When Cloud SEO is enabled, 
Bazaarvoice will host URLs that will contain HTML blobs for all your products.  These HTML blobs 
contain the review, questions, or answers content that user have submitted via Bazaarvoice, wrapped in SEO optimized
markup (schema.org). 

### SEO SDKs
In order to make implementing Cloud SEO easier, Bazaarvoice provides an SDK / library for the following languages:

* [Java](https://github.com/bazaarvoice/HostedUIResources/tree/master/examples/java)
* [.NET](https://github.com/bazaarvoice/HostedUIResources/tree/master/CloudSEO/examples/dotnet) 
* [PHP](https://github.com/bazaarvoice/HostedUIResources/tree/master/examples/php)
* [Python](https://github.com/bazaarvoice/HostedUIResources/tree/master/examples/python)

If you are using a server side language that is not listed above, you can use still use Cloud SEO by following the generic
implementation steps listed at the bottom of this page. NOTE: Using Cloud SEO (SDKs included) require that the requester's
user agent is exposed and accurate per request. This normally only a problem if you are leveraging a CDN like Akamai. There
are workarounds for this like configuring your CDN to pass through the user agent string with each request. 

## Generic Cloud SEO

This section explains how to Cloud SEO works in general.  This will be helpful if you need to use Cloud SEO with a language we do not provide SDK for yet or for those who want to know the steps the SDKs take.  Which each language's SDK may differ slightly in the details, in general they all follow this general workflow.

Here are the necessary to implement Cloud SEO:

1. Determine if the current request is a search engine by checking the user agent string. An example of how to do this would be to do a regular expression against the user agent string like this (take from PHP SDK):
   ```php
        preg_match('/(msnbot|googlebot|teoma|bingbot|yandexbot|yahoo')/i', $_SERVER['HTTP_USER_AGENT']);
    ```

2. If the current request is coming from a search engine, we need to determine which page of SEO content we need to request.  This can be determined by the bvrrp URL parameter.  If this parameter doesn't exist we default to requesting page 1.  If it exists, we can exists, the page we need to request will be inside the value of this parameter.  

3.  Next we make a request to the URL containing the SEO content we need.  You can use this pattern to build that URL:
>http://seo-stg.bazaarvoice.com/{seo-key}/{display-code}/{content-type}/{subject-type}/{page-number}/{product-id}.htm

   For example, you were going to request page 1 of review content for the product1 product, the full URL would look like this:
>http://seo-stg.bazaarvoice.com/agileville-78B2EF7DE83644CAB5F8C72F2D8C8491/12325/questions/product/1/product1.htm
   
   These URLs are pointing to staging.  To request production data you would use seo.bazaarvoice.com instead of seo-stg.bazaarvoice.com

4. Now that we have fetched our SEO content, we need to replace all instances of the string {INSERT\_URI_TOKEN} with the current page's URL.  This is to build in pagination between the pages of content so search engines can crawl all the pages of UGC content. 

5. Now we are ready to render this formatted SEO string onto the page in the correct container.  For reviews this is inside the \<div id="BVRRContainer">\</div>, and for questions this is inside the \<div id="BVQAContainer">\</div>. 

