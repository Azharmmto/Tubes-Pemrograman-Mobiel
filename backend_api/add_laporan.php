<?php

        error_reporting(0);
    ini_set('display_errors', 0);

    header("Content-Type: application/json");
    require_once "koneksi.php";

    $judul     = isset($_POST['judul']) ? $_POST['judul'] : '';
    $deskripsi = isset($_POST['deskripsi']) ? $_POST['deskripsi'] : '';
    $lokasi    = isset($_POST['lokasi']) ? $_POST['lokasi'] : '';
    $id_user   = isset($_POST['id_user']) ? $_POST['id_user'] : '';
    $foto_base64 = isset($_POST['foto']) ? $_POST['foto'] : ''; // Menerima Base64

    if (empty($judul) || empty($deskripsi) || empty($lokasi) || empty($id_user)) {
        echo json_encode(["success" => false, "message" => "Semua field wajib diisi"]);
        exit;
    }

    $idUserInt = filter_var($id_user, FILTER_VALIDATE_INT);
    if ($idUserInt === false) {
        echo json_encode(["success" => false, "message" => "id_user tidak valid"]);
        exit;
    }

    $nama_foto = ""; // Default kosong jika tidak ada foto

    // LOGIKA UPLOAD FOTO
    if (!empty($foto_base64)) {
        // Generate nama file unik: laporan_TIMESTAMP_RANDOM.jpg
        $nama_foto = "laporan_" . time() . "_" . rand(100, 999) . ".jpg";
        $path = "uploads/laporan/" . $nama_foto;
        
        // Decode Base64 dan simpan ke file
        if (file_put_contents($path, base64_decode($foto_base64))) {
            // Berhasil upload
        } else {
            $nama_foto = ""; // Jika gagal, kosongkan
        }
    }

    $status = "Pending";

    // Perhatikan: Tambahkan kolom foto_bukti pada query
    $stmt = $koneksi->prepare("INSERT INTO laporan (judul, deskripsi, lokasi, status, id_user, foto_bukti) VALUES (?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("ssssis", $judul, $deskripsi, $lokasi, $status, $idUserInt, $nama_foto);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Laporan berhasil ditambahkan"]);
    } else {
        echo json_encode(["success" => false, "message" => "Gagal menambahkan laporan"]);
    }

    $stmt->close();
    $koneksi->close();
?>