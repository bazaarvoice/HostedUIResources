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
         'display_code' => '12325', // replace with your display code (BV provided)
         'product_id' => 'product1', // replace with product id 
         'seo_key' => 'agileville-78B2EF7DE83644CAB5F8C72F2D8C8491', // BV provided value
         'staging' => TRUE
    ));
    ```

3. Call $bv->reviews->renderSeo() to grab the product's review SEO content.  This call will return the SEO HTML as a string. This string needs to be rendered on the product page inside the \<div id="BVRRContainer">\</div>. For example: 
    ```php
    <div id="BVRRContainer">
        <?php echo $bv->reviews->renderSeo();?>
    </div>
    ```
4. To test this you will need to modify your HTTP user agent string to match that of a search engine. Or for testing convenience, you can add the query parameter ?bvfakebot=true to trigger the SDK to return SEO content.

    Here is a full list of the parameters you can pass into BV class we instantiated in step 2 above


Parameter Name | Default value | Example Value(s) | Required | Notes
------------ | ------------- | ------------ | ------------ | ------------
display_code |  None | 1234-en_us | Yes | |
product_id |  None | test1 | Yes | |
seo_key |  None | 2b1d0e3b86ffa60cb2079dea11135c1e | Yes | |
staging |  TRUE | TRUE or FALSE | No | |
latency_timeout | 1000 | 500 | No | Integer in ms. Determines how much time the request will be given before timing out. 
current_page_url | Current page using $_SERVER |  http://www.example.com/pdp/test1 | No | If a current URL is not provided, the current page URL will be used instead.  You will want to provide the URL if you use query parameters or # in your URLs that you don't want Google to index. |
subject_type | product | product, category | No | Reviews will always have this value set to product.  This is used only for questions that can be submitted against a category or a product. |
bot_list | msnbot, googlebot, teoma, bingbot, yandexbot, yahoo | No | Any regex valid expression | Regular expression used to determine whether or not the current request is a bot (checking against user agent header) |
