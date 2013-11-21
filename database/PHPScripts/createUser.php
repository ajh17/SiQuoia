<?php
require_once('database_info.php');

if (isset($_POST['username']) && isset($_POST['password']) && isset($_POST['email'])) {

  $username = $_POST['username'];
  $password = $_POST['password'];
  $email = $_POST['email'];
  $currQuiz = "";
  $currAnswers = "";
  $numPackets = 0;
  $numMemro = 0;
  $siquoiaPoints = 0;

  $stmt = $link->prepare("insert into Users (email, password, currentQuiz, currentAnswers, siquoiaPoints, numPackets, numMemro) 
                        values (?, ?, ?, ?, ?, ?, ?);");
  $stmt->bind('ssssddd', $email, $password, $currQuiz, $currAnswers, $currQuiz, $currAnswers, $numPackets, $numMemro);
  $stmt->execute();
}
?>