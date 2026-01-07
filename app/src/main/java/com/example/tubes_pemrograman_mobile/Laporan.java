package com.example.tubes_pemrograman_mobile;

public class Laporan {
    private int idLaporan;
    private String judul;
    private String deskripsi;
    private String lokasi;
    private String status;

    public Laporan(int idLaporan, String judul, String deskripsi, String lokasi, String status) {
        this.idLaporan = idLaporan;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.status = status;
    }

    public int getIdLaporan() {
        return idLaporan;
    }

    public String getJudul() {
        return judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getStatus() {
        return status;
    }

}
