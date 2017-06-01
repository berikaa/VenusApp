package com.example.hatic.venus.yardımcı;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DosyaKontrol {
    public static String jpegName;

    public static void copyTessdataLangFile(Context context, String path){
        folderControl(path);   //method ile yol kontrolü
        String lang="tur.traineddata"; //türkçe dil desteği
        if (!(new File(path+lang)).exists()) {
            try {
                AssetManager assetManager=context.getAssets();
                InputStream inLang = assetManager.open("Tessdata/" + lang); // tessdata sınıfından veri alablmek için nesnesini oluşturdu
                OutputStream outLang = new FileOutputStream(path + lang); //??
                byte[] buff = new byte[2048];
                int len;
                while ((len = inLang.read(buff)) > 0) { //byte uzunluğunu kontrol eder????????
                    outLang.write(buff,0,len);
                }
                inLang.close();
                outLang.close();
                Log.i("Success", "Folder was copied");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Error", "Folder wasnt copied " + e.toString());
            }
        }

    }

    private static File folderControl(String path){
        File file=new File(path);
        // Dosya yolunun oluşturulup oluşturulmadığını kontrol eder.
        if(!file.exists()){
            //Dosya yolunu oluşturur.Dosya Oluşturulamazsa log a düşürür.
            if (!file.mkdirs()){
                Log.i("Path-Folder","File wasnt created");
                //Bu duruma göre program yönlendrilir.
            }
        }
        return file;
    }

    public static File newJPEG(String path){
        jpegName="IMG_"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpeg";  // resimin kaydedilme adı
        return new File(folderControl(path).getPath()+File.separator+jpegName); // kaydedilen yolu
    }

    public static Uri getUriFromFile(String path){
        return Uri.fromFile(newJPEG(path));   //???
    }

    public static void deleteFile(String path){
        File file = new File(path);
        file.delete();//resmi sildi
    }

    public static void saveImage(Bitmap image, File file){
        try{
            FileOutputStream out=new FileOutputStream(file); //FileOutputStream, görüntü verisi gibi ham byte'ların akışlarını yazmak içindir.
            image.compress(Bitmap.CompressFormat.JPEG,100,out); //bitmap ile jpeg formatında sıkıştırılmış resim
            out.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


}
