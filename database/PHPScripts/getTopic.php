<?php
header('X-Content-Type-Options: nosniff');
header('X-Frame-Options: SAMEORIGIN');
require_once('database_info.php');

if (isset($_POST['subject'])) {
    $subject = $_POST['subject'];
    $query = "SELECT * FROM `Topic` WHERE subject = '". "$subject" . "'";
    $result = $link->query($query);
    $json = array();

    if ($result->num_rows !== false) {
        while ($row = $result->fetch_assoc()) {
            array_push($json, $row);
        }
        echo json_encode($json) . "\n";
    }
}
?>
