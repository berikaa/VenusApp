package com.example.hatic.venus.goruntu_isleme;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import com.example.hatic.venus.yardımcı.FormatKontrol;
import static org.opencv.core.Core.extractChannel;

public class KareBul {

    private Bitmap scaledBitmap;
    private Mat originalMatrix,smallImage,gray,gray8Bits;
    List<Point> source = new ArrayList<>(); //dizi oluşturuldu
    private int N=11,thresh=255;

    public KareBul(Bitmap scaledBitmap){
        this.scaledBitmap=scaledBitmap;
    }

    double angle(Point pt1, Point pt2, Point pt0 ) {
        //dikdörtgen için  noktalar belirlendi
        double dx1 = pt1.x - pt0.x;
        double dy1 = pt1.y - pt0.y;
        double dx2 = pt2.x - pt0.x;
        double dy2 = pt2.y - pt0.y;
        return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
    }

    // create matrix
    private void convertMatrix(){
        originalMatrix= FormatKontrol.convertBitmapToMat(scaledBitmap);
        //imagehelper sınıfında bitmap den matrise çevrilen resim
        smallImage= new Mat(new Size(originalMatrix.width()/2,originalMatrix.height()/2),originalMatrix.type());
        //smallimage değişkeni adı altında resmin boyutları yarıya indirilerek yeniden boyutlandırıldı
        gray=new Mat(originalMatrix.size(),originalMatrix.type());
        gray8Bits=new Mat(originalMatrix.size(), CvType.CV_8U);
    }
    // Bir görüntüyü orjinalinden farklı bir boyuta döndürmek istediğimizde primatileri kullanırız.
    // Yaygın bir kullanım.Görüntü işlemeyi kolaylaştırıyor.
    private void resizeImage(){
        //resim yeniden boyutlandırma yapar aşama aşama büyütmak veya küçültmek için kullanılır
        Imgproc.pyrDown(originalMatrix, smallImage, smallImage.size());
        Imgproc.pyrUp(smallImage, originalMatrix, originalMatrix.size());

    }
    public List<Point> findRectangle(){
        convertMatrix();
        resizeImage();
        List<MatOfPoint> rectangle=new ArrayList<>();
        //döngü 3 kez dönücek çünkü rgb şeklinde üç tip renk vardır
        for (int color = 0; color < 3 ; color++) {
            extractChannel(originalMatrix,gray,color);
            for (int l = 0; l < N ; l++) {
                List<MatOfPoint> contours=new ArrayList<MatOfPoint>();
                // Görüntünün analizi gerçekleştiriliyor.Burada resimdeki nesne belirgin hale getiriliyor.
                // Bunu binary formatta gerçekleştiriyor.
                Imgproc.threshold(gray, gray8Bits, (l+1)*thresh/N, thresh, Imgproc.THRESH_BINARY);
                // OpenCv resimdeki en dış hatları buluyor.Bu hatlar içerisinde kullanıcının kartviziti bulunmaktadır.
                Imgproc.findContours(gray8Bits, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                //CHAIN_APPROX_SIMPLE --Yatay, dikey ve köşegen kesimleri sıkıştırır ve yalnızca uç noktalarını bırakır.
                MatOfPoint approx;
                for( int i = 0; i < contours.size(); i++ )
                {
                    // Mümkün olan en yakın dikdörtgene benzetilmeye çalışılır.
                    double epsilon= Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true)*0.02;
                    MatOfPoint2f tempMat=new MatOfPoint2f();
                    Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), tempMat, epsilon, true);
                    //approxPolyDP ile belirtilen hasasslıkta bir çokgen eğrisi alır
                    approx=new MatOfPoint(tempMat.toArray());
                    // Geniş bir alana sahip ve dış bükey ise açı hesabını gerçekleştir.
                    if( approx.toArray().length == 4 &&
                            Math.abs(Imgproc.contourArea(approx)) > 1000 &&
                            Imgproc.isContourConvex(approx) )
                    //dizinin uzunluğu alanı ve dış bükey olup olmamasına göre bu bloğa giricek
                    {
                        double maxCosine = 0; //maksimum kosinüs
                        for( int j = 2; j < 5; j++ )
                        {
                            double cosine = Math.abs(angle(approx.toArray()[j%4], approx.toArray()[j-2], approx.toArray()[j-1]));
                            maxCosine = Math.max(maxCosine, cosine);
                        }
                        // Yaklaşık 90 derece ise noktaları seç
                        if( maxCosine < 0.3 )
                            rectangle.add(approx);
                    }
                }
            }
        }
        setSquaresList(rectangle);
        return source;
    }


    private void setSquaresList(List<MatOfPoint> squares){
        if (squares.size()==0 || squares.equals(null)) {
            Point p1=new Point(0,0);
            Point p2=new Point(0,scaledBitmap.getHeight());
            Point p3=new Point(scaledBitmap.getWidth(),0);
            Point p4=new Point(scaledBitmap.getWidth(),scaledBitmap.getHeight());
            source.add(p1);
            source.add(p2);
            source.add(p3);
            source.add(p4);
        }else{
            Log.i("test",squares.size()+"");
            MatOfPoint rectanglePoints=find_largest_rectangle(squares);
            double[] temp_double;
            temp_double = rectanglePoints.get(0, 0);
            Point p1 = new Point(temp_double[0], temp_double[1]);
            temp_double = rectanglePoints.get(1, 0);
            Point p2 = new Point(temp_double[0], temp_double[1]);
            temp_double = rectanglePoints.get(2, 0);
            Point p3 = new Point(temp_double[0], temp_double[1]);
            temp_double = rectanglePoints.get(3, 0);
            Point p4 = new Point(temp_double[0], temp_double[1]);
            source.add(p1);
            source.add(p2);
            source.add(p3);
            source.add(p4);
            Log.i("test2",source.size()+"");
            squares.clear();

        }
    }

    public MatOfPoint find_largest_rectangle(List<MatOfPoint> squares){
        //en büyük dikdörtgeni bulur
        int area=0;
        int max_square_idx = 0;
        for(int i=0;i<squares.size();i++){
            Rect rectangle = Imgproc.boundingRect(squares.get(i));
            if ((rectangle.width * rectangle.height)>area) {
                //daha büyüğün bulduğunda büyük ile yer değiştirir
                area=rectangle.width * rectangle.height;
                max_square_idx = i;
            }
        }
        Log.i("Biggest Rectangle index",max_square_idx+"");
        return squares.get(max_square_idx);

    }


}
