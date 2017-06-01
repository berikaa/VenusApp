package com.example.hatic.venus.goruntu_isleme;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.util.Log;

import com.example.hatic.venus.yardımcı.FormatKontrol;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Map;


public class Perspektif {

        private Bitmap originalImage;
        private Map<Integer,PointF> rectangleEdgePoints;
        private float width;
        private float height;
        private float[] rateList=new float[8];

        public Perspektif(Bitmap originalImage, Map<Integer,PointF> rectangleEdgePoints, float width, float height){
            this.originalImage=originalImage;
            this.rectangleEdgePoints=rectangleEdgePoints;
            this.width=width;
            this.height=height;
        }

        public Bitmap Scan(){
            //resmin perspective i alınmıştır
            getScannedBitmap();
            zoomImage(rateList);
            return imagePerspective(zoomImage(rateList));
        }

        private void getScannedBitmap() {
            float xRate = (float) originalImage.getWidth() / width;
            float yRate = (float) originalImage.getHeight() / height;
            for (int i=0 ;i<rectangleEdgePoints.size();i++){
                rateList[i]=(rectangleEdgePoints.get(i).x) * xRate;
                Log.i("x",rateList[i]+"");
                rateList[i+4]=(rectangleEdgePoints.get(i).y) * yRate;
                Log.i("y",rateList[i+4]+"");
            }
        }

        private Bitmap imagePerspective(double... max){
            Mat src_mat = new Mat(4,1, CvType.CV_32FC2);
            src_mat.put(0,0,rateList[0],rateList[4],rateList[1],rateList[5],
                    rateList[2],rateList[6],rateList[3],rateList[7]);
            Mat dst_mat = new Mat(4,1,CvType.CV_32FC2);
            dst_mat.put(0,0, 0,0, max[0]-1,0 ,0,max[1]-1 ,max[0]-1,max[1]-1);
            Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src_mat, dst_mat);
            Mat original= FormatKontrol.convertBitmapToMat(originalImage);
            Mat clippingImage = original.clone();
            Imgproc.warpPerspective(original, clippingImage, perspectiveTransform, new Size(max[0],max[1]));
            return FormatKontrol.convertMatToBitmap(clippingImage);
        }


        private double[] zoomImage(float[] rateList){
            double width1=Math.sqrt(Math.pow(rateList[3] - rateList[2] , 2)*2);
            double width2=Math.sqrt(Math.pow(rateList[1] - rateList[0] , 2)*2);
            double height1=Math.sqrt(Math.pow(rateList[5] - rateList[7] , 2)*2);
            double height2=Math.sqrt(Math.pow(rateList[4] - rateList[6] , 2)*2);

            double maxWidth = (width1 < width2) ? width1 : width2;
            Log.i("w",maxWidth+"");
            double maxHeight = (height1 < height2) ? height1 : height2;
            Log.i("w",maxHeight+"");
            return new double[]{maxWidth,maxHeight};
        }
    }

