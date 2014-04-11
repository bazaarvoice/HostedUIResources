### PHP SDK

The PHP SDK has the following requirements:
* PHP 5+
* curl library installed

Follow these steps to use the PHP SDK:

1. Include the bvseosdk.php file:  	
    
    ```php
    require('bvseosdk.php');
    ```
	
2. Instantiate the bv object.
    ```php
    $bv = new BV(array(
         'deployment_zone_id' => 'Main_Site-en_US', // replace with your display code (BV provided)
         'product_id' => 'product1', // replace with product id 
         'cloud_key' => 'agileville-78B2EF7DE83644CAB5F8C72F2D8C8491', // use value populated in "cloud key" field under "SEO configuration" in the config hub. 
         'staging' => TRUE
    ));
    ```

3. Call `$bv->reviews->renderSeo()` to grab the product's review SEO content.  This call will return the SEO HTML as a string. This string needs to be rendered on the product page inside the `<div id="BVRRContainer"></div>`. For example: 
    ```php
    <div id="BVRRContainer">
        <?php echo $bv->reviews->renderSeo();?>
    </div>
    ```
4. To test this you will need to modify your HTTP user agent string to match that of a search engine. Or for testing convenience, you can add the query parameter `?bvreveal=bot` to trigger the SDK to return SEO content. `?bvreveal=debug` will also display additional debug comments in the HTML markup.

    Here is a full list of the parameters you can pass into BV class we instantiated in step 2 above


Parameter Name | Default value | Example Value(s) | Required | Notes
------------ | ------------- | ------------ | ------------ | ------------
deployment_zone_id |  None | 1234-en_us | Yes | Sometimes this is also referred to as your display code. |
product_id |  None | test1 | Yes | The product ID needs to match the product ID you reference in your product data feed and use to power your display of UGC.|
cloud_key |  None | 2b1d0e3b86ffa60cb2079dea11135c1e | Yes | Will be provided by your Bazaarvoice team.  |
staging |  TRUE | TRUE or FALSE | No | Toggle if the SDK should pull SEO content from staging or production. |
latency_timeout | 1000 | 500 | No | Integer in ms. Determines how much time the request will be given before timing out. 
current_page_url | Current page using $_SERVER |  http://www.example.com/pdp/test1 | No | If a current URL is not provided, the current page URL will be used instead.  You will want to provide the URL if you use query parameters or # in your URLs that you don't want Google to index. |
subject_type | product | product, category | No | Reviews will always have this value set to product.  This is used only for questions that can be submitted against a category or a product. |
bot_list | msnbot, googlebot, teoma, bingbot, yandexbot, yahoo | No | Any regex valid expression | Regular expression used to determine whether or not the current request is a bot (checking against user agent header) |
bot_detection | TRUE | TRUE or FALSE | No | Used for clients who are behind a CDN and want the SDK to return SEO content with every call. |
include_display_integration_code |  FALSE | TRUE or FALSE | No | If you want the SDK to also include the JavaScript to power display as well.  You will need to include the bvapi.js file seperately.  |
internal_file_path |  FALSE | '/home/zip/smart_seo/ | No | If you want to still download the zip file of SEO content and serve it from disk, you can pass the SDK an aboslute path to the unzipped folder of Smart SEO content.  |
