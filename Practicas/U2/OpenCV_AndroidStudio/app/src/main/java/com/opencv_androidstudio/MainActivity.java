package com.opencv_androidstudio;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;

//public class Tutorial3Activity
//public class MainActivity extends CameraActivity implements CvCameraViewListener2 {
public class MainActivity extends CameraActivity implements CvCameraViewListener2, OnTouchListener {
    private static final String TAG = "OCVSample::Activity";

    int PosicionX;
    int PosicionY;

    private Tutorial3View mOpenCvCameraView;
    private List<Size> mResolutionList;
    private Menu mMenu;
    private boolean mCameraStarted = false;
    private boolean mMenuItemsCreated = false;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;

    private Mat                    mRgba;
    private Mat                    mIntermediateMat;
    private Mat                    mGray;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        PosicionX=-1;
        PosicionY=-1;

        if (OpenCVLoader.initLocal()) {
            Log.i(TAG, "OpenCV loaded successfully");
        } else {
            Log.e(TAG, "OpenCV initialization failed!");
            (Toast.makeText(this, "OpenCV initialization failed!", Toast.LENGTH_LONG)).show();
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.tutorial3_surface_view);

        mOpenCvCameraView = (Tutorial3View) findViewById(R.id.tutorial3_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.enableView();
            mOpenCvCameraView.setOnTouchListener(MainActivity.this);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(mOpenCvCameraView);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mCameraStarted = true;
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);

    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        // Caso 0: Imagen a Color Original
        mRgba = inputFrame.rgba();

        // Caso 1: Escala de Grises
        Imgproc.cvtColor(inputFrame.gray(), mRgba, Imgproc.COLOR_GRAY2RGBA, 4);

        // Caso 2: Canny
        mRgba = inputFrame.rgba();
        Imgproc.Canny(inputFrame.gray(), mIntermediateMat, 80, 100);
        Imgproc.cvtColor(mIntermediateMat, mRgba, Imgproc.COLOR_GRAY2RGBA, 4);
//      Caso 3: Dibujar Algo
        mRgba = inputFrame.rgba();
        Imgproc.circle(mRgba, new Point(PosicionY, PosicionX),
                200, new Scalar(0, 255, 0), 5);
        return mRgba;



    }


    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        PosicionX = (int) event.getX();
        PosicionY = (int) event.getY();
        Log.i(TAG,"onTouch event");
        Toast.makeText(this, ""+checkStoragePermissions(), Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 1);
                return false;
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateandTime = sdf.format(new Date());
        String fileName = "sample_picture_" + currentDateandTime + ".jpg";
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();
        return false;
    }

    public boolean checkStoragePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11 (R) or above
            return Environment.isExternalStorageManager();
        }else {
            //Below android 11
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
        }
    }
}