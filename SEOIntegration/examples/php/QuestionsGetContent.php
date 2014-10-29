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
    <title>BV SDK PHP Example - Questions: GetContent</title>
</head>
<body>
    This is a test page for Questions: getContent<br>
    This will return questions and answers content<br><br>

    <div id="BVQAContainer">
        <?php echo $bv->questions->getContent();?>
    </div>

</body>
</html>
