<?php

require 'connect.php';

// SCRIPT FOR SIGNING UP
global $connect;

$username = $_POST["username"];
$password = $_POST["password"];

$sql = "INSERT INTO users(username, password) VALUES ('$username', '$password');";

if(!$connect->query($sql)){
  echo json_encode(array("result"=>"failure"));
}
else{
  echo json_encode(array("result"=>"success"));
}



$connect->close();

?>
