package com.example.fikspresif;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private AspirasiAdapter adapter;
    private ArrayList<Aspirasi> aspirasiList;

    private final String URL_GET_ASPIRASI = Db_Contract.urlGetAspirasiById;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.rvAspirasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        aspirasiList = new ArrayList<>();
        adapter = new AspirasiAdapter(aspirasiList);
        recyclerView.setAdapter(adapter);

        loadAspirasi();

        return view;
    }

    private void loadAspirasi() {
        int userId = 1; // Ganti dengan session user ID
        String url = URL_GET_ASPIRASI + "?user_id=" + userId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if ("success".equalsIgnoreCase(response.optString("status"))) {
                            JSONArray dataArray = response.getJSONArray("data");
                            aspirasiList.clear();

                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                String title = obj.optString("title", "Judul tidak ada");
                                String content = obj.optString("content", "Isi tidak tersedia");
                                String createdAt = obj.optString("created_at", "-");

                                aspirasiList.add(new Aspirasi(title, content, createdAt));
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
}
