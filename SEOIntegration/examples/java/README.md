Java BV SEO SDK
================

The Java example code here demonstrates a sample API for retrieving SmartSEO content (and possibly our integration code
as well).

Dependencies:
------------

* Apache's fluent HTTP client ( http://hc.apache.org/httpcomponents-client-ga/fluent-hc/ )

Binaries:
------------

* The JAR files for the Java SDK can be downloaded from the following URL: http://bazaarvoice.github.io/HostedUIResources/

Running the example:
-------------------

Before running the code, you must edit [bvclient.properties](bvclient.properties) and include the settings we've provided you for your integration.

After editing the properties, the main method in [Example.java](src/main/java/Example.java) provides a simple example of how to use the library.  The example reads a sample HTML page file, retrieves the corresponding SmartSEO content from S3, inserts it into the document, and prints the results to stdout.  Be sure to change the hardcoded values in the example to a valid product ID and page URL for your environment.


To compile:

    $ mvn compile

To run the included tests:

    $ mvn clean test

To run the Example class:

    $ mvn exec:java -Dexec.mainClass="Example"


The main line to look at is the point where we call `BazaarvoiceDisplayHelper.getBVContent()`.  This is responsible for performing the user agent detection and including the integration code and SEO payload as needed.

API Usage
---------

There are two static methods and two instance methods in the BazaarvoiceDisplayHelper class that can be used to
generate the Bazaarvoice content.  The instance methods are as follows:

```java
BazaarvoiceDisplayHelper.getBVContent(userAgent,
                                      baseURI,
                                      BazaarvoiceUtils.getQueryString(pageURI),
                                      contentType,
                                      subjectType,
                                      subjectId,
                                      staging));
```

The configuration files for this class are passed in during class instantiation and the remaining parameters are
passed into the method as follows:

Parameter Name | Definition | Example
-------------- | ---------- | --------
userAgent | User agent string for the current request | `"Mozilla/5.0 (compatible; Googlebot/2.1;)"`
baseURL | Base or canonical URL of the page requesting the SmartSEO content.  This will be inserted into the SmartSEO content where needed (for example, pagination links). This should not include any Bazaarvoice-specific parameters. | `"http://www.example.com/store/products/XXYY/"`
queryString | Full query string for this request, including all URL parameters | `campaign=bazaarvoice&bvrrp=1234/reviews/product/2/XXYY.htm`
contentType | Type of content that should be included (reviews, questions and answers, or stories) | `ContentType.REVIEWS`
subjectType | Type of subject (product or category) that the content was written against | `SubjectType.PRODUCT`
subjectId | Product/cagegory ID that the content was written against.  If set, then you should also pass in valid product/category IDs for your staging environment. | `"XXYY"`
staging | True if the code is currently running in the staging (test) environment | `true`

There is another method that will attempt to parse out the base URL and queryString if you do not specify these
explicitly.

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
fullURL | Full URL of the page requesting the SmartSEO content | `"http://www.example.com/store/products/XXYY/?campaign=bazaarvoice&bvrrp=1234/reviews/product/2/XXYY.htm"`


Finally, there are two static versions of these methods which take in a Configuration object as a parameter.  The 
Configuration object is built using the client defined properties file, but individual options can be configured
per instance.

