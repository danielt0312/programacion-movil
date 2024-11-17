package com.z_iti_271304_u3_torres_colorado_juan_daniel;

import static android.os.FileUtils.closeQuietly;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int READ_IMAGE_REQUEST_CODE = 41;
    private ImageView imageView;
    private Button btnSelectImage, btnResetPosition, btnCropImage, btnChangeRegion;
    private Bitmap bitmap;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private TextView txtGrados;

    // Variables para el movimiento, escalado y rotación
    private float startX = 0f;
    private float startY = 0f;
    private float lastAngle = 0f;
    private float rotation = 0f;
    private float[] lastEvent = null;
    private float rotationAngle = 0f;

    // Escalas
    private Region region;
    private ScaleGestureDetector scaleGestureDetector;

    // Modos de interacción
    private enum Mode { NONE, DRAG, ZOOM, ROTATE }
    private Mode mode = Mode.NONE;

    private static final int CODE_WRITE = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();

        imageView = findViewById(R.id.imgSelected);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnResetPosition = findViewById(R.id.btnReset);
        txtGrados = findViewById(R.id.txtGrados);
        btnCropImage = findViewById(R.id.btnCropImage);
        btnChangeRegion = findViewById(R.id.btnChangeRegion);

        FrameLayout fmyImage = findViewById(R.id.fmyImage);

        // Redimensionar tamaño de la región a recortar despues de que su contenedor este creado
        region = new Region(fmyImage, findViewById(R.id.viewCropRegion));
        fmyImage.getViewTreeObserver().addOnGlobalLayoutListener(() -> region.customRegionSize());

        // Gestor de detección de escala
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener(matrix, imageView));

        btnCropImage.setOnClickListener(v -> {
            if (bitmap != null) {
                Bitmap croppedBitmap = cropImage();
                if (croppedBitmap != null) {
                    saveImage(croppedBitmap);
                }
            } else {
                Toast.makeText(this, "No hay imagen cargada", Toast.LENGTH_SHORT).show();
            }
        });

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, READ_IMAGE_REQUEST_CODE);
        });

        btnResetPosition.setOnClickListener(v -> {
            if (bitmap != null) {
                resetImagePosition();
            } else {
                Toast.makeText(this, "No hay imagen cargada", Toast.LENGTH_SHORT).show();
            }
        });

        btnChangeRegion.setOnClickListener(v -> region.changeAspectRegion(this));

        imageView.setOnTouchListener(touchListener());
    }

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

    private View.OnTouchListener touchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (bitmap == null) return false;

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
                drawRotationInfo();

                return true;
            }
        };
    }

    private float getRotation(MotionEvent event) {
        double deltaX = (event.getX(0) - event.getX(1));
        double deltaY = (event.getY(0) - event.getY(1));
        return (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    private Bitmap cropImage() {
        int cropRegionWidth = region.getWidth();
        int cropRegionHeight = region.getHeight();

        float[] values = new float[9];
        matrix.getValues(values);

        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];

        int scaledImageWidth = (int) (bitmap.getWidth() * scaleX);
        int scaledImageHeight = (int) (bitmap.getHeight() * scaleY);

        if (scaledImageWidth < cropRegionWidth || scaledImageHeight < cropRegionHeight) {
            Toast.makeText(this, "La imagen debe ser más grande que la región de recorte", Toast.LENGTH_SHORT).show();
            return null;
        }

        int cropRegionX = (imageView.getWidth() - cropRegionWidth) / 2;
        int cropRegionY = (imageView.getHeight() - cropRegionHeight) / 2;

        int x = (int) ((cropRegionX - values[Matrix.MTRANS_X]) / scaleX);
        int y = (int) ((cropRegionY - values[Matrix.MTRANS_Y]) / scaleY);
        int width = (int) (cropRegionWidth / scaleX);
        int height = (int) (cropRegionHeight / scaleY);

        if (x < 0 || y < 0 || x + width > bitmap.getWidth() || y + height > bitmap.getHeight()) {
            Toast.makeText(this, "La región seleccionada excede los límites de la imagen", Toast.LENGTH_SHORT).show();
            return null;
        }

        int centerX = width / 2;
        int centerY = height / 2;

        float angleInRadians = (float) Math.toRadians(rotationAngle);

        float cosAngle = (float) Math.cos(angleInRadians);
        float sinAngle = (float) Math.sin(angleInRadians);

        float newX = (x - centerX) * cosAngle - (y - centerY) * sinAngle + centerX;
        float newY = (x - centerX) * sinAngle + (y - centerY) * cosAngle + centerY;

        Log.d("Point", x+","+y);
        Log.d("RegionPoint", ((int) region.getX())+"," + ((int) region.getY()));

        return Bitmap.createBitmap(bitmap, (int) newX, (int) newY, width, height, matrix, true);
    }

    private void saveImage(Bitmap bitmap) {
        try {
            ContentResolver contentResolver = getContentResolver();
            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String fileName = "cropped_image.png";

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES); // Directorio Pictures

            Uri fileUri = contentResolver.insert(contentUri, contentValues);

            if (fileUri != null) {
                try (OutputStream outputStream = contentResolver.openOutputStream(fileUri)) {
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        Toast.makeText(this, "Imagen guardada en la carpeta Pictures", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetImagePosition() {
        matrix.reset();
        imageView.setImageMatrix(matrix);
        imageView.invalidate();
    }

    private void drawRotationInfo() {
        float[] values = new float[9];
        matrix.getValues(values);
        rotationAngle = (float) Math.toDegrees(Math.atan2(values[Matrix.MSKEW_X], values[Matrix.MSCALE_X]));
        txtGrados.setText("Grados de la imagen: " + String.format("%.2f°", rotationAngle));
    }
}