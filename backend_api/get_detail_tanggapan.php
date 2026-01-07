<?php
    header("Content-Type: application/json");
    require_once "koneksi.php";

    $id_laporan = isset($_POST['id_laporan']) ? $_POST['id_laporan'] : '';

    if (empty($id_laporan)) {
        echo json_encode(["success" => false, "message" => "id_laporan wajib diisi"]);
        exit;
    }

    $idLaporanInt = filter_var($id_laporan, FILTER_VALIDATE_INT, ["options" => ["min_range" => 1]]);
    if ($idLaporanInt === false) {
        echo json_encode(["success" => false, "message" => "id_laporan tidak valid"]);
        exit;
    }

    $sql = "SELECT t.isi_tanggapan, t.tanggal_tanggapan, p.nama_petugas 
            FROM tanggapan t 
            JOIN petugas p ON t.id_petugas = p.id_petugas 
            WHERE t.id_laporan = ? 
            LIMIT 1";

    $stmt = $koneksi->prepare($sql);
    $stmt->bind_param("i", $idLaporanInt);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        echo json_encode([
            "success" => true,
            "isi_tanggapan" => $row['isi_tanggapan'],
            "tanggal_tanggapan" => $row['tanggal_tanggapan'],
            "nama_petugas" => $row['nama_petugas']
        ]);
    } else {
        echo json_encode(["success" => false, "message" => "Belum ada tanggapan"]);
    }

$stmt->close();
$koneksi->close();

?>