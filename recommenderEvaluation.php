<?PHP

require '../connect.php';
require 'User_Based_Recommender.php';
//require 'Item_Based_Recommender.php';
global $connect;
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// METHOD FOR TESTING RECOMMENDER TYPES

function recommenderEvaluator(string $cfType, string $similarityType, string $userID, array $ratingsMatrix){
  $re;
  $actualRatings;
  $predictedRatings;

  if($cfType == "USER"){
    $re = new User_Based_Recommender($similarityType, true);
    $actualRatings = $ratingsMatrix[$userID];
    $predictedRatings = $re->getRecommendations($ratingsMatrix, $userID);
  }
  if($cfType == "ITEM"){
    $re = new Item_Based_Recommender($similarityType, true);
    $actualRatings = $ratingsMatrix[$userID];
    $predictedRatings = $re->getRecommendations($ratingsMatrix, $userID);
  }

  $loss = 0;
  $numGames = 0;
  $correctlyPredicted = 0;

  foreach ($actualRatings as $entityID=>$rating) {
    if(array_key_exists($entityID, $predictedRatings)){
      $loss = $loss + pow($rating - $predictedRatings[$entityID], 2);
      $numGames++;

      echo "<pre>";
      echo("Predicted value for GAME " . $entityID . ": " . $predictedRatings[$entityID] . "\n");
      echo("Actual value for GAME " . $entityID . ": " . $actualRatings[$entityID] . "\n");
      echo "</pre>";
    }
  }

  // EVALUATION TECHNIQUES - NUMBER REPRESENTS AVERAGE DIFFERENCE IN TERMS OF RATING
  // VALUES CLOSE TO 0 ARE DESIRED
  if($numGames == 0){
    echo("No users in common with these game ratings");
    return;
  }
  $RMSE = sqrt($loss / $numGames);

  echo "<pre>";
  print_r("Root Mean Squared Error Score (" . $cfType . "-BASED, " . $similarityType . " ): " . round($RMSE, 2) . "\n");
  echo "</pre>";
}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
$userID = "309903147";

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

///////////////////////////////////////////////////////////////////////
//USER - BASED TESTS//

// TEST 1: EUCLIDIAN DISTANCE. SCORE:
recommenderEvaluator("USER", "EUCLIDIAN", $userID, $ratingsMatrix);

// TEST 2: COSINE SIMILARITY. SCORE:
//recommenderEvaluator("USER", "COSINE", $userID, $ratingsMatrix);

//recommenderEvaluator("USER", "ADJUSTED COSINE", $userID, $ratingsMatrix);

///////////////////////////////////////////////////////////////////////

// FOR UPDATING THE ITEM-ITEM MATRICES
/*
$recommender = new Item_Based_Recommender("ADJUSTED COSINE", false);
$recommender->generateItem_Item_Matrix();
*/

///////////////////////////////////////////////////////////////////////
//ITEM - BASED TESTS//

// TEST 1: EUCLIDIAN DISTANCE. SCORE:
//recommenderEvaluator("ITEM", "EUCLIDIAN", $userID, $ratingsMatrix); // RESULT: SIMILARITY SCORES ARE TOO SMALL

// TEST 2: COSINE SIMILARITY. SCORE:
//recommenderEvaluator("ITEM", "COSINE", $userID, $ratingsMatrix);

//recommenderEvaluator("ITEM", "ADJUSTED COSINE", $userID, $ratingsMatrix);

///////////////////////////////////////////////////////////////////////
?>
