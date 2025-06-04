package com.example.fikspresif;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class InputFormFragment extends Fragment {

    private EditText etTitle, etContent;
    private CheckBox cbAnonymous;
    private Button btnSubmit;
    private ProgressDialog progressDialog;

    // Use from Db_Contract
    private final String URL_ADD_ASPIRASI = Db_Contract.urlAddAspirasi;

    public InputFormFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_form, container, false);

        etTitle = view.findViewById(R.id.etTitle);
        etContent = view.findViewById(R.id.etContent);
        cbAnonymous = view.findViewById(R.id.cbAnonymous);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Mengirim aspirasi...");
        progressDialog.setCancelable(false);

        btnSubmit.setOnClickListener(v -> submitAspirasi());

        return view;
    }

    private void submitAspirasi() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        int isAnonymous = cbAnonymous.isChecked() ? 1 : 0;
        int userId = 1; // TODO: Replace with session user ID if available

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(getContext(), "Judul dan isi tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ADD_ASPIRASI,
                response -> {
                    progressDialog.dismiss();
                    handleResponse(response);
                },
                error -> {
                    progressDialog.dismiss();
                    handleError(error);
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("title", title);
                params.put("content", content);
                params.put("is_anonymous", String.valueOf(isAnonymous));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(stringRequest);
    }

    private void handleResponse(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            String message = obj.optString("message", "Respon tidak diketahui");
            String status = obj.optString("status", "error");

            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

            if ("success".equalsIgnoreCase(status)) {
                etTitle.setText("");
                etContent.setText("");
                cbAnonymous.setChecked(false);
            }
        } catch (JSONException e) {
            Log.e("ASPIRASI_RESPONSE", "JSON error: " + e.getMessage());
            Toast.makeText(getContext(), "Gagal memproses respon dari server", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(VolleyError error) {
        Toast.makeText(getContext(), "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
        Log.e("ASPIRASI_ERROR", "Volley error: " + error.toString());
        if (error.networkResponse != null) {
            Log.e("ASPIRASI_ERROR", "Status code: " + error.networkResponse.statusCode);
        }
    }
}