package edu.iitj.epsilon_netdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wunderlist.slidinglayer.SlidingLayer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout mStatusBar;
    public static TextView mTotalPointsTextView;
    private TextView mEnetPointsTextView;
    private Button mResetButton;
    private EnetDisplayView mEnetDisplayView;
    private Button mRandomPointsButton;
    private Button mAddPointButton;
    private Button mSetEpsilonButton;
    private Button mGenerateEnetButton;
    private SlidingLayer mSlidingLayer;
    public static TextView mLogAllPointsTextView;
    public static TextView mLogEnetPointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStatusBar = (LinearLayout) findViewById(R.id.status_bar);
        mTotalPointsTextView = (TextView) findViewById(R.id.total_points_text_view);
        mEnetPointsTextView= (TextView) findViewById(R.id.enet_points_text_view);
        mResetButton = (Button) findViewById(R.id.reset_button);
        mEnetDisplayView = (EnetDisplayView) findViewById(R.id.enet_display_view);
        mRandomPointsButton = (Button) findViewById(R.id.random_points_button);
        mAddPointButton = (Button) findViewById(R.id.add_point_button);
        mSetEpsilonButton = (Button) findViewById(R.id.set_epsilon_button);
        mGenerateEnetButton = (Button) findViewById(R.id.generate_enet_button);
        mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer);
        mLogAllPointsTextView = (TextView) findViewById(R.id.log_all_points_text_view);
        mLogEnetPointsTextView = (TextView) findViewById(R.id.log_enet_points_text_view);


        mStatusBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSlidingLayer.isOpened())
                    mSlidingLayer.closeLayer(true);
                else
                    mSlidingLayer.openLayer(true);
            }
        });
        mTotalPointsTextView.setText(getResources().getString(R.string.enet_points, 0));
        mEnetPointsTextView.setText(getResources().getString(R.string.enet_points, "-"));
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEnetDisplayView.reset();
                mEnetPointsTextView.setText(getResources().getString(R.string.enet_points, "-"));
            }
        });
        mEnetDisplayView.setOnTouchListener(mEnetDisplayView);
        mRandomPointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final EditText num_points = new EditText(MainActivity.this);
                num_points.setInputType(InputType.TYPE_CLASS_NUMBER);
                num_points.setHint("Enter number of points");
                final AlertDialog alertDialog = builder.setTitle("Generate Random Points")
                        .setView(num_points)
                        .setPositiveButton("Generate Points", null)
                        .setNegativeButton("Cancel", null)
                        .create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(num_points.getText().toString().trim().isEmpty()) {
                                    num_points.setError("Please enter a number");
                                } else if(Integer.parseInt(num_points.getText().toString()) > 1000 || Integer.parseInt(num_points.getText().toString()) <= 0) {
                                    num_points.setError("Number should be between [1, 1000]");
                                } else {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(num_points.getWindowToken(), 0);
                                    mEnetDisplayView.generateRandomPoints(Integer.parseInt(num_points.getText().toString()));
                                    alertDialog.dismiss();
                                }
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEnetDisplayView.initBitmap();
            }
        });
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(mSlidingLayer.isOpened())
            mSlidingLayer.closeLayer(true);
        else {
            super.onBackPressed();
        }
    }
}
