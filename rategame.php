<?php

require 'connect.php';
global $connect;
// SCRIPT FOR RATING GAMES

$gameID = (int) $_POST["gameID"];
$userID = (int) $_POST["userID"];
$rating = (float) $_POST["rating"];


$sql = "SELECT * FROM ratings WHERE userID = '$userID' AND gameID = '$gameID';";
$result = $connect->query($sql);

// CHECK IF ENTRY ALREADY EXISTS. IF IT DOES: UPDATE VALUE OF RATING
if($result->num_rows == 0){
  $sql = "INSERT INTO ratings(userID, gameID, rating) VALUES('$userID', '$gameID', '$rating');";
  $result = $connect->query($sql);
  echo json_encode(array("result"=>"success"));
}
// ELSE, CREATE A NEW ENTRY
else if($result->num_rows > 0){
  $sql = "UPDATE ratings SET rating = '$rating' WHERE userID = '$userID' AND gameID = '$gameID';";
  $result = $connect->query($sql);
  echo json_encode(array("result"=>"success"));
}

else{
  echo json_encode(array("result"=>"failure"));
}
$connect->close();
?>
