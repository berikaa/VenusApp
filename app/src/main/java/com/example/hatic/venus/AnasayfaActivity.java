package com.example.hatic.venus;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.TransitionRes;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;
import org.w3c.dom.Text;

import com.example.hatic.venus.yardımcı.DosyaKontrol;


import java.util.HashMap;
import java.util.Map;


public class AnasayfaActivity extends ActionBarActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static TransitionRes openCamera;
    private String subFolderLang = "Tessdata/";
    private ImageButton btn1,btn2;
    private Uri image;
    private String path;
    private String appFolder="/Venus/";
    private TextView nameSurname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anasayfa);
        nameSurname=(TextView)findViewById(R.id.txt_adsoyad);

        btn1=(ImageButton)findViewById(R.id.kamera);
        btn2=(ImageButton)findViewById(R.id.ara);

        setName();


    }

    public void menu(View view){

        Intent menu = new Intent(AnasayfaActivity.this, MenuActivity.class);
        startActivity(menu);

    }

    public void setName(){
         preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
         editor = preferences.edit();
        nameSurname.setText(preferences.getString("first_name","")+" "+preferences.getString("last_name",""));

    }

    public void openCamera(View view){

        setPath();
        Intent kamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Resim çekme isteği ve activity başlatılıp id'si tanımlandı
        image = DosyaKontrol.getUriFromFile(path);
        kamera.putExtra(MediaStore.EXTRA_OUTPUT,image);
        startActivityForResult(kamera,200);

    }

    public void openCamera2(View view){

        setPath();
        Intent ara=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Resim çekme isteği ve activity başlatılıp id'si tanımlandı
        image = DosyaKontrol.getUriFromFile(path);
        ara.putExtra(MediaStore.EXTRA_OUTPUT,image);
        startActivityForResult(ara,202);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if ( requestCode ==200) {
            new Thread(new Runnable() { //bu threadin içine gelir
                @Override
                public void run() {
                    Intent tara = new Intent(getApplicationContext(), TaramaActivity.class);
                    tara.putExtra("path", path + DosyaKontrol.jpegName);
                    startActivity(tara);
                }
            }).start();
        }
        if (requestCode==202){
            new Thread(new Runnable() { //bu threadin içine gelir
                @Override
                public void run() {
                    Intent tara = new Intent(getApplicationContext(), Tarama2Activity.class);
                    tara.putExtra("path", path + DosyaKontrol.jpegName);
                    startActivity(tara);
                }
            }).start();
        }


        super.onActivityResult(requestCode, resultCode, data);
        //Çekilen resim id olarak bitmap şeklinde alındı ve imageview'e atandı
    }


    private void setPath(){
        path=getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()+appFolder;
        //harici alandaki dizinin mutlak yolunu döndürür
    }


  /*  private void writeSystemLangData(){
        String traineddataFolder=getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()+appFolder+subFolderLang;
        DosyaKontrol.copyTessdataLangFile(getApplicationContext(),traineddataFolder);
    }*/

}