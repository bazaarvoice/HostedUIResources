<?php
//Please provide cloud_key, deployment_zone_id and product_id
	require('bvseosdk.php');
    $bv = new BV(array(
    'deployment_zone_id' => '',
    'product_id' => '',
    'cloud_key' => '',
    'current_page_url' => '',
    ));
?>

<html>
<head>
    <title>BV SDK PHP Example - getReviews</title>
</head>
<body>
    This is a test page for Reviews: getReviews()<br>
    This will return review content<br><br>
    
    <div id="BVRRContainer">
        <?php echo $bv->reviews->getReviews();?>
    </div>
</body>
</html>
