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

public class Giris extends ActionBarActivity {

    Button btn;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private  Boolean hatirla;
    private  CheckBox tik;
    public static EditText isim;
    public static EditText sifre;
    private String REGISTER_URL = "http://venusapi.herokuapp.com/api/v1/users/login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giris);

        isim = (EditText) findViewById(R.id.txt_kullaniciad);
        sifre = (EditText) findViewById(R.id.txt_prl);
        btn = (Button)findViewById(R.id.btn_grs);
        tik =(CheckBox)findViewById(R.id.check1) ;
        setHatirla();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerUser();

            }
        });
    }

    public void setHatirla(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor  editor = preferences.edit();
        hatirla = preferences.getBoolean("hatirla", false);
        if (hatirla == true) {
            isim.setText(preferences.getString("email", ""));
            sifre.setText(preferences.getString("password", ""));
            tik.setChecked(true);
        }
    }

        private void registerUser() {
            final String username = isim.getText().toString();
            final String password = sifre.getText().toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        setAuthentication(response);
                    }catch(JSONException e) {
                        Log.e("ERROR",e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(Giris.this,"Kullanıcı adı veya şifre hatalı", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams()  {

                    Map<String, String> params = new HashMap<>();
                    params.put("email", username);
                    params.put("password", password);
                    params.put("format","json");

                    return params;

                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
        public void setAuthentication(String response) throws  org.json.JSONException{
            JSONObject jsonObj = new JSONObject(response);

            setAuthenticationAndId(jsonObj);
            if(!jsonObj.getString("auth_token").isEmpty()){
                if (tik.isChecked())	{
                    editor.putBoolean("hatirla", true);
                    editor.putString("email", isim.getText().toString());
                    editor.putString("password",sifre.getText().toString());
                    editor.commit();
                    Intent anasayfa = new Intent(Giris.this, AnasayfaActivity.class);
                    startActivity(anasayfa);
                } else {
                    editor.clear();
                    editor.commit();
                }
            }else {
                Toast.makeText(Giris.this, "HATA: ID boş döndü.", Toast.LENGTH_LONG).show();
            }
        }

        public void setAuthenticationAndId(JSONObject jsonObj)throws  org.json.JSONException{
            JSONObject jsonObj3 = new JSONObject(jsonObj.getString("user_profile"));
            preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            editor = preferences.edit();
            editor.putString("userId",String.valueOf(jsonObj.getString("user_id")));
            editor.putString("authentication",String.valueOf(jsonObj.getString("auth_token").toString()));
            editor.putString("first_name", String.valueOf(jsonObj3.getString("first_name").toString()));
            editor.putString("last_name", String.valueOf(jsonObj3.getString("last_name").toString()));
            editor.commit();

        }
}



