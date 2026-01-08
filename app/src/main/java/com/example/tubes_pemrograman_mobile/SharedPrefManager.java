package com.example.tubes_pemrograman_mobile;

import android.content.Context;
import android.content.SharedPreferences;
public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "laporpak_shared";
    private static final String KEY_ID_USER = "key_id_user";
    private static final String KEY_NAMA = "key_nama";

    private static SharedPrefManager mInstance;
    private static Context mCtx;
    private static final String KEY_FOTO = "key_foto";

    private SharedPrefManager(Context context) {
        mCtx = context.getApplicationContext();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void userLogin(int idUser, String namaLengkap, String foto) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID_USER, idUser);
        editor.putString(KEY_NAMA, namaLengkap);
        editor.putString(KEY_FOTO, foto); // Simpan foto
        editor.apply();
    }

    // Method khusus update foto
    public void updateFoto(String fotoBaru) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FOTO, fotoBaru);
        editor.apply();
    }

    // Method ambil foto
    public String getFoto() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FOTO, ""); // Default kosong
    }

    public void updateNama(String namaBaru) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAMA, namaBaru);
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.contains(KEY_ID_USER);
    }

    public int getIdUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_ID_USER, -1);
    }

    public String getNamaLengkap() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NAMA, "");
    }

    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
