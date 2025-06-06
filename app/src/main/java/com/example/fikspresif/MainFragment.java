package com.example.fikspresif;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MainFragment extends Fragment {

    private RecyclerView recyclerView;
    private AspirasiAdapter adapter;
    private ArrayList<Aspirasi> aspirasiList;
    private final String URL_GET_ASPIRASI = Db_Contract.urlGetAllAspirasi;
    private String order = "desc";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        recyclerView = view.findViewById(R.id.rvAspirasiMain);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        aspirasiList = new ArrayList<>();
        adapter = new AspirasiAdapter(aspirasiList, true, false); // public view, no edit/delete
        recyclerView.setAdapter(adapter);

        Button btnTerbaru = view.findViewById(R.id.btnTerbaru);
        Button btnTerlama = view.findViewById(R.id.btnTerlama);

        btnTerbaru.setOnClickListener(v -> {
            order = "desc";
            loadAspirasi();
        });

        btnTerlama.setOnClickListener(v -> {
            order = "asc";
            loadAspirasi();
        });

        loadAspirasi();

        return view;
    }

    private void loadAspirasi() {
        String url = URL_GET_ASPIRASI + "?order=" + order;

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

                                boolean isAnonymous = obj.optBoolean("is_anonymous", false);
                                String username = obj.optString("username", "Tidak diketahui");
                                String displayUsername = isAnonymous ? "Anonim" : username;

                                aspirasiList.add(new Aspirasi(aspirationId, title, content, createdAt, displayUsername, isAnonymous));
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Log.e("MainFragment", "Parsing error: " + e.getMessage());
                        Toast.makeText(getContext(), "Gagal parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("MainFragment", "Volley error: " + error.toString());
                    Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest);
    }
}
