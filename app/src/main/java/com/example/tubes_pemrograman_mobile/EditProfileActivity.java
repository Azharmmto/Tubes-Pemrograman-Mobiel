package com.example.tubes_pemrograman_mobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etNama, etEmail, etHp;
    private Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etNama = findViewById(R.id.etEditNama);
        etEmail = findViewById(R.id.etEditEmail);
        etHp = findViewById(R.id.etEditHP);
        btnSimpan = findViewById(R.id.btnSimpanProfil);

        // Isi form dengan data saat ini (Nama diambil dari SharedPref)
        // Idealnya Anda buat API get_user_detail.php untuk ambil email/hp juga
        // Di sini kita set Nama dulu sebagai contoh
        etNama.setText(SharedPrefManager.getInstance(this).getNamaLengkap());

        btnSimpan.setOnClickListener(v -> updateProfil());
    }

    private void updateProfil() {
        final String nama = etNama.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String hp = etHp.getText().toString().trim();
        final int idUser = SharedPrefManager.getInstance(this).getIdUser();

        if (nama.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Nama dan Email harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSimpan.setEnabled(false);
        btnSimpan.setText("Menyimpan...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_PROFIL,
                response -> {
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText("Simpan Perubahan");
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            // Update SharedPref agar nama di Dashboard berubah
                            SharedPrefManager.getInstance(getApplicationContext()).updateNama(nama);
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            finish(); // Kembali ke dashboard
                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnSimpan.setEnabled(true);
                    btnSimpan.setText("Simpan Perubahan");
                    Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_user", String.valueOf(idUser));
                params.put("nama", nama);
                params.put("email", email);
                params.put("no_hp", hp);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}