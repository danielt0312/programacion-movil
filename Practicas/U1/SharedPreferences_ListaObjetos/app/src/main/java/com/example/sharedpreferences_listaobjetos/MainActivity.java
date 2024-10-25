package com.example.sharedpreferences_listaobjetos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/* Para poder agregar este import es necesario agregar la siguiente linea:
compile 'com.google.code.gson:gson:2.3.1'
Al archivo build.graddle (Module:app)
Inmediantamente despues de la linea
compile 'com.android.support:appcompat-v7:25.1.0'
Resincronizar proyecto (CON CONEXION A INTERNET!!)
 */
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final Context context = this;

    ListView LV1;

    ArrayList<EstudiantesGueros> ListaEG;
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor editor;
    ArrayAdapter<EstudiantesGueros> adapter;

    Button BT1;
    EditText ETNombre, ETAp, ETAm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LV1 = (ListView) findViewById(R.id.listview1);
        BT1 = findViewById(R.id.btnAgregar);
        ETNombre = findViewById(R.id.nombre);
        ETAp = findViewById(R.id.apellidoPaterno);
        ETAm = findViewById(R.id.apellidoMaterno);

        LV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),
//                        ""+ListaEG.get(i).toString(), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Selecciona una opción");
                builder.setCancelable(false);
                // Add the buttons
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        ListaEG.remove(i);
                        adapter = new ArrayAdapter<EstudiantesGueros>(getApplication(),android.R.layout.simple_list_item_1, ListaEG);
                        LV1.setAdapter(adapter);

//                        Toast.makeText(context,"Seleccionaste Excelente",Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("Visualizar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
//                        Toast.makeText(context,"Seleccionaste ser Mediocre",Toast.LENGTH_SHORT).show();

                        EstudiantesGueros EG1 = new EstudiantesGueros(18,"Smith","Kennedy","John",2,true,true,10.5,5.5);
                        ListaEG.set(i, EG1);
                        adapter = new ArrayAdapter<EstudiantesGueros>(getApplication(),android.R.layout.simple_list_item_1, ListaEG);
                        LV1.setAdapter(adapter);

                        dialog.cancel();
                    }
                });
                // Set other dialog properties

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                // Mostrar el dialogl
                dialog.show();
            }
        });

        BT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListaEG.add(new EstudiantesGueros(18, ETNombre.getText().toString(), ETAp.getText().toString(), ETAm.getText().toString(), 2,true,true,10.5,5.5));


                String connectionsJSONString = new Gson().toJson(ListaEG);
                editor.putString("ListaGueros", connectionsJSONString);
                editor.apply();

                ETNombre.setText("");
                ETAp.setText("");
                ETAm.setText("");
            }
        });
    }

    @Override
    public void onResume () {
        super.onResume();
        ListaEG = new ArrayList<EstudiantesGueros>();

        sharedPrefs = context.getSharedPreferences("MarcoNunoGSON5", Context.MODE_PRIVATE);
        editor = sharedPrefs .edit();

        String connectionsJSONString = sharedPrefs.getString("ListaGueros", null);
        if (connectionsJSONString!=null) {
            Type type = new TypeToken<ArrayList<EstudiantesGueros>>() {}.getType();
            ListaEG = new Gson().fromJson(connectionsJSONString, type);
            Toast.makeText(getApplicationContext(), "Elementos en la lista: " + ListaEG.size(), Toast.LENGTH_SHORT).show();
            adapter = new ArrayAdapter<EstudiantesGueros>(getApplication(),android.R.layout.simple_list_item_1, ListaEG);
            LV1.setAdapter(adapter);
        }
        else
            Toast.makeText(getApplicationContext(), "Lista NULA!!: " , Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first

        EstudiantesGueros EG1 = new EstudiantesGueros(18,"Nuño","Maganda","Toromaxito",2,true,true,10.5,5.5);
        ListaEG.add(EG1);

        String connectionsJSONString = new Gson().toJson(ListaEG);
        editor.putString("ListaGueros", connectionsJSONString);
        editor.apply();

        Toast.makeText(getApplicationContext(),"Elementos en la lista: "+ListaEG.size()+"\n"+connectionsJSONString,Toast.LENGTH_SHORT).show();
    }
}
