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

public class GoruntuActivity extends AppCompatActivity {
    private String REGISTER_URL = "http://venusapi.herokuapp.com/api/v1/notes/create";
    private String path;
    private String text;
    private Button btn;
    private ProgressDialog progressDialog;
    private String appFolder = "/Venus/";
    private List<String> textLines = new ArrayList<>();
    private TextView Tfiyat;
    private TextView Ttarih;
    private TextView Tsaat;
    private TextView Turunad;
    private TextView Tmagazaad,Tbarkod;

//    private static final String tarih = "(Tarih|TARİH|tarih).*[\"\\\\d{4}.(0[1-9]|1[012]).(0[1-9]|[12][0-9]|3[01])\"])";
//    private static final String saat = "(Saat|SAAT|saat).*[ \"^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])\" +\n" + "\n" + "\"(:([0-5]?[0-9]))?$\")]";
//    private static final String isim = "^[a-zA-ZàáâäãåąčćçęèéêëėıįìíîïłńòóôöõøùúûüųūÿýżźñçčšşžÀÁÂÄÃÅĄĆČÇĖĘÈÉÊËÌÍÎÏİĮŁŃÒÓÔÖÕŞØÙÚÛÜŲŪŸÝŻŹÑßÇŒÆČŠŽ∂ð -]+$*,.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goruntu);
        setPath();

        Tbarkod = (TextView) findViewById(R.id.barkod);
        Tfiyat = (TextView) findViewById(R.id.fiyat);
        Ttarih = (TextView) findViewById(R.id.tarih);
        Tsaat = (TextView) findViewById(R.id.saat);
        Turunad = (TextView) findViewById(R.id.urunad);
        Tmagazaad = (TextView) findViewById(R.id.magazaad);
        ImageView fatura = (ImageView) findViewById(R.id.image_goruntu);
        fatura.setImageBitmap((FormatKontrol.getImageToBitmap(path + DosyaKontrol.jpegName)));
        text = getIntent().getStringExtra("text");
        // tarih.setText(text);
        readLine(text);
        // Ttarih.setText(textLines.get(0));
        for (int i = 0; i < 20; i++) {
            if (textLines.size() == 0) {
                break;
            }
            for (int index = 0; index < textLines.size(); index++) {
                String line = textLines.get(index);

                if (line.contains("TARİH") || line.contains("Tarih") || line.contains("tarih")) {
                    Ttarih.setText(getData(line));
                }

                if (line.contains("FişNo")||line.contains("FisNo")||line.contains("FİŞNO")||line.contains("FİSNO")){
                    Ttarih.setText(getData5(line));
                }

                if (line.contains("SAAT") || line.contains("Saat") || line.contains("saat")) {
                    Tsaat.setText(getData4(line));
                }

                if (line.contains("*") && line.contains("%")) {
                    Tfiyat.setText((getData2(line)).substring(1, 6));
                    Turunad.setText(getData3(line));
                }

                if (index == 0) {
                    Tmagazaad.setText(line);
                }

                Pattern pattern = Pattern.compile("\\d{13}");
                Matcher matcher = pattern.matcher(line);

                if (matcher.find() &&( line.contains("T") || line.contains("P") ||line.contains("S"))){
                    Tbarkod.setText(getData6(line).substring(15,28));
                }
            }
        }

        btn = (Button) findViewById(R.id.button_cikis);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                faturaKaydet();

            }
        });
    }
  //  !Tfiyat.getText().toString().isEmpty() && !Tsaat.getText().toString().isEmpty() && !Turunad.getText().toString().isEmpty() &&&& !Ttarih.getText().toString().isEmpty() && !Tbarkod.getText().toString().isEmpty()
    public void faturaKaydet() {
        if (!Tmagazaad.getText().toString().isEmpty() ) {
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

                    Toast.makeText(GoruntuActivity.this, "Kaydederken bir problem oluştu"+volleyError, Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() {

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    Map<String, String> params = new HashMap<>();
                    params.put("auth_token", preferences.getString("authentication", ""));
                    params.put("user_id", preferences.getString("userId", "-1"));
                    params.put("date", Ttarih.getText().toString() + Tsaat.getText().toString());
                    params.put("market", Tmagazaad.getText().toString());
                    params.put("products",Tbarkod.getText().toString()+","+Tfiyat.getText().toString());
                    params.put("format","json");
                    return params;

                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }

        else {
            Toast.makeText(GoruntuActivity.this, "Eksik bilgiler mecut.Lütfen fişi tekrar çekiniz.", Toast.LENGTH_LONG).show();
            Intent anasayfa = new Intent(GoruntuActivity.this, AnasayfaActivity.class);
            startActivity(anasayfa);
        }
    }

    public void getResponseStatus(String response) throws JSONException{
        JSONObject jsonObj = new JSONObject(response);
        if("created".equals(jsonObj.getString("status").toString())){
            Toast.makeText(GoruntuActivity.this, "Kayıt Başarılı", Toast.LENGTH_LONG).show();
            if(!jsonObj.getString("barcode").toString().isEmpty()){
                Intent intent=new Intent(this,MyReceiver.class);
                intent.putExtra("barcode",jsonObj.getString("barcode").toString());
                intent.putExtra("price",jsonObj.getString("price").toString());
                ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(),234324243,intent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
                intentArray.add(pendingIntent);
                Intent anasayfa = new Intent(GoruntuActivity.this, AnasayfaActivity.class);
                startActivity(anasayfa);
            } else{
                Intent anasayfa2 = new Intent(GoruntuActivity.this, AnasayfaActivity.class);
                startActivity(anasayfa2);
            }

        }
        else{
            Toast.makeText(GoruntuActivity.this, "Kayıt Başarısız", Toast.LENGTH_LONG).show();
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

    private static String getData2(String line){
        if(line.contains("8")==false){
            return line;
        }
        return line.split("8")[1].trim();

    }
    private static String getData3(String line){
        if(line.contains("%")==false){
            return line;
        }
        return line.split("%")[0].trim();

    }

    private static String getData4(String line) {
        if (line.contains(" ") == false) {
            return line;
        }
        return line.split(" ")[1].trim();
    }
        private static String getData5(String line){
            if(line.contains("F")==false){
                return line;
            }
            return line.split("F")[0].trim();
        }
        private static String getData6(String line){
            if(line.contains("S")==false){
                return line;
            }
            return line.split("S")[1].trim();
        }
}




