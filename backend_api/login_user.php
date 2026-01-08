<?php

        error_reporting(0);
    ini_set('display_errors', 0);

  header("Content-Type: application/json");
  require_once "koneksi.php";

  $email    = isset($_POST['email']) ? $_POST['email'] : '';
  $password = isset($_POST['password']) ? $_POST['password'] : '';

  if (empty($email) || empty($password)) {
      echo json_encode(["success" => false, "message" => "Email dan password wajib diisi"]);
      exit;
  }

  $stmt = $koneksi->prepare("SELECT id, nama_lengkap, password FROM users WHERE email = ?");
  $stmt->bind_param("s", $email);
  $stmt->execute();
  $result = $stmt->get_result();

  if ($row = $result->fetch_assoc()) {
      $storedPassword = $row['password'];
      if (password_verify($password, $storedPassword)) {
          echo json_encode([
              "success" => true,
              "message" => "Login berhasil",
              "id_user" => $row['id'],
              "nama_lengkap" => $row['nama_lengkap']
          ]);
      } else {
          echo json_encode(["success" => false, "message" => "Password salah"]);
      }
  } else {
      echo json_encode(["success" => false, "message" => "Pengguna tidak ditemukan"]);
  }

  $stmt->close();
  $koneksi->close();

?>