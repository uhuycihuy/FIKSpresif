package com.example.fikspresif;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etFullName, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private ProgressDialog progressDialog;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etRegisterUsername);
        etFullName = findViewById(R.id.etRegisterName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sedang mendaftar...");
        progressDialog.setCancelable(false);
    }

    private void setupListeners() {
        tvLoginLink.setOnClickListener(v -> navigateToLogin());

        btnRegister.setOnClickListener(v -> attemptRegistration());
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void attemptRegistration() {
        String username = etUsername.getText().toString().trim();
        String fullname = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(username, fullname, email, password)) {
            return;
        }

        registerUser(username, fullname, email, password);
    }

    private boolean validateInputs(String username, String fullname, String email, String password) {
        if (username.isEmpty() || fullname.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            etUsername.setError("Username hanya boleh mengandung huruf, angka, dan underscore (3-20 karakter)");
            etUsername.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Masukkan email yang valid");
            etEmail.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void registerUser(String username, String fullname, String email, String password) {
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Db_Contract.urlRegister,
                response -> {
                    progressDialog.dismiss();
                    handleRegistrationResponse(response);
                },
                error -> {
                    progressDialog.dismiss();
                    handleRegistrationError(error);
                }) {
            @Override
            protected HashMap<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("full_name", fullname);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void handleRegistrationResponse(String response) {
        String normalizedResponse = response.trim().toLowerCase();

        if (normalizedResponse.contains("berhasil")) {
            Toast.makeText(this, "Pendaftaran berhasil! Silakan login.", Toast.LENGTH_SHORT).show();
            navigateToLogin();
        } else if (normalizedResponse.contains("username")) {
            etUsername.setError("Username sudah digunakan");
            etUsername.requestFocus();
        } else if (normalizedResponse.contains("email")) {
            etEmail.setError("Email sudah digunakan");
            etEmail.requestFocus();
        } else {
            Toast.makeText(this, "Proses pendaftaran gagal", Toast.LENGTH_SHORT).show();
            Log.e("REGISTER_ERROR", "Server response: " + response);
        }
    }

    private void handleRegistrationError(VolleyError error) {
        Toast.makeText(this, "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();

        Log.e("REGISTER_ERROR", "Network error: " + error.toString());
        if (error.networkResponse != null) {
            Log.e("REGISTER_ERROR", "Status code: " + error.networkResponse.statusCode);
        }
    }
}