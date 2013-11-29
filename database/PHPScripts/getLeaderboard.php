<?php 
require_once 'database_info.php';

$query = "select questionText, rank from Question order by rank desc limit 20";
$result = $link->query($query);
$json = array();

if ($result->num_rows !== false) {
    while ($row = $result->fetch_assoc()) {
        array_push($json, $row);
    }
    echo json_encode($json) . "\n";
}
?>