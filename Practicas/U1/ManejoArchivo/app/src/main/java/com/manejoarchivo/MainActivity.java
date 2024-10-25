package com.manejoarchivo;

/*
https://github.com/kesenhoo/AndroidApiDemo/blob/master/app/src/main/java/com/example/android/apis/content/DocumentsSample.java
*/

import static android.os.FileUtils.closeQuietly;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> launcher; // Initialise this object in Activity.onCreate()
    private Uri baseDocumentTreeUri;

    private Context context;
    private static final int READ_REQUEST_CODE = 40;
    private static final int READ_IMAGE_REQUEST_CODE = 41;
    private static final int CODE_WRITE = 43;
    private static final int CODE_TREE = 44;

    private String FileContents;

    Button B1, B2, B3;
    EditText ET1;
    Bitmap bitmap;
    int ImageRows = 480*2;
    int ImageCols = 640*2;
    ImageView IM1;
    String FileName;
    ListView LV1;
    List <String> fileNames, fileContent;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileName="PulpFictionDialogue.txt";
        FileContents ="¿Y sabes cómo llaman al cuarto de libra con queso en París? - ¿No lo llaman cuarto de libra con queso? - Utilizan el sistema métrico, no sabrían qué carajos es un cuarto de libra. - ¿Pues cómo lo llaman? - Lo llaman una “Royale con queso” – Royale con queso. jajaa… ¿y cómo llaman al Big Mac? – Un Big Mac es un Big Mac, pero lo llaman “Le Big Mac” – “Le Big Mac”( Pulp Fiction)";

        B1 = findViewById(R.id.BotonUsuarioSeleccionaArchivoTextoLectura);
        B2 = findViewById(R.id.BotonUsuarioSeleccionaImagenes);
        B3 = findViewById(R.id.BotonUsuarioGuardaArchivoTexto);
        ET1 = findViewById(R.id.editText1);
        IM1 = findViewById(R.id.imageView1);
        LV1 = findViewById(R.id.listView1);
        fileNames = new ArrayList<>();
        fileContent = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, fileNames);

        B1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                // filter to only show openable items.
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                //intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

        B2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                // filter to only show openable items.
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, READ_IMAGE_REQUEST_CODE);
            }
        });

        B3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TITLE, FileName);
                startActivityForResult(intent, CODE_WRITE);
            }
        });

        LV1.setAdapter(adapter);
        LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ET1.setText("");
                ET1.setText(fileContent.get(i));
                Toast.makeText(getApplicationContext(),
                        ((TextView) view).getText()+" - Posicion: "+ i, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ContentResolver cr = getContentResolver();
        final Uri uri = data != null ? data.getData() : null;
        if (uri != null) {
            //log("isDocumentUri=" + DocumentsContract.isDocumentUri(this, uri));
        } else {
            //log("missing URI?");
            return;
        }


        if (requestCode == READ_REQUEST_CODE) {
            try {
                cr.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                //log("FAILED TO TAKE PERMISSION", e);
            }
            InputStream is = null;
            try {
                is = cr.openInputStream(uri);
                //is.readAllBytes();
                //String result = IOUtils.toString(is, StandardCharsets.UTF_8);
                String result = new BufferedReader(new InputStreamReader(is))
                        .lines().collect(Collectors.joining("\n"));

                fileNames.add(queryName(cr, uri));
                fileContent.add(result);

                ET1.setText(result);
                ET1.setText(fileNames.size() + "\n" + fileContent.size());

                //log("read length=" + readFullyNoClose(is).length);
            } catch (Exception e) {
                //log("FAILED TO READ", e);
            } finally {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    closeQuietly(is);
                }
            }
        }

        if (requestCode == READ_IMAGE_REQUEST_CODE) {
            try {
                cr.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (SecurityException e) {
                //log("FAILED TO TAKE PERMISSION", e);
            }
            InputStream is = null;
            try {
                is = cr.openInputStream(uri);
                bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getApplicationContext()
                            .getContentResolver().openInputStream(data.getData()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap scaledbmp = Bitmap.createScaledBitmap(bitmap, ImageCols, ImageRows, false);
                IM1.setImageBitmap(scaledbmp);

            } catch (Exception e) {
                //log("FAILED TO READ", e);
            } finally {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    closeQuietly(is);
                }
            }
        }

        if (requestCode == CODE_WRITE) {
            try {
                cr.takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            } catch (SecurityException e) {
                //log("FAILED TO TAKE PERMISSION", e);
            }
            OutputStream os;
            os = null;
            try {
                os = cr.openOutputStream(uri);
                os.write(FileContents.getBytes());
                //log("wrote data");
            } catch (Exception e) {
                //log("FAILED TO WRITE", e);
            } finally {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    closeQuietly(os);
                }
            }
        }
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
}