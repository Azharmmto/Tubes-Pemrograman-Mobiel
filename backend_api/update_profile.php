<?php

        error_reporting(0);
    ini_set('display_errors', 0);

    header("Content-Type: application/json");
    require_once "koneksi.php";

    $id_user     = isset($_POST['id_user']) ? $_POST['id_user'] : '';
    $nama        = isset($_POST['nama']) ? $_POST['nama'] : '';
    $email       = isset($_POST['email']) ? $_POST['email'] : '';
    $no_hp       = isset($_POST['no_hp']) ? $_POST['no_hp'] : '';
    $foto_base64 = isset($_POST['foto']) ? $_POST['foto'] : '';

    if (empty($id_user) || empty($nama) || empty($email)) {
        echo json_encode(["success" => false, "message" => "Data tidak boleh kosong"]);
        exit;
    }

    // Logic Upload Foto Profil
    $update_foto_sql = "";
    $nama_foto_baru = "";

    if (!empty($foto_base64)) {
        $nama_foto_baru = "profil_" . $id_user . "_" . time() . ".jpg";
        $path = "uploads/profil/" . $nama_foto_baru;
        
        if (file_put_contents($path, base64_decode($foto_base64))) {
            $update_foto_sql = ", foto_profil = ?"; // Tambahan query update foto
        }
    }

    // Susun Query Dinamis
    if (!empty($update_foto_sql)) {
        // Jika ada update foto
        $stmt = $koneksi->prepare("UPDATE users SET nama_lengkap = ?, email = ?, no_hp = ? $update_foto_sql WHERE id = ?");
        $stmt->bind_param("ssssi", $nama, $email, $no_hp, $nama_foto_baru, $id_user);
    } else {
        // Jika tidak ada update foto (hanya data teks)
        $stmt = $koneksi->prepare("UPDATE users SET nama_lengkap = ?, email = ?, no_hp = ? WHERE id = ?");
        $stmt->bind_param("sssi", $nama, $email, $no_hp, $id_user);
    }

    if ($stmt->execute()) {
        echo json_encode([
            "success" => true, 
            "message" => "Profil berhasil diperbarui",
            "nama_lengkap" => $nama,
            "email" => $email
        ]);
    } else {
        echo json_encode(["success" => false, "message" => "Gagal memperbarui profil"]);
    }

    $stmt->close();
    $koneksi->close();
?>