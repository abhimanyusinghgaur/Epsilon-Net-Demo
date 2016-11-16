package edu.iitj.epsilon_netdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 15-11-2016.
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

    public void initBitmap() {
        if(!mBitmapCreated) {
            mBitmapCreated = true;
            maxX = this.getMeasuredWidth();
            maxY = this.getMeasuredHeight();
            mBitmap = Bitmap.createBitmap(maxX + 1, maxY + 1, Bitmap.Config.ARGB_8888);
        }
    }

    private void drawBitmap() {
        Canvas canvas = mHolder.lockCanvas();
        if(canvas != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void generateRandomPoints(int num_points) {
        reset();
        Random random = new Random();

        for(int i=0; i<num_points; i++) {
            int x = random.nextInt(maxX + 1);
            int y = random.nextInt(maxY + 1);
            points.add(x);
            points.add(y);
            mBitmap.setPixel(x, y, Color.WHITE);
            MainActivity.mLogAllPointsTextView.append("X: " + x + ", Y: " + y + "\n");
        }

        drawBitmap();

        MainActivity.mTotalPointsTextView.setText(getResources().getString(R.string.total_points, points.size()/2));
    }

    public void addPoint(int x, int y) {
        points.add(x);
        points.add(y);
        mBitmap.setPixel(x, y, Color.WHITE);

        drawBitmap();

        MainActivity.mLogAllPointsTextView.append("X: " + x + ", Y: " + y + "\n");
        MainActivity.mTotalPointsTextView.setText(getResources().getString(R.string.total_points, points.size()/2));
    }

    public void drawEnet(int[] indices) {
        Canvas canvas = mHolder.lockCanvas();

        for(int i=0; i<indices.length; i++) {
            int x = points.get(indices[i]*2);
            int y = points.get(indices[i]*2 + 1);
            mBitmap.setPixel(x, y, Color.RED);

            MainActivity.mLogEnetPointsTextView.append("X: " + x + ", Y: " + y + "\n");
        }
        drawBitmap();

        mHolder.unlockCanvasAndPost(canvas);
    }

    public void reset() {
        points.clear();
        mBitmap.eraseColor(Color.BLACK);
        drawBitmap();
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
        if(mBitmapCreated)
            drawBitmap();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
