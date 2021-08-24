<?PHP
require 'connect.php';
global $connect;
// SCRIPT FOR FETCHING PARTICULAR USER'S RATINGS FOR FRONTEND
// WILL ALLOW VISUALISATION OF WHAT SCORE THEY GAVE
$userID = $_POST["userID"];
$sql = "SELECT gameID, rating FROM ratings WHERE userID = '$userID';";
$result = $connect->query($sql);

// CHECK IF ANY GAMES WERE RATED
if($result->num_rows > 0){
  $gameRatings = array();

	while($row = $result->fetch_assoc()){
		$temp = array();
		$temp['gameID'] = $row['gameID'];
    $temp['rating'] = $row['rating'];

    array_push($gameRatings, $temp);
  }
  echo json_encode(array("result"=>"success", "gameRatings"=>$gameRatings));
}

else{
	echo json_encode(array("result"=>"failure"));
}

$connect->close();
?>
