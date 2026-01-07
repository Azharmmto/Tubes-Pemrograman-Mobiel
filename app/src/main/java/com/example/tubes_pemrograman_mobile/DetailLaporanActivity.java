package com.example.tubes_pemrograman_mobile;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailLaporanActivity extends AppCompatActivity {

    private TextView tvJudul, tvDeskripsi, tvLokasi, tvStatus, tvTanggapan, tvPetugas;
    private int idLaporan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_laporan);

        tvJudul = findViewById(R.id.tvDetailJudul);
        tvDeskripsi = findViewById(R.id.tvDetailDeskripsi);
        tvLokasi = findViewById(R.id.tvDetailLokasi);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvTanggapan = findViewById(R.id.tvIsiTanggapan);
        tvPetugas = findViewById(R.id.tvNamaPetugas);

        idLaporan = getIntent().getIntExtra("id_laporan", -1);
        tvJudul.setText(getIntent().getStringExtra("judul"));
        tvDeskripsi.setText(getIntent().getStringExtra("deskripsi"));
        tvLokasi.setText(getIntent().getStringExtra("lokasi"));
        tvStatus.setText(getIntent().getStringExtra("status"));

        fetchTanggapan();
    }

    private void fetchTanggapan() {
        if (idLaporan == -1) {
            tvTanggapan.setText("Belum ada tanggapan");
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_GET_TANGGAPAN,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            tvTanggapan.setText(obj.getString("isi_tanggapan"));
                            tvPetugas.setText(obj.getString("nama_petugas"));
                        } else {
                            tvTanggapan.setText("Belum ada tanggapan");
                            tvPetugas.setText("-");
                        }
                    } catch (JSONException e) {
                        tvTanggapan.setText("Belum ada tanggapan");
                        tvPetugas.setText("-");
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Gagal memuat tanggapan", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_laporan", String.valueOf(idLaporan));
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}