<?php

  header("Content-Type: application/json");

  $host = "localhost";
  $user = "root";
  $pass = "18mariadb";
  $db   = "laporpak";

  $koneksi = new mysqli($host, $user, $pass, $db);

  if ($koneksi->connect_error) {
      die(json_encode([
          "success" => false,
          "message" => "Koneksi database gagal"
      ]));
  }

?>