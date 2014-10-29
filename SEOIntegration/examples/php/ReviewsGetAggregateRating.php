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
    <title>BV SDK PHP Example - getAggregateRating</title>
</head>
<body>
    This is a test page for Reviews: getAggregateRating()<br>
    This will return aggregate rating content<br><br>
    <div id="BVRRSummaryContainer">
        <?php echo $bv->reviews->getAggregateRating();?>
    </div>
</body>
</html>
