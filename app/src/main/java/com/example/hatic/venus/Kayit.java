package com.example.hatic.venus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Kayit extends ActionBarActivity {
    Button btn;
    EditText ad,soyad,mail,parola1,parola2,yas;
    CheckBox e,k;
    private String REGISTER_URL="http://venusapi.herokuapp.com/api/v1/users/register";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kayit);

        ad = (EditText) findViewById(R.id.txt_ad);
        soyad = (EditText) findViewById(R.id.txt_soyad);
        yas= (EditText) findViewById(R.id.txt_yas);
        mail = (EditText) findViewById(R.id.txt_email);
        parola1 = (EditText) findViewById(R.id.txt_parola);
        parola2 = (EditText) findViewById(R.id.txt_tkrparola);
        e = (CheckBox) findViewById(R.id.erkek);
        k = (CheckBox) findViewById(R.id.kadın);
        btn = (Button)findViewById(R.id.btn_kyt);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();

            }
        });

    }
    public void singIn(){
        if(!ad.getText().toString().isEmpty() && !soyad.getText().toString().isEmpty() && !mail.getText().toString().isEmpty() && !parola1.getText().toString().isEmpty() && !parola2.getText().toString().isEmpty() && (e.isChecked() || k.isChecked())){
            if(parola1.getText().toString().equals(parola2.getText().toString())){
                if(parola1.getText().toString().length()>5){
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                getResponse(response);
                            } catch (JSONException e) {
                                Log.e("ERROR", e.toString());
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(Kayit.this, "Böyle bir kullanıcı zaten var.Farklı bir E-posta adresi ile kayıt olmayı deneyin.", Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor editor = preferences.edit();
                            Map<String, String> params = new HashMap<>();
                            params.put("email",mail.getText().toString());
                            params.put("first_name", ad.getText().toString());
                            params.put("last_name", soyad.getText().toString());
                            params.put("age", yas.getText().toString());
                            params.put("password", parola1.getText().toString());
                            params.put("password_confirmation",parola2.getText().toString());
                            if(e.isChecked()){
                                params.put("gender","Erkek");
                            }
                            else if(k.isChecked()) {
                                params.put("gender", "Kadın");
                            }
                            return params;

                        }
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Authorization", "Token <token>");
                            return headers;
                        }

                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(this);
                    requestQueue.add(stringRequest);
                }
                else{
                    Toast.makeText(Kayit.this, "Parola 6 haneden kısa olamaz.", Toast.LENGTH_LONG).show();
                }

            }
            else{
                Toast.makeText(Kayit.this, "Parolalar uyuşmamaktadır.", Toast.LENGTH_LONG).show();
            }

        }
        else{
            Toast.makeText(Kayit.this, "Lütfen bütün alanları doldurun.", Toast.LENGTH_LONG).show();

        }
        }
    public void getResponse(String response) throws JSONException{
        JSONObject jsonObj = new JSONObject(response);
        if(!jsonObj.getString("auth_token").toString().isEmpty()){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("userId",String.valueOf(jsonObj.getString("user_id")));
            editor.putString("authentication",String.valueOf(jsonObj.getString("auth_token").toString()));
            editor.putString("first_name",String.valueOf(jsonObj.getString("first_name")));
            editor.putString("last_name",String.valueOf(jsonObj.getString("last_name")));
            editor.commit();
            Intent anasayfa = new Intent(Kayit.this, AnasayfaActivity.class);
            startActivity(anasayfa);
        }
        else{
            Toast.makeText(Kayit.this, "Böyle bir kullanıcı zaten var.Farklı bir E-posta adresi ile kayıt olmayı deneyin.", Toast.LENGTH_LONG).show();
        }


    }
}