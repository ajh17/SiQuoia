<?php
require_once('database_info.php');

$result = $link->query("SELECT * FROM `Subject`");
$json = array();

if ($result->num_rows !== false) {
    while ($row = $result->fetch_assoc()) {
        array_push($json, $row);
    }

    echo json_encode($json) . "\n";
}
?>
