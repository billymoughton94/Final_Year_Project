<?php
/*
CODE HAS BEEN DERIVED FROM THE FOLLOWING SOURCE:
  AUTHOR: SAMEER
  Original Date Posted: 24/04/2008
  Date Aquired: 08/02/2021
  Source Files: sample_recommend(recommend.php, reccomend_test.php, sample_list.php)
  URL: https://www.codediesel.com/php/item-based-collaborative-filtering-php/

[37] "Item based collaborative filtering in PHP", 
codediesel, 2008. [Online]. 
Available: https://www.codediesel.com/php/item-based-collaborative-filtering-php/. [Accessed: 08- Feb- 2021].

  -------------------------------------------------------------------------
  USER - BASED C.F.
    IMPLEMENTS THE USER-ITEM MATRIX
    USER SIMILARITIES COMPUTED BY CHECKING SAME ITEMS THEY'VE RATED.
    FOR USERS THAT ARE SIMILAR, EACH OF THEIR RATED GAMES ARE CHECKED
      AND ADD TOWARDS TOTAL SCORE FOR EACH GAME (SIMILARITY * THEIR RATING).
    THE SUMS OF SIMILARITY BETWEEN USERS FOR FOR EACH GAME ALSO COMPUTED.
    THE FINAL RANK OF A GAME = TOTAL_WEIGHTED_RATING / SIMILARITY SUMS

    IN SHORT - FINDS SIMILAR USERS AND SIMILAR USERS ADD MORE WEIGHT TO THE SIMILARITY OF THE GAMES
*/

require '../connect.php';

class User_Based_Recommender{
  private string $similarityType;
  private Similarity_Function $simFunc;
  private bool $testing; // used for evaluation

  public function __construct(string $similarityType, bool $testing){
    $this->similarityType = $similarityType;
    $this->testing = $testing; // IF WE'RE EVALUATING, TESTING = TRUE:
    $this->simFunc = new Similarity_Function();
  }

  // METHOD CALLED WHEN GETTING SIMILAR ITEMS
  public function getRecommendations($ratingsMatrix, $userID)
  {
      $total = array();
      $simSums = array();
      $ranks = array();
      $sim = 0;

      // FINDS SIMILAR USERS
      foreach($ratingsMatrix as $other_userID=>$gameID)
      {
          if($other_userID != $userID)
          {
            if($this->similarityType == "EUCLIDIAN"){
              $sim = $this->simFunc->euclidianDistance($ratingsMatrix, $userID, $other_userID); // TEST 1: EUCLIDIAN DISTANCE
            }
            else if($this->similarityType == "COSINE"){
              $sim = $this->simFunc->cosineSimilarity($ratingsMatrix, $userID, $other_userID); // TEST 2: COSINE SIMILARITY
            }
            else if($this->similarityType == "ADJUSTED COSINE"){
              $sim = $this->simFunc->adjustedCosineSimilarity($ratingsMatrix, $userID, $other_userID); // TEST 3: ADJUSTED COSINE SIMILARITY
            }
            else{
              return;
            }
          }
          // ONLY CHECK USERS WITH A SIMIALRITY GREATER THAN / EQUAL TO 0.3 - THIS IS OUR THRESHOLD
          if($sim >= 0.3)
          {
              foreach($ratingsMatrix[$other_userID] as $gameID=>$rating)
              {

                  // ONLY PROVIDE RECOMMENDATIONS FOR GAMES NOT RATED (UNLESS WE ARE TESTING)
                  if(!array_key_exists($gameID, $ratingsMatrix[$userID]) || $this->testing)
                  {
                      if(!array_key_exists($gameID, $total)) {
                          $total[$gameID] = 0;
                      }
                      // INITIAL PREDICTED RATINGS ARE FOUND BY MULTIPLYING OTHER USER'S RATING BY SIMILARITY LEVEL (ACTS AS A WEIGHT)
                      $total[$gameID] += $ratingsMatrix[$other_userID][$gameID] * $sim;

                      if(!array_key_exists($gameID, $simSums)) {
                          $simSums[$gameID] = 0;
                      }
                      $simSums[$gameID] += $sim;
                  }
              }
          }
      }
      foreach($total as $gameID=>$totalWeightedRating)
      {
          $ranks[$gameID] = round(($totalWeightedRating / $simSums[$gameID]), 3);
      }

  if($this->testing)
    return $ranks;
  arsort($ranks);
  return array_slice($ranks, 0, 10, true); // returns top 10 most similar games
  }
}//end of class
?>
