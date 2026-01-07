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

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder> {

    private final Context context;
    private final List<Laporan> laporanList;

    public LaporanAdapter(Context context, List<Laporan> laporanList) {
        this.context = context;
        this.laporanList = laporanList;
    }

    @NonNull
    @Override
    public LaporanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_laporan, parent, false);
        return new LaporanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LaporanViewHolder holder, int position) {
        Laporan laporan = laporanList.get(position);
        holder.tvJudul.setText(laporan.getJudul());
        holder.tvStatus.setText(laporan.getStatus());
        holder.tvLokasi.setText(laporan.getLokasi());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailLaporanActivity.class);
            intent.putExtra("id_laporan", laporan.getIdLaporan());
            intent.putExtra("judul", laporan.getJudul());
            intent.putExtra("deskripsi", laporan.getDeskripsi());
            intent.putExtra("lokasi", laporan.getLokasi());
            intent.putExtra("status", laporan.getStatus());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return laporanList.size();
    }

    static class LaporanViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvStatus, tvLokasi;

        LaporanViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvItemJudul);
            tvStatus = itemView.findViewById(R.id.tvItemStatus);
            tvLokasi = itemView.findViewById(R.id.tvItemLokasi);
        }
    }
}
