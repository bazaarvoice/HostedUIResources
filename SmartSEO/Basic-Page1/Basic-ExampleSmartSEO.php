<!--

Copyright 2013 Bazaarvoice, Inc.

Unless required by applicable law or agreed to in writing, the example code
below is provided "as is" without warranty of any kind, either express or
implied, including without limitation any implied warranties of condition,
uninterrupted use, merchantability, or fitness for a particular purpose.

-->
<div id="BVRRContainer">
<?php
    $CONFIG = array(
        "DISPLAY_CODE" => "1234-en_us",
        "PRODUCT_ID" => "123456",
        "SMART_SEO_PATH_PREFIX" => "/path/to/smartseo/",              //The absolute path that does not contain your display code.  Include trailing /
        "CHARACTER_SET" => "ISO-8859-1"                               // replace ISO-8859-1 with the appropriate character set to render
    );

    $reviewsSmartSEOFileName = bvGetSourceSVIFile($CONFIG);
    
    if (!is_null($reviewsSmartSEOFileName)) {
        $indexableContent = file_get_contents($reviewsSmartSEOFileName);
        print mb_convert_encoding($indexableContent, $CONFIG["CHARACTER_SET"], 'UTF-8');  
    }
    
    function bvGetSourceSVIFile($CONFIG) {
        $firstPageContent = $CONFIG["SMART_SEO_PATH_PREFIX"] . $CONFIG["DISPLAY_CODE"] . '/reviews/product/1/' . rawurlencode($CONFIG["PRODUCT_ID"]) . '.htm';
        if (is_file($firstPageContent)) {
            return $firstPageContent;
        }
        return NULL;
    }
?>
</div>