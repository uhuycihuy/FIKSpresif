package com.example.fikspresif;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AspirasiAdapter extends RecyclerView.Adapter<AspirasiAdapter.ViewHolder> {

    private final ArrayList<Aspirasi> aspirasiList;
    private final boolean isPublicView;
    private final boolean isEditable;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AspirasiAdapter(ArrayList<Aspirasi> aspirasiList, boolean isPublicView, boolean isEditable) {
        this.aspirasiList = aspirasiList;
        this.isPublicView = isPublicView;
        this.isEditable = isEditable;
    }

    @NonNull
    @Override
    public AspirasiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_aspirasi, parent, false);
        return new ViewHolder(view, listener, isEditable);
    }

    @Override
    public void onBindViewHolder(@NonNull AspirasiAdapter.ViewHolder holder, int position) {
        Aspirasi aspirasi = aspirasiList.get(position);
        holder.bind(aspirasi, isPublicView, isEditable);
    }

    @Override
    public int getItemCount() {
        return aspirasiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate, tvUser, tvSelengkapnya;
        ImageButton btnEdit, btnDelete;
        private boolean isExpanded = false;

        public ViewHolder(View itemView, OnItemClickListener listener, boolean isEditable) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJudul);
            tvContent = itemView.findViewById(R.id.tvIsi);
            tvDate = itemView.findViewById(R.id.tvTanggal);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvSelengkapnya = itemView.findViewById(R.id.tvSelengkapnya);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            if (isEditable) {
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            } else {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            }

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });
        }

        public void bind(Aspirasi aspirasi, boolean isPublicView, boolean isEditable) {
            tvTitle.setText(aspirasi.getTitle());
            tvContent.setText(aspirasi.getContent());
            tvDate.setText(aspirasi.getCreatedAt());

            if (isPublicView) {
                tvUser.setVisibility(View.VISIBLE);
                tvUser.setText(aspirasi.getUsername());
            } else {
                tvUser.setVisibility(View.GONE);
            }

            if (aspirasi.getContent().length() > 150) {
                tvSelengkapnya.setVisibility(View.VISIBLE);
                tvContent.setMaxLines(4);
                tvContent.setEllipsize(TextUtils.TruncateAt.END);
                isExpanded = false;
                tvSelengkapnya.setText("View More");

                tvSelengkapnya.setOnClickListener(v -> {
                    isExpanded = !isExpanded;
                    if (isExpanded) {
                        tvContent.setMaxLines(Integer.MAX_VALUE);
                        tvContent.setEllipsize(null);
                        tvSelengkapnya.setText("View Less");
                    } else {
                        tvContent.setMaxLines(4);
                        tvContent.setEllipsize(TextUtils.TruncateAt.END);
                        tvSelengkapnya.setText("View More");
                    }
                });
            } else {
                tvSelengkapnya.setVisibility(View.GONE);
                tvContent.setMaxLines(Integer.MAX_VALUE);
                tvContent.setEllipsize(null);
            }
        }
    }
}
