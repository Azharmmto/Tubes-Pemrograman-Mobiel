package com.example.tubes_pemrograman_mobile;

public class Laporan {
    private int id;
    private String judul;
    private String deskripsi;
    private String lokasi;
    private String status;
    private String fotoBukti; // Field baru

    public Laporan(int id, String judul, String deskripsi, String lokasi, String status, String fotoBukti) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.status = status;
        this.fotoBukti = fotoBukti;
    }

    // Getter untuk fotoBukti
    public String getFotoBukti() {
        return fotoBukti;
    }

    public int getId() { return id; }
    public String getJudul() { return judul; }
    public String getDeskripsi() { return deskripsi; }
    public String getLokasi() { return lokasi; }
    public String getStatus() { return status; }
}