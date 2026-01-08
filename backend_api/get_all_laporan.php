<?php

        error_reporting(0);
    ini_set('display_errors', 0);

    header("Content-Type: application/json");
    require_once "koneksi.php";

    $id_user = isset($_GET['id_user']) ? $_GET['id_user'] : '';

    if (empty($id_user)) {
        // Fallback cek POST
        $id_user = isset($_POST['id_user']) ? $_POST['id_user'] : '';
    }

    if (empty($id_user)) {
        echo json_encode(["success" => false, "message" => "id_user wajib diisi", "laporan" => []]);
        exit;
    }

    $idUserInt = filter_var($id_user, FILTER_VALIDATE_INT);
    if ($idUserInt === false) {
        echo json_encode(["success" => false, "message" => "id_user tidak valid", "laporan" => []]);
        exit;
    }

    // Tambahkan foto_bukti di SELECT
    // Sesuaikan nama kolom ID: id_laporan atau id
    $stmt = $koneksi->prepare("SELECT id, judul, deskripsi, foto_bukti, lokasi, status FROM laporan WHERE id_user = ? ORDER BY id DESC");

    $stmt->bind_param("i", $idUserInt);
    $stmt->execute();
    $result = $stmt->get_result();

    $laporan = [];
    while ($row = $result->fetch_assoc()) {
        $laporan[] = $row;
    }

    echo json_encode(["success" => true, "laporan" => $laporan]);

    $stmt->close();
    $koneksi->close();
?>