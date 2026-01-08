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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BuatLaporanActivity extends AppCompatActivity {

    private EditText etJudul, etDeskripsi, etLokasi;
    private Button btnKirim, btnPilihFoto;
    private ImageView ivPreviewFoto;
    private Bitmap bitmapFoto; // Menyimpan gambar yang dipilih

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_laporan);

        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etLokasi = findViewById(R.id.etLokasi);
        btnKirim = findViewById(R.id.btnKirim);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        ivPreviewFoto = findViewById(R.id.ivPreviewFoto);

        // Listener untuk tombol Pilih Foto
        btnPilihFoto.setOnClickListener(v -> pilihFoto());

        btnKirim.setOnClickListener(v -> kirimLaporan());
    }

    // Launcher untuk membuka Galeri (Cara Modern)
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        // Konversi URI ke Bitmap
                        bitmapFoto = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        // Tampilkan di ImageView
                        ivPreviewFoto.setImageBitmap(bitmapFoto);
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

    // Fungsi mengubah Bitmap ke String Base64
    private String imageToString(Bitmap bitmap) {
        if (bitmap == null) return "";

        // Resize gambar agar tidak terlalu besar (Max lebar/tinggi 1024px)
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratioBitmap = (float) width / (float) height;
        int maxSize = 1024;

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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream); // Kompresi 70%
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

    private void kirimLaporan() {
        final String judul = etJudul.getText().toString().trim();
        final String deskripsi = etDeskripsi.getText().toString().trim();
        final String lokasi = etLokasi.getText().toString().trim();
        final int idUser = SharedPrefManager.getInstance(this).getIdUser();

        if (judul.isEmpty() || deskripsi.isEmpty() || lokasi.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        btnKirim.setEnabled(false);
        btnKirim.setText("Mengirim...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_ADD_LAPORAN,
                response -> {
                    btnKirim.setEnabled(true);
                    btnKirim.setText("Kirim Laporan");
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getBoolean("success")) {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error parsing: " + response, Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    btnKirim.setEnabled(true);
                    btnKirim.setText("Kirim Laporan");
                    Toast.makeText(getApplicationContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_user", String.valueOf(idUser));
                params.put("judul", judul);
                params.put("deskripsi", deskripsi);
                params.put("lokasi", lokasi);

                // Kirim Foto jika ada
                if (bitmapFoto != null) {
                    params.put("foto", imageToString(bitmapFoto));
                }
                return params;
            }
        };

        // Penting: Set Timeout lebih lama untuk upload gambar
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000, // 30 detik
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}