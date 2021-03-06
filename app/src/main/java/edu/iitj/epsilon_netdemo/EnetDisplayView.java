package edu.iitj.epsilon_netdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Abhimanyu Singh Gaur on 15-11-2016.
 * Custom View to display Epsilon-net.
 */
public class EnetDisplayView extends SurfaceView implements View.OnTouchListener, SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private ArrayList<Integer> points;  //  ArrayList of points to draw [x0 y0 x1 y1 x2 y2 ...]
    private Bitmap mBitmap;
    private boolean mBitmapCreated;
    private int maxX;
    private int maxY;

    public EnetDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mHolder = getHolder();
        mHolder.addCallback(this);

        points = new ArrayList<>();

        mBitmap = null;
        mBitmapCreated = false;

        maxX = maxY = 0;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void initBitmap() {
        //if(!mBitmapCreated) {
            mBitmapCreated = true;
            maxX = this.getMeasuredWidth();
            maxY = this.getMeasuredHeight();
            mBitmap = Bitmap.createBitmap(maxX + 1, maxY + 1, Bitmap.Config.ARGB_8888);
        //}
    }

    private void drawBitmap() {
        Canvas canvas = mHolder.lockCanvas();
        if(canvas != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    /*
    * Actually draws a square :P
    */
    private void drawCircle(int cx, int cy, int radius, @ColorInt int color) {
        final float scale = getResources().getDisplayMetrics().density;
        int startX = (int)(cx - scale*radius);
        int startY = (int)(cy - scale*radius);
        int endX = (int)(cx + scale*radius);
        int endY = (int)(cy + scale*radius);

        startX = startX >= 0 ? startX : 0;
        startY = startY >= 0 ? startY : 0;
        endX = endX <= maxX ? endX : maxX;
        endY = endY <= maxY ? endY : maxY;

        for(int x=startX; x<=endX; x++)
            for (int y=startY; y<=endY; y++)
                mBitmap.setPixel(x, y, color);
    }

    public void generateRandomPoints(int num_points) {
        int x = 0, y = 0;
        reset();
        Random random = new Random();

        for(int i=0; i<num_points; i++) {
            x = random.nextInt(maxX + 1);
            y = random.nextInt(maxY + 1);
            points.add(x);
            points.add(y);
            drawCircle(x, y, 1, Color.WHITE);
            MainActivity.mLogAllPointsTextView.append("X: " + x + ", Y: " + y + "\n");
        }

        drawBitmap();
        MainActivity.mLastPointTextView.setText(getResources().getString(R.string.last_point, x, y));
        MainActivity.mTotalPointsTextView.setText(getResources().getString(R.string.total_points, points.size()/2));
    }

    public void addPoint(int x, int y) {
        points.add(x);
        points.add(y);
        drawCircle(x, y, 1, Color.WHITE);

        drawBitmap();

        MainActivity.mLogAllPointsTextView.append("X: " + x + ", Y: " + y + "\n");
        MainActivity.mLastPointTextView.setText(getResources().getString(R.string.last_point, x, y));
        MainActivity.mTotalPointsTextView.setText(getResources().getString(R.string.total_points, points.size()/2));
    }

    public void drawEnet(int[] indices) {
        for (int index : indices) {
            int x = points.get(index * 2);
            int y = points.get(index * 2 + 1);
            drawCircle(x, y, 1, Color.RED);

            MainActivity.mLogEnetPointsTextView.append("X: " + x + ", Y: " + y + "\n");
        }
        drawBitmap();
    }

    public void reset() {
        points.clear();
        mBitmap.eraseColor(Color.BLACK);
        drawBitmap();
        MainActivity.mLastPointTextView.setText(getResources().getString(R.string.last_point, "-", "-"));
        MainActivity.mTotalPointsTextView.setText(getResources().getString(R.string.total_points, 0));
        MainActivity.mLogAllPointsTextView.setText(R.string.log_all_points);
        MainActivity.mLogEnetPointsTextView.setText(R.string.log_enet_points);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                // Getting the X and Y Coordinate of the touched position
                int x = (int) event.getX();
                int y = (int) event.getY();
                addPoint(x, y);
                break;
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!mBitmapCreated)
            initBitmap();
        drawBitmap();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
