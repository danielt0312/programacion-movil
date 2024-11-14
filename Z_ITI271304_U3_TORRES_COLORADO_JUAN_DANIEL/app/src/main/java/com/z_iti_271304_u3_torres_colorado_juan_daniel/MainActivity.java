package com.z_iti_271304_u3_torres_colorado_juan_daniel;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int READ_IMAGE_REQUEST_CODE = 41;
    private ImageView imageView;
    private Button selectImageBtn;
    private Bitmap bitmap;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // Variables para el movimiento, escalado y rotación
    private float startX = 0f;
    private float startY = 0f;
    private float scale = 1f;
    private float lastAngle = 0f;
    private float rotation = 0f;
    private float[] lastEvent = null;

    private ScaleGestureDetector scaleGestureDetector;

    // Modos de interacción
    private enum Mode { NONE, DRAG, ZOOM, ROTATE }
    private Mode mode = Mode.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Seleccione una imágen", Toast.LENGTH_SHORT).show();

        imageView = findViewById(R.id.imageSelected);
        selectImageBtn = findViewById(R.id.btnSelectImg);

        // Gestor de detección de escala
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Botón para seleccionar la imagen
        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, READ_IMAGE_REQUEST_CODE);
        });

        // Evento táctil en el ImageView
        imageView.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: // Primer dedo toca la pantalla
                    savedMatrix.set(matrix);
                    startX = event.getX();
                    startY = event.getY();
                    mode = Mode.DRAG;
                    lastEvent = null;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN: // Segundo dedo toca la pantalla
                    savedMatrix.set(matrix);
                    mode = Mode.ZOOM;
                    if (event.getPointerCount() == 2) {
                        lastAngle = getRotation(event);
                        mode = Mode.ROTATE;
                    }
                    lastEvent = new float[4];
                    lastEvent[0] = event.getX(0);
                    lastEvent[1] = event.getY(0);
                    lastEvent[2] = event.getX(1);
                    lastEvent[3] = event.getY(1);
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode == Mode.DRAG) {
                        // Movimiento
                        matrix.set(savedMatrix);
                        float dx = event.getX() - startX;
                        float dy = event.getY() - startY;
                        matrix.postTranslate(dx, dy);

                    } else if (mode == Mode.ZOOM || mode == Mode.ROTATE) {
                        // Escala y rotación
                        if (lastEvent != null && event.getPointerCount() == 2) {
                            float newAngle = getRotation(event);
                            rotation = newAngle - lastAngle;
                            matrix.postRotate(rotation, imageView.getWidth() / 2f, imageView.getHeight() / 2f);
                            lastAngle = newAngle;
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    mode = Mode.NONE;
                    lastEvent = null;
                    break;
            }

            imageView.setImageMatrix(matrix);
            return true;
        });
    }

    // Manejo de selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_IMAGE_REQUEST_CODE && data != null) {
            Uri uri = data.getData();
            ContentResolver cr = getContentResolver();
            try (InputStream is = cr.openInputStream(uri)) {
                bitmap = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bitmap);
                matrix.reset();
                imageView.setImageMatrix(matrix);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Listener para el gesto de escala
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            scale *= scaleFactor;
            if (scale < 0.1f) scale = 0.1f; // Evitar que se haga muy pequeña
            if (scale > 10f) scale = 10f;   // Evitar que se haga muy grande
            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            imageView.setImageMatrix(matrix);
            return true;
        }
    }

    // Obtener el ángulo de rotación
    private float getRotation(MotionEvent event) {
        double deltaX = (event.getX(0) - event.getX(1));
        double deltaY = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }
}
