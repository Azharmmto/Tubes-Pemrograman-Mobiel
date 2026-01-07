package com.example.tubes_pemrograman_mobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNik, etNama, etEmail, etPass, etHp;
    private Button btnRegister;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNik = findViewById(R.id.etNik);
        etNama = findViewById(R.id.etNamaLengkap);
        etEmail = findViewById(R.id.etEmailRegister);
        etPass = findViewById(R.id.etPasswordRegister);
        etHp = findViewById(R.id.etHp);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        final String nik = etNik.getText().toString().trim();
        final String nama = etNama.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String pass = etPass.getText().toString().trim();
        final String hp = etHp.getText().toString().trim();

        if (nik.isEmpty() || nama.isEmpty() || email.isEmpty() || pass.isEmpty() || hp.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);

        StringRequest request = new StringRequest(Request.Method.POST, URLs.URL_REGISTER,
                response -> {
                    btnRegister.setEnabled(true);

                    System.out.println("RESPON SERVER: " + response);

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
                    btnRegister.setEnabled(true);
                    String errorMsg = error.toString();
                    if (error.networkResponse != null) {
                        errorMsg += " Status Code: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(getApplicationContext(), "Error: " + errorMsg, Toast.LENGTH_LONG).show();
                    error.printStackTrace(); // Cek Logcat di Android Studio untuk detailnya
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nik", nik);
                params.put("nama", nama);
                params.put("email", email);
                params.put("pass", pass);
                params.put("hp", hp);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}