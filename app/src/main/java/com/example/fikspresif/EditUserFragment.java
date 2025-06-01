package com.example.fikspresif;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Pattern;

public class EditUserFragment extends Fragment {

    private TextInputEditText etUsername, etFullName, etEmail, etPasswordLama, etPasswordBaru;
    private Button btnUpdate;
    private int userId;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);

        etUsername = view.findViewById(R.id.etUsername);
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPasswordLama = view.findViewById(R.id.etPasswordLama);
        etPasswordBaru = view.findViewById(R.id.etPasswordBaru);
        btnUpdate = view.findViewById(R.id.btnUpdate);

        userId = getUserIdFromPrefs();

        if (userId != 0) {
            new FetchUserDataTask(userId).execute();
        } else {
            Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT).show();
        }

        btnUpdate.setOnClickListener(v -> updateUser());

        return view;
    }

    private int getUserIdFromPrefs() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", 0);
    }

    private void updateUser() {
        String username = etUsername.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String passwordLama = etPasswordLama.getText().toString().trim();
        String passwordBaru = etPasswordBaru.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Username, nama lengkap dan email wajib diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            etUsername.setError("Username hanya boleh mengandung huruf, angka, dan underscore (3-20 karakter)");
            etUsername.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Format email salah", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(passwordBaru) && TextUtils.isEmpty(passwordLama)) {
            Toast.makeText(requireContext(), "Password lama harus diisi untuk mengganti password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!TextUtils.isEmpty(passwordBaru)) {
            // Validasi password lama harus diisi
            if (TextUtils.isEmpty(passwordLama)) {
                etPasswordLama.setError("Password lama harus diisi untuk mengganti password");
                etPasswordLama.requestFocus();
                return;
            }

            // Validasi panjang password baru
            if (passwordBaru.length() < 6) {
                etPasswordBaru.setError("Password baru harus minimal 6 karakter");
                etPasswordBaru.requestFocus();
                return;
            }
        }

        new UpdateUserTask(userId, username, fullName, email, passwordLama, passwordBaru).execute();
    }

    private class FetchUserDataTask extends AsyncTask<Void, Void, JSONObject> {

        private final int userId;

        FetchUserDataTask(int userId) {
            this.userId = userId;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                String urlString = Db_Contract.urlGetAccount + "?user_id=" + userId;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                Scanner in = new Scanner(conn.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (in.hasNext()) {
                    sb.append(in.nextLine());
                }
                in.close();

                return new JSONObject(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null && !result.has("error")) {
                etUsername.setText(result.optString("username"));
                etFullName.setText(result.optString("name")); // sesuaikan key jika beda
                etEmail.setText(result.optString("email"));
            } else {
                Toast.makeText(requireContext(), "Gagal memuat data user", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateUserTask extends AsyncTask<Void, Void, String> {

        private final int userId;
        private final String username, fullName, email, passwordLama, passwordBaru;

        UpdateUserTask(int userId, String username, String fullName, String email, String passwordLama, String passwordBaru) {
            this.userId = userId;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.passwordLama = passwordLama;
            this.passwordBaru = passwordBaru;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(Db_Contract.urlEditUser);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("user_id", userId);
                jsonParam.put("username", username);
                jsonParam.put("full_name", fullName);
                jsonParam.put("email", email);
                jsonParam.put("password_lama", passwordLama);
                jsonParam.put("password", passwordBaru);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                Scanner in = new Scanner(conn.getInputStream());
                StringBuilder sb = new StringBuilder();
                while (in.hasNext()) {
                    sb.append(in.nextLine());
                }
                in.close();

                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.contains("Berhasil")) {
                Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();

                // Update SharedPreferences
                SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", username);
                editor.putString("full_name", fullName);
                editor.putString("email", email);
                editor.apply();

                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show();
            }
        }
    }
}
