package com.example.hatic.venus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bildirim.MyReceiver;
import com.example.hatic.venus.yardımcı.DosyaKontrol;
import com.example.hatic.venus.yardımcı.FormatKontrol;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AramaActivity extends AppCompatActivity {
    private String REGISTER_URL = "http://venusapi.herokuapp.com/api/v1/notes/create";
    private String path;
    private String text;
    private Button btn;
    private TextView Tfiyat,Tbarkod;
    private ProgressDialog progressDialog;
    private String appFolder = "/Venus/";
    private List<String> textLines = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arama);
        setPath();

         Tfiyat= (TextView) findViewById(R.id.fyt);
         Tbarkod = (TextView) findViewById(R.id.barkod);

        ImageView fatura = (ImageView) findViewById(R.id.image_goruntu);
        fatura.setImageBitmap((FormatKontrol.getImageToBitmap(path + DosyaKontrol.jpegName)));
        text = getIntent().getStringExtra("text");
        readLine(text);
        for (int i = 0; i < 5; i++) {
            if (textLines.size() == 0){
                break;
            }
            for (int index = 0; index < textLines.size(); index++) {
                String line = textLines.get(index);

                Pattern pattern = Pattern.compile("\\d{14}");
                Matcher matcher = pattern.matcher(line);

                if (line.contains("TL") || line.contains("Tl") || line.contains("tl")) {
                    Tfiyat.setText(getData4(line));

                }
                if (matcher.find()){
                    Tbarkod.setText(line.substring(0, 13));
                }

            }
        }

        btn = (Button) findViewById(R.id.button_cikis);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etiketKaydet();
            }
        });
    }
    public void etiketKaydet() {
        if (!Tfiyat.getText().toString().isEmpty() && !Tbarkod.getText().toString().isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        getResponseStatus(response);
                        progressIcon();
                    } catch (JSONException e) {
                        Log.e("ERROR", e.toString());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(AramaActivity.this, "Kaydederken bir problem oluştu"+volleyError, Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    Map<String, String> params = new HashMap<>();
                    params.put("auth_token", preferences.getString("authentication", ""));
                    params.put("user_id", preferences.getString("userId", "-1"));
                    params.put("products",Tbarkod.getText().toString()+","+Tfiyat.getText().toString());
                    params.put("format","json");
                    return params;

                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

        else {
            Toast.makeText(AramaActivity.this, "Eksik bilgiler mecut.Lütfen fişi tekrar çekiniz.", Toast.LENGTH_LONG).show();
            Intent anasayfa = new Intent(AramaActivity.this, AnasayfaActivity.class);
            startActivity(anasayfa);
        }
    }

    public void getResponseStatus(String response) throws JSONException{
        JSONObject jsonObj = new JSONObject(response);
        if("created".equals(jsonObj.getString("status").toString())){
            Toast.makeText(AramaActivity.this, "Kayıt Başarılı", Toast.LENGTH_LONG).show();
            if(!jsonObj.getString("barcode").toString().isEmpty()){
                Intent intent=new Intent(this,MyReceiver.class);
                intent.putExtra("barcode",jsonObj.getString("barcode").toString());
                intent.putExtra("price",jsonObj.getString("price").toString());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),234324243,intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                Intent anasayfa = new Intent(AramaActivity.this, AnasayfaActivity.class);
                startActivity(anasayfa);
            } else{
                Intent anasayfa2 = new Intent(AramaActivity.this, AnasayfaActivity.class);
                startActivity(anasayfa2);
            }

        }
        else{
            Toast.makeText(AramaActivity.this, "Kayıt Başarısız", Toast.LENGTH_LONG).show();
        }
    }
    private void setPath() {
        path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + appFolder;
        //harici alandaki dizinin mutlak yolunu döndürür
    }

    private void progressIcon() {
        progressDialog = new ProgressDialog(this); //bekleyen müşteri için dönen halka ikonu
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Kaydediliyor..."); //ve yazısı
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    private void readLine(String text){
        Scanner scanText=new Scanner(text);
        while (scanText.hasNextLine()){
            String line=scanText.nextLine();
            if (line!=null && !line.isEmpty()&& line.length()>2){
                textLines.add(line);
            }
            Log.i("boyut",line);
        }
    }

    private static String getData(String line){
        if(line.contains(":")==false){
            return line;
        }
        return line.split(":")[1].trim();

    }
    private static String getData4(String line){
        if(line.contains("T")==false){
            return line;
        }
        return line.split("T")[0].trim();

    }

}




