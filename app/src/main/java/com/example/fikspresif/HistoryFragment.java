package com.example.fikspresif;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private AspirasiAdapter adapter;
    private ArrayList<Aspirasi> aspirasiList;

    private final String URL_GET_ASPIRASI = Db_Contract.urlGetAspirasiById;
    private final String URL_DELETE_ASPIRASI = Db_Contract.urlDeleteAspirasi;

    public HistoryFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.rvAspirasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        aspirasiList = new ArrayList<>();

        adapter = new AspirasiAdapter(aspirasiList, false, true);

        adapter.setOnItemClickListener(new AspirasiAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Aspirasi aspirasi = aspirasiList.get(position);
                // TODO: Implement edit aspirasi (bisa buka dialog/edit activity)
                Toast.makeText(getContext(), "Edit: " + aspirasi.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteClick(int position) {
                Aspirasi aspirasi = aspirasiList.get(position);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Hapus Aspirasi")
                        .setMessage("Yakin ingin menghapus aspirasi ini?")
                        .setPositiveButton("Hapus", (dialog, which) -> {
                            hapusAspirasi(aspirasi, position);
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        loadAspirasi();

        return view;
    }

    private void loadAspirasi() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        int userId = prefs.getInt("user_id", 0);

        if (userId == 0) {
            Toast.makeText(getContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = URL_GET_ASPIRASI + "?user_id=" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equalsIgnoreCase(response.optString("status"))) {
                            JSONArray dataArray = response.getJSONArray("data");
                            aspirasiList.clear();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                int aspirationId = obj.optInt("aspiration_id", 0);
                                String title = obj.optString("title", "Judul tidak ada");
                                String content = obj.optString("content", "Isi tidak tersedia");
                                String createdAt = obj.optString("created_at", "-");
                                String username = obj.optString("username", "Anonim");
                                boolean isAnonymous = obj.optBoolean("is_anonymous", false);

                                aspirasiList.add(new Aspirasi(aspirationId, title, content, createdAt, username, isAnonymous));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("HistoryFragment", "Parsing error: " + e.getMessage());
                        Toast.makeText(getContext(), "Gagal parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("HistoryFragment", "Volley error: " + error.toString());
                    Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }

    private void hapusAspirasi(Aspirasi aspirasi, int position) {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        int userId = prefs.getInt("user_id", 0);

        if (userId == 0) {
            Toast.makeText(getContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        int aspirationId = aspirasi.getAspirationId();

        StringRequest deleteRequest = new StringRequest(Request.Method.POST, URL_DELETE_ASPIRASI,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status");
                        String message = jsonObject.optString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            aspirasiList.remove(position);
                            adapter.notifyItemRemoved(position);
                            Toast.makeText(getContext(), "Aspirasi berhasil dihapus", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Gagal hapus aspirasi: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(getContext(), "Error koneksi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("aspiration_id", String.valueOf(aspirationId));
                params.put("user_id", String.valueOf(userId));
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(deleteRequest);
    }
}
