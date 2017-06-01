package com.example.hatic.venus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.hatic.venus.yardımcı.DosyaKontrol;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

public class MainActivity extends AppCompatActivity {
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCVManager setup", "OpenCV loaded successfully");
                    //Use openCV libraries after this
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    private String appFolder = "/Venus/"; //karvizitimcepta adında bir klosör açtı
    private String subFolderLang = "Tessdata/"; // daha önceden türkçe dil desteğini alt klosör olorak değişkene atadı
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            btn = (Button) findViewById(R.id.btn_uye);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent giris = new Intent(MainActivity.this, Giris.class);
                    startActivity(giris);
                    writeSystemLangData();
                }
            });

            btn = (Button) findViewById(R.id.btn_kyt);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Ardından Intent methodunu kullanarak nereden nereye gideceğini söylüyoruz.
                    Intent kayit = new Intent(MainActivity.this, Kayit.class);
                    startActivity(kayit);
                    writeSystemLangData();
                }
            });

    }

    private void writeSystemLangData() {
        //aşağıda harici bir alana kaydedilecek resmin standart yolu verilmiştir
        // getpath'in peşine de klosörlerin isimleri eklenmiştir
        String traineddataFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + appFolder + subFolderLang;
        //filehelper sınıfından aşağıdaki method çağırılmıştır.
        DosyaKontrol.copyTessdataLangFile(getApplicationContext(), traineddataFolder);
    }

}
