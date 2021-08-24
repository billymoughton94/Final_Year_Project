<?php

// SCRIPT FOR RECOMMENDING GAMES

// REFERENCE TO AUTHOR OF WHICH CODE HAS BEEN DERIVED FROM
/*
AUTHOR: SAMEER
Date of Aquisition: 08/02/2021
Title: Item based collaborative filtering in PHP
Folder: sample_recommend
Found At: https://www.codediesel.com/php/item-based-collaborative-filtering-php/
*/
require '../connect.php';
require 'User_Based_Recommender.php';
require 'Similarity_Function.php';
global $connect;

$userID = $_POST["userID"];

//////////////////////////////////////////////////////////////////////////////
// GENERATES THE USER-ITEM MATRIX FROM THE DATABASE//
$ratingsMatrix = array();
$sql = "SELECT * FROM ratings;";
$result = $connect->query($sql);

while($row = $result->fetch_array(MYSQLI_ASSOC)){
  $sql = "SELECT userID FROM ratings WHERE userID = $row[userID];";
  $user =  $connect->query($sql);
  $userArray = $user->fetch_array(MYSQLI_ASSOC);
  $ratingsMatrix[$userArray['userID']][$row['gameID']] = $row['rating'];
}
//////////////////////////////////////////////////////////////////////////////

// IF A USER HAS NOT RATED ANY ITEMS
if(!array_key_exists($userID, $ratingsMatrix)){
  echo json_encode(array("result"=>"null ratings"));
  return;
}

$recommender = new User_Based_Recommender("EUCLIDIAN", false);
$recommendationsList = $recommender->getRecommendations($ratingsMatrix, $userID);

// FETCH ALL GAMEID IN ARRAY - TEST
$query_include = "'".implode("','", array_keys($recommendationsList))."'";
$sql = "SELECT * FROM videogames WHERE gameID IN ($query_include);";
$result = $connect->query($sql);

if($result->num_rows > 0){

	$games = array();

	while($row = $result->fetch_assoc()){
		$temp = array();
		$temp['gameID'] = $row['gameID'];
		$temp['name'] = $row['name'];
		$temp['box_art'] = $row['box_art'];
		$temp['release_date'] = $row['release_date'];
		$temp['developer'] = $row['developer'];
		$temp['genre'] = $row['genre'];
		$temp['description'] = $row['description'];

    // retrieve average rating from n users for the game
		$gameID = $row['gameID'];
		$sql = "SELECT AVG(rating) as average, COUNT(*) as count FROM ratings WHERE gameID = '$gameID';";
		$result2 = $connect->query($sql);
		$row2 = $result2->fetch_assoc();
		$temp['avgRating'] = round($row2['average'], 1);
		$temp['numRatings'] = $row2['count'];

		array_push($games, $temp);
}

echo json_encode(array("result"=>"success", "games"=>$games));

}

else{
	echo json_encode(array("result"=>"failure"));
}
$connect->close();
?>
