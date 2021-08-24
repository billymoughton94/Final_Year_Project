<?php

$DB_SERVER = "localhost";
$DB_USER = "billy";
$DB_PASSWORD = "21February94!";
$DB_NAME = "games_db";

$connect = new mysqli($DB_SERVER, $DB_USER, $DB_PASSWORD, $DB_NAME);

if (mysqli_connect_errno()) {
  die("Connection failed: " . $connect->connect_error);
}

?>
