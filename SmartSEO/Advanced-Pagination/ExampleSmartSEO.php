<div id="BVRRContainer">
<?php
    $CONFIG = array(
        "DISPLAY_CODE" => "1234-en_us",
        "PRODUCT_ID" => "123456",
        
        // The current product's URL - include training "?" or "&" as part of URL
        "PRODUCT_URL" => "http://www.example.com/product/123456/?", 
        
        // This is the URL parameter that will be appended to the product page URL
        // Configurable, but must be kept in sync with your Bazaarvoice Implementation Engineer
        "REVIEWS_SMARTSEO_PARAM" => "bvrrp",                    
        
        // The absolute path that does not contain your display code.  
        // WARNING: The Smart SEO files should be the ONLY files within this location.
        // Files that should not be publicly exposed should not be in this directory.
        // Include trailing /.
        "SMART_SEO_PATH_PREFIX" => "/path/to/smartseo/",   
        
        // replace ISO-8859-1 with the appropriate character set to render
        "CHARACTER_SET" => "ISO-8859-1"                               
    );

    $bvrrpValue = NULL;
    if (isset($_GET[$CONFIG["REVIEWS_SMARTSEO_PARAM"]])) {
        $bvrrpValue = $_GET[$CONFIG["REVIEWS_SMARTSEO_PARAM"]];
    }

    $reviewsSmartSEOFileName = bvGetSourceSVIFile($bvrrpValue, $CONFIG);
    if (!is_null($reviewsSmartSEOFileName)) {
        $indexableContent = bvURLTokenReplace($reviewsSmartSEOFileName, $CONFIG["PRODUCT_URL"]);
        print mb_convert_encoding($indexableContent, $CONFIG["CHARACTER_SET"], 'UTF-8');  
    }
    
    function bvURLTokenReplace($filename, $url) {
        return str_replace('{INSERT_PAGE_URI}', $url, file_get_contents($filename));
    }
    
    function bvGetSourceSVIFile($rrPage, $CONFIG) {
        if ($rrPage) {
            $sourceFile = realpath($CONFIG["SMART_SEO_PATH_PREFIX"] . $rrPage);
            if (strpos($sourceFile, $CONFIG["SMART_SEO_PATH_PREFIX"] .  $CONFIG["DISPLAY_CODE"]) == 0 && is_file($sourceFile)) {
                return $sourceFile;
            }
        }
        $firstPageContent = $CONFIG["SMART_SEO_PATH_PREFIX"] . $CONFIG["DISPLAY_CODE"] . '/reviews/product/1/' . rawurlencode($CONFIG["PRODUCT_ID"]) . '.htm';
        if (is_file($firstPageContent)) {
            return $firstPageContent;
        }
        return NULL;
    }
?>
</div>