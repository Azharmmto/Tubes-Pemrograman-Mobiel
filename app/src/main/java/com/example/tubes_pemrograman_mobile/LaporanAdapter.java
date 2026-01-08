package com.example.tubes_pemrograman_mobile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {

    private Context context;
    private List<Laporan> laporanList;

    public LaporanAdapter(Context context, List<Laporan> laporanList) {
        this.context = context;
        this.laporanList = laporanList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_laporan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Laporan laporan = laporanList.get(position);

        holder.tvJudul.setText(laporan.getJudul());
        holder.tvLokasi.setText(laporan.getLokasi());
        holder.tvStatus.setText(laporan.getStatus());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailLaporanActivity.class);
            intent.putExtra("id_laporan", laporan.getId());
            intent.putExtra("judul", laporan.getJudul());
            intent.putExtra("deskripsi", laporan.getDeskripsi());
            intent.putExtra("lokasi", laporan.getLokasi());
            intent.putExtra("status", laporan.getStatus());
            intent.putExtra("foto_bukti", laporan.getFotoBukti());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // INI YANG SEBELUMNYA KURANG: Deklarasi Variabel
        TextView tvJudul, tvLokasi, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi variabel dengan ID dari XML
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}