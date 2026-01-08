package com.example.tubes_pemrograman_mobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BuatLaporanActivity extends AppCompatActivity {

    private EditText etJudul, etDeskripsi, etLokasi;
    private Button btnKirim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_laporan);

        // tombol back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Buat Laporan"); // Opsional: Set judul
        }

        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etLokasi = findViewById(R.id.etLokasi);
        btnKirim = findViewById(R.id.btnKirim);

        btnKirim.setOnClickListener(v -> kirimLaporan());
    }

    // --- TAMBAHKAN FUNGSI INI AGAR TOMBOL BACK BERFUNGSI ---
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Menutup activity ini dan kembali ke sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    // --------------------------------------------------------

    private void kirimLaporan() {
        final String judul = etJudul.getText().toString().trim();
        final String deskripsi = etDeskripsi.getText().toString().trim();
        final String lokasi = etLokasi.getText().toString().trim();
        final int idUser = SharedPrefManager.getInstance(this).getIdUser();

        System.out.println("ID USER: " + idUser);
        System.out.println("JUDUL: " + judul);
        System.out.println("DESKRIPSI: " + deskripsi);
        System.out.println("LOKASI: " + lokasi);

        if (judul.isEmpty() || deskripsi.isEmpty() || lokasi.isEmpty()){
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        btnKirim.setEnabled(false);

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_ADD_LAPORAN,
                response -> {
                    btnKirim.setEnabled(true);
                    try {
                        JSONObject obj = new JSONObject(response);
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        if (obj.getBoolean("success")) {
                            finish();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Kesalahan parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnKirim.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Gagal mengirim laporan", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("judul", judul);
                params.put("deskripsi", deskripsi);
                params.put("lokasi", lokasi);
                params.put("id_user", String.valueOf(idUser));
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}