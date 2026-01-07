<?php

    header("Content-Type: application/json");
    require_once "koneksi.php";

    $judul     = isset($_POST['judul']) ? $_POST['judul'] : '';
    $deskripsi = isset($_POST['deskripsi']) ? $_POST['deskripsi'] : '';
    $lokasi    = isset($_POST['lokasi']) ? $_POST['lokasi'] : '';
    $id_user   = isset($_POST['id_user']) ? $_POST['id_user'] : '';

    if (empty($judul) || empty($deskripsi) || empty($lokasi) || empty($id_user)) {
        echo json_encode(["success" => false, "message" => "Semua field wajib diisi"]);
        exit;
    }

    $idUserInt = filter_var($id_user, FILTER_VALIDATE_INT, ["options" => ["min_range" => 1]]);
    if ($idUserInt === false) {
        echo json_encode(["success" => false, "message" => "id_user tidak valid"]);
        exit;
    }

    $status = "Pending";
    $stmt = $koneksi->prepare("INSERT INTO laporan (judul, deskripsi, lokasi, status, id_user) VALUES (?, ?, ?, ?, ?)");
    $stmt->bind_param("ssssi", $judul, $deskripsi, $lokasi, $status, $idUserInt);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Laporan berhasil ditambahkan"]);
    } else {
        echo json_encode(["success" => false, "message" => "Gagal menambahkan laporan"]);
    }

    $stmt->close();
    $koneksi->close();

?>