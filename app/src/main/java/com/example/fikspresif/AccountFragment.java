package com.example.fikspresif;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AccountFragment extends Fragment {
    private TextView tvUsername, tvName, tvEmail;
    private Button btnLogout, btnEditAkun;
    private OnAccountLogoutListener logoutListener;

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

        int userId = getUserIdFromPrefs();
        if (userId != 0) {
            new FetchAccountTask(userId).execute();
        }

        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
        btnEditAkun.setOnClickListener(v -> goToEditUser());

        return view;
    }

    private void goToEditUser() {
        EditUserFragment editUserFragment = new EditUserFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editUserFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya", (dialog, which) -> performLogout())
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .show();
    }

    // Method to handle actual logout process
    private void performLogout() {
        // Clear SharedPreferences (session data)
        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Show success toast
        Toast.makeText(requireContext(), "Anda berhasil logout", Toast.LENGTH_SHORT).show();

        // Trigger logout callback
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
}