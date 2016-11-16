package edu.iitj.epsilon_netdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wunderlist.slidinglayer.SlidingLayer;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static TextView mLastPointTextView;
    public static TextView mTotalPointsTextView;
    private TextView mEpsilonTextView;
    private TextView mEnetPointsTextView;
    private EnetDisplayView mEnetDisplayView;
    public static TextView mLogAllPointsTextView;
    public static TextView mLogEnetPointsTextView;

    private float mEpsilon;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLastPointTextView = (TextView) findViewById(R.id.last_point_text_view);
        mTotalPointsTextView = (TextView) findViewById(R.id.total_points_text_view);
        mEpsilonTextView = (TextView) findViewById(R.id.epsilon_text_view);
        mEnetPointsTextView= (TextView) findViewById(R.id.enet_points_text_view);
        mEnetDisplayView = (EnetDisplayView) findViewById(R.id.enet_display_view);
        mLogAllPointsTextView = (TextView) findViewById(R.id.log_all_points_text_view);
        mLogEnetPointsTextView = (TextView) findViewById(R.id.log_enet_points_text_view);

        mEpsilon = 0.0f;

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);

        mLastPointTextView.setText(getResources().getString(R.string.last_point, "-", "-"));
        mTotalPointsTextView.setText(getResources().getString(R.string.enet_points, 0));
        mEpsilonTextView.setText(getResources().getString(R.string.epsilon, "-"));
        mEnetPointsTextView.setText(getResources().getString(R.string.enet_points, "-"));

        mEnetDisplayView.setOnTouchListener(mEnetDisplayView);


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

    public void onClickStatusBar(View v) {
        SlidingLayer mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer);
        if(mSlidingLayer.isOpened())
            mSlidingLayer.closeLayer(true);
        else
            mSlidingLayer.openLayer(true);
    }

    public void onClickResetButton(View v) {
        mEpsilon = 0.0f;
        mEnetDisplayView.reset();
        mEpsilonTextView.setText(getResources().getString(R.string.epsilon, "-"));
        mEnetPointsTextView.setText(getResources().getString(R.string.enet_points, "-"));
    }

    public void onClickRandomPointsButton(View v) {
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

    public void onClickAddPointButton(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText x_input = new EditText(MainActivity.this);
        x_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        x_input.setHint("Enter x co-ordinate");
        final EditText y_input = new EditText(MainActivity.this);
        y_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        y_input.setHint("Enter y co-ordinate");
        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(x_input);
        layout.addView(y_input);
        final AlertDialog alertDialog = builder.setTitle("Add Point")
                .setView(layout)
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(x_input.getText().toString().trim().isEmpty()) {
                            x_input.setError("Please enter x co-ordinate");
                        } else if(Integer.parseInt(x_input.getText().toString()) > mEnetDisplayView.getMaxX() || Integer.parseInt(x_input.getText().toString()) < 0) {
                            x_input.setError("x co-ordinate must be between [0, " + mEnetDisplayView.getMaxX() + "]");
                        } else if(y_input.getText().toString().trim().isEmpty()) {
                            y_input.setError("Please enter y co-ordinate");
                        } else if(Integer.parseInt(y_input.getText().toString()) > mEnetDisplayView.getMaxY() || Integer.parseInt(y_input.getText().toString()) < 0) {
                            y_input.setError("y co-ordinate must be between [0, " + mEnetDisplayView.getMaxY() + "]");
                        } else {
                            int x = Integer.parseInt(x_input.getText().toString());
                            int y = Integer.parseInt(y_input.getText().toString());
                            mEnetDisplayView.addPoint(x, y);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    public void onClickSetEpsilonButton(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final EditText epsilon = new EditText(MainActivity.this);
        epsilon.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        epsilon.setHint("Enter epsilon value");
        final AlertDialog alertDialog = builder.setTitle("Set Epsilon")
                .setView(epsilon)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(epsilon.getText().toString().trim().isEmpty()) {
                            epsilon.setError("Please enter epsilon value");
                        } else if(Float.parseFloat(epsilon.getText().toString()) > 1.0f || Float.parseFloat(epsilon.getText().toString()) <= 0.0f) {
                            epsilon.setError("Epsilon should be between (0, 1]");
                        } else {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(epsilon.getWindowToken(), 0);
                            mEpsilon = Float.parseFloat(epsilon.getText().toString());
                            mEpsilonTextView.setText(getResources().getString(R.string.epsilon, epsilon.getText().toString()));
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    public void onClickGenerateEnetButton(View v) {
        if(mEpsilon == 0.0f) {
            Toast.makeText(this, "Please set Epsilon value first!", Toast.LENGTH_LONG).show();
        } else if(mEnetDisplayView.getPoints().isEmpty()) {
            Toast.makeText(this, "Please add some points first!", Toast.LENGTH_LONG).show();
        } else if(Utils.isNetworkConnected(getApplicationContext())) {
            getEnet();
        } else {
            Toast.makeText(this, "Could not get Epsilon-Net!\nInternet is not talking!", Toast.LENGTH_LONG).show();
        }
    }

    public void getEnet() {
        try {
            mProgressDialog.setMessage("Getting Epsilon-Net...");
            mProgressDialog.show();
            AsyncServerTask task = new AsyncServerTask("https://epsilon-net.herokuapp.com/getEnet.php", this, this.getClass().getMethod("onPostGetEnet", String.class));
            task.addPostParameter("epsilon", Float.toString(mEpsilon));
            task.addPostParameter("points", Utils.toString(mEnetDisplayView.getPoints()));
            task.execute();
        } catch (Exception e) {
            if(mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            Toast.makeText(this,e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void onPostGetEnet(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            if(status.equals("Error")) {
                Toast.makeText(this,jsonObject.getString("error"), Toast.LENGTH_LONG).show();
            } else {
                JSONArray epsnet = jsonObject.getJSONArray("epsnet");
                int[] indices = new int[epsnet.length()];
                for(int i=0; i<epsnet.length(); i++) {
                    indices[i] = epsnet.getInt(i);
                }
                mEnetDisplayView.drawEnet(indices);
                mEnetPointsTextView.setText(getResources().getString(R.string.enet_points, indices.length));
            }
        } catch (Exception e) {
            Toast.makeText(this,e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        if(mProgressDialog.isShowing())
            mProgressDialog.dismiss();
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
        SlidingLayer mSlidingLayer = (SlidingLayer) findViewById(R.id.slidingLayer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(mSlidingLayer.isOpened())
            mSlidingLayer.closeLayer(true);
        else {
            super.onBackPressed();
        }
    }
}
