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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AccountFragment extends Fragment {
    private TextView tvUsername, tvName, tvEmail;
    private Button btnLogout, btnEditAkun;
    private OnAccountLogoutListener logoutListener;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private int userId;

    public interface OnAccountLogoutListener {
        void onLogout();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAccountLogoutListener) {
            logoutListener = (OnAccountLogoutListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvUsername = view.findViewById(R.id.tvAccountUsername);
        tvName = view.findViewById(R.id.tvAccountName);
        tvEmail = view.findViewById(R.id.tvAccountEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditAkun = view.findViewById(R.id.btnEditUser);

        userId = getUserIdFromPrefs();
        if (userId != 0) {
            new FetchAccountTask(userId).execute();
        }

        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        btnEditAkun.setOnClickListener(v -> showEditProfileDialog());

        return view;
    }

    private void showEditProfileDialog() {
        // Inflate layout dialog
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);

        // Initialize dialog views
        TextInputEditText etUsername = dialogView.findViewById(R.id.etUsername);
        TextInputEditText etFullName = dialogView.findViewById(R.id.etFullName);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        TextInputEditText etPasswordLama = dialogView.findViewById(R.id.etPasswordLama);
        TextInputEditText etPasswordBaru = dialogView.findViewById(R.id.etPasswordBaru);

        // Populate current data
        etUsername.setText(tvUsername.getText().toString());
        etFullName.setText(tvName.getText().toString());
        etEmail.setText(tvEmail.getText().toString());

        // Create dialog
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Edit Profil")
                .setView(dialogView)
                .setPositiveButton("Update", null) // Set null untuk custom handling
                .setNegativeButton("Batal", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        // Custom handling untuk tombol Update
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String passwordLama = etPasswordLama.getText().toString().trim();
            String passwordBaru = etPasswordBaru.getText().toString().trim();

            if (validateInput(username, fullName, email, passwordLama, passwordBaru)) {
                new UpdateUserTask(userId, username, fullName, email, passwordLama, passwordBaru, dialog).execute();
            }
        });
    }

    private boolean validateInput(String username, String fullName, String email, String passwordLama, String passwordBaru) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext(), "Username, nama lengkap dan email wajib diisi", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            Toast.makeText(requireContext(), "Username hanya boleh mengandung huruf, angka, dan underscore (3-20 karakter)", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Format email salah", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!TextUtils.isEmpty(passwordBaru) && TextUtils.isEmpty(passwordLama)) {
            Toast.makeText(requireContext(), "Password lama harus diisi untuk mengganti password", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!TextUtils.isEmpty(passwordBaru) && passwordBaru.length() < 6) {
            Toast.makeText(requireContext(), "Password baru harus minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya", (dialog, which) -> performLogout())
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogout() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Toast.makeText(requireContext(), "Anda berhasil logout", Toast.LENGTH_SHORT).show();

        if (logoutListener != null) {
            logoutListener.onLogout();
        }
    }

    private int getUserIdFromPrefs() {
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", 0);
    }

    private class FetchAccountTask extends AsyncTask<Void, Void, JSONObject> {
        private final int userId;

        FetchAccountTask(int userId) {
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
                tvUsername.setText(result.optString("username"));
                tvName.setText(result.optString("name"));
                tvEmail.setText(result.optString("email"));
            }
        }
    }

    private class UpdateUserTask extends AsyncTask<Void, Void, String> {
        private final int userId;
        private final String username, fullName, email, passwordLama, passwordBaru;
        private final AlertDialog dialog;

        UpdateUserTask(int userId, String username, String fullName, String email, String passwordLama, String passwordBaru, AlertDialog dialog) {
            this.userId = userId;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.passwordLama = passwordLama;
            this.passwordBaru = passwordBaru;
            this.dialog = dialog;
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

                // Update UI
                tvUsername.setText(username);
                tvName.setText(fullName);
                tvEmail.setText(email);

                // Close dialog
                dialog.dismiss();
            } else {
                Toast.makeText(requireContext(), result, Toast.LENGTH_LONG).show();
            }
        }
    }
}