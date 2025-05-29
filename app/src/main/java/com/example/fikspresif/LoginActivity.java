package com.example.fikspresif;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);

        tvCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showToast("Harap isi username dan password");
            return;
        }

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = Db_Contract.urlLogin + "?username=" + username + "&password=" + password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> handleLoginResponse(response.trim()),
                error -> handleLoginError(error));

        requestQueue.add(stringRequest);
    }

    private void handleLoginResponse(String response) {
        if (response.contains("Selamat Datang")) {
            showToast("Login berhasil");
            startMainActivity();
        }
        else if (response.contains("Akun tidak ditemukan")) {
            showToast("Akun tidak ditemukan, silakan register");
        }
        else if (response.contains("Username atau Password salah")) {
            showToast("Username atau password salah");
        }
        else {
            // Generic error message for any other response
            showToast("Login gagal. Silakan coba lagi");
            Log.e("LOGIN_ERROR", "Unexpected response: " + response);
        }
    }

    private void handleLoginError(VolleyError error) {
        showToast("Gagal terhubung ke server");
        Log.e("LOGIN_ERROR", "Network error: " + error.toString());
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}