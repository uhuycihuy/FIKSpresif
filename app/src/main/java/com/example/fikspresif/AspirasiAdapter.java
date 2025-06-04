package com.example.fikspresif;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AspirasiAdapter extends RecyclerView.Adapter<AspirasiAdapter.ViewHolder> {

    private final ArrayList<Aspirasi> aspirasiList;
    private final boolean isPublicView;

    public AspirasiAdapter(ArrayList<Aspirasi> aspirasiList, boolean isPublicView) {
        this.aspirasiList = aspirasiList;
        this.isPublicView = isPublicView;
    }

    @NonNull
    @Override
    public AspirasiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_aspirasi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AspirasiAdapter.ViewHolder holder, int position) {
        Aspirasi aspirasi = aspirasiList.get(position);
        holder.tvTitle.setText(aspirasi.getTitle());
        holder.tvContent.setText(aspirasi.getContent());
        holder.tvDate.setText(aspirasi.getCreatedAt());

        if (isPublicView) {
            holder.tvUser.setVisibility(View.VISIBLE);
            holder.tvUser.setText(aspirasi.getUsername());
        } else {
            holder.tvUser.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return aspirasiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate, tvUser;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJudul);
            tvContent = itemView.findViewById(R.id.tvIsi);
            tvDate = itemView.findViewById(R.id.tvTanggal);
            tvUser = itemView.findViewById(R.id.tvUser);
        }
    }
}
