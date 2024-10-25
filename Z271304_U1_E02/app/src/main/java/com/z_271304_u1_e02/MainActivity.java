package com.z_271304_u1_e02;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.graphics.pdf.PdfDocument;
import android.os.ParcelFileDescriptor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGES_REQUEST = 1;
    private static final int CREATE_FILE_REQUEST_CODE = 2;
    private Uri pdfUri;
    int x = 1224;
    int xSuma = 1224;
    int y = 1632;
    int ySuma = 1632;
    private Bitmap mosaicoBitmap;
    private ArrayList<BitmapWithCoordinates> bitmapsList = new ArrayList<>();
    private String tamanoSeleccionado;
    private boolean proportion = false;

    // Almacena un ArrayList de Bitmaps por cada página
    private ArrayList<ArrayList<BitmapWithCoordinates>> paginasBitmapsList = new ArrayList<>();
    // Página actual en la que se encuentra el usuario
    private int paginaActual = 0;

    public class BitmapWithCoordinates {
        private Bitmap bitmap;
        private int x;
        private int y;

        // Constructor para inicializar los valores
        public BitmapWithCoordinates(Bitmap bitmap, int x, int y) {
            this.bitmap = bitmap;
            this.x = x;
            this.y = y;
        }

        public BitmapWithCoordinates(Bitmap bitmap) {
            this.bitmap = bitmap;
            this.x = 1224;
            this.y = 1632;
        }

        // Getters y Setters
        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    private void agregarPagina() {
        paginasBitmapsList.add(new ArrayList<>()); // Añade una nueva página vacía
        paginaActual = paginasBitmapsList.size(); // Cambia a la última página agregada
        actualizarVistaPagina();
    }

    // Cambia a la siguiente página
    private void cambiarPaginaSiguiente() {
        if (paginaActual < paginasBitmapsList.size()) {
            paginaActual++;
            actualizarVistaPagina();
        }
    }

    // Cambia a la página anterior
    private void cambiarPaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            actualizarVistaPagina();
        }
    }

    // Actualiza la vista según la página actual
    private void actualizarVistaPagina() {
        if (paginaActual < paginasBitmapsList.size()) {
            bitmapsList = paginasBitmapsList.get(paginaActual);
            mosaicoBitmap = crearMosaico(bitmapsList);
            ImageView imageViewPreview = findViewById(R.id.imageViewPreview);
            imageViewPreview.setImageBitmap(mosaicoBitmap);
        } else if(paginaActual == paginasBitmapsList.size()){
            bitmapsList = new ArrayList<>();
            paginasBitmapsList.add(bitmapsList);
            mosaicoBitmap = crearMosaico(bitmapsList);
            ImageView imageViewPreview = findViewById(R.id.imageViewPreview);
            imageViewPreview.setImageBitmap(mosaicoBitmap);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        Button buttonSeleccionar = findViewById(R.id.buttonSeleccionar);
        Button buttonGenerarPDF = findViewById(R.id.buttonGenerarPDF);

        Button buttonPaginaAnterior = findViewById(R.id.buttonPaginaAnterior);
        Button buttonPaginaSiguiente = findViewById(R.id.buttonPaginaSiguiente);
        TextView textpaginaActual = findViewById(R.id.textViewPaginaActual);

        ImageView imageViewPreview = findViewById(R.id.imageViewPreview);

        buttonSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCropOptionsDialog();

                textpaginaActual.setText(String.valueOf(paginaActual + 1));
            }
        });

        buttonPaginaAnterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paginaActual > 0) {
                    cambiarPaginaAnterior();
                    textpaginaActual.setText(String.valueOf(paginaActual + 1));
                }
            }
        });

        buttonPaginaSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paginasBitmapsList.size() == paginaActual || paginasBitmapsList.isEmpty()){
                    agregarPagina();
                    cambiarPaginaSiguiente();
                    textpaginaActual.setText(String.valueOf(paginaActual + 1));
                } else if (paginasBitmapsList.size() > paginaActual){
                    cambiarPaginaSiguiente();
                    textpaginaActual.setText(String.valueOf(paginaActual  + 1));
                }
            }
        });

        buttonGenerarPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarUbicacionPDF();
            }
        });
    }

    private void seleccionarImagenes() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_IMAGES_REQUEST);
    }

    private void showCropOptionsDialog() {
        String[] options = {"Completo", "Mitad", "Proporciones"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona la opción de recorte")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            proportion = false;
                            // Lógica para recorte completo
                            Toast.makeText(this, "Recorte Completo seleccionado", Toast.LENGTH_SHORT).show();
                            x = 2550;
                            y = 3300;
                            seleccionarImagenes();
                            break;
                        case 1:
                            proportion = false;
                            // Lógica para recorte a la mitad
                            Toast.makeText(this, "Recorte Mitad seleccionado", Toast.LENGTH_SHORT).show();
                            x = 1275;
                            y = 1650;
                            seleccionarImagenes();
                            break;
                        case 2:
                            // Lógica para proporciones, pedir x y y
                            proportion = true;
                            showProportionInputs();
                            break;
                    }

                });
        builder.show();
    }


    private void showProportionInputs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ingresa proporciones (X y Y)");

        // Inflar el layout personalizado para inputs
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_proportion_inputs, null);
        builder.setView(customLayout);

        builder.setCancelable(false);

        // Referenciar los EditText desde `customLayout`
        EditText inputX = customLayout.findViewById(R.id.editTextX);
        EditText inputY = customLayout.findViewById(R.id.editTextY);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            try {
                // Obtén los valores de X y Y desde los EditText
                x = Integer.parseInt(inputX.getText().toString());
                y = Integer.parseInt(inputY.getText().toString());

                x = (int) ((x) * (300/2.54));
                y = (int) ((y) * (300/2.54));

                xSuma += x;
                ySuma += y;

                // Implementa aquí la lógica para usar los valores de X y Y, por ejemplo:

                Toast.makeText(this, "Proporciones ingresadas: X=" + x + ", Y=" + y, Toast.LENGTH_SHORT).show();
                seleccionarImagenes();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error al ingresar proporciones. Asegúrate de ingresar números.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {

                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                            bitmap = corregirOrientacion(bitmap, imageUri);
                            BitmapWithCoordinates bit = new BitmapWithCoordinates(bitmap, x, y);
                            bitmapsList.add(bit);
                        } catch (IOException e) {
                            Log.e("MainActivity", "Error al cargar la imagen: " + e.getMessage());
                        }
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        bitmap = corregirOrientacion(bitmap, imageUri);
                        BitmapWithCoordinates bit = new BitmapWithCoordinates(bitmap, x, y);
                        bitmapsList.add(bit);
                    } catch (IOException e) {
                        Log.e("MainActivity", "Error al cargar la imagen: " + e.getMessage());
                    }
                }

                if (!bitmapsList.isEmpty()) {

                    mosaicoBitmap = crearMosaico(bitmapsList);
                    ImageView imageViewPreview = findViewById(R.id.imageViewPreview);
                    imageViewPreview.setImageBitmap(mosaicoBitmap);
                    if(paginasBitmapsList.isEmpty()){
                        paginasBitmapsList.add(bitmapsList);
                    } else {
                        paginasBitmapsList.set(paginaActual, bitmapsList);
                    }
                }

            }
        } else if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                pdfUri = data.getData();
                Toast.makeText(this, "Ubicación seleccionada: " + pdfUri.toString(), Toast.LENGTH_LONG).show();
                crearPDF();
            }
        }
    }

    private Bitmap crearMosaico(ArrayList<BitmapWithCoordinates> bitmaps) {
        if (bitmaps.size() == 0) return null;

        // Dimensiones de una hoja tamaño carta en píxeles (21.59 x 27.94 cm a 300 PPI)
        int pageWidth = 2550;  // Ancho en píxeles
        int pageHeight = 3300; // Alto en píxeles

        // Crear un bitmap para la página tamaño carta
        Bitmap mosaico = Bitmap.createBitmap(pageWidth, pageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mosaico);
        Paint paint = new Paint();

        // Posición inicial para colocar las imágenes
        int currentX = 0;
        int currentY = 0;
        int maxHeightInRow = 0;  // Altura máxima en la fila actual

        // Dibujar las imágenes en el mosaico
        for (int i = 0; i < bitmaps.size(); i++) {
            Bitmap originalBitmap = bitmaps.get(i).getBitmap();
            int originalWidth = bitmaps.get(i).getX();  // Ancho original de la imagen
            int originalHeight = bitmaps.get(i).getY(); // Alto original de la imagen

            // Ajustar el tamaño de la imagen si es más grande que la página
            float scalingFactor = Math.min(1.0f, Math.min((float) pageWidth / originalWidth, (float) pageHeight / originalHeight));

            // Calcular el nuevo tamaño de la imagen con el factor de escalado
            int scaledWidth = (int) (originalWidth * scalingFactor);
            int scaledHeight = (int) (originalHeight * scalingFactor);

            // Verificar si la imagen cabe en la fila actual
            if (currentX + scaledWidth > pageWidth) {
                // Si no cabe, mover a la siguiente fila
                currentX = 0;
                currentY += maxHeightInRow;  // Mover hacia abajo, según la altura máxima de la fila anterior
                maxHeightInRow = 0;  // Reiniciar la altura máxima de la nueva fila
            }

            // Verificar si la imagen cabe en la página (en altura)
            if (currentY + scaledHeight > pageHeight) {
                // Si no cabe en la página, mostrar un mensaje
                Toast.makeText(this, "La imagen no cabe en la página.", Toast.LENGTH_LONG).show();
                continue;  // Ignorar esta imagen y seguir con la siguiente
            }

            // Dibujar la imagen escalada en la posición actual
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);
            canvas.drawBitmap(scaledBitmap, currentX, currentY, paint);

            // Actualizar la posición X para la siguiente imagen
            currentX += scaledWidth;

            // Actualizar la altura máxima de la fila actual
            if (scaledHeight > maxHeightInRow) {
                maxHeightInRow = scaledHeight;
            }

            // Ajuste adicional: Si el espacio restante en la fila es suficiente para otra imagen,
            // verificar que el espacio se aproveche completamente
            if (currentX + scaledWidth > pageWidth) {
                currentX = 0;  // Reiniciar al inicio de la fila
                currentY += maxHeightInRow;  // Bajar al inicio de la siguiente fila
                maxHeightInRow = 0;  // Reiniciar la altura de la fila
            }
        }

        return mosaico;
    }


    private String obtenerRutaDesdeUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return null;
    }


    private Bitmap corregirOrientacion(Bitmap bitmap, Uri uri) {
        try {
            // Convertir URI a una ruta de archivo (file path)
            String filePath = obtenerRutaDesdeUri(uri);
            if (filePath != null) {
                ExifInterface exif = new ExifInterface(filePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        return rotarBitmap(bitmap, 90);
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        return rotarBitmap(bitmap, 180);
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        return rotarBitmap(bitmap, 270);
                    default:
                        return bitmap;
                }
            } else {
                Log.e("MainActivity", "Error al obtener la ruta de la URI.");
                return bitmap;
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Error al corregir la orientación: " + e.getMessage());

            return bitmap;
        }
    }


    private Bitmap rotarBitmap(Bitmap bitmap, int grados) {
        Matrix matrix = new Matrix();
        matrix.postRotate(grados);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }


    private void crearPDF() {
        if (pdfUri != null) {
            try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(pdfUri, "w");
                 FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())) {

                PdfDocument document = new PdfDocument();

                int pageCount = 1;
                for (ArrayList<BitmapWithCoordinates> paginaBitmaps : paginasBitmapsList) {
                    if (paginaBitmaps.isEmpty()) continue;
                    Bitmap mosaicoBitmap = crearMosaico(paginaBitmaps);
                    if (mosaicoBitmap != null) {
                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                                mosaicoBitmap.getWidth(),
                                mosaicoBitmap.getHeight(),
                                pageCount).create();
                        pageCount++;
                        PdfDocument.Page page = document.startPage(pageInfo);
                        Canvas canvas = page.getCanvas();
                        Paint paint = new Paint();
                        canvas.drawBitmap(mosaicoBitmap, 0, 0, paint);
                        document.finishPage(page);
                    }
                }

                document.writeTo(fos);
                document.close();

                Toast.makeText(this, "PDF creado exitosamente en " + pdfUri.getPath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e("MainActivity", "Error al crear el PDF: " + e.getMessage());
            }
        }
    }


    private void seleccionarUbicacionPDF() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "mosaico.pdf");
        startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }
}
