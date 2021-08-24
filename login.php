<?php

require 'connect.php';

// SCRIPT FOR LOGGING IN AND FETCHING USER ID
global $connect;


$username = $_POST["username"];
$password = $_POST["password"];

$sql = "SELECT userID, username FROM users WHERE username = '$username' AND password = '$password';";

$result = $connect->query($sql);

if($result->num_rows > 0){
  $row =  $result->fetch_assoc();
  echo json_encode(array("result"=>"success", "userID"=>$row["userID"], "username"=>$row["username"]));
}

else{
  echo json_encode(array("result"=>"failure"));
}

$connect->close();
?>
