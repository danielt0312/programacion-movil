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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int READ_IMAGE_REQUEST_CODE = 41;
    private ImageView imageView;
    private View overlayRegion;
    private Button selectImageBtn, resetBtn;
    private Bitmap bitmap;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private TextView txtGrados;

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

        Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();

        imageView = findViewById(R.id.imageSelected);
        overlayRegion = findViewById(R.id.overlayRegion);
        selectImageBtn = findViewById(R.id.btnSelectImg);
        resetBtn = findViewById(R.id.btnReset);
        txtGrados = findViewById(R.id.txtGrados);

        // Gestor de detección de escala
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        // Botón para seleccionar la imagen
        selectImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, READ_IMAGE_REQUEST_CODE);
        });

        // Botón para reiniciar la imagen
        resetBtn.setOnClickListener(v -> {
            if (bitmap != null) {
                resetImagePosition();
            } else {
                Toast.makeText(this, "No hay imagen cargada", Toast.LENGTH_SHORT).show();
            }
        });

        // Evento táctil en el ImageView
        imageView.setOnTouchListener((v, event) -> {
            if (bitmap == null) return false; // Validar que la imagen esté cargada

            scaleGestureDetector.onTouchEvent(event);

            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    startX = event.getX();
                    startY = event.getY();
                    mode = Mode.DRAG;
                    lastEvent = null;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
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
                        matrix.set(savedMatrix);
                        float dx = event.getX() - startX;
                        float dy = event.getY() - startY;
                        matrix.postTranslate(dx, dy);

                    } else if (mode == Mode.ZOOM || mode == Mode.ROTATE) {
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
                    checkAlignment();
                    break;
            }

            imageView.setImageMatrix(matrix);
            drawRotationInfo(); // Mostrar los grados de rotación
            return true;
        });
    }

    // Manejo de selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            ContentResolver cr = getContentResolver();
            try (InputStream is = cr.openInputStream(uri)) {
                bitmap = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bitmap);
                resetImagePosition();
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
            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            imageView.setImageMatrix(matrix);
            return true;
        }
    }

    private float getRotation(MotionEvent event) {
        double deltaX = (event.getX(0) - event.getX(1));
        double deltaY = (event.getY(0) - event.getY(1));
        return (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    private void resetImagePosition() {
        matrix.reset();
        imageView.setImageMatrix(matrix);
        imageView.invalidate();
    }

    private void drawRotationInfo() {
        float[] values = new float[9];
        matrix.getValues(values);
        float currentRotation = (float) Math.toDegrees(Math.atan2(values[Matrix.MSKEW_X], values[Matrix.MSCALE_X]));
        txtGrados.setText("Grados de la imagen: " + String.format("%.2f°", currentRotation));
    }

    private void checkAlignment() {
        float[] values = new float[9];
        matrix.getValues(values);

        // Obtener los límites de la región superpuesta
        float regionLeft = overlayRegion.getLeft();
        float regionTop = overlayRegion.getTop();
        float regionRight = overlayRegion.getRight();
        float regionBottom = overlayRegion.getBottom();

        float regionWidth = regionRight - regionLeft;
        float regionHeight = regionBottom - regionTop;

        // Obtener los límites de la imagen transformada
        float[] imageCorners = new float[]{
                0, 0,
                bitmap.getWidth(), 0,
                bitmap.getWidth(), bitmap.getHeight(),
                0, bitmap.getHeight()
        };
        matrix.mapPoints(imageCorners);

        float imageLeft = Math.min(Math.min(imageCorners[0], imageCorners[2]), Math.min(imageCorners[4], imageCorners[6]));
        float imageTop = Math.min(Math.min(imageCorners[1], imageCorners[3]), Math.min(imageCorners[5], imageCorners[7]));
        float imageRight = Math.max(Math.max(imageCorners[0], imageCorners[2]), Math.max(imageCorners[4], imageCorners[6]));
        float imageBottom = Math.max(Math.max(imageCorners[1], imageCorners[3]), Math.max(imageCorners[5], imageCorners[7]));

        float imageWidth = imageRight - imageLeft;
        float imageHeight = imageBottom - imageTop;

        // Definir tolerancia
        float sizeTolerance = 20f; // Tolerancia de 20 píxeles

        // Verificar si la imagen cumple con el criterio
        boolean isHorizontalAligned =
                (imageHeight >= regionHeight - sizeTolerance) &&
                        (imageHeight <= regionHeight + sizeTolerance) &&
                        (imageLeft <= regionRight && imageRight >= regionLeft);

        boolean isVerticalAligned =
                (imageWidth >= regionWidth - sizeTolerance) &&
                        (imageWidth <= regionWidth + sizeTolerance) &&
                        (imageTop <= regionBottom && imageBottom >= regionTop);

        // Evaluar si al menos una de las dos dimensiones está alineada
        if (isHorizontalAligned)
            Toast.makeText(this, "Imágen alineada horizontalmente", Toast.LENGTH_SHORT).show();
        else if (isVerticalAligned)
            Toast.makeText(this, "Imágen alineada verticalmente", Toast.LENGTH_SHORT).show();
    }
}
