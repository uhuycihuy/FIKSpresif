package com.example.fikspresif;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;

import org.json.JSONObject;

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
        try {
            JSONObject jsonResponse = new JSONObject(response);

            if (jsonResponse.has("message") && jsonResponse.getString("message").equals("Selamat Datang")) {
                int userId = jsonResponse.getInt("user_id");
                String username = jsonResponse.getString("username");

                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("username", username);
                editor.putInt("user_id", userId);
                editor.apply();

                showToast("Login berhasil");
                startMainActivity();
            } else if (jsonResponse.has("error")) {
                String errorMsg = jsonResponse.getString("error");
                showToast(errorMsg);
            } else {
                showToast("Login gagal. Silakan coba lagi");
                Log.e("LOGIN_ERROR", "Unexpected response: " + response);
            }
        } catch (Exception e) {
            showToast("Login gagal. Silakan coba lagi");
            Log.e("LOGIN_ERROR", "JSON parsing error: " + e.getMessage());
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
