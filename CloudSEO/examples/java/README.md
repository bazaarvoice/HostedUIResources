Java BV SEO SDK
================

The Java example code here demonstrates a sample API for retrieving SmartSEO content (and possibly our integration code
as well).

Dependencies:
------------

* Apache's fluent HTTP client ( http://hc.apache.org/httpcomponents-client-ga/fluent-hc/ )

Running the example:
-------------------

Before running the code, you must edit [client.properties](client.properties) and include the settings we've provided you for your integration.

After editing the properties, the main method in [Example.java](src/main/java/Example.java) provides a simple example of how to use the library.  The example simply reads a sample HTML page file, retrieves the corresponding SmartSEO content from S3, inserts it into the document and prints the results to stdout.  Be sure to change the hardcoded values in the example to a valid product ID and page URL for your environment.


To compile:

    $ mvn compile

To run the included tests:

    $ mvn clean test

To run the Example class:

    $ mvn exec:java -Dexec.mainClass="Example"


The main line to look at is the point where we call `BazaarvoiceDisplayHelper.getBVContent()`.  This is responsible for performing the user agent detection and including the integration code and SEO payload as needed.

API Usage
---------

There are two static methods which can be used to generate the Bazaarvoice content:

```java
BazaarvoiceDisplayHelper.getBVContent(userAgent,
                                      baseURI,
                                      BazaarvoiceUtils.getQueryString(pageURI),
                                      contentType,
                                      subjectType,
                                      subjectId,
                                      staging));
```

The configuration files for this class are loaded during class instantiation and the remaining parameters are passed into the method as follows:

Parameter Name | Definition | Example
-------------- | ---------- | --------
userAgent | the user agent string for the current request | `"Mozilla/5.0 (compatible; Googlebot/2.1;)"`
baseURL | the base or canonical URL of the page requesting the SmartSEO content.  This will be inserted into the SmartSEO content where needed (i.e. pagination links). This should not include any Bazaarvoice specific parameters. | `"http://www.example.com/store/products/XXYY/"`
queryString | The full query string for this request including all URL parameters | `campaign=bazaarvoice&bvrrp=1234/reviews/product/2/XXYY.htm`
contentType | the type of content that should be included (reviews, questions/answers or stories) | `ContentType.REVIEWS`
subjectType | the type of subject (product or category) that the content was written against | `SubjectType.PRODUCT`
subjectId | the product/cagegory ID that the content was written against.  If set, please note that you should also pass in valid product/category IDs for your staging environment. | `"XXYY"`
staging | true if the code is currently running in the staging (test) environment | `true`

In addition, there is another method which will appempt to parse out the base URL and queryString if you do not which to specify these explicitly.

```java
BazaarvoiceDisplayHelper.getBVContent(userAgent,
                                      fullURL,
                                      contentType,
                                      subjectType,
                                      subjectId,
                                      staging));
```
The one new parameter is used as follows:

Parameter Name | Definition | Example
-------------- | ---------- | --------
fullURL | the full URL of the page requesting the SmartSEO content | `"http://www.example.com/store/products/XXYY/?campaign=bazaarvoice&bvrrp=1234/reviews/product/2/XXYY.htm"`


