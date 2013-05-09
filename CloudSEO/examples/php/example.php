<?php
    
    /**
     * Example of using BV SEO SDK for PHP.
     *
     * This will show a basic example of how to 
     * use the BV SEO SDK for PHP. 
     * 
     * This SDK was built with the following assumptions:
     *      - you are running PHP 5 or greater
     *      - you have the curl library installed
     *      - every request has the user agent string 
     *        in it (if using a CDN like Akamai additional configuration
     *        maybe required).
     */

    // first include the bvsdk.php file
    require('bvseosdk.php');

    // next instantiate the bv object by passing in 
    // the following required fields. There is a list of 
    // all the possible parameters in the comments of
    // the bvsdk.php file
    $bv = new BV(array(
        'deployment_zone_id' => '12325',
        'product_id' => 'product1',
        'cloud_key' => 'agileville-78B2EF7DE83644CAB5F8C72F2D8C8491',
        'staging' => TRUE,
    ));

    // if you are wanting to load the SEO content from disk you can do something 
    // this. NOTE this requires you to handle connecting to our FTP server, downloading
    // the zip file, and unzipped it daily. 
    // 
    // $bv = new BV(array(
    //     'deployment_zone_id' => '12335',
    //     'product_id' => 'product1',
    //     'internal_file_path' => '/path/to/unzipped/folder'
    // ));

    // In order to test the SDK you can either modify your HTTP header
    // user agent string to match a search engine bot, or add the ?bvreveal=bot
    // query parameter in URL. For example if the URL to this example page was
    // http://localhost/example.php, then this would trigger the SDK to pull SEO content
    // http://localhost/example.php?bvreveal=bot
?>

<html>
<head>
    <title>BV SDK PHP Example</title>
</head>
<body>
    Example PDP page.

    <div id="BVRRContainer">
        <?php echo $bv->reviews->renderSeo();?>
    </div>

    <div id="BVQAContainer">
        <?php echo $bv->questions->renderSeo();?>
    </div>
</body>
</html>