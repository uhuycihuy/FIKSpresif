package com.example.fikspresif;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
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
import com.google.android.material.textfield.TextInputEditText;

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
    private TextView tvEmpty;

    private final String URL_GET_ASPIRASI = Db_Contract.urlGetAspirasiById;
    private final String URL_DELETE_ASPIRASI = Db_Contract.urlDeleteAspirasi;
    private final String URL_EDIT_ASPIRASI = Db_Contract.urlEditAspirasi;

    public HistoryFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.rvAspirasi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvEmpty = view.findViewById(R.id.tvEmpty);
        aspirasiList = new ArrayList<>();

        adapter = new AspirasiAdapter(aspirasiList, false, true);

        adapter.setOnItemClickListener(new AspirasiAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {
                Aspirasi aspirasi = aspirasiList.get(position);
                showEditDialog(aspirasi, position);
            }

            @Override
            public void onDeleteClick(int position) {
                Aspirasi aspirasi = aspirasiList.get(position);

                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Aspiration")
                        .setMessage("Are you sure you want to delete this aspiration?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            hapusAspirasi(aspirasi, position);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        loadAspirasi();

        return view;
    }

    private void showEditDialog(Aspirasi aspirasi, int position) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_aspirasi, null);

        TextInputEditText etJudulEdit = dialogView.findViewById(R.id.etJudulEdit);
        TextInputEditText etIsiEdit = dialogView.findViewById(R.id.etIsiEdit);
        CheckBox cbAnonimusEdit = dialogView.findViewById(R.id.cbAnonimusEdit);

        etJudulEdit.setText(aspirasi.getTitle());
        etIsiEdit.setText(aspirasi.getContent());
        cbAnonimusEdit.setChecked(aspirasi.isAnonymous());

        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
                .setTitle("Update Aspiration")
                .setView(dialogView)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.85);
        dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String judul = etJudulEdit.getText().toString().trim();
            String isi = etIsiEdit.getText().toString().trim();
            boolean isAnonim = cbAnonimusEdit.isChecked();

            if (validateEditInput(judul, isi, etJudulEdit, etIsiEdit)) {
                editAspirasi(aspirasi.getAspirationId(), judul, isi, isAnonim, position, dialog);
            }
        });
    }

    private boolean validateEditInput(String judul, String isi,
                                      TextInputEditText etJudul,
                                      TextInputEditText etIsi) {
        if (judul.isEmpty()) {
            etJudul.setError("Judul tidak boleh kosong");
            etJudul.requestFocus();
            return false;
        }

        if (isi.isEmpty()) {
            etIsi.setError("Isi aspirasi tidak boleh kosong");
            etIsi.requestFocus();
            return false;
        }

        if (judul.length() > 28) {
            etJudul.setError("Judul tidak boleh lebih dari 28 karakter");
            etJudul.requestFocus();
            return false;
        }
        return true;
    }

    private void editAspirasi(int aspirationId, String title, String content, boolean isAnonymous, int position, AlertDialog dialog) {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        int userId = prefs.getInt("user_id", 0);

        if (userId == 0) {
            Toast.makeText(getContext(), "Silakan login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest editRequest = new StringRequest(Request.Method.POST, URL_EDIT_ASPIRASI,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.optString("status");
                        String message = jsonObject.optString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            Aspirasi aspirasi = aspirasiList.get(position);
                            Aspirasi updatedAspirasi = new Aspirasi(
                                    aspirasi.getAspirationId(),
                                    title,
                                    content,
                                    aspirasi.getCreatedAt(),
                                    aspirasi.getUsername(),
                                    isAnonymous
                            );

                            aspirasiList.set(position, updatedAspirasi);
                            adapter.notifyItemChanged(position);

                            dialog.dismiss();
                            Toast.makeText(getContext(), "Aspirasi berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Gagal memperbarui aspirasi: " + message, Toast.LENGTH_SHORT).show();
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
                params.put("title", title);
                params.put("content", content);
                params.put("is_anonymous", isAnonymous ? "1" : "0");
                return params;
            }
        };

        Volley.newRequestQueue(requireContext()).add(editRequest);
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
                            if (dataArray.length() == 0) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

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
                            }
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

                            if (aspirasiList.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

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