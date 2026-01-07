package com.example.tubes_pemrograman_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView rvLaporan;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private final List<Laporan> laporanList = new ArrayList<>();
    private LaporanAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        rvLaporan = findViewById(R.id.rvLaporan);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAdd = findViewById(R.id.fabAdd);

        rvLaporan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LaporanAdapter(this, laporanList);
        rvLaporan.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, BuatLaporanActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchLaporan();
    }

    private void fetchLaporan() {
        int idUser = SharedPrefManager.getInstance(this).getIdUser();

        System.out.println("DEBUG LAPORPAK - ID USER SAAT INI: " + idUser);

        if (idUser == -1) {
            Toast.makeText(this, "Silakan login kembali", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String url = URLs.URL_GET_LAPORAN + "?id_user=" + idUser;

        System.out.println("DEBUG LAPORPAK - URL: " + url);

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        System.out.println("DEBUG LAPORPAK - RESPON: " + response);
                        JSONObject obj = new JSONObject(response);
                        laporanList.clear();
                        if (obj.getBoolean("success")) {
                            JSONArray arr = obj.getJSONArray("laporan");
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject item = arr.getJSONObject(i);
                                laporanList.add(new Laporan(
                                        item.getInt("id"),
                                        item.getString("judul"),
                                        item.getString("deskripsi"),
                                        item.getString("lokasi"),
                                        item.getString("status")
                                ));
                            }
                        }
                        adapter .notifyDataSetChanged();
                        toggleEmpty();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Kesalahan parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show());

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void toggleEmpty() {
        if (laporanList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvLaporan.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvLaporan.setVisibility(View.VISIBLE);
        }
    }
}