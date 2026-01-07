package com.example.tubes_pemrograman_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rvLaporan;
    private LinearLayout tvEmpty;
    private FloatingActionButton fabAdd;
    private final List<Laporan> laporanList = new ArrayList<>();
    private LaporanAdapter adapter;

    // Variabel untuk Drawer/Sidebar
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private TextView tvNavName; // Nama di header sidebar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Inisialisasi Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inisialisasi Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Toggle Hamburger Icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update Nama di Header Sidebar
        View headerView = navigationView.getHeaderView(0);
        tvNavName = headerView.findViewById(R.id.tvNavName);
        updateHeaderInfo();

        // Komponen Dashboard Lama
        rvLaporan = findViewById(R.id.rvLaporan);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAdd = findViewById(R.id.fabAdd);

        rvLaporan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LaporanAdapter(this, laporanList);
        rvLaporan.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> startActivity(new Intent(DashboardActivity.this, BuatLaporanActivity.class)));
    }

    private void updateHeaderInfo() {
        String nama = SharedPrefManager.getInstance(this).getNamaLengkap();
        tvNavName.setText(nama);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeaderInfo(); // Update nama kalau habis edit profil
        fetchLaporan();
    }

    // Logic saat menu diklik
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Sudah di dashboard, tutup aja
        } else if (id == R.id.nav_edit_profile) {
            startActivity(new Intent(DashboardActivity.this, EditProfileActivity.class));
        } else if (id == R.id.nav_logout) {
            SharedPrefManager.getInstance(this).logout();
            Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Handle tombol back agar menutup drawer dulu jika terbuka
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void fetchLaporan() {
        int idUser = SharedPrefManager.getInstance(this).getIdUser();

        if (idUser == -1) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        String url = URLs.URL_GET_LAPORAN + "?id_user=" + idUser;

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        laporanList.clear();
                        if (obj.getBoolean("success")) {
                            JSONArray arr = obj.getJSONArray("laporan");
                            for (int i = 0; i < arr.length(); i++) {
                                JSONObject item = arr.getJSONObject(i);
                                laporanList.add(new Laporan(
                                        item.getInt("id"), // Sesuaikan dengan kolom DB (id atau id_laporan)
                                        item.getString("judul"),
                                        item.getString("deskripsi"),
                                        item.getString("lokasi"),
                                        item.getString("status")
                                ));
                            }
                        }
                        adapter.notifyDataSetChanged();
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