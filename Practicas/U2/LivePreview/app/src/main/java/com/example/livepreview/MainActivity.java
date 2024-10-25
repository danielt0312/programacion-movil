package com.example.livepreview;
/*
Version Simplificada de la aplicacion vision-quickstart de MLToolKit

** NO se requiere ningun permiso de Firebase
(De hecho Firebase esta descontinuado, si encuentran demos que hablen de el huyan)


 */

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LivePreviewActivity";
    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;

    private static final String FACE_DETECTION = "Face Detection";

    private static final int CAMERA_PERMISSION_CODE = 100;

    private String selectedModel = FACE_DETECTION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(getApplicationContext(),"Pluto",Toast.LENGTH_SHORT).show();

        preview = findViewById(R.id.preview_view);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }
        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        createCameraSource(selectedModel);
        checkPermission(android.Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);

        ToggleButton facingSwitch = findViewById(R.id.facing_switch);
        facingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Toast.makeText(getApplicationContext(),"PL",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Set facing");
                if (cameraSource != null) {
                    if (isChecked) {
                        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
                    } else {
                        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);

                    }
                }
                preview.stop();
                startCameraSource();
            }
        });
    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }

    }


    private void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }

        try {
            Log.i(TAG, "Using Face Detector Processor");
            cameraSource.setMachineLearningFrameProcessor(new FaceDetectorProcessor(this));
        } catch (RuntimeException e) {
            Log.e(TAG, "Can not create image processor: " + model, e);
            Toast.makeText(
                            getApplicationContext(),
                            "Can not create image processor: " + e.getMessage(),
                            Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        createCameraSource(selectedModel);
        startCameraSource();
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    /** Stops the camera. */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }
}
