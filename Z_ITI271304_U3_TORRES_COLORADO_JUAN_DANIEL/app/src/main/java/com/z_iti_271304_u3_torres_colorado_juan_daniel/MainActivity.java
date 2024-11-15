package com.z_iti_271304_u3_torres_colorado_juan_daniel;

import static android.os.FileUtils.closeQuietly;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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

    private View cropRegion;
    private Button cropImageBtn;


    // Modos de interacción
    private enum Mode { NONE, DRAG, ZOOM, ROTATE }
    private Mode mode = Mode.NONE;

    private static final int CODE_WRITE = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();

        imageView = findViewById(R.id.imageSelected);
        selectImageBtn = findViewById(R.id.btnSelectImg);
        resetBtn = findViewById(R.id.btnReset);
        txtGrados = findViewById(R.id.txtGrados);

        cropRegion = findViewById(R.id.cropRegion);
        cropImageBtn = findViewById(R.id.btnCropImage);

        // Botón para recortar la imagen
        cropImageBtn.setOnClickListener(v -> {
            if (bitmap != null) {
                Bitmap croppedBitmap = cropImage();
                if (croppedBitmap != null) {
                    saveCroppedImage(croppedBitmap);
                }
            } else {
                Toast.makeText(this, "No hay imagen cargada", Toast.LENGTH_SHORT).show();
            }
        });

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
                    break;
            }

            imageView.setImageMatrix(matrix);
            drawRotationInfo(); // Mostrar los grados de rotación

            // Añadir esto para corregir la advertencia
            return true;
        });
    }

    // Manejo de selección de imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final ContentResolver cr = getContentResolver();
        final Uri uri = data != null ? data.getData() : null;

        if (requestCode == READ_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try (InputStream is = cr.openInputStream(uri)) {
                bitmap = BitmapFactory.decodeStream(is);
                imageView.setImageBitmap(bitmap);
                resetImagePosition();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CODE_WRITE && resultCode == RESULT_OK && data != null) {
            try {
                cr.takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } catch (SecurityException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al obtener permisos para guardar la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            OutputStream os = null;
            try {
                os = cr.openOutputStream(uri);
                if (os != null) {
                    // Comprimir y guardar el bitmap como PNG
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    Toast.makeText(this, "Imagen guardada exitosamente", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            } finally {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    closeQuietly(os);
                }
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

    private Bitmap cropImage() {
        // Aplicar la transformación al bitmap
        Bitmap transformedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(transformedBitmap);
        canvas.drawBitmap(bitmap, matrix, null);

        // Obtener dimensiones de la región de recorte
        int cropRegionWidth = cropRegion.getWidth();
        int cropRegionHeight = cropRegion.getHeight();
        int cropRegionX = (imageView.getWidth() - cropRegionWidth) / 2;
        int cropRegionY = (imageView.getHeight() - cropRegionHeight) / 2;

        // Calcular las coordenadas de recorte
        float[] values = new float[9];
        matrix.getValues(values);

        // Coordenadas de la imagen en la pantalla
        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];
        float transX = values[Matrix.MTRANS_X];
        float transY = values[Matrix.MTRANS_Y];

        // Transformar coordenadas al espacio de la imagen transformada
        int x = (int) ((cropRegionX - transX) / scaleX);
        int y = (int) ((cropRegionY - transY) / scaleY);
        int width = (int) (cropRegionWidth / scaleX);
        int height = (int) (cropRegionHeight / scaleY);

        // Validar si las coordenadas están dentro de los límites de la imagen transformada
        if (x < 0 || y < 0 || x + width > transformedBitmap.getWidth() || y + height > transformedBitmap.getHeight()) {
            Toast.makeText(this, "La región seleccionada excede los límites de la imagen", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Recortar y devolver el Bitmap
        return Bitmap.createBitmap(transformedBitmap, x, y, width, height);
    }

    private void saveCroppedImage(Bitmap croppedBitmap) {
        try {
            // Generar un nombre de archivo
            String fileName = "cropped_image_" + System.currentTimeMillis() + ".png";
            File file = new File(getExternalFilesDir(null), fileName);

            // Guardar el bitmap
            FileOutputStream outputStream = new FileOutputStream(file);
            croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            Toast.makeText(this, "Imagen guardada en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }
}