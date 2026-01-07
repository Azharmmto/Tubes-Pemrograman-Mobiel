<?php
  header("Content-Type: application/json");
  require_once "koneksi.php";

  $nik   = isset($_POST['nik']) ? $_POST['nik'] : '';
  $nama  = isset($_POST['nama']) ? $_POST['nama'] : '';
  $email = isset($_POST['email']) ? $_POST['email'] : '';
  $pass  = isset($_POST['pass']) ? $_POST['pass'] : '';
  $hp    = isset($_POST['hp']) ? $_POST['hp'] : '';

  if (empty($nik) || empty($nama) || empty($email) || empty($pass) || empty($hp)) {
      echo json_encode(["success" => false, "message" => "Semua field wajib diisi"]);
      exit;
  }

  if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
      echo json_encode(["success" => false, "message" => "Format email tidak valid"]);
      exit;
  }

  if (!preg_match('/^[0-9]+$/', $nik) || !preg_match('/^[0-9]+$/', $hp)) {
      echo json_encode(["success" => false, "message" => "NIK dan No HP harus berupa angka"]);
      exit;
  }

  if (strlen($nik) !== 16) {
      echo json_encode(["success" => false, "message" => "NIK harus 16 digit"]);
      exit;
  }

  if (strlen($hp) < 9 || strlen($hp) > 15) {
      echo json_encode(["success" => false, "message" => "No HP tidak valid"]);
      exit;
  }

  $cek = $koneksi->prepare("SELECT id FROM users WHERE nik = ? OR email = ? LIMIT 1");
  $cek->bind_param("ss", $nik, $email);
  $cek->execute();
  $cek->store_result();
  if ($cek->num_rows > 0) {
      echo json_encode(["success" => false, "message" => "NIK atau email sudah terdaftar"]);
      $cek->close();
      exit;
  }
  $cek->close();

  $hashedPass = password_hash($pass, PASSWORD_BCRYPT);

  $stmt = $koneksi->prepare("INSERT INTO users (nik, nama_lengkap, email, password, no_hp) VALUES (?, ?, ?, ?, ?)");
  $stmt->bind_param("sssss", $nik, $nama, $email, $hashedPass, $hp);

  if ($stmt->execute()) {
      echo json_encode(["success" => true, "message" => "Registrasi berhasil"]);
  } else {
      echo json_encode(["success" => false, "message" => "Registrasi gagal"]);
  }

  $stmt->close();
  $koneksi->close();

?>