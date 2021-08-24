<?php

/* ITEM-BASED CF STEPS

  1. BEGIN BY COMPUTING THE SIMILARITY FOR ALL GAMES (EUCLIDIAN, COSINE, ETC) BY THE RATING VECTOR OF EACH GAME

  2.FOR EVERY GAME_i THAT A USER HAS NOT RATED:
    2.1 PREDICT THE RATING: RATING(USER, GAME_I) = ( SUM (USER'S RATING FOR THIS GAME * SIMILARITY OF THIS GAME TO GAME_I) / SUM(SIMILARITY OF THIS GAME TO GAME_I ) )
    2.2 CHOOSE THE GAMES WHICH SCORED THE HIGHEST PREDICTED RATING

  PREREQUISITES: A ITEM SIMILARITY MATRIX
*/

require '../connect.php';


class Item_Based_Recommender {
  private string $similarityType;
  private Similarity_Function $simFunc;
  private bool $testing; // used for evaluation

  public function __construct(string $similarityType, bool $testing){
    $this->similarityType = $similarityType;
    $this->testing = $testing;
    $this->simFunc = new Similarity_Function();
  }


///////////////////////////////////////////////////////////////////////////////////////////////////////////
  public function getRecommendations($ratingsMatrix, $userID){
    $predictedRatings = array();
    $itemSimMatrix;

    if($this->similarityType == "EUCLIDIAN"){
      $itemSimMatrix = unserialize(file_get_contents('Matrices/EUCLIDIAN_ITEM_SIM_MATRIX.txt'));
    }
    if($this->similarityType == "COSINE"){
      $itemSimMatrix = unserialize(file_get_contents('Matrices/COSINE_ITEM_SIM_MATRIX.txt'));
    }
    if($this->similarityType == "ADJUSTED COSINE"){
      $itemSimMatrix = unserialize(file_get_contents('Matrices/ADJUSTED COSINE_ITEM_SIM_MATRIX.txt'));
    }

    foreach($itemSimMatrix as $gameID => $rating){
      $rating_sim_sum = 0;
      $sim_sum = 0;

      // ONLY PROVIDE RECOMMENDATIONS FOR GAMES NOT RATED (UNLESS WE ARE TESTING)
      if(!array_key_exists($gameID, $ratingsMatrix[$userID]) || $this->testing){
        foreach($ratingsMatrix[$userID] as $userRatedGame => $userRating){
          if(array_key_exists($userRatedGame, $itemSimMatrix[$gameID])){
            $rating_sim_sum += $userRating * $itemSimMatrix[$gameID][$userRatedGame];
            $sim_sum += $itemSimMatrix[$gameID][$userRatedGame];
          }
        }
        if($sim_sum > 0){
          $predictedRatings[$gameID] = $rating_sim_sum / $sim_sum;
        }
        else{
          $predictedRatings[$gameID] = 0;
        }
      }
    }
    if($this->testing)
      return $predictedRatings;
    arsort($predictedRatings);
    return array_slice($predictedRatings, 0, 10, true); // returns top 10 most similar games
  }
///////////////////////////////////////////////////////////////////////////////////////////////////////////


  // THIS FUNCTION UPDATES THE ITEM-ITEM MATRIX WHENEVER A NEW GAME IS ADDED TO THE SYSTEM OR AFTER A PERIOD OF TIME (E.G. WEEKLY)
  // OUTPUTS A MATRIX INTO ANOTHER FILE, WHICH IS THEN USED BY getRecommendations()
  function generateItem_Item_Matrix(){
    // GENERATES THE ITEM-USER MATRIX FROM THE DATABASE - USED FOR BUILDING THE ITEM-ITEM SIMILARITY MATRIX//
    global $connect;
    $ratingsMatrix = array();
    $sql = "SELECT * FROM ratings;";
    $result = $connect->query($sql);

    while($row = $result->fetch_array(MYSQLI_ASSOC)){
      $sql = "SELECT gameID FROM ratings WHERE gameID = $row[gameID];";
      $item =  $connect->query($sql);
      $itemArray = $item->fetch_array(MYSQLI_ASSOC);
      $ratingsMatrix[$itemArray['gameID']][$row['userID']] = $row['rating'];
    }
    //////////////////////////////////////////////////////////////////////////////

    $item_sim_matrix = array();
    $sim = 0;

    foreach($ratingsMatrix as $gameID=>$rating){
      foreach($ratingsMatrix as $othergameID=>$otherRating)
      {
          if($othergameID != $gameID)
          {
            if($this->similarityType == "EUCLIDIAN"){
              $sim = $this->simFunc->euclidianDistance($ratingsMatrix, $gameID, $othergameID); // TEST 1: EUCLIDIAN DISTANCE
            }
            else if($this->similarityType == "COSINE"){
              $sim = $this->simFunc->cosineSimilarity($ratingsMatrix, $gameID, $othergameID); // TEST 2: COSINE SIMILARITY
            }
            else if($this->similarityType == "ADJUSTED COSINE"){
              $sim = $this->simFunc->adjustedCosineSimilarity($ratingsMatrix, $gameID, $othergameID); // TEST 3: ADJUSTED COSINE SIMILARITY
            }
            else{
              return;
            }

            // REMOVES THE REDUNDANCY OF HAVING A DENSE MATRIX FULL OF ZEROES
            if($sim > 0){
              $item_sim_matrix[$gameID][$othergameID] = round($sim, 3);
            }
          }
      }
    }
    // WRITES THE ITEM-ITEM MATRIX TO A SEPERATE PHP FILE WHICH CAN BE CALL UPON WHEN DOING ITEM-BASED RECOMMENDATIONS (UPDATED INFREQUENTLY)
    $content = serialize($item_sim_matrix);
    $filepath = 'Matrices/' . $this->similarityType . '_ITEM_SIM_MATRIX.txt';
    file_put_contents($filepath, $content);
  }
}
?>
