package com.example.hatic.venus.yardımcı;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * Created by pc on 15.02.2017.
 */

public class FormatKontrol {
    public static Bitmap getImageToBitmap(String path){
        Bitmap bitmap= BitmapFactory.decodeFile(path); //dosya yolunu bitmap e kodlandı
        return bitmap;
    }

    public static Mat convertBitmapToMat(Bitmap bmpImage ){
        Mat image = new Mat(bmpImage.getWidth(), bmpImage.getHeight(), CvType.CV_8UC1);
        //resim matrisi oluşturuldu boyunu ve enini aldı cvtype ile de ise gri tonlama işini  yapar
        Bitmap myBitmap32 = bmpImage.copy(Bitmap.Config.ARGB_8888, true);
        //burada her bir piksel 4 byte ile depolanmaktadır.
        Utils.bitmapToMat(myBitmap32, image);
        //android bitmapi opencv mat a dönüştürme işlemidir
        return image;
    }

    public static  Bitmap convertMatToBitmap(Mat image){
        Bitmap bitmap = Bitmap.createBitmap(image.cols(),image.rows(), Bitmap.Config.ARGB_8888);
        //resmi belirtilen genişlik ve yükseklikte değiştirilebilir bir bitmape döndürür
        Utils.matToBitmap(image, bitmap);
        //OpenCV Mat'i Android Bitmap'e dönüştürür.
        return bitmap;
    }
}
