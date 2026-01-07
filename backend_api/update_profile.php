<?php
  header("Content-Type: application/json");
  require_once "koneksi.php";

  // Menerima input
  $id_user = isset($_POST['id_user']) ? $_POST['id_user'] : '';
  $nama    = isset($_POST['nama']) ? $_POST['nama'] : '';
  $email   = isset($_POST['email']) ? $_POST['email'] : '';
  $no_hp   = isset($_POST['no_hp']) ? $_POST['no_hp'] : '';

  if (empty($id_user) || empty($nama) || empty($email)) {
      echo json_encode(["success" => false, "message" => "Data tidak boleh kosong"]);
      exit;
  }

  // Update data user
  $stmt = $koneksi->prepare("UPDATE users SET nama_lengkap = ?, email = ?, no_hp = ? WHERE id = ?");
  $stmt->bind_param("sssi", $nama, $email, $no_hp, $id_user);

  if ($stmt->execute()) {
      echo json_encode([
          "success" => true, 
          "message" => "Profil berhasil diperbarui",
          "nama_lengkap" => $nama, // Kembalikan nama baru untuk update SharedPref
          "email" => $email
      ]);
  } else {
      echo json_encode(["success" => false, "message" => "Gagal memperbarui profil"]);
  }

  $stmt->close();
  $koneksi->close();
?>