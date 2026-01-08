package com.example.tubes_pemrograman_mobile;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailLaporanActivity extends AppCompatActivity {

    private TextView tvJudul, tvDeskripsi, tvLokasi, tvStatus, tvTanggapan, tvPetugas;
    private ImageView ivFotoBukti;
    private int idLaporan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_laporan);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Laporan");
        }

        tvJudul = findViewById(R.id.tvDetailJudul);
        tvDeskripsi = findViewById(R.id.tvDetailDeskripsi);
        tvLokasi = findViewById(R.id.tvDetailLokasi);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvTanggapan = findViewById(R.id.tvIsiTanggapan);
        tvPetugas = findViewById(R.id.tvNamaPetugas);
        ivFotoBukti = findViewById(R.id.ivFotoBukti);

        idLaporan = getIntent().getIntExtra("id_laporan", -1);
        String judul = getIntent().getStringExtra("judul");
        String deskripsi = getIntent().getStringExtra("deskripsi");
        String lokasi = getIntent().getStringExtra("lokasi");
        String status = getIntent().getStringExtra("status");
        String fotoBukti = getIntent().getStringExtra("foto_bukti");

        tvJudul.setText(judul);
        tvDeskripsi.setText(deskripsi);
        tvLokasi.setText(lokasi);
        tvStatus.setText(status);

        // Load Foto Jika Ada
        if (fotoBukti != null && !fotoBukti.isEmpty()) {
            ivFotoBukti.setVisibility(View.VISIBLE);
            // URL Gambar: IP Laptop + path folder upload
            // Contoh: http://192.168.1.5/laporpak_api/uploads/laporan/nama_file.jpg
            // Kita ambil base URL dari URLs.java, tapi potong bagian "laporpak_api/" kalau perlu
            // Asumsi BASE_URL di URLs.java sudah ada slash di akhir

            // Kita construct URL manual karena BASE_URL biasanya menunjuk root API
            // Ambil BASE_URL dari class URLs, buang nama file php (tidak perlu, karena BASE_URL = folder api)
            String urlGambar = URLs.URL_LOGIN.replace("login_user.php", "") + "uploads/laporan/" + fotoBukti;

            loadFoto(urlGambar);
        } else {
            ivFotoBukti.setVisibility(View.GONE);
        }

        fetchTanggapan();
    }

    private void loadFoto(String url) {
        ImageRequest imageRequest = new ImageRequest(
                url,
                response -> ivFotoBukti.setImageBitmap(response),
                0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
                error -> {
                    // Jika gagal load gambar (misal 404), sembunyikan atau biarkan placeholder
                    Toast.makeText(DetailLaporanActivity.this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
                }
        );
        VolleySingleton.getInstance(this).addToRequestQueue(imageRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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