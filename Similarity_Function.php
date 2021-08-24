<?PHP

class Similarity_Function{

  function euclidianDistance($matrix, $entity_A, $entity_B){
    $similar = array();
    $sum = 0;

    foreach($matrix[$entity_A] as $id=>$rating)
    {
        if(array_key_exists($id, $matrix[$entity_B]))
            $similar[$id] = 1;
    }
    if(count($similar) == 0)
        return 0;
    // CHECKING ALL GAMES IN USER A's LIST AGAINST USER B
    foreach($matrix[$entity_A] as $id=>$rating)
    {
        if(array_key_exists($id, $matrix[$entity_B])){
          $sum = $sum + pow($rating - $matrix[$entity_B][$id], 2);
        }
    }
    // CONVERTS DISTANCE TO SIMILARITY - IDENTICAL ENTITIES = 1
    return  1/(1 + sqrt($sum));
  }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  function cosineSimilarity($matrix, $entity_A, $entity_B){
    $similar = array();
    $dotProductSum = 0;
    $entityA_Magnitude = 0;
    $entityB_Magnitude = 0;

    foreach($matrix[$entity_A] as $id=>$rating)
    {
        if(array_key_exists($id, $matrix[$entity_B]))
        {
          $similar[$id] = 1;
          $dotProductSum += ($rating * $matrix[$entity_B][$id]);
          $entityA_Magnitude = $entityA_Magnitude + $rating**2;
        }
    }
    if(count($similar) == 0)
        return 0;

    foreach($matrix[$entity_B] as $id=>$rating)
    {
      if(array_key_exists($id, $matrix[$entity_A]))
        $entityB_Magnitude = $entityB_Magnitude + $rating**2;
    }
    $sqrtMag = sqrt($entityA_Magnitude) * sqrt($entityB_Magnitude);
    return $dotProductSum / $sqrtMag;
  }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  function adjustedCosineSimilarity($matrix, $entity_A, $entity_B){
    $similar = array();
    $dotProductSum = 0;
    $entityA_Magnitude = 0;
    $entityB_Magnitude = 0;

    // CALCULATING EACH USER'S AVERAGE RAITNG //
    $userA_ratingsum = 0;
    $userA_numRatings = 0;
    foreach($matrix[$entity_A] as $id=>$rating){
      $userA_ratingsum += $rating;
      $userA_numRatings++;
    }
    $userA_average = $userA_ratingsum / $userA_numRatings;

    $userB_ratingsum = 0;
    $userB_numRatings = 0;
    foreach($matrix[$entity_B] as $id=>$rating){
      $userB_ratingsum += $rating;
      $userB_numRatings++;
    }
    $userB_average = $userB_ratingsum / $userB_numRatings;
    ////////////////////////////////////////////////////////

    foreach($matrix[$entity_A] as $id=>$rating)
    {
        if(array_key_exists($id, $matrix[$entity_B]))
        {
          $similar[$id] = 1;
          $dotProductSum += (($rating - $userA_average) * ($matrix[$entity_B][$id] - $userB_average));
          $entityA_Magnitude = $entityA_Magnitude + ($rating - $userA_average)**2;
        }
    }
    if(count($similar) == 0)
        return 0;

    foreach($matrix[$entity_B] as $id=>$rating)
    {
      if(array_key_exists($id, $matrix[$entity_A]))
        $entityB_Magnitude = $entityB_Magnitude + ($rating - $userB_average)**2;
    }
    $sqrtMag = sqrt($entityA_Magnitude) * sqrt($entityB_Magnitude);
    if($sqrtMag == 0)
      return 0;
    return $dotProductSum / $sqrtMag;
  }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  function pearsonCorrelation($matrix, $entity_A, $entity_B){
  }
}
?>
