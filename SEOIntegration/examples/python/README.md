# Python SDK

## Prerequisites

* Python 2.6+

## Use

1. Import the BV SEO SDK python module:

	`
	import bv
	`
	
2. Initialize the BV object:

	```
    bvSeo = bv.BV(
        deployment_zone_id='Main_Site-en_US',
        product_id='test1',
        page_url='http://example.com/reviews/?foo=bar&test=yes',
        seo_key='agileville-78B2EF7DE83644CAB5F8C72F2D8C8491', 
        content_type='reviews',
        current_request_query_string=request.args,
        user_agent='msnbot'
    )
	```
	
3. Execute the `getSeoWithDisplay` method:

	```
	print bvSeo.getSeoWithDisplay()
	```
	
## Parameters

Name | Default Value | Example Values | Required | Notes
------ | ------------- | ------------ | ------------ | ------------
product_id |  None | test1 | Yes | This will match the value of the product External ID in the XML product feed.  |
display_code |  None | Main_Site-en_US | Yes | Default display code. Will vary when implementing on different locales/languages. |
bv_product | reviews | reviews, questions | No | |
page_url | None |  http://www.example.com/pdp/test1 | Yes | Pass the URL of the current page to this parameter. For example, using the Flask framework, you would pass `request.path` to this parameter. |
seo_key |  None | 2b1d0e3b86ffa60cb2079dea11135c1e | Yes | Get from the config hub. On the left panel, click "Technical Setup" > "SEO Configuration." The value will be in the "Cloud Key" field. |
user_agent | None | Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22 | Yes | Pass the user agent string of the request to this parameter. For example, using the Flask framework, you would pass `request.headers.get('User-Agent')` to this parameter. |
current_request_query_string | Empty | | Yes | Pass the current request query string as a `dict` object. For example, using the Flask framework, you would pass `request.args` as a `dict` object. |
product_or_category | product | product, category | No | |
staging |  True | True, False | No | |
hosted_display | False | True, False | No | |
timeout_ms | 1000 | 500 | No | Integer in milliseconds. Determines how much time the request will be given before timing out. |
bot_regex_string | msnbot, googlebot, teoma, bingbot, yandexbot, yahoo | No | any valid regex | This is the regex used to determine whether or not the current request is a bot (checking against user agent header). |
cache_enabled | True | True, False | No | Enables caching results of an object so that calling the same object's accessor methods multiple times won't go through the whole process multiple times. It simply returns the previous response if there was one. |
