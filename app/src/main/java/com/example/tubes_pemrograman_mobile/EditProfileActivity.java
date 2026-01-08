package com.example.tubes_pemrograman_mobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etNama, etEmail, etHp;
    private Button btnSimpan, btnGantiFoto;
    private ImageView ivProfil;
    private Bitmap bitmapProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        etNama = findViewById(R.id.etEditNama);
        etEmail = findViewById(R.id.etEditEmail);
        etHp = findViewById(R.id.etEditHP);
        btnSimpan = findViewById(R.id.btnSimpanProfil);
        btnGantiFoto = findViewById(R.id.btnGantiFoto);
        ivProfil = findViewById(R.id.ivProfil);


        etNama.setText(SharedPrefManager.getInstance(this).getNamaLengkap());
        // Idealnya load data lain dan foto profil dari API "get_user_detail" di sini

        btnGantiFoto.setOnClickListener(v -> pilihFoto());
        btnSimpan.setOnClickListener(v -> updateProfil());

        String fotoLama = SharedPrefManager.getInstance(this).getFoto();
        if (!fotoLama.isEmpty()) {
            String urlFoto = URLs.BASE_URL + "backend_api/uploads/profil/" + fotoLama;
            ImageRequest imageRequest = new ImageRequest(urlFoto,
                    response -> ivProfil.setImageBitmap(response),
                    0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565, null);
            VolleySingleton.getInstance(this).addToRequestQueue(imageRequest);
        }
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        bitmapProfil = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        ivProfil.setImageBitmap(bitmapProfil);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void pilihFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private String imageToString(Bitmap bitmap) {
        if (bitmap == null) return "";

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratioBitmap = (float) width / (float) height;
        int maxSize = 512; // Foto profil lebih kecil saja

        if (width > maxSize || height > maxSize) {
            if (ratioBitmap > 1) {
                width = maxSize;
                height = (int) (width / ratioBitmap);
            } else {
                height = maxSize;
                width = (int) (height * ratioBitmap);
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes, Base64.DEFAULT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                            SharedPrefManager.getInstance(getApplicationContext()).updateNama(nama);

                            // CEK APAKAH ADA FOTO BARU DARI SERVER
                            String fotoBaru = obj.optString("foto_profil", "");
                            if (!fotoBaru.isEmpty()) {
                                SharedPrefManager.getInstance(getApplicationContext()).updateFoto(fotoBaru);
                            }

                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
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
                params.put("id", String.valueOf(idUser));
                params.put("nama", nama);
                params.put("email", email);
                params.put("no_hp", hp);

                if (bitmapProfil != null) {
                    params.put("foto", imageToString(bitmapProfil));
                }
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}