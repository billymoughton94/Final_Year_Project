<?php

require 'connect.php';

// SCRIPT FOR FETCHING GAMES
$genre = $_GET["genre"];
$sql = "SELECT * FROM videogames WHERE genre LIKE '%$genre%' ORDER BY RAND();";
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
