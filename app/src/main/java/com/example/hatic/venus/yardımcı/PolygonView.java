package com.example.hatic.venus.yardımcı;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.hatic.venus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolygonView extends FrameLayout {

    protected Context context;
    private ImageView mPointer1;
    private ImageView mPointer2;
    private ImageView mPointer3;
    private ImageView mPointer4;
    private ImageView midPointer13;
    private ImageView midPointer12;
    private ImageView midPointer24;
    private ImageView midPointer34;
    private PolygonView polygonView;
    private Paint paint;


    public PolygonView(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public PolygonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public PolygonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    @Override
    protected void attachViewToParent(View child, int index, ViewGroup.LayoutParams params) {
        super.attachViewToParent(child, index, params);
    }

    private void init(){
        //resim üzerindeki imegevewlerin yerlerini belirledi
        //köşeler ve orta noktaları
        polygonView=this;
        mPointer1=getImageView(0,0);
        mPointer2=getImageView(getWidth(),0);
        mPointer3=getImageView(0,getHeight());
        mPointer4=getImageView(getWidth(), getHeight());
        midPointer13 = getImageView(0, getHeight() / 2);
        midPointer13.setOnTouchListener(new MidPointTouchListenerImpl(mPointer1, mPointer3));
        midPointer12 = getImageView(0, getWidth() / 2);
        midPointer12.setOnTouchListener(new MidPointTouchListenerImpl(mPointer1, mPointer2));
        midPointer34 = getImageView(0, getHeight() / 2);
        midPointer34.setOnTouchListener(new MidPointTouchListenerImpl(mPointer3, mPointer4));
        midPointer24 = getImageView(0, getHeight() / 2);
        midPointer24.setOnTouchListener(new MidPointTouchListenerImpl(mPointer2, mPointer4));

        //altta imageviewleri ekledi
        addView(mPointer1);
        addView(mPointer2);
        addView(midPointer13);
        addView(midPointer12);
        addView(midPointer34);
        addView(midPointer24);
        addView(mPointer3);
        addView(mPointer4);
        initPaint();

    }

    private ImageView getImageView(int x, int y) {
        //imageviewe burda oluşturdu
        ImageView imageView = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(R.drawable.circle);
        imageView.setX(x);
        imageView.setY(y);
        imageView.setOnTouchListener(new TouchListenerImpl());
        return imageView;
    }

    private void initPaint() {
        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.blue));
        paint.setStrokeWidth(2); //dokunmak için gerekli geniişlik
        paint.setAntiAlias(true); //çizilenin kenarlarını yumuşatır, ancak şeklin iç kısmı üzerinde hiçbir etkisi yoktur
    }

    public Map<Integer, PointF> getPoints() {
        List<PointF> points = new ArrayList<PointF>();
        //noktaları liste şeklinde almış
        points.add(new PointF(mPointer1.getX(), mPointer1.getY()));
        points.add(new PointF(mPointer2.getX(), mPointer2.getY()));
        points.add(new PointF(mPointer3.getX(), mPointer3.getY()));
        points.add(new PointF(mPointer4.getX(), mPointer4.getY()));
        return getOrderedPoints(points);
    }

    public void setPoints(Map<Integer, PointF> pointFMap) {
        setPointsCoordinates(pointFMap);
    }

    public Map<Integer, PointF> getOrderedPoints(List<PointF> points) {
        //aldığı 4 noktanın her biri için 4 noktanın her birini bölüyor
        //yani dikdörtgenin orta noktadını buluyor
        PointF centerPoint = new PointF();
        int size = points.size();
        for (PointF pointF : points) {
            centerPoint.x += pointF.x / size;
            centerPoint.y += pointF.y / size;
        }
        Map<Integer, PointF> orderedPoints = new HashMap<>();
        for (PointF pointF : points) {
            int index = -1;
            if (pointF.x < centerPoint.x && pointF.y < centerPoint.y) {
                index = 0;
            } else if (pointF.x > centerPoint.x && pointF.y < centerPoint.y) {
                index = 1;
            } else if (pointF.x < centerPoint.x && pointF.y > centerPoint.y) {
                index = 2;
            } else if (pointF.x > centerPoint.x && pointF.y > centerPoint.y) {
                index = 3;
            }
            orderedPoints.put(index, pointF);
        }
        return orderedPoints;
    }


    private void setPointsCoordinates(Map<Integer, PointF> pointFMap) {
        //koordinatlar giriliyor
        mPointer1.setX(pointFMap.get(0).x);
        mPointer1.setY(pointFMap.get(0).y);

        mPointer2.setX(pointFMap.get(1).x);
        mPointer2.setY(pointFMap.get(1).y);

        mPointer3.setX(pointFMap.get(2).x);
        mPointer3.setY(pointFMap.get(2).y);

        mPointer4.setX(pointFMap.get(3).x);
        mPointer4.setY(pointFMap.get(3).y);
        //Herhangi biri null dönebilir programı sonlandır.
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //belirtilen noktalara çizgileri çizer
        canvas.drawLine(mPointer1.getX() + (mPointer1.getWidth() / 2), mPointer1.getY() + (mPointer1.getHeight() / 2), mPointer3.getX() + (mPointer3.getWidth() / 2), mPointer3.getY() + (mPointer3.getHeight() / 2), paint);
        canvas.drawLine(mPointer1.getX() + (mPointer1.getWidth() / 2), mPointer1.getY() + (mPointer1.getHeight() / 2), mPointer2.getX() + (mPointer2.getWidth() / 2), mPointer2.getY() + (mPointer2.getHeight() / 2), paint);
        canvas.drawLine(mPointer2.getX() + (mPointer2.getWidth() / 2), mPointer2.getY() + (mPointer2.getHeight() / 2), mPointer4.getX() + (mPointer4.getWidth() / 2), mPointer4.getY() + (mPointer4.getHeight() / 2), paint);
        canvas.drawLine(mPointer3.getX() + (mPointer3.getWidth() / 2), mPointer3.getY() + (mPointer3.getHeight() / 2), mPointer4.getX() + (mPointer4.getWidth() / 2), mPointer4.getY() + (mPointer4.getHeight() / 2), paint);
        midPointer13.setX(mPointer3.getX() - ((mPointer3.getX() - mPointer1.getX()) / 2));
        midPointer13.setY(mPointer3.getY() - ((mPointer3.getY() - mPointer1.getY()) / 2));
        midPointer24.setX(mPointer4.getX() - ((mPointer4.getX() - mPointer2.getX()) / 2));
        midPointer24.setY(mPointer4.getY() - ((mPointer4.getY() - mPointer2.getY()) / 2));
        midPointer34.setX(mPointer4.getX() - ((mPointer4.getX() - mPointer3.getX()) / 2));
        midPointer34.setY(mPointer4.getY() - ((mPointer4.getY() - mPointer3.getY()) / 2));
        midPointer12.setX(mPointer2.getX() - ((mPointer2.getX() - mPointer1.getX()) / 2));
        midPointer12.setY(mPointer2.getY() - ((mPointer2.getY() - mPointer1.getY()) / 2));
    }


    private class MidPointTouchListenerImpl implements OnTouchListener {

        PointF DownPT = new PointF();
        PointF StartPT = new PointF();

        private ImageView mainPointer1;
        private ImageView mainPointer2;

        public MidPointTouchListenerImpl(ImageView mainPointer1, ImageView mainPointer2) {
            this.mainPointer1 = mainPointer1;
            this.mainPointer2 = mainPointer2;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int eid = event.getAction();
            switch (eid) {
                case MotionEvent.ACTION_MOVE:
                    PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);

                    if (Math.abs(mainPointer1.getX() - mainPointer2.getX()) > Math.abs(mainPointer1.getY() - mainPointer2.getY())) {
                        if (((mainPointer2.getY() + mv.y + v.getHeight() < polygonView.getHeight()) && (mainPointer2.getY() + mv.y > 0))) {
                            v.setX((int) (StartPT.y + mv.y));
                            StartPT = new PointF(v.getX(), v.getY());
                            mainPointer2.setY((int) (mainPointer2.getY() + mv.y));
                        }
                        if (((mainPointer1.getY() + mv.y + v.getHeight() < polygonView.getHeight()) && (mainPointer1.getY() + mv.y > 0))) {
                            v.setX((int) (StartPT.y + mv.y));
                            StartPT = new PointF(v.getX(), v.getY());
                            mainPointer1.setY((int) (mainPointer1.getY() + mv.y));
                        }
                    } else {
                        if ((mainPointer2.getX() + mv.x + v.getWidth() < polygonView.getWidth()) && (mainPointer2.getX() + mv.x > 0)) {
                            v.setX((int) (StartPT.x + mv.x));
                            StartPT = new PointF(v.getX(), v.getY());
                            mainPointer2.setX((int) (mainPointer2.getX() + mv.x));
                        }
                        if ((mainPointer1.getX() + mv.x + v.getWidth() < polygonView.getWidth()) && (mainPointer1.getX() + mv.x > 0)) {
                            v.setX((int) (StartPT.x + mv.x));
                            StartPT = new PointF(v.getX(), v.getY());
                            mainPointer1.setX((int) (mainPointer1.getX() + mv.x));
                        }
                    }

                    break;
                case MotionEvent.ACTION_DOWN:
                    DownPT.x = event.getX();
                    DownPT.y = event.getY();
                    StartPT = new PointF(v.getX(), v.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    int color = 0;
                    if (isValidShape(getPoints())) {
                        color = getResources().getColor(R.color.blue);
                    } else {
                        color = getResources().getColor(R.color.orange);
                    }
                    paint.setColor(color);
                    break;
                default:
                    break;
            }
            polygonView.invalidate();
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public boolean isValidShape(Map<Integer, PointF> pointFMap) {
        return pointFMap.size() == 4;
    }

    private class TouchListenerImpl implements OnTouchListener {

        PointF DownPT = new PointF();
        PointF StartPT = new PointF();

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int eid = event.getAction();
            switch (eid) {
                case MotionEvent.ACTION_MOVE:
                    PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
                    if (((StartPT.x + mv.x + v.getWidth()) < polygonView.getWidth() && (StartPT.y + mv.y + v.getHeight() < polygonView.getHeight())) && ((StartPT.x + mv.x) > 0 && StartPT.y + mv.y > 0)) {
                        v.setX((int) (StartPT.x + mv.x));
                        v.setY((int) (StartPT.y + mv.y));
                        StartPT = new PointF(v.getX(), v.getY());
                    }
                    break;
                case MotionEvent.ACTION_DOWN:
                    DownPT.x = event.getX();
                    DownPT.y = event.getY();
                    StartPT = new PointF(v.getX(), v.getY());
                    break;
                case MotionEvent.ACTION_UP:
                    int color = 0;
                    if (isValidShape(getPoints())) {
                        color = getResources().getColor(R.color.blue);
                    } else {
                        color = getResources().getColor(R.color.orange);
                    }
                    paint.setColor(color);
                    break;
                default:
                    break;
            }
            polygonView.invalidate();
            return true;
        }

    }


}
