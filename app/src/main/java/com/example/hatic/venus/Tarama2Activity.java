package com.example.hatic.venus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.hatic.venus.goruntu_isleme.KareBul;
import com.example.hatic.venus.goruntu_isleme.Perspektif;
import com.example.hatic.venus.yardımcı.DosyaKontrol;
import com.example.hatic.venus.yardımcı.FormatKontrol;
import com.example.hatic.venus.yardımcı.PolygonView;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.example.hatic.venus.AnasayfaActivity;


import org.opencv.core.Point;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tarama2Activity extends AppCompatActivity {

    private String path;
    private String appFolder = "/Venus/";
    private Button scan2;
    private ImageView captureImage;
    private FrameLayout insideFrame2;
    private PolygonView polygonView;
    private ProgressDialog progressDialog;
    private Bitmap scaledBitmap,originalImage;
    private TessBaseAPI tessBaseAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarama2);
        setPath();
        // set view
        scan2=(Button)findViewById(R.id.button_tara2);
        insideFrame2=(FrameLayout)findViewById(R.id.inside_frame2);
        captureImage=(ImageView)findViewById(R.id.imageView2);
        polygonView=(PolygonView)findViewById(R.id.polygonView2);
        setSelectedImage2Scan();

    }

    private void  setSelectedImage2Scan() {
        captureImage.post(new Runnable() {
            @Override
            public void run() {
                originalImage = FormatKontrol.getImageToBitmap(getIntent().getStringExtra("path"));
                if (originalImage != null) {
                    //resim boş değilse setbitmap metoduna yollandı
                    setBitmap(originalImage);
                    findPoints();
                }
            }
        });
    }

    private void  setBitmap(Bitmap original) {
        scaledBitmap= scaledBitmap(original, insideFrame2.getWidth(), insideFrame2.getHeight());
        //buradaki scaledBitmap metodu ile (AŞAĞIDA) değişkende çerçeve boyutları verildi
        captureImage.setImageBitmap(scaledBitmap);
        //resimin orjinalli bu framee yollandı
    }

    private Bitmap scaledBitmap(Bitmap bitmap, int width, int height) {
        Matrix matrix = new Matrix(); //matris oluşturuldu
        matrix.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, width, height), Matrix.ScaleToFit.CENTER);
        //kaynak dikdöretgeni hedef dikdötgene eşler
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        // mevcut bitmapi ölçeklendirilmiş yeni ve değişmez bir bitmape dönüştürür.
    }

    private void findPoints(){
        progressDialog=new ProgressDialog(this); //bekleyen müşteri için dönen halka ikonu
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("İşleniyor..."); //ve yazısı
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                setPolygonView();
                progressDialog.dismiss();
            }
        }).start();
    }

    private void setPolygonView(){
        polygonView.setPoints(getEdgePoints());
        polygonView.post(new Runnable() {
            @Override
            public void run() {
                polygonView.setVisibility(View.VISIBLE);
                int padding = (int) getResources().getDimension(R.dimen.scanPadding);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(scaledBitmap.getWidth()+2*padding,scaledBitmap.getHeight()+2*padding);
                //Belirtilen genişlik, yükseklik ve ağırlık ile yeni bir düzen parametreleri kümesi oluşturur.
                layoutParams.gravity = Gravity.CENTER;
                polygonView.setLayoutParams(layoutParams);
            }
        });

    }

    private Map<Integer, PointF> getEdgePoints() {
        List<PointF> pointFs = getContourEdgePoints();
        //kenar çizimleri için noktalat konur????
        return polygonView.getOrderedPoints(pointFs);
    }

    private List<PointF> getContourEdgePoints(){
        KareBul rectangle=new KareBul(scaledBitmap);
        //find rectangele sınıfına scaleBitmapi yollar
        List<Point> points= rectangle.findRectangle();
        List<PointF> pointFs = new ArrayList<>();
        for(Point point : points){
            pointFs.add(new PointF((float)point.x,(float)point.y));
            //noktalar eklenir
        }
        return pointFs;
    }

    public void scanner(View view){  //BUTON İŞLEMLERİ
        scan2.setVisibility(View.GONE);
        progressDialog=new ProgressDialog(this); //bekleme halkası
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Okunuyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Perspektif scanner=new Perspektif(originalImage,polygonView.getPoints(),captureImage.getWidth(),captureImage.getHeight());                //bitmape atanmış resmin üstüne noktaları yerleştirip image viewdeki resmi boyutlandırır ve kesme işlemi için ayarlar
                Bitmap scanningImage=scanner.Scan();
                //butona basıldığında resmi bitmape çevirip kesme işlemini tamamlar
                DosyaKontrol.deleteFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()+"/Venus/"+DosyaKontrol.jpegName);
                //eski resmi siler
                File file=DosyaKontrol.newJPEG(getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath()+"/Venus/");
                //boyutlandırılmışı kaydetmek için isimlendirir
                DosyaKontrol.saveImage(scanningImage,file);
                //ve kaydede
                readWithTesseract(scanningImage);
                //bitmape dönüştürülüp yeniden boyutlandırılan resmi tesseract ile okuması için
                // readWhit.. metodunda işlemler gerçekleştirir

            }
        }).start();

    }

    private void readWithTesseract(final Bitmap scaningImage){
        new Thread(new Runnable() {
            @Override
            public void run() {
                tessBaseAPI = new TessBaseAPI();
                tessBaseAPI.init(path, "tur");
                tessBaseAPI.setImage(scaningImage);
                String text = tessBaseAPI.getUTF8Text();
                tessBaseAPI.end();
                progressDialog.dismiss();
                Intent goruntu = new Intent(getApplicationContext(), AramaActivity.class);
                goruntu.putExtra("text", text);
                startActivity(goruntu);
                finish();
            }
        }).start();

        Log.i("okudu","bebeğim");
    }

    private void setPath() {
        path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath() + appFolder;
    }
}
